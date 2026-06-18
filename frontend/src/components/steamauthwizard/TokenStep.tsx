import React from 'react';
import { Alert, Box, Button, CircularProgress, TextField, Typography } from '@mui/material';
import { AuthType, SteamLoginRequestDto, SteamLoginResult } from '../../api/generated';
import {steamAuthApi} from '../../api/client';

interface TokenStepProps {
    credentials: SteamLoginRequestDto;
    setCredentials: React.Dispatch<React.SetStateAction<SteamLoginRequestDto>>;
    authType: AuthType;
    onNext: () => void;
    onBack: () => void;
    loading: boolean;
    setLoading: React.Dispatch<React.SetStateAction<boolean>>;
    error: string | null;
    setError: React.Dispatch<React.SetStateAction<string | null>>;
}

const TokenStep: React.FC<TokenStepProps> = ({
    credentials,
    setCredentials,
    authType,
    onNext,
    onBack,
    loading,
    setLoading,
    error,
    setError,
}) => {
    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setCredentials(prev => ({ ...prev, steamGuardCode: e.target.value }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!credentials.steamGuardCode) {
            setError('Steam Guard code is required');
            return;
        }

        setLoading(true);
        setError(null);

        try {
            const { data } = await steamAuthApi.steamLogin({steamLoginRequestDto: credentials});

            if (data.result === SteamLoginResult.Success) {
                onNext();
            } else if (data.result === SteamLoginResult.InvalidCode) {
                setError('Invalid code. Please try again.');
            } else if (data.result === SteamLoginResult.RateLimited) {
                setError('Too many login attempts. Please try again later.');
            } else {
                setError(data.message ?? 'Verification failed. Please try again.');
            }
        } catch (err) {
            console.error(err);
            setError('Failed to verify code. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    const isEmail = authType === AuthType.Email;

    return (
        <Box component="form" onSubmit={handleSubmit}>
            <Typography variant="h5" gutterBottom>
                Enter Steam Guard Code
            </Typography>

            <Typography variant="body1" component="p">
                {isEmail
                    ? 'A Steam Guard code has been sent to the email address associated with your account. Please enter it below.'
                    : 'Enter the current code from your Steam mobile authenticator app.'}
            </Typography>

            {error && (
                <Alert severity="error" sx={{ mb: 2 }} data-testid="token-error">
                    {error}
                </Alert>
            )}

            <TextField
                fullWidth
                margin="normal"
                label="Steam Guard Code"
                name="steamGuardCode"
                value={credentials.steamGuardCode ?? ''}
                onChange={handleChange}
                disabled={loading}
                required
                autoFocus
                slotProps={{ htmlInput: { 'data-testid': 'token-input' } }}
            />

            <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 4 }}>
                <Button onClick={onBack} disabled={loading} data-testid="token-back">
                    Back
                </Button>
                <Button
                    type="submit"
                    variant="contained"
                    color="primary"
                    disabled={loading}
                    startIcon={loading ? <CircularProgress size={20} /> : null}
                    data-testid="token-submit"
                >
                    {loading ? 'Verifying…' : 'Continue'}
                </Button>
            </Box>
        </Box>
    );
};

export default TokenStep;
