import React from 'react';
import '@testing-library/jest-dom';
import ModsManagement from '../../../src/components/mods/ModsManagement';
import {useMods} from '../../../src/hooks/queries/useMods';
import {useSteamCmdItemInfos} from '../../../src/hooks/queries/useSteamCmdItemInfos';
import renderWithPermissions from '../../helpers/renderWithPermissions';

jest.mock('../../../src/hooks/queries/useMods');
jest.mock('../../../src/hooks/queries/useSteamCmdItemInfos');
jest.mock('../../../src/api/client', () => ({
    modsApi: {
        addMods: jest.fn(),
        deleteMods: jest.fn(),
        updateMod: jest.fn(),
        setModFlags: jest.fn(),
    },
    modPresetsApi: {
        createPreset: jest.fn(),
    },
}));
jest.mock('../../../src/config', () => ({
    __esModule: true,
    default: {
        apiUrl: '/api',
        dateFormat: {year: 'numeric', month: '2-digit', day: 'numeric', hour: '2-digit', minute: '2-digit'},
        version: 'test',
    },
}));

const mockUseMods = jest.mocked(useMods);
const mockUseSteamCmdItemInfos = jest.mocked(useSteamCmdItemInfos);

beforeEach(() => {
    jest.clearAllMocks();
    mockUseMods.mockReturnValue({data: [], isLoading: false} as any);
    mockUseSteamCmdItemInfos.mockReturnValue({data: {}} as any);
});

describe('ModsManagement permissions', () => {
    it('passes enabled:false to mods hook without MOD_VIEW', () => {
        renderWithPermissions(<ModsManagement/>, []);
        expect(mockUseMods).toHaveBeenCalledWith(undefined, expect.objectContaining({enabled: false}));
    });

    it('passes enabled:true to mods hook with MOD_VIEW', () => {
        renderWithPermissions(<ModsManagement/>, ['MOD_VIEW']);
        expect(mockUseMods).toHaveBeenCalledWith(undefined, expect.objectContaining({enabled: true}));
    });

    it('passes enabled:false to SteamCmd hook without STEAM_AUTH_ADMIN', () => {
        renderWithPermissions(<ModsManagement/>, ['MOD_VIEW']);
        expect(mockUseSteamCmdItemInfos).toHaveBeenCalledWith(expect.objectContaining({enabled: false}));
    });

    it('passes enabled:true to SteamCmd hook with STEAM_AUTH_ADMIN', () => {
        renderWithPermissions(<ModsManagement/>, ['MOD_VIEW', 'STEAM_AUTH_ADMIN']);
        expect(mockUseSteamCmdItemInfos).toHaveBeenCalledWith(expect.objectContaining({enabled: true}));
    });
});
