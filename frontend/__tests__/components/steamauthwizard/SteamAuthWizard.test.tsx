import { screen, waitFor, act } from '@testing-library/react';
import '@testing-library/jest-dom';
import SteamAuthWizard from '../../../src/components/steamauthwizard/SteamAuthWizard.tsx';
import {steamAuthApi} from '../../../src/api/client';
import renderWithPermissions from '../../helpers/renderWithPermissions';

jest.mock('../../../src/api/client', () => ({
  steamAuthApi: {
    getSteamAuthStatus: jest.fn(),
    steamLogin: jest.fn(),
  }
}));

const mockedSteamAuthApi = jest.mocked(steamAuthApi);

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
        <button onClick={() => props.onCodeRequired('EMAIL')}>Continue</button>
        <button onClick={props.onSuccess}>Success</button>
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
    localStorage.clear();

    const localStorageMock = {
      getItem: jest.fn(),
      setItem: jest.fn(),
      clear: jest.fn()
    };
    Object.defineProperty(window, 'localStorage', { value: localStorageMock });
  });

  it('does not show the wizard if it has been completed before', async () => {
    (localStorage.getItem as jest.Mock).mockReturnValue('true');

    renderWithPermissions(<SteamAuthWizard />, ['STEAM_AUTH_ADMIN']);

    await waitFor(() => {
      expect(mockedSteamAuthApi.getSteamAuthStatus).not.toHaveBeenCalled();
    });

    expect(screen.queryByText('Steam Authentication Setup')).not.toBeInTheDocument();
  });

  it('shows the wizard if auth is not configured', async () => {
    (localStorage.getItem as jest.Mock).mockReturnValue(null);
    (mockedSteamAuthApi.getSteamAuthStatus as jest.Mock).mockResolvedValue({
      data: { isConfigured: false }
    });

    await act(async () => {
      renderWithPermissions(<SteamAuthWizard />, ['STEAM_AUTH_ADMIN']);
    });

    await waitFor(() => {
      expect(mockedSteamAuthApi.getSteamAuthStatus).toHaveBeenCalled();
    });

    expect(screen.getByText('Steam Authentication Setup')).toBeInTheDocument();
    expect(screen.getByTestId('welcome-step')).toBeInTheDocument();
  });

  it('does not show the wizard if auth is already configured', async () => {
    (localStorage.getItem as jest.Mock).mockReturnValue(null);
    (mockedSteamAuthApi.getSteamAuthStatus as jest.Mock).mockResolvedValue({
      data: { isConfigured: true }
    });

    await act(async () => {
      renderWithPermissions(<SteamAuthWizard />, ['STEAM_AUTH_ADMIN']);
    });

    await waitFor(() => {
      expect(mockedSteamAuthApi.getSteamAuthStatus).toHaveBeenCalled();
    });

    expect(screen.queryByText('Steam Authentication Setup')).not.toBeInTheDocument();
  });

  it('navigates through the wizard steps correctly (CODE_REQUIRED path)', async () => {
    (localStorage.getItem as jest.Mock).mockReturnValue(null);
    (mockedSteamAuthApi.getSteamAuthStatus as jest.Mock).mockResolvedValue({
      data: { isConfigured: false }
    });

    await act(async () => {
      renderWithPermissions(<SteamAuthWizard />, ['STEAM_AUTH_ADMIN']);
    });

    await waitFor(() => expect(screen.getByTestId('welcome-step')).toBeInTheDocument());

    await act(async () => { screen.getByText('Continue').click(); });
    expect(screen.getByTestId('credentials-step')).toBeInTheDocument();

    await act(async () => { screen.getByText('Continue').click(); }); // CODE_REQUIRED -> Token
    expect(screen.getByTestId('token-step')).toBeInTheDocument();

    await act(async () => { screen.getByText('Continue').click(); });
    expect(screen.getByTestId('completion-step')).toBeInTheDocument();
  });

  it('goes directly to completion when credentials succeed without 2FA', async () => {
    (localStorage.getItem as jest.Mock).mockReturnValue(null);
    (mockedSteamAuthApi.getSteamAuthStatus as jest.Mock).mockResolvedValue({
      data: { isConfigured: false }
    });

    await act(async () => {
      renderWithPermissions(<SteamAuthWizard />, ['STEAM_AUTH_ADMIN']);
    });

    await waitFor(() => expect(screen.getByTestId('welcome-step')).toBeInTheDocument());

    await act(async () => { screen.getByText('Continue').click(); }); // Welcome -> Credentials
    await act(async () => { screen.getByText('Success').click(); });  // success -> Completion

    expect(screen.getByTestId('completion-step')).toBeInTheDocument();
  });

  it('handles back navigation correctly', async () => {
    (localStorage.getItem as jest.Mock).mockReturnValue(null);
    (mockedSteamAuthApi.getSteamAuthStatus as jest.Mock).mockResolvedValue({
      data: { isConfigured: false }
    });

    await act(async () => {
      renderWithPermissions(<SteamAuthWizard />, ['STEAM_AUTH_ADMIN']);
    });

    await act(async () => { screen.getByText('Continue').click(); });
    expect(screen.getByTestId('credentials-step')).toBeInTheDocument();

    await act(async () => { screen.getByText('Back').click(); });
    expect(screen.getByTestId('welcome-step')).toBeInTheDocument();
  });

  it('completes the wizard when Finish is clicked', async () => {
    (localStorage.getItem as jest.Mock).mockReturnValue(null);
    (mockedSteamAuthApi.getSteamAuthStatus as jest.Mock).mockResolvedValue({
      data: { isConfigured: false }
    });

    await act(async () => {
      renderWithPermissions(<SteamAuthWizard />, ['STEAM_AUTH_ADMIN']);
    });

    await act(async () => { screen.getByText('Continue').click(); });
    await act(async () => { screen.getByText('Continue').click(); });
    await act(async () => { screen.getByText('Continue').click(); });
    await act(async () => { screen.getByText('Finish').click(); });

    expect(localStorage.setItem).toHaveBeenCalledWith('wizardCompleted', 'true');
  });

  it('skips the wizard when Skip is clicked', async () => {
    (localStorage.getItem as jest.Mock).mockReturnValue(null);
    (mockedSteamAuthApi.getSteamAuthStatus as jest.Mock).mockResolvedValue({
      data: { isConfigured: false }
    });

    await act(async () => {
      renderWithPermissions(<SteamAuthWizard />, ['STEAM_AUTH_ADMIN']);
    });

    await act(async () => { screen.getByText('Skip').click(); });

    expect(localStorage.setItem).toHaveBeenCalledWith('wizardCompleted', 'true');
  });

  it('does not call getSteamAuthStatus when user lacks STEAM_AUTH_ADMIN permission', async () => {
    (localStorage.getItem as jest.Mock).mockReturnValue(null);

    await act(async () => {
      renderWithPermissions(<SteamAuthWizard />, []);
    });

    expect(mockedSteamAuthApi.getSteamAuthStatus).not.toHaveBeenCalled();
    expect(screen.queryByText('Steam Authentication Setup')).not.toBeInTheDocument();
  });
});
