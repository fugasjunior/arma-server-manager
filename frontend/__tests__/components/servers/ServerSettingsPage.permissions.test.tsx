import React from 'react';
import '@testing-library/jest-dom';
import {useServer} from '../../../src/hooks/queries/useServer';
import {useServerStatus} from '../../../src/hooks/queries/useServerStatus';

jest.mock('../../../src/hooks/queries/useServer');
jest.mock('../../../src/hooks/queries/useServerStatus');
jest.mock('../../../src/api/client', () => ({}));
jest.mock('../../../src/components/servers/EditArma3ServerSettingsForm', () => ({
    __esModule: true,
    default: () => null,
}));
jest.mock('../../../src/components/servers/EditDayZServerSettingsForm', () => ({
    __esModule: true,
    default: () => null,
}));
jest.mock('../../../src/components/servers/EditReforgerServerSettingsForm', () => ({
    __esModule: true,
    default: () => null,
}));
jest.mock('react-router-dom', () => ({
    useParams: () => ({id: '1'}),
    useNavigate: () => jest.fn(),
}));
jest.mock('react-toastify', () => ({toast: {success: jest.fn()}}));
jest.mock('../../../src/config', () => ({
    __esModule: true,
    default: {
        apiUrl: '/api',
        dateFormat: {year: 'numeric', month: '2-digit', day: 'numeric', hour: '2-digit', minute: '2-digit'},
        version: 'test',
    },
}));

import ServerSettingsPage from '../../../src/pages/ServerSettingsPage';
import renderWithPermissions from '../../helpers/renderWithPermissions';

const mockUseServer = jest.mocked(useServer);
const mockUseServerStatus = jest.mocked(useServerStatus);

beforeEach(() => {
    jest.clearAllMocks();
    mockUseServer.mockReturnValue({data: undefined, isLoading: false} as any);
    mockUseServerStatus.mockReturnValue({data: undefined} as any);
});

describe('ServerSettingsPage permissions', () => {
    it('passes enabled:false to useServer without SERVER_VIEW', () => {
        renderWithPermissions(<ServerSettingsPage/>, []);
        expect(mockUseServer).toHaveBeenCalledWith(expect.anything(), expect.objectContaining({enabled: false}));
    });

    it('passes enabled:true to useServer with SERVER_VIEW', () => {
        renderWithPermissions(<ServerSettingsPage/>, ['SERVER_VIEW']);
        expect(mockUseServer).toHaveBeenCalledWith(expect.anything(), expect.objectContaining({enabled: true}));
    });

    it('passes enabled:false to useServerStatus without SERVER_VIEW', () => {
        renderWithPermissions(<ServerSettingsPage/>, []);
        expect(mockUseServerStatus).toHaveBeenCalledWith(expect.anything(), expect.objectContaining({enabled: false}));
    });

    it('passes enabled:true to useServerStatus with SERVER_VIEW', () => {
        renderWithPermissions(<ServerSettingsPage/>, ['SERVER_VIEW']);
        expect(mockUseServerStatus).toHaveBeenCalledWith(expect.anything(), expect.objectContaining({enabled: true}));
    });
});
