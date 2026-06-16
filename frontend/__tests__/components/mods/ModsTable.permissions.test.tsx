import React from 'react';
import '@testing-library/jest-dom';
import {screen, within} from '@testing-library/react';
import ModsTable from '../../../src/components/mods/ModsTable';
import {ModDto, InstallationStatus} from '../../../src/api/generated';
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

const sampleMod: ModDto = {
    id: 42,
    name: 'TestMod',
    loadOnClient: true,
    loadOnServer: true,
    loadOnHeadlessClient: true,
    installationStatus: InstallationStatus.Finished,
};

const defaultProps = {
    rows: [sampleMod],
    selected: [],
    filter: '',
    arma3ModsCount: 0,
    dayZModsCount: 0,
    mixedModsSelected: false,
    loading: false,
    steamCmdItemInfo: {},
    onModInstallClicked: jest.fn(),
    onModUpdateClicked: jest.fn(),
    onCreatePresetClicked: jest.fn(),
    onModUninstallClicked: jest.fn(),
    onFilterChange: jest.fn(),
    onRowClick: jest.fn(),
    onSelectAllRowsClick: jest.fn(),
    onFlagsChange: jest.fn(),
};

beforeEach(() => {
    jest.clearAllMocks();
});

describe('ModsTable permissions', () => {
    describe('install-by-id controls', () => {
        it('hides install input without MOD_MODIFY', () => {
            renderWithPermissions(<ModsTable {...defaultProps}/>, ['MOD_VIEW']);
            expect(screen.queryByTestId('mod-install-input')).not.toBeInTheDocument();
        });

        it('shows install input with MOD_MODIFY', () => {
            renderWithPermissions(<ModsTable {...defaultProps}/>, ['MOD_VIEW', 'MOD_MODIFY']);
            expect(screen.getByTestId('mod-install-input')).toBeInTheDocument();
        });

        it('hides install submit button without MOD_MODIFY', () => {
            renderWithPermissions(<ModsTable {...defaultProps}/>, ['MOD_VIEW']);
            expect(screen.queryByTestId('mod-install-submit')).not.toBeInTheDocument();
        });

        it('shows install submit button with MOD_MODIFY', () => {
            renderWithPermissions(<ModsTable {...defaultProps}/>, ['MOD_VIEW', 'MOD_MODIFY']);
            expect(screen.getByTestId('mod-install-submit')).toBeInTheDocument();
        });
    });

    describe('flags control', () => {
        it('is disabled without MOD_MODIFY', () => {
            renderWithPermissions(<ModsTable {...defaultProps}/>, ['MOD_VIEW']);
            const wrapper = screen.getByTestId('mod-flags-42');
            within(wrapper).getAllByRole('button').forEach(btn => expect(btn).toBeDisabled());
        });

        it('is enabled with MOD_MODIFY', () => {
            renderWithPermissions(<ModsTable {...defaultProps}/>, ['MOD_VIEW', 'MOD_MODIFY']);
            const wrapper = screen.getByTestId('mod-flags-42');
            within(wrapper).getAllByRole('button').forEach(btn => expect(btn).not.toBeDisabled());
        });
    });
});
