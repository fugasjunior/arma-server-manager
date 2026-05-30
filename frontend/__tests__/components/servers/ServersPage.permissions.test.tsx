import React from 'react';
import '@testing-library/jest-dom';
import ServersPage from '../../../src/pages/ServersPage';
import {useServers} from '../../../src/hooks/queries/useServers';
import renderWithPermissions from '../../helpers/renderWithPermissions';

jest.mock('../../../src/hooks/queries/useServers');
jest.mock('../../../src/api/client', () => ({
    serversApi: {
        getServerStatus: jest.fn(),
        startServer: jest.fn(),
        stopServer: jest.fn(),
        restartServer: jest.fn(),
        deleteServer: jest.fn(),
        createServer: jest.fn(),
    },
}));
jest.mock('react-router-dom', () => ({
    Link: ({children}: {children: React.ReactNode}) => <a>{children}</a>,
}));
jest.mock('react-toastify', () => ({toast: {success: jest.fn(), error: jest.fn()}}));

const mockUseServers = jest.mocked(useServers);

beforeEach(() => {
    jest.clearAllMocks();
    mockUseServers.mockReturnValue({data: []} as any);
});

describe('ServersPage permissions', () => {
    it('passes enabled:false to useServers without SERVER_VIEW', () => {
        renderWithPermissions(<ServersPage/>, []);
        expect(mockUseServers).toHaveBeenCalledWith(expect.objectContaining({enabled: false}));
    });

    it('passes enabled:true to useServers with SERVER_VIEW', () => {
        renderWithPermissions(<ServersPage/>, ['SERVER_VIEW']);
        expect(mockUseServers).toHaveBeenCalledWith(expect.objectContaining({enabled: true}));
    });
});
