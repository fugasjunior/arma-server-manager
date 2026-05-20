import React from 'react';
import '@testing-library/jest-dom';
import {screen} from '@testing-library/react';
import ModsTableToolbar from '../../../src/components/mods/ModsTableToolbar';
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
    selectedModsCount: 1,
    filter: '',
    arma3ModsCount: 1,
    dayZModsCount: 0,
    mixedModsSelected: false,
    onUpdateClicked: jest.fn(),
    onCreatePresetClicked: jest.fn(),
    onUninstallClicked: jest.fn(),
    onFilterChange: jest.fn(),
};

beforeEach(() => {
    jest.clearAllMocks();
});

describe('ModsTableToolbar permissions', () => {
    it('hides update button without MOD_MODIFY', () => {
        renderWithPermissions(<ModsTableToolbar {...defaultProps}/>, ['MOD_VIEW']);
        expect(screen.queryByTestId('mod-update-btn')).not.toBeInTheDocument();
    });

    it('shows update button with MOD_MODIFY', () => {
        renderWithPermissions(<ModsTableToolbar {...defaultProps}/>, ['MOD_VIEW', 'MOD_MODIFY']);
        expect(screen.getByTestId('mod-update-btn')).toBeInTheDocument();
    });

    it('hides save-as-preset button without MOD_MODIFY', () => {
        renderWithPermissions(<ModsTableToolbar {...defaultProps}/>, ['MOD_VIEW']);
        expect(screen.queryByTestId('mod-save-preset-btn')).not.toBeInTheDocument();
    });

    it('shows save-as-preset button with MOD_MODIFY', () => {
        renderWithPermissions(<ModsTableToolbar {...defaultProps}/>, ['MOD_VIEW', 'MOD_MODIFY']);
        expect(screen.getByTestId('mod-save-preset-btn')).toBeInTheDocument();
    });

    it('hides uninstall button without MOD_DELETE', () => {
        renderWithPermissions(<ModsTableToolbar {...defaultProps}/>, ['MOD_VIEW']);
        expect(screen.queryByTestId('mod-uninstall-btn')).not.toBeInTheDocument();
    });

    it('shows uninstall button with MOD_DELETE', () => {
        renderWithPermissions(<ModsTableToolbar {...defaultProps}/>, ['MOD_VIEW', 'MOD_DELETE']);
        expect(screen.getByTestId('mod-uninstall-btn')).toBeInTheDocument();
    });
});
