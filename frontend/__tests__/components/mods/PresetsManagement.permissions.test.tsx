import React from 'react';
import '@testing-library/jest-dom';
import {screen, fireEvent} from '@testing-library/react';
import PresetsManagement from '../../../src/components/mods/PresetsManagement';
import {usePresets} from '../../../src/hooks/queries/usePresets';
import {useMods} from '../../../src/hooks/queries/useMods';
import renderWithPermissions from '../../helpers/renderWithPermissions';
import {PresetResponseDto, ServerType} from '../../../src/api/generated';

jest.mock('../../../src/hooks/queries/usePresets');
jest.mock('../../../src/hooks/queries/useMods');
jest.mock('../../../src/api/client', () => ({
    modPresetsApi: {
        deletePreset: jest.fn(),
        updatePreset: jest.fn(),
        renamePreset: jest.fn(),
        getPresets: jest.fn(),
    },
    armaLauncherPresetApi: {
        importLauncherPreset: jest.fn(),
    },
}));
jest.mock('../../../src/api/downloads', () => ({
    downloadExportedPreset: jest.fn(),
}));
jest.mock('../../../src/config', () => ({
    __esModule: true,
    default: {
        apiUrl: '/api',
        dateFormat: {year: 'numeric', month: '2-digit', day: 'numeric', hour: '2-digit', minute: '2-digit'},
        version: 'test',
    },
}));

const mockUsePresets = jest.mocked(usePresets);
const mockUseMods = jest.mocked(useMods);

const samplePreset: PresetResponseDto = {
    id: 1,
    name: 'TestPreset',
    type: ServerType.Arma3,
    mods: [],
    totalModsSize: 0,
};

beforeEach(() => {
    jest.clearAllMocks();
    mockUsePresets.mockReturnValue({data: [samplePreset], isLoading: false} as any);
    mockUseMods.mockReturnValue({data: [], isLoading: false, isSuccess: false} as any);
});

describe('PresetsManagement permissions', () => {
    describe('query gating', () => {
        it('passes enabled:false to presets hook without MOD_VIEW', () => {
            renderWithPermissions(<PresetsManagement/>, []);
            expect(mockUsePresets).toHaveBeenCalledWith(undefined, expect.objectContaining({enabled: false}));
        });

        it('passes enabled:true to presets hook with MOD_VIEW', () => {
            renderWithPermissions(<PresetsManagement/>, ['MOD_VIEW']);
            expect(mockUsePresets).toHaveBeenCalledWith(undefined, expect.objectContaining({enabled: true}));
        });
    });

    describe('import button', () => {
        it('hides import button without MOD_MODIFY', () => {
            renderWithPermissions(<PresetsManagement/>, ['MOD_VIEW']);
            expect(screen.queryByTestId('preset-import-btn')).not.toBeInTheDocument();
        });

        it('shows import button with MOD_MODIFY', () => {
            renderWithPermissions(<PresetsManagement/>, ['MOD_VIEW', 'MOD_MODIFY']);
            expect(screen.getByTestId('preset-import-btn')).toBeInTheDocument();
        });
    });

    describe('row edit button', () => {
        it('hides edit button without MOD_MODIFY', () => {
            renderWithPermissions(<PresetsManagement/>, ['MOD_VIEW']);
            expect(screen.queryByTestId('preset-edit-1')).not.toBeInTheDocument();
        });

        it('shows edit button with MOD_MODIFY', () => {
            renderWithPermissions(<PresetsManagement/>, ['MOD_VIEW', 'MOD_MODIFY']);
            expect(screen.getByTestId('preset-edit-1')).toBeInTheDocument();
        });
    });

    describe('more-actions menu items', () => {
        function openMenu(permissions: string[]) {
            renderWithPermissions(<PresetsManagement/>, permissions);
            fireEvent.click(screen.getByTestId('preset-menu-1'));
        }

        it('hides Edit mods menu item without MOD_MODIFY', () => {
            openMenu(['MOD_VIEW']);
            expect(screen.queryByTestId('preset-menu-edit-1')).not.toBeInTheDocument();
        });

        it('shows Edit mods menu item with MOD_MODIFY', () => {
            openMenu(['MOD_VIEW', 'MOD_MODIFY']);
            expect(screen.getByTestId('preset-menu-edit-1')).toBeInTheDocument();
        });

        it('hides Rename menu item without MOD_MODIFY', () => {
            openMenu(['MOD_VIEW']);
            expect(screen.queryByTestId('preset-menu-rename-1')).not.toBeInTheDocument();
        });

        it('shows Rename menu item with MOD_MODIFY', () => {
            openMenu(['MOD_VIEW', 'MOD_MODIFY']);
            expect(screen.getByTestId('preset-menu-rename-1')).toBeInTheDocument();
        });

        it('hides Delete menu item without MOD_DELETE', () => {
            openMenu(['MOD_VIEW']);
            expect(screen.queryByTestId('preset-menu-delete-1')).not.toBeInTheDocument();
        });

        it('shows Delete menu item with MOD_DELETE', () => {
            openMenu(['MOD_VIEW', 'MOD_DELETE']);
            expect(screen.getByTestId('preset-menu-delete-1')).toBeInTheDocument();
        });

        it('shows Download menu item regardless of MOD_MODIFY or MOD_DELETE', () => {
            openMenu(['MOD_VIEW']);
            expect(screen.getByTestId('preset-menu-download-1')).toBeInTheDocument();
        });
    });
});
