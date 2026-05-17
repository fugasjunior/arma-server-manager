import React from 'react';
import { Box, Button, CircularProgress, TextField, Typography, Alert } from '@mui/material';
import { SteamAuthDto } from '../../api/generated';
import {steamAuthApi} from '../../api/client';

interface TokenStepProps {
    credentials: SteamAuthDto;
    setCredentials: React.Dispatch<React.SetStateAction<SteamAuthDto>>;
    onNext: () => void;
    onBack: () => void;
    loading: boolean;
    setLoading: React.Dispatch<React.SetStateAction<boolean>>;
    error: string | null;
    setError: React.Dispatch<React.SetStateAction<string | null>>;
}

/**
 * Token step of the Steam Auth Wizard
 * Handles Steam Guard token input and verification
 */
const TokenStep: React.FC<TokenStepProps> = ({
    credentials,
    setCredentials,
    onNext,
    onBack,
    loading,
    setLoading,
    error,
    setError,
}) => {
    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setCredentials(prev => ({
            ...prev,
            steamGuardToken: e.target.value
        }));
    };
    
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        
        // Validate inputs
        if (!credentials.steamGuardToken) {
            setError('Steam Guard token is required');
            return;
        }
        
        setLoading(true);
        setError(null);
        
        try {
            // Verify credentials with token
            const { data } = await steamAuthApi.verifySteamAuth({steamAuthDto: credentials});
            
            if (data.status === 'SUCCESS') {
                // Save the credentials
                await steamAuthApi.setSteamAuth({steamAuthDto: credentials});
                onNext();
            } else {
                setError(data.message || 'Unknown error occurred.');
            }
        } catch (err) {
            console.error(err);
            setError('Failed to verify token. Please try again.');
        } finally {
            setLoading(false);
        }
    };
    
    return (
        <Box component="form" onSubmit={handleSubmit}>
            <Typography variant="h5" gutterBottom>
                Enter Steam Guard Token
            </Typography>

            <Typography variant="body1" paragraph>
                A Steam Guard code has been sent to the email address associated with your Steam account.
                Please check your email and enter the code below.
            </Typography>

            {error && (
                <Alert severity="error" sx={{ mb: 2 }} data-testid="token-error">
                    {error}
                </Alert>
            )}

            <TextField
                fullWidth
                margin="normal"
                label="Steam Guard Token"
                name="steamGuardToken"
                value={credentials.steamGuardToken}
                onChange={handleChange}
                disabled={loading}
                required
                autoFocus
                inputProps={{ 'data-testid': 'token-input' }}
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
                    {loading ? 'Verifying...' : 'Continue'}
                </Button>
            </Box>
        </Box>
    );
};

export default TokenStep;