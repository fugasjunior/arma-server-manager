import React from 'react';
import {render, screen} from '@testing-library/react';
import '@testing-library/jest-dom';
import userEvent from '@testing-library/user-event';
import RenamePresetDialog from '../../../src/components/mods/RenamePresetDialog';

describe('RenamePresetDialog', () => {
    const mockOnConfirm = jest.fn();
    const mockOnClose = jest.fn();

    beforeEach(() => {
        jest.clearAllMocks();
    });

    function renderDialog(overrides: Partial<React.ComponentProps<typeof RenamePresetDialog>> = {}) {
        render(
            <RenamePresetDialog
                open={true}
                currentName="Original Name"
                existingPresetNames={[]}
                onConfirmClicked={mockOnConfirm}
                onClose={mockOnClose}
                {...overrides}
            />
        );
    }

    test('pre-fills text field with current name', () => {
        renderDialog();

        expect(screen.getByDisplayValue('Original Name')).toBeInTheDocument();
    });

    test('Rename button disabled when name unchanged', () => {
        renderDialog();

        expect(screen.getByRole('button', {name: /rename/i})).toBeDisabled();
    });

    test('Rename button enabled when name changed to unique value', async () => {
        const user = userEvent.setup();
        renderDialog();

        const input = screen.getByRole('textbox');
        await user.clear(input);
        await user.type(input, 'New Unique Name');

        expect(screen.getByRole('button', {name: /rename/i})).toBeEnabled();
    });

    test('shows error when new name is already taken', async () => {
        const user = userEvent.setup();
        renderDialog({existingPresetNames: ['Taken Name']});

        const input = screen.getByRole('textbox');
        await user.clear(input);
        await user.type(input, 'Taken Name');

        expect(screen.getByText('This preset name is already in use')).toBeInTheDocument();
        expect(screen.getByRole('button', {name: /rename/i})).toBeDisabled();
    });

    test('no error when name same as current (even if in existingPresetNames)', async () => {
        const user = userEvent.setup();
        renderDialog({existingPresetNames: ['Original Name']});

        const input = screen.getByRole('textbox');
        await user.clear(input);
        await user.type(input, 'Original Name');

        expect(screen.queryByText('This preset name is already in use')).not.toBeInTheDocument();
    });

    test('calls onConfirmClicked with new name on confirm', async () => {
        const user = userEvent.setup();
        renderDialog();

        const input = screen.getByRole('textbox');
        await user.clear(input);
        await user.type(input, 'Renamed Preset');
        await user.click(screen.getByRole('button', {name: /rename/i}));

        expect(mockOnConfirm).toHaveBeenCalledWith('Renamed Preset');
    });

    test('calls onClose when Cancel clicked', async () => {
        const user = userEvent.setup();
        renderDialog();

        await user.click(screen.getByRole('button', {name: /cancel/i}));

        expect(mockOnClose).toHaveBeenCalled();
    });
});
