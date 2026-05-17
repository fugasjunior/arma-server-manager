import {Button, Grid, Stack, Typography} from "@mui/material";
import {useEffect, useState} from "react";
import {useForm, FormProvider} from "react-hook-form";
import {steamAuthApi} from "../../api/client";
import {toast} from "react-toastify";
import ConfirmationDialog from "../../UI/ConfirmationDialog";
import {SteamAuthDto} from "../../api/generated";
import {CustomTextField} from "../../UI/Form/CustomTextField.tsx";

const SteamAuthForm = () => {
    const [loadedAuth, setLoadedAuth] = useState<SteamAuthDto>({
        username: '',
        password: '',
        steamGuardToken: ''
    });
    const [clearDialogOpen, setClearDialogOpen] = useState(false);
    const [loaded, setLoaded] = useState(false);

    const methods = useForm<SteamAuthDto>({
        defaultValues: loadedAuth
    });

    useEffect(() => {
        const fetchAuth = async () => {
            try {
                const {data: auth} = await steamAuthApi.getSteamAuth();
                setLoadedAuth({
                    username: auth.username ?? "",
                    password: auth.password ?? "",
                    steamGuardToken: auth.steamGuardToken ?? ""
                });
            } catch (e) {
                console.error(e);
                toast.error("Could not fetch Steam Auth.");
            }
            setLoaded(true);
        }

        fetchAuth();
    }, []);

    useEffect(() => {
        methods.reset(loadedAuth);
    }, [loadedAuth, methods]);

    const handleSubmit = async (values: SteamAuthDto) => {
        try {
            await steamAuthApi.setSteamAuth({steamAuthDto: {
                username: values.username,
                password: values.password,
                steamGuardToken: values.steamGuardToken?.trim()
            }});
            setLoadedAuth(prevState => {
                return {...prevState, password: ""};
            });
            toast.success("Steam Auth successfully set.");
        } catch (e) {
            console.error(e);
            toast.error("Setting Steam Auth failed.");
        }
    }

    const handleToggleClearDialog = () => {
        setClearDialogOpen(prevState => !prevState);
    }

    const handleClear = async () => {
        setLoadedAuth({
            username: "",
            password: "",
            steamGuardToken: ""
        });
        methods.reset({
            username: "",
            password: "",
            steamGuardToken: ""
        });
        setClearDialogOpen(false);
        await steamAuthApi.clearSteamAuth();
        toast.success("Steam Auth successfully cleared.");
    }

    const handleReopenWizard = () => {
        localStorage.removeItem('wizardCompleted');
        window.dispatchEvent(new Event('steam-auth-wizard:open'));
    };

    return (
        <>
            <ConfirmationDialog
                open={clearDialogOpen} title={`Clear Steam Auth credentials?`}
                description={"Clearing the credentials will make it impossible to update servers or workshop mods " +
                    "until it's set up again."}
                onConfirm={handleClear} onClose={handleToggleClearDialog} actionLabel="Confirm"
            />

            <Typography variant="h5" component="h3">Steam Auth</Typography>

            <Typography variant='body1'>
                Steam account with a copy of Arma 3 is needed for downloading workshop mods and keeping
                them up to date.
            </Typography>
            <Typography variant='body1' sx={{marginTop: 1}}>
                If you have Steam Guard 2FA enabled, please fill in the optional token field. You will receive
                this token in your email upon the first attempt to download any server or workshop item.
            </Typography>
            {loaded && <FormProvider {...methods}>
                <form onSubmit={methods.handleSubmit(handleSubmit)}>
                    <Grid container spacing={3} sx={{marginTop: 2}}>
                        <CustomTextField name='username' label='User name' required containerMd={12}/>
                        <CustomTextField name='password' label='Password' type='password' containerMd={12}
                                         helperText='By leaving the password empty, previously saved password will be used instead.'/>
                        <CustomTextField name='steamGuardToken' label='Steam Guard token' containerMd={12}/>

                        <Grid size={12}>
                            <Stack direction='column' spacing={2}>
                                <Button fullWidth variant="contained" type="submit">Submit</Button>
                                <Button fullWidth variant="outlined" color="error"
                                        onClick={handleToggleClearDialog}>Clear</Button>
                                <Button fullWidth variant="outlined"
                                        onClick={handleReopenWizard}
                                        data-testid="reopen-steam-auth-wizard">
                                    Re-run setup wizard
                                </Button>
                            </Stack>
                        </Grid>
                    </Grid>
                </form>
            </FormProvider>}
        </>
    );
};

export default SteamAuthForm;