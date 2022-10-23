import {Button, Stack, TextField, Typography} from "@mui/material";
import React, {useEffect, useState} from "react";
import {useFormik} from "formik";
import Tooltip from "@mui/material/Tooltip";
import {getAuth, setAuth} from "../../services/configService";
import {toast} from "material-react-toastify";

const SteamAuthForm = () => {

    const [loadedAuth, setLoadedAuth] = useState({});
    const [loaded, setLoaded] = useState(false);

    useEffect(() => {
        fetchAuth()
    }, []);

    const fetchAuth = async () => {
        const {data: auth} = await getAuth();
        setLoadedAuth(auth);
        setLoaded(true);
    }

    const handleSubmit = async (values) => {
        try {
            await setAuth(values);
            toast.success("Steam loadedAuth successfully set");
        } catch (e) {
            console.error(e);
            toast.error("Setting steam loadedAuth failed");
        }
    }

    const formik = useFormik({
        initialValues: loadedAuth,
        onSubmit: handleSubmit,
        enableReinitialize: true
    });

    return (
            <>
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
                                    value={formik.values.username}
                                    onChange={formik.handleChange}
                                    error={formik.touched.username && Boolean(formik.errors.username)}
                                    helperText={formik.touched.username && formik.errors.username}
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
                                        value={formik.values.password}
                                        onChange={formik.handleChange}
                                        error={formik.touched.password && Boolean(formik.errors.password)}
                                        helperText={formik.touched.password && formik.errors.password}
                                /></Tooltip>
                            <TextField
                                    fullWidth
                                    id="steamGuardToken"
                                    name="steamGuardToken"
                                    label="Steam Guard token"
                                    value={formik.values.steamGuardToken}
                                    onChange={formik.handleChange}
                                    error={formik.touched.steamGuardToken && Boolean(formik.errors.steamGuardToken)}
                                    helperText={formik.touched.steamGuardToken && formik.errors.steamGuardToken}
                            />
                            <Button variant="contained" type="submit">Submit</Button>
                    </Stack>
                </form>}
            </>
    );
};

export default SteamAuthForm;