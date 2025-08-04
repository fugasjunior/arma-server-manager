import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import WelcomeStep from '../../../src/components/steamauthwizard/WelcomeStep.tsx';

describe('WelcomeStep', () => {
  const mockOnNext = jest.fn();
  const mockOnSkip = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders the welcome message and instructions', () => {
    render(<WelcomeStep onNext={mockOnNext} onSkip={mockOnSkip} />);
    
    // Check for title
    expect(screen.getByText('Welcome to the Steam Authentication Setup')).toBeInTheDocument();
    
    // Check for explanatory text
    expect(screen.getByText('This wizard will help you configure your Steam credentials for the Arma Server Manager.')).toBeInTheDocument();
    expect(screen.getByText('Why do we need your Steam credentials?')).toBeInTheDocument();
    expect(screen.getByText(/The Arma Server Manager uses SteamCMD to download and update servers and workshop mods./)).toBeInTheDocument();
    expect(screen.getByText(/Your credentials will be stored securely/)).toBeInTheDocument();
    expect(screen.getByText(/We recommend creating a separate Steam account/)).toBeInTheDocument();
  });

  it('has Continue and Skip Setup buttons', () => {
    render(<WelcomeStep onNext={mockOnNext} onSkip={mockOnSkip} />);
    
    const continueButton = screen.getByText('Continue');
    const skipButton = screen.getByText('Skip Setup');
    
    expect(continueButton).toBeInTheDocument();
    expect(skipButton).toBeInTheDocument();
  });

  it('calls onNext when Continue button is clicked', () => {
    render(<WelcomeStep onNext={mockOnNext} onSkip={mockOnSkip} />);
    
    const continueButton = screen.getByText('Continue');
    fireEvent.click(continueButton);
    
    expect(mockOnNext).toHaveBeenCalledTimes(1);
    expect(mockOnSkip).not.toHaveBeenCalled();
  });

  it('calls onSkip when Skip Setup button is clicked', () => {
    render(<WelcomeStep onNext={mockOnNext} onSkip={mockOnSkip} />);
    
    const skipButton = screen.getByText('Skip Setup');
    fireEvent.click(skipButton);
    
    expect(mockOnSkip).toHaveBeenCalledTimes(1);
    expect(mockOnNext).not.toHaveBeenCalled();
  });
});