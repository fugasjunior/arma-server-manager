import React from 'react';
import { Box, Button, Typography } from '@mui/material';

interface WelcomeStepProps {
    onNext: () => void;
    onSkip: () => void;
}

/**
 * Welcome step of the Steam Auth Wizard
 * Explains the purpose of the wizard and why Steam credentials are needed
 */
const WelcomeStep: React.FC<WelcomeStepProps> = ({ onNext, onSkip }) => {
    return (
        <Box>
            <Typography variant="h5" gutterBottom>
                Welcome to the Steam Authentication Setup
            </Typography>
            
            <Typography variant="body1" component="p">
                This wizard will help you configure your Steam credentials for the Arma Server Manager.
            </Typography>

            <Typography variant="body1" component="p">
                <strong>Why do we need your Steam credentials?</strong>
            </Typography>

            <Typography variant="body1" component="p">
                The Arma Server Manager uses SteamCMD to download and update servers and workshop mods.
                SteamCMD requires valid Steam credentials to access these resources.
            </Typography>

            <Typography variant="body1" component="p">
                Your credentials will be stored securely and will only be used for downloading and updating
                game servers and workshop mods.
            </Typography>

            <Typography variant="body1" component="p">
                <strong>Note:</strong> We recommend creating a separate Steam account for this purpose rather than using your main gaming account.
            </Typography>
            
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 4 }}>
                <Button onClick={onSkip} color="secondary" data-testid="welcome-skip">
                    Skip Setup
                </Button>
                <Button onClick={onNext} variant="contained" color="primary" data-testid="welcome-continue">
                    Continue
                </Button>
            </Box>
        </Box>
    );
};

export default WelcomeStep;