import React from 'react';
import {fireEvent, render, screen} from '@testing-library/react';
import '@testing-library/jest-dom';
import TokenStep from '../../../src/components/steamauthwizard/TokenStep.tsx';
jest.mock('../../../src/api/client', () => ({
  steamAuthApi: {
    verifySteamAuth: jest.fn().mockResolvedValue({ data: { status: 'SUCCESS' } }),
    setSteamAuth: jest.fn().mockResolvedValue({})
  }
}));

describe('TokenStep', () => {
  const mockProps = {
    credentials: { username: 'testuser', password: 'testpass', steamGuardToken: '' },
    setCredentials: jest.fn(),
    onNext: jest.fn(),
    onBack: jest.fn(),
    loading: false,
    setLoading: jest.fn(),
    error: null,
    setError: jest.fn()
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders the token input form when authType is EMAIL', () => {
    render(<TokenStep {...mockProps} />);
    
    expect(screen.getByText('Enter Steam Guard Token')).toBeInTheDocument();
    expect(screen.getByText('Steam Guard Token')).toBeInTheDocument();
    expect(screen.getByText('Continue')).toBeInTheDocument();
    expect(screen.getByText('Back')).toBeInTheDocument();
  });

  it('updates token when input value changes', () => {
    let capturedValue: string | null = null;
    mockProps.setCredentials.mockImplementation((updaterFn) => {
      const testState = {
        username: 'testuser',
        password: 'testpass',
        steamGuardToken: ''
      };
      const result = updaterFn(testState);
      capturedValue = result.steamGuardToken;
    });

    render(<TokenStep {...mockProps} />);

    const tokenInput = screen.getByRole('textbox', { name: /steam guard token/i });
    fireEvent.change(tokenInput, { target: { value: '123456' } });

    expect(capturedValue).toBe('123456');
  });

  it('shows error when submitting with empty token', () => {
    render(<TokenStep {...mockProps} />);
    
    const continueButton = screen.getByText('Continue');
    fireEvent.submit(continueButton);
    
    expect(mockProps.setError).toHaveBeenCalledWith('Steam Guard token is required');
    expect(mockProps.onNext).not.toHaveBeenCalled();
  });

  it('calls onBack when Back button is clicked', () => {
    render(<TokenStep {...mockProps} />);
    
    const backButton = screen.getByText('Back');
    fireEvent.click(backButton);
    
    expect(mockProps.onBack).toHaveBeenCalledTimes(1);
  });

  it('disables buttons when loading', () => {
    render(<TokenStep {...mockProps} loading={true} />);
    
    const continueButton = screen.getByText('Verifying...');
    const backButton = screen.getByText('Back');
    
    expect(continueButton).toBeDisabled();
    expect(backButton).toBeDisabled();
  });

  it('displays error message when error state is set', () => {
    render(<TokenStep {...mockProps} error="Test error message" />);
    
    expect(screen.getByText('Test error message')).toBeInTheDocument();
  });
});