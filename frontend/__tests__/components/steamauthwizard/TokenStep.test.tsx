import React from 'react';
import {fireEvent, render, screen} from '@testing-library/react';
import '@testing-library/jest-dom';
import TokenStep from '../../../src/components/steamauthwizard/TokenStep.tsx';
import { AuthType } from '../../../src/api/generated';

jest.mock('../../../src/api/client', () => ({
  steamAuthApi: {
    steamLogin: jest.fn().mockResolvedValue({ data: { result: 'SUCCESS' } }),
  }
}));

describe('TokenStep', () => {
  const mockProps = {
    credentials: { username: 'testuser', password: 'testpass', steamGuardCode: '' },
    setCredentials: jest.fn(),
    authType: AuthType.Email,
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

    expect(screen.getByText('Enter Steam Guard Code')).toBeInTheDocument();
    expect(screen.getByRole('textbox', { name: /steam guard code/i })).toBeInTheDocument();
    expect(screen.getByText('Continue')).toBeInTheDocument();
    expect(screen.getByText('Back')).toBeInTheDocument();
  });

  it('updates code when input value changes', () => {
    let capturedValue: string | null = null;
    mockProps.setCredentials.mockImplementation((updaterFn: (prev: object) => { steamGuardCode: string }) => {
      const result = updaterFn({ username: 'testuser', password: 'testpass', steamGuardCode: '' });
      capturedValue = result.steamGuardCode;
    });

    render(<TokenStep {...mockProps} />);

    const codeInput = screen.getByRole('textbox', { name: /steam guard code/i });
    fireEvent.change(codeInput, { target: { value: '123456' } });

    expect(capturedValue).toBe('123456');
  });

  it('shows error when submitting with empty code', () => {
    render(<TokenStep {...mockProps} />);

    fireEvent.submit(screen.getByText('Continue'));

    expect(mockProps.setError).toHaveBeenCalledWith('Steam Guard code is required');
    expect(mockProps.onNext).not.toHaveBeenCalled();
  });

  it('calls onBack when Back button is clicked', () => {
    render(<TokenStep {...mockProps} />);

    fireEvent.click(screen.getByText('Back'));

    expect(mockProps.onBack).toHaveBeenCalledTimes(1);
  });

  it('disables buttons when loading', () => {
    render(<TokenStep {...mockProps} loading={true} />);

    expect(screen.getByText('Verifying…')).toBeDisabled();
    expect(screen.getByText('Back')).toBeDisabled();
  });

  it('displays error message when error state is set', () => {
    render(<TokenStep {...mockProps} error="Test error message" />);

    expect(screen.getByText('Test error message')).toBeInTheDocument();
  });
});
