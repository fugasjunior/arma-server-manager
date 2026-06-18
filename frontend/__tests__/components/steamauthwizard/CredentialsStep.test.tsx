import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import CredentialsStep from '../../../src/components/steamauthwizard/CredentialsStep.tsx';

jest.mock('../../../src/api/client', () => ({
  steamAuthApi: {
    steamLogin: jest.fn().mockResolvedValue({ data: { result: 'SUCCESS' } }),
  }
}));

describe('CredentialsStep', () => {
  const mockProps = {
    credentials: { username: '', password: '' },
    setCredentials: jest.fn(),
    onSuccess: jest.fn(),
    onCodeRequired: jest.fn(),
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
    mockProps.setCredentials.mockImplementation((updaterFn: (prev: object) => { username: string; password: string }) => {
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

    fireEvent.submit(screen.getByText('Continue'));

    expect(mockProps.setError).toHaveBeenCalledWith('Username and password are required');
    expect(mockProps.onSuccess).not.toHaveBeenCalled();
  });

  it('calls onBack when Back button is clicked', () => {
    render(<CredentialsStep {...mockProps} />);

    fireEvent.click(screen.getByText('Back'));

    expect(mockProps.onBack).toHaveBeenCalledTimes(1);
  });

  it('disables buttons when loading', () => {
    render(<CredentialsStep {...mockProps} loading={true} />);

    expect(screen.getByText('Logging in…')).toBeDisabled();
    expect(screen.getByText('Back')).toBeDisabled();
  });

  it('displays error message when error state is set', () => {
    render(<CredentialsStep {...mockProps} error="Test error message" />);

    expect(screen.getByText('Test error message')).toBeInTheDocument();
  });
});
