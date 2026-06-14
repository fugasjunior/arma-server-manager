import React from 'react';
import '@testing-library/jest-dom';
import {screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';

jest.mock('../../../src/api/client', () => ({
    serversApi: {
        seedConfigOverride: jest.fn(),
    },
}));
jest.mock('../../../src/hooks/queries/useReforgerScenarios', () => ({
    useReforgerScenarios: jest.fn(() => ({data: []})),
}));
jest.mock('../../../src/components/servers/CustomLaunchParametersInput', () => ({
    CustomLaunchParametersInput: jest.fn(() => null),
}));
jest.mock('react-toastify', () => ({
    toast: {
        success: jest.fn(),
        error: jest.fn(),
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

import EditReforgerServerSettingsForm from '../../../src/components/servers/EditReforgerServerSettingsForm';
import {ReforgerServerDto} from '../../../src/api/serverModels';
import renderWithPermissions from '../../helpers/renderWithPermissions';
import {ServerType} from '../../../src/api/generated';

const {serversApi} = require('../../../src/api/client');
const mockSeedConfigOverride = serversApi.seedConfigOverride;

const baseServer: ReforgerServerDto = {
    name: 'Test Reforger',
    description: '',
    port: 2001,
    queryPort: 17777,
    maxPlayers: 32,
    scenarioId: '{ECC61978EDCC2B5A}Missions/23_Campaign.conf',
    type: ServerType.Reforger,
    configOverrides: [],
};

const defaultProps = {
    server: baseServer,
    onSubmit: jest.fn(),
    onCancel: jest.fn(),
};

const bothPermissions = ['SERVER_VIEW', 'SERVER_MODIFY', 'ADVANCED_CONFIG_EDIT', 'SERVER_SECRETS_VIEW'];

beforeEach(() => jest.clearAllMocks());

describe('EditReforgerServerSettingsForm advanced config', () => {
    it('hides Advanced edit switch without ADVANCED_CONFIG_EDIT or SERVER_SECRETS_VIEW', () => {
        renderWithPermissions(
            <EditReforgerServerSettingsForm {...defaultProps}/>,
            ['SERVER_VIEW', 'SERVER_MODIFY'],
        );
        expect(screen.queryByText('Advanced edit (server.json)')).not.toBeInTheDocument();
    });

    it('shows Advanced edit switch with both permissions', () => {
        renderWithPermissions(
            <EditReforgerServerSettingsForm {...defaultProps}/>,
            bothPermissions,
        );
        expect(screen.getByText('Advanced edit (server.json)')).toBeInTheDocument();
    });

    it('shows enable confirm modal when switch clicked', async () => {
        const user = userEvent.setup();
        renderWithPermissions(
            <EditReforgerServerSettingsForm {...defaultProps}/>,
            bothPermissions,
        );
        await user.click(screen.getByRole('switch', {name: 'Advanced edit (server.json)'}));
        expect(screen.getByText(/Enable advanced config editing/i)).toBeInTheDocument();
    });

    it('calls seed endpoint and shows editor on enable confirm', async () => {
        const user = userEvent.setup();
        mockSeedConfigOverride.mockResolvedValue({
            data: {configKey: 'REFORGER_JSON', advanced: true, content: '{"seeded": "content"}'},
        });
        renderWithPermissions(
            <EditReforgerServerSettingsForm {...defaultProps}/>,
            bothPermissions,
        );
        await user.click(screen.getByRole('switch', {name: 'Advanced edit (server.json)'}));
        await user.click(screen.getByRole('button', {name: /enable/i}));
        await waitFor(() => {
            expect(mockSeedConfigOverride).toHaveBeenCalled();
        });
        expect(screen.getByDisplayValue('{"seeded": "content"}')).toBeInTheDocument();
    });

    it('hides config-mapped fields when override exists', () => {
        const serverWithOverride: ReforgerServerDto = {
            ...baseServer,
            configOverrides: [{configKey: 'REFORGER_JSON', advanced: true, content: '{"test": true}'}],
        };
        renderWithPermissions(
            <EditReforgerServerSettingsForm {...defaultProps} server={serverWithOverride}/>,
            bothPermissions,
        );
        expect(screen.queryByLabelText('Max players')).not.toBeInTheDocument();
        expect(screen.queryByLabelText('Password')).not.toBeInTheDocument();
        expect(screen.queryByLabelText('Admin password')).not.toBeInTheDocument();
        expect(screen.queryByText(/BattlEye enabled/i)).not.toBeInTheDocument();
        expect(screen.queryByText(/Third person view enabled/i)).not.toBeInTheDocument();
        expect(screen.getByRole('textbox', {name: 'Server name'})).toBeInTheDocument();
        expect(screen.getByRole('spinbutton', {name: 'Port'})).toBeInTheDocument();
        expect(screen.getByRole('spinbutton', {name: 'Query port'})).toBeInTheDocument();
    });

    it('shows mods notice when advanced', () => {
        const serverWithOverride: ReforgerServerDto = {
            ...baseServer,
            configOverrides: [{configKey: 'REFORGER_JSON', advanced: true, content: '{"test": true}'}],
        };
        renderWithPermissions(
            <EditReforgerServerSettingsForm {...defaultProps} server={serverWithOverride}/>,
            bothPermissions,
        );
        expect(screen.getByText(/Mods are managed through the raw JSON config/)).toBeInTheDocument();
    });

    it('shows read-only advanced notice for unauthorized users', () => {
        const serverWithOverride: ReforgerServerDto = {
            ...baseServer,
            configOverrides: [{configKey: 'REFORGER_JSON', advanced: true, content: '{"test": true}'}],
        };
        renderWithPermissions(
            <EditReforgerServerSettingsForm {...defaultProps} server={serverWithOverride}/>,
            ['SERVER_VIEW', 'SERVER_MODIFY'],
        );
        expect(screen.getByText(/advanced mode/i)).toBeInTheDocument();
        expect(screen.getByText(/managed by an authorized user/i)).toBeInTheDocument();
        expect(screen.queryByText('Advanced edit (server.json)')).not.toBeInTheDocument();
    });

    it('includes override in submit payload', async () => {
        const user = userEvent.setup();
        const onSubmit = jest.fn();
        mockSeedConfigOverride.mockResolvedValue({
            data: {configKey: 'REFORGER_JSON', advanced: true, content: '{"raw": "config"}'},
        });
        renderWithPermissions(
            <EditReforgerServerSettingsForm server={baseServer} onSubmit={onSubmit} onCancel={jest.fn()}/>,
            bothPermissions,
        );
        await user.click(screen.getByRole('switch', {name: 'Advanced edit (server.json)'}));
        await user.click(screen.getByRole('button', {name: /enable/i}));
        await waitFor(() => {
            expect(screen.queryByText(/Enable advanced config editing/i)).not.toBeInTheDocument();
        });
        await user.click(screen.getByRole('button', {name: /submit/i}));
        expect(onSubmit).toHaveBeenCalledWith(
            expect.objectContaining({
                configOverrides: expect.arrayContaining([
                    expect.objectContaining({configKey: 'REFORGER_JSON', advanced: true}),
                ]),
            }),
        );
    });

    it('shows revert modal with clipboard when switch toggled off', async () => {
        const user = userEvent.setup();
        mockSeedConfigOverride.mockResolvedValue({
            data: {configKey: 'REFORGER_JSON', advanced: true, content: 'raw content to copy'},
        });
        renderWithPermissions(
            <EditReforgerServerSettingsForm {...defaultProps}/>,
            bothPermissions,
        );
        await user.click(screen.getByRole('switch', {name: 'Advanced edit (server.json)'}));
        await user.click(screen.getByRole('button', {name: /enable/i}));
        await waitFor(() => screen.getByDisplayValue('raw content to copy'));
        await waitFor(() => {
            expect(screen.queryByText(/Enable advanced config editing/i)).not.toBeInTheDocument();
        });
        await user.click(screen.getByRole('switch', {name: 'Advanced edit (server.json)'}));
        expect(screen.getByText(/Copy raw config to clipboard/i)).toBeInTheDocument();
    });

    it('removes override on revert, shows fields again', async () => {
        const user = userEvent.setup();
        mockSeedConfigOverride.mockResolvedValue({
            data: {configKey: 'REFORGER_JSON', advanced: true, content: '{"raw": "config"}'},
        });
        renderWithPermissions(
            <EditReforgerServerSettingsForm {...defaultProps}/>,
            bothPermissions,
        );
        await user.click(screen.getByRole('switch', {name: 'Advanced edit (server.json)'}));
        await user.click(screen.getByRole('button', {name: /enable/i}));
        await waitFor(() => screen.getByDisplayValue('{"raw": "config"}'));
        await waitFor(() => {
            expect(screen.queryByText(/Enable advanced config editing/i)).not.toBeInTheDocument();
        });
        await user.click(screen.getByRole('switch', {name: 'Advanced edit (server.json)'}));
        await user.click(screen.getByRole('button', {name: /revert/i}));
        await waitFor(() => {
            expect(screen.getByRole('spinbutton', {name: /max players/i})).toBeInTheDocument();
        });
    });
});
