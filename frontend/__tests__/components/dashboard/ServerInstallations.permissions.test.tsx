import React from 'react';
import '@testing-library/jest-dom';
import ServerInstallations from '../../../src/components/dashboard/ServerInstallations';
import {useServerInstallations} from '../../../src/hooks/queries/useServerInstallations';
import {useSteamCmdItemInfos} from '../../../src/hooks/queries/useSteamCmdItemInfos';
import renderWithPermissions from '../../helpers/renderWithPermissions';

jest.mock('../../../src/hooks/queries/useServerInstallations');
jest.mock('../../../src/hooks/queries/useSteamCmdItemInfos');
jest.mock('../../../src/api/client', () => ({
    serverInstallationApi: {
        installServer: jest.fn(),
        uninstallServer: jest.fn(),
        setActiveBranch: jest.fn(),
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

const mockUseServerInstallations = jest.mocked(useServerInstallations);
const mockUseSteamCmdItemInfos = jest.mocked(useSteamCmdItemInfos);

beforeEach(() => {
    jest.clearAllMocks();
    mockUseServerInstallations.mockReturnValue({data: []} as any);
    mockUseSteamCmdItemInfos.mockReturnValue({data: {}} as any);
});

describe('ServerInstallations permissions', () => {
    it('renders nothing without INSTALL_VIEW', () => {
        const {container} = renderWithPermissions(<ServerInstallations/>, []);
        expect(container).toBeEmptyDOMElement();
    });

    it('passes enabled:false to installations hook without INSTALL_VIEW', () => {
        renderWithPermissions(<ServerInstallations/>, []);
        expect(mockUseServerInstallations).toHaveBeenCalledWith(expect.objectContaining({enabled: false}));
    });

    it('passes enabled:true to installations hook with INSTALL_VIEW', () => {
        renderWithPermissions(<ServerInstallations/>, ['INSTALL_VIEW']);
        expect(mockUseServerInstallations).toHaveBeenCalledWith(expect.objectContaining({enabled: true}));
    });

    it('passes enabled:false to SteamCmd hook without STEAM_AUTH_ADMIN', () => {
        renderWithPermissions(<ServerInstallations/>, ['INSTALL_VIEW']);
        expect(mockUseSteamCmdItemInfos).toHaveBeenCalledWith(expect.objectContaining({enabled: false}));
    });

    it('passes enabled:true to SteamCmd hook with STEAM_AUTH_ADMIN', () => {
        renderWithPermissions(<ServerInstallations/>, ['INSTALL_VIEW', 'STEAM_AUTH_ADMIN']);
        expect(mockUseSteamCmdItemInfos).toHaveBeenCalledWith(expect.objectContaining({enabled: true}));
    });
});
