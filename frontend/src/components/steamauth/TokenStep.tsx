import React, { useEffect } from 'react';
import { Box, Button, CircularProgress, TextField, Typography, Alert } from '@mui/material';
import { SteamAuthDto } from '../../dtos/SteamAuthDto';
import { verifyCredentials, generateSteamGuardToken, setAuth } from '../../services/configService';

interface TokenStepProps {
    credentials: SteamAuthDto;
    setCredentials: React.Dispatch<React.SetStateAction<SteamAuthDto>>;
    onNext: () => void;
    onBack: () => void;
    loading: boolean;
    setLoading: React.Dispatch<React.SetStateAction<boolean>>;
    error: string | null;
    setError: React.Dispatch<React.SetStateAction<string | null>>;
    authType: string | null;
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
    authType
}) => {
    // Generate token on component mount
    useEffect(() => {
        const generateToken = async () => {
            if (authType === 'EMAIL') {
                setLoading(true);
                setError(null);
                
                try {
                    const { data } = await generateSteamGuardToken({
                        username: credentials.username,
                        password: credentials.password,
                        steamGuardToken: ''
                    });
                    
                    if (!data.success) {
                        setError('Failed to generate Steam Guard token: ' + data.message);
                    }
                } catch (err) {
                    console.error(err);
                    setError('Failed to generate Steam Guard token. Please try again.');
                } finally {
                    setLoading(false);
                }
            }
        };
        
        generateToken();
    }, [authType, credentials.username, credentials.password, setError, setLoading]);
    
    // Handle input changes
    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setCredentials(prev => ({
            ...prev,
            steamGuardToken: e.target.value
        }));
    };
    
    // Handle form submission
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
            const { data } = await verifyCredentials(credentials);
            
            if (data.status === 'SUCCESS') {
                // Save the credentials
                await setAuth(credentials);
                onNext();
            } else {
                setError('Invalid Steam Guard token. Please try again.');
            }
        } catch (err) {
            console.error(err);
            setError('Failed to verify token. Please try again.');
        } finally {
            setLoading(false);
        }
    };
    
    // If not using email authentication, skip this step
    if (authType !== 'EMAIL') {
        return (
            <Box sx={{ textAlign: 'center', py: 4 }}>
                <CircularProgress />
                <Typography variant="body1" sx={{ mt: 2 }}>
                    Processing...
                </Typography>
            </Box>
        );
    }
    
    return (
        <Box component="form" onSubmit={handleSubmit}>
            <Typography variant="h5" gutterBottom>
                Enter Steam Guard Token
            </Typography>
            
            <Typography variant="body1" paragraph>
                A Steam Guard code has been sent to the email address associated with your Steam account.
                Please check your email and enter the code below.
            </Typography>
            
            <Typography variant="body1" paragraph>
                <strong>Note:</strong> The code is time-sensitive and will expire after a few minutes.
            </Typography>
            
            {error && (
                <Alert severity="error" sx={{ mb: 2 }}>
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
            />
            
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 4 }}>
                <Button onClick={onBack} disabled={loading}>
                    Back
                </Button>
                <Button
                    type="submit"
                    variant="contained"
                    color="primary"
                    disabled={loading}
                    startIcon={loading ? <CircularProgress size={20} /> : null}
                >
                    {loading ? 'Verifying...' : 'Continue'}
                </Button>
            </Box>
        </Box>
    );
};

export default TokenStep;