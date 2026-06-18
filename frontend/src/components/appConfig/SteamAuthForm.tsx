import {Alert, Button, CircularProgress, Grid, Stack, TextField, Typography} from "@mui/material";
import {useEffect, useState} from "react";
import {useForm, FormProvider} from "react-hook-form";
import {steamAuthApi} from "../../api/client";
import {toast} from "react-toastify";
import ConfirmationDialog from "../../UI/ConfirmationDialog";
import {AuthType, SteamLoginRequestDto, SteamLoginResult} from "../../api/generated";
import {CustomTextField} from "../../UI/Form/CustomTextField.tsx";

interface FormValues {
    username: string;
    password: string;
}

const SteamAuthForm = () => {
    const [loaded, setLoaded] = useState(false);
    const [clearDialogOpen, setClearDialogOpen] = useState(false);
    const [sessionStatus, setSessionStatus] = useState<string | null>(null);

    const [loginLoading, setLoginLoading] = useState(false);
    const [loginError, setLoginError] = useState<string | null>(null);
    const [pendingCreds, setPendingCreds] = useState<SteamLoginRequestDto | null>(null);
    const [steamGuardCode, setSteamGuardCode] = useState('');
    const [authType, setAuthType] = useState<AuthType | null>(null);

    const methods = useForm<FormValues>({defaultValues: {username: '', password: ''}});

    useEffect(() => {
        const fetchAuth = async () => {
            try {
                const {data: auth} = await steamAuthApi.getSteamAuth();
                methods.reset({username: auth.username ?? '', password: ''});
            } catch (e) {
                console.error(e);
                toast.error("Could not fetch Steam Auth.");
            }
            setLoaded(true);
        };
        const fetchStatus = async () => {
            try {
                const {data} = await steamAuthApi.getSteamAuthStatus();
                setSessionStatus(data.sessionStatus ?? null);
            } catch (e) {
                console.error(e);
            }
        };
        void fetchAuth();
        void fetchStatus();
    }, []);

    const runLogin = async (req: SteamLoginRequestDto) => {
        setLoginLoading(true);
        setLoginError(null);
        try {
            const {data} = await steamAuthApi.steamLogin({steamLoginRequestDto: req});
            if (data.result === SteamLoginResult.Success) {
                methods.reset({username: req.username, password: ''});
                setSessionStatus('ACTIVE');
                setPendingCreds(null);
                setSteamGuardCode('');
                setAuthType(null);
                toast.success("Steam Auth successfully set.");
            } else if (data.result === SteamLoginResult.CodeRequired) {
                setPendingCreds(req);
                setAuthType(data.authType ?? AuthType.Unknown);
                setLoginError(
                    data.authType === AuthType.Email
                        ? "Check your email for a Steam Guard code and enter it below."
                        : "Confirm the login in your Steam mobile app, or enter the authenticator code below."
                );
            } else if (data.result === SteamLoginResult.InvalidCredentials) {
                setLoginError("Invalid username or password.");
            } else if (data.result === SteamLoginResult.InvalidCode) {
                setLoginError("Invalid Steam Guard code. Please try again.");
            } else if (data.result === SteamLoginResult.RateLimited) {
                setLoginError("Too many login attempts. Please try again later.");
            } else {
                setLoginError(data.message ?? "Login failed. Please try again.");
            }
        } catch (e) {
            console.error(e);
            setLoginError("Login failed. Please try again.");
        } finally {
            setLoginLoading(false);
        }
    };

    const handleSubmit = async (values: FormValues) => {
        await runLogin({username: values.username, password: values.password});
    };

    const handleVerifyCode = async () => {
        if (!pendingCreds) return;
        await runLogin({...pendingCreds, steamGuardCode: steamGuardCode.trim()});
    };

    const handleToggleClearDialog = () => setClearDialogOpen(prev => !prev);

    const handleClear = async () => {
        methods.reset({username: '', password: ''});
        setSessionStatus(null);
        setPendingCreds(null);
        setSteamGuardCode('');
        setAuthType(null);
        setLoginError(null);
        setClearDialogOpen(false);
        await steamAuthApi.clearSteamAuth();
        toast.success("Steam Auth successfully cleared.");
    };

    const handleReopenWizard = () => {
        localStorage.removeItem('wizardCompleted');
        window.dispatchEvent(new Event('steam-auth-wizard:open'));
    };

    const sessionBadgeMap: Record<string, {label: string; color: string}> = {
        ACTIVE:         {label: "Session: Active",       color: "success.main"},
        EXPIRED:        {label: "Session: Expired",      color: "error.main"},
        NOT_CONFIGURED: {label: "Not configured",        color: "text.secondary"},
        UNKNOWN:        {label: "Session: Unknown",      color: "text.secondary"},
    };
    const sessionBadge = sessionStatus ? sessionBadgeMap[sessionStatus] : null;

    return (
        <>
            <ConfirmationDialog
                open={clearDialogOpen} title="Clear Steam Auth credentials?"
                description={"Clearing the credentials will make it impossible to update servers or workshop mods until it's set up again."}
                onConfirm={handleClear} onClose={handleToggleClearDialog} actionLabel="Confirm"
            />

            <Stack direction="row" spacing={2} sx={{mb: 1, alignItems: "center"}}>
                <Typography variant="h5" component="h3">Steam Auth</Typography>
                {sessionBadge && (
                    <Typography variant="body2" sx={{color: sessionBadge.color, fontWeight: 600}}>
                        {sessionBadge.label}
                    </Typography>
                )}
            </Stack>

            <Typography variant='body1'>
                Steam account with a copy of Arma 3 is needed for downloading workshop mods and keeping
                them up to date.
            </Typography>

            {loaded && <FormProvider {...methods}>
                <form onSubmit={methods.handleSubmit(handleSubmit)}>
                    <Grid container spacing={3} sx={{marginTop: 2}}>
                        <CustomTextField name='username' label='User name' required containerMd={12}/>
                        <CustomTextField name='password' label='Password' type='password' containerMd={12}/>

                        {loginError && (
                            <Grid size={12}>
                                <Alert severity={pendingCreds ? "info" : "error"}>{loginError}</Alert>
                            </Grid>
                        )}

                        {pendingCreds && (
                            <Grid size={12}>
                                <Stack direction="row" spacing={2} sx={{alignItems: "center"}}>
                                    <TextField
                                        label={authType === AuthType.Email ? "Email Steam Guard code" : "Authenticator code"}
                                        value={steamGuardCode}
                                        onChange={e => setSteamGuardCode(e.target.value)}
                                        size="small"
                                        fullWidth
                                    />
                                    <Button
                                        variant="contained"
                                        onClick={handleVerifyCode}
                                        disabled={loginLoading || !steamGuardCode.trim()}
                                    >
                                        {loginLoading ? <CircularProgress size={20}/> : "Verify"}
                                    </Button>
                                </Stack>
                            </Grid>
                        )}

                        <Grid size={12}>
                            <Stack direction='column' spacing={2}>
                                <Button
                                    fullWidth variant="contained" type="submit"
                                    disabled={loginLoading}
                                    startIcon={loginLoading && !pendingCreds ? <CircularProgress size={20}/> : null}
                                >
                                    {loginLoading && !pendingCreds ? "Logging in…" : "Submit"}
                                </Button>
                                <Button fullWidth variant="outlined" color="error" onClick={handleToggleClearDialog}>
                                    Clear
                                </Button>
                                <Button fullWidth variant="outlined" onClick={handleReopenWizard}
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
