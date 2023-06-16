import {Button, Stack, TextField, Typography} from "@mui/material";
import {useEffect, useState} from "react";
import {useFormik} from "formik";
import Tooltip from "@mui/material/Tooltip";
import {clearAuth, getAuth, setAuth} from "../../services/configService";
import {toast} from "material-react-toastify";
import ConfirmationDialog from "../../UI/ConfirmationDialog";

const SteamAuthForm = () => {

    const [loadedAuth, setLoadedAuth] = useState({
        username: "",
        password: "",
        steamGuardToken: ""
    });
    const [clearDialogOpen, setClearDialogOpen] = useState(false);
    const [loaded, setLoaded] = useState(false);

    useEffect(() => {
        fetchAuth()
    }, []);

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
            toast.error("Could not fetch Steam Auth");
        }
        setLoaded(true);
    }

    const handleSubmit = async (values) => {
        try {
            await setAuth({
                username: values.username,
                password: values.password,
                steamGuardToken: values.steamGuardToken.trim()
            });
            setLoadedAuth(prevState => {
                return {...prevState, password: ""};
            });
            formik.resetForm();
            toast.success("Steam Auth successfully set");
        } catch (e) {
            console.error(e);
            toast.error("Setting Steam Auth failed");
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
        toast.success("Steam Auth successfully cleared");
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

            <p>Steam account with a copy of Arma 3 is needed for downloading workshop mods and keeping
                them up to date.
            </p>
            <p>If you have Steam Guard 2FA enabled, please fill in the optional token field. You will receive
                this token in your email upon the first attempt to download any workshop item</p>
            {loaded && <form onSubmit={formik.handleSubmit}>

                <Stack spacing={2} mt={2}>
                    <TextField
                        fullWidth
                        required
                        id="username"
                        name="username"
                        label="Username"
                        value={formik.values.username || ''}
                        onChange={formik.handleChange}
                        error={formik.touched.username && Boolean(formik.errors.username)}

                    />
                    <Tooltip
                        title="By leaving the password empty, previously saved password will be used instead"
                        placement="bottom-start"
                    >
                        <TextField
                            fullWidth
                            id="password"
                            name="password"
                            type="password"
                            label="Password"
                            value={formik.values.password || ''}
                            onChange={formik.handleChange}
                            error={formik.touched.password && Boolean(formik.errors.password)}
                        />
                    </Tooltip>
                    <TextField
                        fullWidth
                        id="steamGuardToken"
                        name="steamGuardToken"
                        label="Steam Guard token"
                        value={formik.values.steamGuardToken || ''}
                        onChange={formik.handleChange}
                        error={formik.touched.steamGuardToken && Boolean(formik.errors.steamGuardToken)}

                    />
                    <Button variant="contained" type="submit">Submit</Button>
                    <Button variant="outlined" color="error" onClick={handleToggleClearDialog}>Clear</Button>
                </Stack>
            </form>}
        </>
    );
};

export default SteamAuthForm;