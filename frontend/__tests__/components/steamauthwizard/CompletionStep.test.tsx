import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import CompletionStep from '../../../src/components/steamauthwizard/CompletionStep.tsx';

describe('CompletionStep', () => {
  const mockOnComplete = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders the completion message and success icon', () => {
    render(<CompletionStep onComplete={mockOnComplete} />);
    
    // Check for title and success messages
    expect(screen.getByText('Setup Complete!')).toBeInTheDocument();
    expect(screen.getByText('Your Steam credentials have been successfully configured.')).toBeInTheDocument();
    expect(screen.getByText('You can now download and update game servers and workshop mods using SteamCMD.')).toBeInTheDocument();
    expect(screen.getByText('If you ever need to change your Steam credentials, you can do so in the Settings page.')).toBeInTheDocument();
    
    // Check for success icon (this is more challenging as it's an MUI icon)
    // We can verify the icon is rendered by checking for its parent element
    const iconContainer = document.querySelector('svg');
    expect(iconContainer).toBeInTheDocument();
  });

  it('has a Finish button', () => {
    render(<CompletionStep onComplete={mockOnComplete} />);
    
    const finishButton = screen.getByText('Finish');
    expect(finishButton).toBeInTheDocument();
  });

  it('calls onComplete when Finish button is clicked', () => {
    render(<CompletionStep onComplete={mockOnComplete} />);
    
    const finishButton = screen.getByText('Finish');
    fireEvent.click(finishButton);
    
    expect(mockOnComplete).toHaveBeenCalledTimes(1);
  });
});