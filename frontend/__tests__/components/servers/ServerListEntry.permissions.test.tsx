import React from 'react';
import {fireEvent, screen} from '@testing-library/react';
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

const openActionsMenu = () => {
    fireEvent.click(screen.getByTestId(`server-${server.id}-actions-btn`));
};

beforeEach(() => {
    jest.clearAllMocks();
    mockUseServerStatus.mockReturnValue({data: null} as any);
});

describe('ServerListEntry permissions', () => {
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

    it('hides delete button without SERVER_DELETE', () => {
        mockUseServerStatus.mockReturnValue({data: {alive: false}} as any);
        renderWithPermissions(
            <table><tbody><ServerListEntry {...defaultProps}/></tbody></table>,
            ['SERVER_VIEW'],
        );
        openActionsMenu();
        expect(screen.queryByText('Delete')).not.toBeInTheDocument();
    });

    it('shows delete button with SERVER_DELETE', () => {
        mockUseServerStatus.mockReturnValue({data: {alive: false}} as any);
        renderWithPermissions(
            <table><tbody><ServerListEntry {...defaultProps}/></tbody></table>,
            ['SERVER_VIEW', 'SERVER_DELETE'],
        );
        openActionsMenu();
        expect(screen.getByText('Delete')).toBeInTheDocument();
    });

    it('hides duplicate button without SERVER_MODIFY', () => {
        renderWithPermissions(
            <table><tbody><ServerListEntry {...defaultProps}/></tbody></table>,
            ['SERVER_VIEW'],
        );
        openActionsMenu();
        expect(screen.queryByText('Duplicate')).not.toBeInTheDocument();
    });

    it('shows duplicate button with SERVER_MODIFY', () => {
        renderWithPermissions(
            <table><tbody><ServerListEntry {...defaultProps}/></tbody></table>,
            ['SERVER_VIEW', 'SERVER_MODIFY'],
        );
        openActionsMenu();
        expect(screen.getByText('Duplicate')).toBeInTheDocument();
    });
});
