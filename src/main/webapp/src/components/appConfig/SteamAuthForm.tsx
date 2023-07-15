import {Button, Grid, Stack, Typography} from "@mui/material";
import {useEffect, useState} from "react";
import {useFormik} from "formik";
import {clearAuth, getAuth, setAuth} from "../../services/configService";
import {toast} from "material-react-toastify";
import ConfirmationDialog from "../../UI/ConfirmationDialog";
import {SteamAuthDto} from "../../dtos/SteamAuthDto.ts";
import {CustomTextField} from "../../UI/Form/CustomTextField.tsx";

const SteamAuthForm = () => {
    const [loadedAuth, setLoadedAuth] = useState<SteamAuthDto>({
        username: '',
        password: '',
        steamGuardToken: ''
    });
    const [clearDialogOpen, setClearDialogOpen] = useState(false);
    const [loaded, setLoaded] = useState(false);

    useEffect(() => {
        const fetchAuth = async () => {
            try {
                const {data: auth} = await getAuth();
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

    const handleSubmit = async (values: SteamAuthDto) => {
        try {
            await setAuth({
                username: values.username,
                password: values.password,
                steamGuardToken: values.steamGuardToken.trim()
            });
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

    const formik = useFormik({
        initialValues: loadedAuth,
        onSubmit: handleSubmit,
        enableReinitialize: true
    });

    const handleClear = async () => {
        setLoadedAuth({
            username: "",
            password: "",
            steamGuardToken: ""
        });
        formik.resetForm();
        setClearDialogOpen(false);
        await clearAuth();
        toast.success("Steam Auth successfully cleared.");
    }

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
            <Typography variant='body1' marginTop={1}>
                If you have Steam Guard 2FA enabled, please fill in the optional token field. You will receive
                this token in your email upon the first attempt to download any server or workshop item.
            </Typography>
            {loaded && <form onSubmit={formik.handleSubmit}>
                <Grid container spacing={3} marginTop={2}>
                    <CustomTextField id='username' label='User name' required containerMd={12} formik={formik}/>
                    <CustomTextField id='password' label='Password' type='password' containerMd={12}
                                     helperText='By leaving the password empty, previously saved password will be used instead.'
                                     formik={formik}/>
                    <CustomTextField id='steamGuardToken' label='Steam Guard token' containerMd={12} formik={formik}/>

                    <Grid item xs={12}>
                        <Stack direction='column' spacing={2}>
                            <Button fullWidth variant="contained" type="submit">Submit</Button>
                            <Button fullWidth variant="outlined" color="error"
                                    onClick={handleToggleClearDialog}>Clear</Button>
                        </Stack>
                    </Grid>
                </Grid>
            </form>}
        </>
    );
};

export default SteamAuthForm;