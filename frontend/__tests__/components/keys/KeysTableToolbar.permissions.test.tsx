import React from 'react';
import '@testing-library/jest-dom';
import {screen} from '@testing-library/react';
import {KeysTableToolbar} from '../../../src/components/keys/KeysTableToolbar';
import renderWithPermissions from '../../helpers/renderWithPermissions';

jest.mock('../../../src/api/client', () => ({}));
jest.mock('../../../src/config', () => ({
    __esModule: true,
    default: {
        apiUrl: '/api',
        dateFormat: {year: 'numeric', month: '2-digit', day: 'numeric', hour: '2-digit', minute: '2-digit'},
        version: 'test',
    },
}));

const defaultProps = {
    selectedKeysCount: 1,
    uploadInProgress: false,
    onFileChange: jest.fn(),
    onDeleteClicked: jest.fn(),
};

beforeEach(() => {
    jest.clearAllMocks();
});

describe('KeysTableToolbar permissions', () => {
    it('hides upload button without BIKEY_MODIFY', () => {
        renderWithPermissions(<KeysTableToolbar {...defaultProps}/>, ['BIKEY_VIEW']);
        expect(screen.queryByTestId('key-upload-btn')).not.toBeInTheDocument();
    });

    it('shows upload button with BIKEY_MODIFY', () => {
        renderWithPermissions(<KeysTableToolbar {...defaultProps}/>, ['BIKEY_VIEW', 'BIKEY_MODIFY']);
        expect(screen.getByTestId('key-upload-btn')).toBeInTheDocument();
    });

    it('hides delete button without BIKEY_DELETE', () => {
        renderWithPermissions(<KeysTableToolbar {...defaultProps}/>, ['BIKEY_VIEW']);
        expect(screen.queryByTestId('key-delete-btn')).not.toBeInTheDocument();
    });

    it('shows delete button with BIKEY_DELETE', () => {
        renderWithPermissions(<KeysTableToolbar {...defaultProps}/>, ['BIKEY_VIEW', 'BIKEY_DELETE']);
        expect(screen.getByTestId('key-delete-btn')).toBeInTheDocument();
    });

    it('delete button is disabled when no keys selected', () => {
        renderWithPermissions(
            <KeysTableToolbar {...defaultProps} selectedKeysCount={0}/>,
            ['BIKEY_VIEW', 'BIKEY_DELETE'],
        );
        expect(screen.getByTestId('key-delete-btn')).toBeDisabled();
    });

    it('delete button is enabled when keys are selected', () => {
        renderWithPermissions(
            <KeysTableToolbar {...defaultProps} selectedKeysCount={2}/>,
            ['BIKEY_VIEW', 'BIKEY_DELETE'],
        );
        expect(screen.getByTestId('key-delete-btn')).not.toBeDisabled();
    });

    it('upload button is disabled when server is running', () => {
        renderWithPermissions(
            <KeysTableToolbar {...defaultProps} disabled={true}/>,
            ['BIKEY_VIEW', 'BIKEY_MODIFY'],
        );
        // Upload renders as MUI component="label" — MUI sets aria-disabled
        expect(screen.getByTestId('key-upload-btn')).toHaveAttribute('aria-disabled', 'true');
    });

    it('delete button is disabled when server is running', () => {
        renderWithPermissions(
            <KeysTableToolbar {...defaultProps} disabled={true}/>,
            ['BIKEY_VIEW', 'BIKEY_DELETE'],
        );
        expect(screen.getByTestId('key-delete-btn')).toBeDisabled();
    });
});
