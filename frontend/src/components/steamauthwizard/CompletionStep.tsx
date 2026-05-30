import React from 'react';
import { Box, Button, Typography } from '@mui/material';
import { CheckCircleOutlined } from '@mui/icons-material';

interface CompletionStepProps {
    onComplete: () => void;
}

/**
 * Completion step of the Steam Auth Wizard
 * Shows success message and completes the wizard
 */
const CompletionStep: React.FC<CompletionStepProps> = ({ onComplete }) => {
    return (
        <Box sx={{ textAlign: 'center' }}>
            <CheckCircleOutlined sx={{ fontSize: 60, color: 'success.main', mb: 2 }} />
            
            <Typography variant="h5" gutterBottom>
                Setup Complete!
            </Typography>
            
            <Typography variant="body1" component="p">
                Your Steam credentials have been successfully configured.
            </Typography>

            <Typography variant="body1" component="p">
                You can now download and update game servers and workshop mods using SteamCMD.
            </Typography>

            <Typography variant="body1" component="p">
                If you ever need to change your Steam credentials, you can do so in the Settings page.
            </Typography>
            
            <Box sx={{ mt: 4 }}>
                <Button
                    variant="contained"
                    color="primary"
                    onClick={onComplete}
                    data-testid="completion-finish"
                >
                    Finish
                </Button>
            </Box>
        </Box>
    );
};

export default CompletionStep;