import React from 'react';
import {screen} from '@testing-library/react';
import '@testing-library/jest-dom';
import {ServerListEntryDetails} from '../../../src/components/servers/serverListEntry/ServerListEntryDetails';
import {ServerDto, ServerType} from '../../../src/api/generated';
import renderWithPermissions from '../../helpers/renderWithPermissions';

jest.mock('../../../src/api/client', () => ({
    serversApi: {},
    headlessClientApi: {},
}));
jest.mock('../../../src/hooks/queries/useServer', () => ({
    useServer: () => ({data: undefined, isLoading: false}),
}));
jest.mock('../../../src/hooks/queries/useMods', () => ({
    useMods: () => ({data: [], isLoading: false}),
}));
jest.mock('../../../src/hooks/queries/usePresets', () => ({
    usePresets: () => ({data: [], isLoading: false}),
}));
jest.mock('../../../src/hooks/queries/useCreatorDlcs', () => ({
    useCreatorDlcs: () => ({data: [], isLoading: false}),
}));

const arma3Server: ServerDto = {
    id: 1,
    name: 'Test',
    type: ServerType.Arma3,
    port: 2302,
    queryPort: 2303,
};

const defaultProps = {
    server: arma3Server,
    serverStatus: null,
    onClick: jest.fn(),
    onDuplicateServer: jest.fn(),
};

beforeEach(() => {
    jest.clearAllMocks();
});

describe('ServerListEntryDetails permissions', () => {
    it('hides logs button without SERVER_LOGS_VIEW', () => {
        renderWithPermissions(
            <ServerListEntryDetails {...defaultProps}/>,
            ['SERVER_VIEW', 'MOD_VIEW', 'MOD_MODIFY'],
        );
        expect(screen.queryByText('Logs')).not.toBeInTheDocument();
    });

    it('shows logs button with SERVER_LOGS_VIEW', () => {
        renderWithPermissions(
            <ServerListEntryDetails {...defaultProps}/>,
            ['SERVER_VIEW', 'SERVER_LOGS_VIEW'],
        );
        expect(screen.getByText('Logs')).toBeInTheDocument();
    });

    it('hides mods button without MOD_VIEW', () => {
        renderWithPermissions(
            <ServerListEntryDetails {...defaultProps}/>,
            ['SERVER_VIEW'],
        );
        expect(screen.queryByText('Mods')).not.toBeInTheDocument();
    });

    it('shows mods button with MOD_VIEW', () => {
        renderWithPermissions(
            <ServerListEntryDetails {...defaultProps}/>,
            ['SERVER_VIEW', 'MOD_VIEW'],
        );
        expect(screen.getByText('Mods')).toBeInTheDocument();
    });

    it('hides DLCs button without MOD_MODIFY on Arma3 server', () => {
        renderWithPermissions(
            <ServerListEntryDetails {...defaultProps}/>,
            ['SERVER_VIEW', 'MOD_VIEW'],
        );
        expect(screen.queryByText('DLCs')).not.toBeInTheDocument();
    });

    it('shows DLCs button with MOD_MODIFY on Arma3 server', () => {
        renderWithPermissions(
            <ServerListEntryDetails {...defaultProps}/>,
            ['SERVER_VIEW', 'MOD_VIEW', 'MOD_MODIFY'],
        );
        expect(screen.getByText('DLCs')).toBeInTheDocument();
    });

    it('hides duplicate button without SERVER_MODIFY', () => {
        renderWithPermissions(
            <ServerListEntryDetails {...defaultProps}/>,
            ['SERVER_VIEW'],
        );
        expect(screen.queryByText('Duplicate')).not.toBeInTheDocument();
    });

    it('shows duplicate button with SERVER_MODIFY', () => {
        renderWithPermissions(
            <ServerListEntryDetails {...defaultProps}/>,
            ['SERVER_VIEW', 'SERVER_MODIFY'],
        );
        expect(screen.getByText('Duplicate')).toBeInTheDocument();
    });
});
