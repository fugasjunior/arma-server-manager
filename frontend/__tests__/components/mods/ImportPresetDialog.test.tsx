import React from 'react';
import {render, screen, waitFor} from '@testing-library/react';
import '@testing-library/jest-dom';
import userEvent from '@testing-library/user-event';
import ImportPresetDialog from '../../../src/components/mods/ImportPresetDialog';

describe('ImportPresetDialog', () => {
    const mockOnConfirm = jest.fn();
    const mockOnClose = jest.fn();

    beforeEach(() => {
        jest.clearAllMocks();
    });

    function makeHtmlFile(content: string, name = 'preset.html'): File {
        return new File([content], name, {type: 'text/html'});
    }

    function renderDialog(overrides: Partial<React.ComponentProps<typeof ImportPresetDialog>> = {}) {
        render(
            <ImportPresetDialog
                open={true}
                file={null}
                existingPresetNames={[]}
                onConfirmClicked={mockOnConfirm}
                onClose={mockOnClose}
                {...overrides}
            />
        );
    }

    test('extracts preset name from arma:PresetName meta tag', async () => {
        const html = `<html><head><meta name="arma:PresetName" content="My Preset"></head></html>`;
        const file = makeHtmlFile(html);

        renderDialog({file});

        await waitFor(() => {
            expect(screen.getByDisplayValue('My Preset')).toBeInTheDocument();
        });
    });

    test('shows error when suggested name is already taken', async () => {
        const html = `<html><head><meta name="arma:PresetName" content="Taken"></head></html>`;
        const file = makeHtmlFile(html);

        renderDialog({file, existingPresetNames: ['Taken']});

        await waitFor(() => {
            expect(screen.getByText('This preset name is already in use')).toBeInTheDocument();
        });
    });

    test('Import button disabled when name is empty', () => {
        renderDialog();

        expect(screen.getByRole('button', {name: /^import$/i})).toBeDisabled();
    });

    test('Import button disabled when name is taken', async () => {
        const html = `<html><head><meta name="arma:PresetName" content="Taken"></head></html>`;
        const file = makeHtmlFile(html);

        renderDialog({file, existingPresetNames: ['Taken']});

        await waitFor(() => {
            expect(screen.getByRole('button', {name: /^import$/i})).toBeDisabled();
        });
    });

    test('shows error when user types taken name', async () => {
        const user = userEvent.setup();
        renderDialog({existingPresetNames: ['Existing']});

        await user.type(screen.getByRole('textbox'), 'Existing');

        expect(screen.getByText('This preset name is already in use')).toBeInTheDocument();
        expect(screen.getByRole('button', {name: /^import$/i})).toBeDisabled();
    });

    test('calls onConfirmClicked with entered name on confirm', async () => {
        const user = userEvent.setup();
        renderDialog();

        await user.type(screen.getByRole('textbox'), 'My New Preset');
        await user.click(screen.getByRole('button', {name: /^import$/i}));

        expect(mockOnConfirm).toHaveBeenCalledWith('My New Preset');
    });

    test('calls onClose when Cancel clicked', async () => {
        const user = userEvent.setup();
        renderDialog();

        await user.click(screen.getByRole('button', {name: /cancel/i}));

        expect(mockOnClose).toHaveBeenCalled();
    });
});
