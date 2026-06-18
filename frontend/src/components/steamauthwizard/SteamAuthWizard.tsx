import React, { useEffect, useState } from 'react';
import {
    Box,
    Dialog,
    DialogContent,
    DialogTitle,
    IconButton,
    Step,
    StepLabel,
    Stepper
} from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import {steamAuthApi} from '../../api/client';
import { AuthType, SteamLoginRequestDto } from '../../api/generated';
import WelcomeStep from './WelcomeStep';
import CredentialsStep from './CredentialsStep';
import TokenStep from './TokenStep';
import CompletionStep from './CompletionStep';
import {usePermission} from '../../hooks/usePermission';

/**
 * Steam Authentication Wizard component
 * Shows a multi-step wizard for setting up Steam credentials on first application startup
 */
const SteamAuthWizard: React.FC = () => {
    const canManageSteamAuth = usePermission('STEAM_AUTH_ADMIN');

    const [open, setOpen] = useState(false);
    const [activeStep, setActiveStep] = useState(0);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [credentials, setCredentials] = useState<SteamLoginRequestDto>({
        username: '',
        password: '',
    });
    const [authType, setAuthType] = useState<AuthType>(AuthType.None);

    // Steps in the wizard
    const steps = ['Welcome', 'Steam Credentials', 'Steam Guard', 'Complete'];

    // Check if wizard should be shown
    useEffect(() => {
        if (!canManageSteamAuth) {
            return;
        }

        const checkAuthStatus = async () => {
            try {
                const wizardCompleted = localStorage.getItem('wizardCompleted') === 'true';
                if (wizardCompleted) {
                    return;
                }

                const { data } = await steamAuthApi.getSteamAuthStatus();
                if (!data.isConfigured) {
                    setOpen(true);
                }
            } catch (error) {
                console.error('Failed to check auth status:', error);
            }
        };

        void checkAuthStatus();
    }, [canManageSteamAuth]);

    // Allow re-opening from Settings via custom event
    useEffect(() => {
        const handleReopen = () => {
            setActiveStep(0);
            setError(null);
            setCredentials({ username: '', password: '' });
            setOpen(true);
        };
        window.addEventListener('steam-auth-wizard:open', handleReopen);
        return () => window.removeEventListener('steam-auth-wizard:open', handleReopen);
    }, []);

    const handleNext = () => setActiveStep((prev) => prev + 1);
    const handleBack = () => setActiveStep((prev) => prev - 1);

    const handleComplete = () => {
        localStorage.setItem('wizardCompleted', 'true');
        setOpen(false);
    };

    const handleGoToCompletion = () => setActiveStep(3);

    const handleCodeRequired = (type: AuthType) => {
        setAuthType(type);
        handleNext();
    };

    const handleSkip = () => {
        localStorage.setItem('wizardCompleted', 'true');
        setOpen(false);
    };

    const getStepContent = (step: number) => {
        switch (step) {
            case 0:
                return <WelcomeStep onNext={handleNext} onSkip={handleSkip} />;
            case 1:
                return (
                    <CredentialsStep
                        credentials={credentials}
                        setCredentials={setCredentials}
                        onSuccess={handleGoToCompletion}
                        onCodeRequired={handleCodeRequired}
                        onBack={handleBack}
                        loading={loading}
                        setLoading={setLoading}
                        error={error}
                        setError={setError}
                    />
                );
            case 2:
                return (
                    <TokenStep
                        credentials={credentials}
                        setCredentials={setCredentials}
                        authType={authType}
                        onNext={handleNext}
                        onBack={handleBack}
                        loading={loading}
                        setLoading={setLoading}
                        error={error}
                        setError={setError}
                    />
                );
            case 3:
                return <CompletionStep onComplete={handleComplete} />;
            default:
                return 'Unknown step';
        }
    };

    return (
        <Dialog open={open} maxWidth="md" fullWidth data-testid="steam-auth-wizard">
            <DialogTitle sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                Steam Authentication Setup
                <IconButton onClick={handleSkip} size="small" data-testid="steam-auth-wizard-close">
                    <CloseIcon />
                </IconButton>
            </DialogTitle>
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
