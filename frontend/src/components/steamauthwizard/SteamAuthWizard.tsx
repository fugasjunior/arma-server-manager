import React, { useEffect, useState } from 'react';
import {
    Box,
    Dialog,
    DialogContent,
    DialogTitle,
    Step,
    StepLabel,
    Stepper
} from '@mui/material';
import {steamAuthApi} from '../../api/client';
import { SteamAuthDto } from '../../api/generated';
import WelcomeStep from './WelcomeStep';
import CredentialsStep from './CredentialsStep';
import TokenStep from './TokenStep';
import CompletionStep from './CompletionStep';

/**
 * Steam Authentication Wizard component
 * Shows a multi-step wizard for setting up Steam credentials on first application startup
 */
const SteamAuthWizard: React.FC = () => {
    const [open, setOpen] = useState(false);
    const [activeStep, setActiveStep] = useState(0);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [credentials, setCredentials] = useState<SteamAuthDto>({
        username: '',
        password: '',
        steamGuardToken: ''
    });
    const [authType, setAuthType] = useState<string | null>(null);

    // Steps in the wizard
    const steps = ['Welcome', 'Steam Credentials', 'Steam Guard', 'Complete'];

    // Check if wizard should be shown
    useEffect(() => {
        const checkAuthStatus = async () => {
            try {
                // Check if wizard has been completed before
                const wizardCompleted = localStorage.getItem('wizardCompleted') === 'true';
                if (wizardCompleted) {
                    return;
                }

                // Check if Steam auth is already configured
                const { data } = await steamAuthApi.getSteamAuthStatus();
                if (!data.isConfigured) {
                    setOpen(true);
                }
            } catch (error) {
                console.error('Failed to check auth status:', error);
            }
        };

        void checkAuthStatus();
    }, []);

    // Handle next step button click
    const handleNext = () => {
        setActiveStep((prevStep) => prevStep + 1);
    };

    // Handle back button click
    const handleBack = () => {
        setActiveStep((prevStep) => prevStep - 1);
    };

    // Handle wizard completion
    const handleComplete = () => {
        localStorage.setItem('wizardCompleted', 'true');
        setOpen(false);
    };

    // Handle wizard skip
    const handleSkip = () => {
        localStorage.setItem('wizardCompleted', 'true');
        setOpen(false);
    };

    // Render the current step content
    const getStepContent = (step: number) => {
        switch (step) {
            case 0:
                return <WelcomeStep onNext={handleNext} onSkip={handleSkip} />;
            case 1:
                return (
                    <CredentialsStep
                        credentials={credentials}
                        setCredentials={setCredentials}
                        onNext={handleNext}
                        onBack={handleBack}
                        loading={loading}
                        setLoading={setLoading}
                        error={error}
                        setError={setError}
                        setAuthType={setAuthType}
                    />
                );
            case 2:
                return (
                    <TokenStep
                        credentials={credentials}
                        setCredentials={setCredentials}
                        onNext={handleNext}
                        onBack={handleBack}
                        loading={loading}
                        setLoading={setLoading}
                        error={error}
                        setError={setError}
                        authType={authType}
                    />
                );
            case 3:
                return <CompletionStep onComplete={handleComplete} />;
            default:
                return 'Unknown step';
        }
    };

    return (
        <Dialog open={open} maxWidth="md" fullWidth>
            <DialogTitle>Steam Authentication Setup</DialogTitle>
            <DialogContent>
                <Box sx={{ width: '100%' }}>
                    <Stepper activeStep={activeStep} alternativeLabel>
                        {steps.map((label) => (
                            <Step key={label}>
                                <StepLabel>{label}</StepLabel>
                            </Step>
                        ))}
                    </Stepper>
                    <Box sx={{ mt: 4, mb: 2 }}>
                        {getStepContent(activeStep)}
                    </Box>
                </Box>
            </DialogContent>
        </Dialog>
    );
};

export default SteamAuthWizard;