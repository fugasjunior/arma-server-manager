import React from 'react';
import { Box, Button, CircularProgress, TextField, Typography, Alert } from '@mui/material';
import { AuthType, SteamAuthDto } from '../../api/generated';
import {steamAuthApi} from '../../api/client';

interface CredentialsStepProps {
    credentials: SteamAuthDto;
    setCredentials: React.Dispatch<React.SetStateAction<SteamAuthDto>>;
    onNext: () => void;
    onSuccessNoTwoFactor: () => void;
    onBack: () => void;
    loading: boolean;
    setLoading: React.Dispatch<React.SetStateAction<boolean>>;
    error: string | null;
    setError: React.Dispatch<React.SetStateAction<string | null>>;
}

/**
 * Credentials step of the Steam Auth Wizard
 * Allows the user to input Steam username and password
 */
const CredentialsStep: React.FC<CredentialsStepProps> = ({
    credentials,
    setCredentials,
    onNext,
    onSuccessNoTwoFactor,
    onBack,
    loading,
    setLoading,
    error,
    setError,
}) => {
    // Handle input changes
    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setCredentials(prev => ({
            ...prev,
            [name]: value
        }));
    };

    // Handle form submission
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        
        // Validate inputs
        if (!credentials.username || !credentials.password) {
            setError('Username and password are required');
            return;
        }
        
        setLoading(true);
        setError(null);
        
        try {
            // Verify credentials
            const { data } = await steamAuthApi.verifySteamAuth({steamAuthDto: credentials});
            
            if (data.status === 'SUCCESS') {
                // No 2FA required, proceed to completion
                await steamAuthApi.setSteamAuth({steamAuthDto: credentials});
                onSuccessNoTwoFactor();
            } else if (data.status === 'REQUIRES_2FA') {
                if (data.authType === AuthType.Email) {
                    // Email 2FA required, proceed to token step
                    onNext();
                } else if (data.authType === AuthType.Mobile) {
                    // Mobile 2FA not supported
                    setError('Mobile authenticator is not supported. Please disable it or use a different account.');
                } else {
                    setError('Unknown 2FA type detected.');
                }
            } else if (data.status === 'INVALID_CREDENTIALS') {
                setError('Invalid username or password.');
            } else {
                setError('Verification failed: ' + data.message);
            }
        } catch (err) {
            console.error(err);
            setError('Failed to verify credentials. Please try again.');
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
                    {loading ? 'Verifying...' : 'Continue'}
                </Button>
            </Box>
        </Box>
    );
};

export default CredentialsStep;