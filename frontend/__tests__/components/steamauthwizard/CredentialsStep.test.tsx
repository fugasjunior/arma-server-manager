import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import CredentialsStep from '../../../src/components/steamauthwizard/CredentialsStep.tsx';
import TokenStep from "../../../src/components/steamauthwizard/TokenStep.tsx";

jest.mock('../../../src/api/client', () => ({
  steamAuthApi: {
    verifySteamAuth: jest.fn().mockResolvedValue({ data: { status: 'SUCCESS' } }),
    setSteamAuth: jest.fn().mockResolvedValue({})
  }
}));

describe('CredentialsStep', () => {
  const mockProps = {
    credentials: { username: '', password: '', steamGuardToken: '' },
    setCredentials: jest.fn(),
    onNext: jest.fn(),
    onSuccessNoTwoFactor: jest.fn(),
    onBack: jest.fn(),
    loading: false,
    setLoading: jest.fn(),
    error: null,
    setError: jest.fn()
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders the credentials form', () => {
    render(<CredentialsStep {...mockProps} />);
    
    expect(screen.getByText('Enter Your Steam Credentials')).toBeInTheDocument();
    expect(screen.getByText('Steam Username')).toBeInTheDocument();
    expect(screen.getByText('Steam Password')).toBeInTheDocument();
    expect(screen.getByText('Continue')).toBeInTheDocument();
    expect(screen.getByText('Back')).toBeInTheDocument();
  });

  it('updates credentials when input values change', () => {
    let capturedUsername: string | null = null;
    let capturedPassword: string | null = null;
    mockProps.setCredentials.mockImplementation((updaterFn) => {
      const result = updaterFn({});
      capturedUsername = result.username;
      capturedPassword = result.password;
    });

    render(<CredentialsStep {...mockProps} />);

    const usernameInput = screen.getByRole('textbox', { name: /steam username/i });
    const passwordInput = screen.getByLabelText(/steam password/i, { selector: 'input' });

    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    expect(capturedUsername).toBe('testuser');

    fireEvent.change(passwordInput, { target: { value: 'testpassword' } });
    expect(capturedPassword).toBe('testpassword');
  });

  it('shows error when submitting with empty fields', () => {
    render(<CredentialsStep {...mockProps} />);
    
    const continueButton = screen.getByText('Continue');
    fireEvent.submit(continueButton);
    
    expect(mockProps.setError).toHaveBeenCalledWith('Username and password are required');
    expect(mockProps.onNext).not.toHaveBeenCalled();
  });

  it('calls onBack when Back button is clicked', () => {
    render(<CredentialsStep {...mockProps} />);
    
    const backButton = screen.getByText('Back');
    fireEvent.click(backButton);
    
    expect(mockProps.onBack).toHaveBeenCalledTimes(1);
  });

  it('disables buttons when loading', () => {
    render(<CredentialsStep {...mockProps} loading={true} />);
    
    const continueButton = screen.getByText('Verifying...');
    const backButton = screen.getByText('Back');
    
    expect(continueButton).toBeDisabled();
    expect(backButton).toBeDisabled();
  });

  it('displays error message when error state is set', () => {
    render(<CredentialsStep {...mockProps} error="Test error message" />);
    
    expect(screen.getByText('Test error message')).toBeInTheDocument();
  });
});