import { screen, waitFor, act } from '@testing-library/react';
import '@testing-library/jest-dom';
import SteamAuthWizard from '../../../src/components/steamauthwizard/SteamAuthWizard.tsx';
import {steamAuthApi} from '../../../src/api/client';
import renderWithPermissions from '../../helpers/renderWithPermissions';

jest.mock('../../../src/api/client', () => ({
  steamAuthApi: {
    getSteamAuthStatus: jest.fn(),
    verifySteamAuth: jest.fn(),
    setSteamAuth: jest.fn()
  }
}));

const mockedSteamAuthApi = jest.mocked(steamAuthApi);

// Mock the child components
jest.mock('../../../src/components/steamauthwizard/WelcomeStep.tsx', () => {
  return function MockWelcomeStep(props: any) {
    return (
      <div data-testid="welcome-step">
        <button onClick={props.onNext}>Continue</button>
        <button onClick={props.onSkip}>Skip</button>
      </div>
    );
  };
});

jest.mock('../../../src/components/steamauthwizard/CredentialsStep.tsx', () => {
  return function MockCredentialsStep(props: any) {
    return (
      <div data-testid="credentials-step">
        <button onClick={props.onNext}>Continue</button>
        <button onClick={props.onBack}>Back</button>
      </div>
    );
  };
});

jest.mock('../../../src/components/steamauthwizard/TokenStep.tsx', () => {
  return function MockTokenStep(props: any) {
    return (
      <div data-testid="token-step">
        <button onClick={props.onNext}>Continue</button>
        <button onClick={props.onBack}>Back</button>
      </div>
    );
  };
});

jest.mock('../../../src/components/steamauthwizard/CompletionStep.tsx', () => {
  return function MockCompletionStep(props: any) {
    return (
      <div data-testid="completion-step">
        <button onClick={props.onComplete}>Finish</button>
      </div>
    );
  };
});

describe('SteamAuthWizard', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    // Clear localStorage
    localStorage.clear();
    
    // Mock localStorage methods
    const localStorageMock = {
      getItem: jest.fn(),
      setItem: jest.fn(),
      clear: jest.fn()
    };
    Object.defineProperty(window, 'localStorage', { value: localStorageMock });
  });

  it('does not show the wizard if it has been completed before', async () => {
    // Mock localStorage to indicate wizard was completed
    (localStorage.getItem as jest.Mock).mockReturnValue('true');
    
    renderWithPermissions(<SteamAuthWizard />, ['STEAM_AUTH_ADMIN']);
    
    // Wait for useEffect to complete
    await waitFor(() => {
      expect(mockedSteamAuthApi.getSteamAuthStatus).not.toHaveBeenCalled();
    });
    
    // Dialog should not be visible
    expect(screen.queryByText('Steam Authentication Setup')).not.toBeInTheDocument();
  });

  it('shows the wizard if auth is not configured', async () => {
    // Mock localStorage to indicate wizard was not completed
    (localStorage.getItem as jest.Mock).mockReturnValue(null);
    
    // Mock API response
    (mockedSteamAuthApi.getSteamAuthStatus as jest.Mock).mockResolvedValue({
      data: { isConfigured: false }
    });
    
    await act(async () => {
      renderWithPermissions(<SteamAuthWizard />, ['STEAM_AUTH_ADMIN']);
    });
    
    // Wait for useEffect to complete
    await waitFor(() => {
      expect(mockedSteamAuthApi.getSteamAuthStatus).toHaveBeenCalled();
    });
    
    // Dialog should be visible
    expect(screen.getByText('Steam Authentication Setup')).toBeInTheDocument();
    
    // First step (Welcome) should be visible
    expect(screen.getByTestId('welcome-step')).toBeInTheDocument();
  });

  it('does not show the wizard if auth is already configured', async () => {
    // Mock localStorage to indicate wizard was not completed
    (localStorage.getItem as jest.Mock).mockReturnValue(null);
    
    // Mock API response
    (mockedSteamAuthApi.getSteamAuthStatus as jest.Mock).mockResolvedValue({
      data: { isConfigured: true }
    });
    
    await act(async () => {
      renderWithPermissions(<SteamAuthWizard />, ['STEAM_AUTH_ADMIN']);
    });
    
    // Wait for useEffect to complete
    await waitFor(() => {
      expect(mockedSteamAuthApi.getSteamAuthStatus).toHaveBeenCalled();
    });
    
    // Dialog should not be visible
    expect(screen.queryByText('Steam Authentication Setup')).not.toBeInTheDocument();
  });

  it('navigates through the wizard steps correctly', async () => {
    // Mock localStorage to indicate wizard was not completed
    (localStorage.getItem as jest.Mock).mockReturnValue(null);
    
    // Mock API response
    (mockedSteamAuthApi.getSteamAuthStatus as jest.Mock).mockResolvedValue({
      data: { isConfigured: false }
    });
    
    await act(async () => {
      renderWithPermissions(<SteamAuthWizard />, ['STEAM_AUTH_ADMIN']);
    });
    
    // Wait for useEffect to complete
    await waitFor(() => {
      expect(mockedSteamAuthApi.getSteamAuthStatus).toHaveBeenCalled();
    });
    
    // First step (Welcome) should be visible
    expect(screen.getByTestId('welcome-step')).toBeInTheDocument();
    
    // Navigate to Credentials step
    await act(async () => {
      screen.getByText('Continue').click();
    });
    
    // Second step (Credentials) should be visible
    expect(screen.getByTestId('credentials-step')).toBeInTheDocument();
    
    // Navigate to Token step
    await act(async () => {
      screen.getByText('Continue').click();
    });
    
    // Third step (Token) should be visible
    expect(screen.getByTestId('token-step')).toBeInTheDocument();
    
    // Navigate to Completion step
    await act(async () => {
      screen.getByText('Continue').click();
    });
    
    // Fourth step (Completion) should be visible
    expect(screen.getByTestId('completion-step')).toBeInTheDocument();
  });

  it('handles back navigation correctly', async () => {
    // Mock localStorage to indicate wizard was not completed
    (localStorage.getItem as jest.Mock).mockReturnValue(null);
    
    // Mock API response
    (mockedSteamAuthApi.getSteamAuthStatus as jest.Mock).mockResolvedValue({
      data: { isConfigured: false }
    });
    
    await act(async () => {
      renderWithPermissions(<SteamAuthWizard />, ['STEAM_AUTH_ADMIN']);
    });
    
    // Navigate to Credentials step
    await act(async () => {
      screen.getByText('Continue').click();
    });
    
    // Second step (Credentials) should be visible
    expect(screen.getByTestId('credentials-step')).toBeInTheDocument();
    
    // Navigate back to Welcome step
    await act(async () => {
      screen.getByText('Back').click();
    });
    
    // First step (Welcome) should be visible again
    expect(screen.getByTestId('welcome-step')).toBeInTheDocument();
  });

  it('completes the wizard when Finish is clicked', async () => {
    // Mock localStorage to indicate wizard was not completed
    (localStorage.getItem as jest.Mock).mockReturnValue(null);
    
    // Mock API response
    (mockedSteamAuthApi.getSteamAuthStatus as jest.Mock).mockResolvedValue({
      data: { isConfigured: false }
    });
    
    await act(async () => {
      renderWithPermissions(<SteamAuthWizard />, ['STEAM_AUTH_ADMIN']);
    });
    
    // Navigate through all steps
    await act(async () => {
      screen.getByText('Continue').click(); // Welcome -> Credentials
    });
    
    await act(async () => {
      screen.getByText('Continue').click(); // Credentials -> Token
    });
    
    await act(async () => {
      screen.getByText('Continue').click(); // Token -> Completion
    });
    
    // Complete the wizard
    await act(async () => {
      screen.getByText('Finish').click();
    });
    
    // Check that localStorage was updated
    expect(localStorage.setItem).toHaveBeenCalledWith('wizardCompleted', 'true');
  });

  it('skips the wizard when Skip is clicked', async () => {
    // Mock localStorage to indicate wizard was not completed
    (localStorage.getItem as jest.Mock).mockReturnValue(null);

    // Mock API response
    (mockedSteamAuthApi.getSteamAuthStatus as jest.Mock).mockResolvedValue({
      data: { isConfigured: false }
    });

    await act(async () => {
      renderWithPermissions(<SteamAuthWizard />, ['STEAM_AUTH_ADMIN']);
    });

    // Skip the wizard
    await act(async () => {
      screen.getByText('Skip').click();
    });

    // Check that localStorage was updated
    expect(localStorage.setItem).toHaveBeenCalledWith('wizardCompleted', 'true');
  });

  it('does not call getSteamAuthStatus when user lacks STEAM_AUTH_ADMIN permission', async () => {
    (localStorage.getItem as jest.Mock).mockReturnValue(null);
    (mockedSteamAuthApi.getSteamAuthStatus as jest.Mock).mockResolvedValue({
      data: { isConfigured: false }
    });

    await act(async () => {
      renderWithPermissions(<SteamAuthWizard />, []);
    });

    expect(mockedSteamAuthApi.getSteamAuthStatus).not.toHaveBeenCalled();
    expect(screen.queryByText('Steam Authentication Setup')).not.toBeInTheDocument();
  });
});