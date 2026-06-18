import React from 'react';
import { Alert, Box, Button, CircularProgress, TextField, Typography } from '@mui/material';
import { AuthType, SteamLoginRequestDto, SteamLoginResult } from '../../api/generated';
import {steamAuthApi} from '../../api/client';

interface CredentialsStepProps {
    credentials: SteamLoginRequestDto;
    setCredentials: React.Dispatch<React.SetStateAction<SteamLoginRequestDto>>;
    onSuccess: () => void;
    onCodeRequired: (authType: AuthType) => void;
    onBack: () => void;
    loading: boolean;
    setLoading: React.Dispatch<React.SetStateAction<boolean>>;
    error: string | null;
    setError: React.Dispatch<React.SetStateAction<string | null>>;
}

const CredentialsStep: React.FC<CredentialsStepProps> = ({
    credentials,
    setCredentials,
    onSuccess,
    onCodeRequired,
    onBack,
    loading,
    setLoading,
    error,
    setError,
}) => {
    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setCredentials(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!credentials.username || !credentials.password) {
            setError('Username and password are required');
            return;
        }

        setLoading(true);
        setError(null);

        try {
            const { data } = await steamAuthApi.steamLogin({steamLoginRequestDto: credentials});

            if (data.result === SteamLoginResult.Success) {
                onSuccess();
            } else if (data.result === SteamLoginResult.CodeRequired) {
                onCodeRequired(data.authType ?? AuthType.Unknown);
            } else if (data.result === SteamLoginResult.InvalidCredentials) {
                setError('Invalid username or password.');
            } else if (data.result === SteamLoginResult.RateLimited) {
                setError('Too many login attempts. Please try again later.');
            } else {
                setError(data.message ?? 'Login failed. Please try again.');
            }
        } catch (err) {
            console.error(err);
            setError('Failed to connect. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Box component="form" onSubmit={handleSubmit}>
            <Typography variant="h5" gutterBottom>
                Enter Your Steam Credentials
            </Typography>

            <Typography variant="body1" component="p">
                Please enter your Steam username and password. These credentials will be used to download and update
                game servers and workshop mods.
            </Typography>

            {error && (
                <Alert severity="error" sx={{ mb: 2 }} data-testid="credentials-error">
                    {error}
                </Alert>
            )}

            {loading && (
                <Alert severity="info" sx={{ mb: 2 }}>
                    Logging in… If you use a mobile authenticator, confirm the login in your Steam app now or wait
                    to enter a code (up to ~1 min).
                </Alert>
            )}

            <TextField
                fullWidth
                margin="normal"
                label="Steam Username"
                name="username"
                value={credentials.username}
                onChange={handleChange}
                disabled={loading}
                required
                slotProps={{ htmlInput: { 'data-testid': 'credentials-username' } }}
            />

            <TextField
                fullWidth
                margin="normal"
                label="Steam Password"
                name="password"
                type="password"
                value={credentials.password}
                onChange={handleChange}
                disabled={loading}
                required
                slotProps={{ htmlInput: { 'data-testid': 'credentials-password' } }}
            />

            <TextField
                fullWidth
                margin="normal"
                label="Steam Guard Code (optional)"
                name="steamGuardCode"
                value={credentials.steamGuardCode ?? ''}
                onChange={handleChange}
                disabled={loading}
                helperText="Mobile authenticator? Enter the current code to skip a step. Email users: leave blank."
                slotProps={{ htmlInput: { 'data-testid': 'credentials-steam-guard-code' } }}
            />

            <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 4 }}>
                <Button onClick={onBack} disabled={loading} data-testid="credentials-back">
                    Back
                </Button>
                <Button
                    type="submit"
                    variant="contained"
                    color="primary"
                    disabled={loading}
                    startIcon={loading ? <CircularProgress size={20} /> : null}
                    data-testid="credentials-submit"
                >
                    {loading ? 'Logging in…' : 'Continue'}
                </Button>
            </Box>
        </Box>
    );
};

export default CredentialsStep;
