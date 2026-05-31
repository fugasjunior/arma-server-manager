import React from 'react';
import {screen} from '@testing-library/react';
import '@testing-library/jest-dom';
import ServerListEntry from '../../../src/components/servers/serverListEntry/ServerListEntry';
import {useServerStatus} from '../../../src/hooks/queries/useServerStatus';
import {ServerDto, ServerType} from '../../../src/api/generated';
import renderWithPermissions from '../../helpers/renderWithPermissions';

jest.mock('../../../src/hooks/queries/useServerStatus');
jest.mock('../../../src/api/client', () => ({
    headlessClientApi: {setHeadlessClientsTarget: jest.fn()},
    serversApi: {},
    scenariosApi: {},
}));
jest.mock('../../../src/hooks/queries/useServerScenarios', () => ({
    useServerScenarios: () => ({data: [], isLoading: false}),
}));
jest.mock('../../../src/config', () => ({
    __esModule: true,
    default: {
        apiUrl: '/api',
        dateFormat: {year: 'numeric', month: '2-digit', day: 'numeric', hour: '2-digit', minute: '2-digit'},
        version: 'test',
    },
}));
jest.mock('react-router-dom', () => ({
    Link: ({children}: {children: React.ReactNode}) => <a>{children}</a>,
}));

const mockUseServerStatus = jest.mocked(useServerStatus);

const server: ServerDto = {
    id: 1,
    name: 'Test Server',
    type: ServerType.Arma3,
    port: 2302,
    queryPort: 2303,
};

const defaultProps = {
    server,
    serverWithSamePortRunning: false,
    onStartServer: jest.fn(),
    onStopServer: jest.fn(),
    onRestartServer: jest.fn(),
    onDeleteServer: jest.fn(),
    onOpenLogs: jest.fn(),
    onDuplicateServer: jest.fn(),
};

beforeEach(() => {
    jest.clearAllMocks();
    mockUseServerStatus.mockReturnValue({data: null} as any);
});

describe('ServerListEntry permissions', () => {
    it('hides settings button without SERVER_VIEW', () => {
        renderWithPermissions(
            <table><tbody><ServerListEntry {...defaultProps}/></tbody></table>,
            [],
        );
        expect(screen.queryByTestId(`server-${server.id}-settings-btn`) ??
               screen.queryByText('Settings')).not.toBeInTheDocument();
    });

    it('shows settings button with SERVER_VIEW', () => {
        renderWithPermissions(
            <table><tbody><ServerListEntry {...defaultProps}/></tbody></table>,
            ['SERVER_VIEW'],
        );
        expect(screen.getByText('Settings')).toBeInTheDocument();
    });

    it('hides server controls without SERVER_OPERATE', () => {
        mockUseServerStatus.mockReturnValue({data: {alive: false}} as any);
        renderWithPermissions(
            <table><tbody><ServerListEntry {...defaultProps}/></tbody></table>,
            ['SERVER_VIEW'],
        );
        expect(screen.queryByText('Start')).not.toBeInTheDocument();
    });

    it('shows server controls with SERVER_OPERATE', () => {
        mockUseServerStatus.mockReturnValue({data: {alive: false}} as any);
        renderWithPermissions(
            <table><tbody><ServerListEntry {...defaultProps}/></tbody></table>,
            ['SERVER_VIEW', 'SERVER_OPERATE'],
        );
        expect(screen.getByText('Start')).toBeInTheDocument();
    });

    it('hides delete button without SERVER_DELETE when server stopped', () => {
        mockUseServerStatus.mockReturnValue({data: {alive: false}} as any);
        renderWithPermissions(
            <table><tbody><ServerListEntry {...defaultProps}/></tbody></table>,
            ['SERVER_VIEW'],
        );
        expect(screen.queryByText('Delete')).not.toBeInTheDocument();
    });

    it('shows delete button with SERVER_DELETE when server stopped', () => {
        mockUseServerStatus.mockReturnValue({data: {alive: false}} as any);
        renderWithPermissions(
            <table><tbody><ServerListEntry {...defaultProps}/></tbody></table>,
            ['SERVER_VIEW', 'SERVER_DELETE'],
        );
        expect(screen.getByText('Delete')).toBeInTheDocument();
    });
});
