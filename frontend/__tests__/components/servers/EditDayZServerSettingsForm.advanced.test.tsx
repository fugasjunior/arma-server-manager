import React from 'react';
import '@testing-library/jest-dom';
import {screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';

jest.mock('../../../src/api/client', () => ({
    serversApi: {
        seedConfigOverride: jest.fn(),
    },
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

jest.mock('../../../src/hooks/queries/useServerStatus', () => ({
    useServerStatus: jest.fn(() => ({data: undefined})),
}));

import EditDayZServerSettingsForm from '../../../src/components/servers/EditDayZServerSettingsForm';
import {DayZServerDto} from '../../../src/api/serverModels';
import renderWithPermissions from '../../helpers/renderWithPermissions';
import {ServerType} from '../../../src/api/generated';

const {serversApi} = require('../../../src/api/client');
const mockSeedConfigOverride = serversApi.seedConfigOverride;

const baseServer: DayZServerDto = {
    name: 'Test',
    description: '',
    port: 2302,
    queryPort: 27016,
    maxPlayers: 32,
    type: ServerType.Dayz,
    configOverrides: [],
};

const defaultProps = {
    server: baseServer,
    onSubmit: jest.fn(),
    onCancel: jest.fn(),
};

const bothPermissions = ['SERVER_VIEW', 'SERVER_MODIFY', 'ADVANCED_CONFIG_EDIT', 'SERVER_SECRETS_VIEW'];

beforeEach(() => jest.clearAllMocks());

describe('EditDayZServerSettingsForm advanced config', () => {
    it('hides Advanced edit switch without ADVANCED_CONFIG_EDIT or SERVER_SECRETS_VIEW', () => {
        renderWithPermissions(
            <EditDayZServerSettingsForm {...defaultProps}/>,
            ['SERVER_VIEW', 'SERVER_MODIFY'],
        );
        expect(screen.queryByText('Advanced edit (server.cfg)')).not.toBeInTheDocument();
    });

    it('shows Advanced edit switch with both permissions', () => {
        renderWithPermissions(
            <EditDayZServerSettingsForm {...defaultProps}/>,
            bothPermissions,
        );
        expect(screen.getByText('Advanced edit (server.cfg)')).toBeInTheDocument();
    });

    it('shows enable confirm modal when switch clicked', async () => {
        const user = userEvent.setup();
        renderWithPermissions(
            <EditDayZServerSettingsForm {...defaultProps}/>,
            bothPermissions,
        );
        await user.click(screen.getByRole('switch', {name: 'Advanced edit (server.cfg)'}));
        expect(screen.getByText(/Enable advanced config editing/i)).toBeInTheDocument();
    });

    it('calls seed endpoint and shows editor on enable confirm', async () => {
        const user = userEvent.setup();
        mockSeedConfigOverride.mockResolvedValue({
            data: {configKey: 'DAYZ_SERVER_CFG', advanced: true, content: 'seeded content'},
        });
        renderWithPermissions(
            <EditDayZServerSettingsForm {...defaultProps}/>,
            bothPermissions,
        );
        await user.click(screen.getByRole('switch', {name: 'Advanced edit (server.cfg)'}));
        await user.click(screen.getByRole('button', {name: /enable/i}));
        await waitFor(() => {
            expect(mockSeedConfigOverride).toHaveBeenCalled();
        });
        expect(screen.getByDisplayValue('seeded content')).toBeInTheDocument();
    });

    it('hides config-mapped fields when override exists', () => {
        const serverWithOverride: DayZServerDto = {
            ...baseServer,
            configOverrides: [{configKey: 'DAYZ_SERVER_CFG', advanced: true, content: 'raw config'}],
        };
        renderWithPermissions(
            <EditDayZServerSettingsForm {...defaultProps} server={serverWithOverride}/>,
            bothPermissions,
        );
        expect(screen.queryByLabelText('Max players')).not.toBeInTheDocument();
        expect(screen.queryByLabelText('Server password')).not.toBeInTheDocument();
        expect(screen.getByRole('textbox', {name: 'Server name'})).toBeInTheDocument();
        expect(screen.getByRole('spinbutton', {name: 'Port'})).toBeInTheDocument();
        expect(screen.getByRole('spinbutton', {name: 'Query port'})).toBeInTheDocument();
    });

    it('shows read-only notice for unauthorized users when advanced', () => {
        const serverWithOverride: DayZServerDto = {
            ...baseServer,
            configOverrides: [{configKey: 'DAYZ_SERVER_CFG', advanced: true, content: 'raw config'}],
        };
        renderWithPermissions(
            <EditDayZServerSettingsForm {...defaultProps} server={serverWithOverride}/>,
            ['SERVER_VIEW', 'SERVER_MODIFY'],
        );
        expect(screen.getByText(/advanced mode/i)).toBeInTheDocument();
        expect(screen.getByText(/managed by an authorized user/i)).toBeInTheDocument();
        expect(screen.queryByText('Advanced edit (server.cfg)')).not.toBeInTheDocument();
    });

    it('includes override in submit payload', async () => {
        const user = userEvent.setup();
        const onSubmit = jest.fn();
        mockSeedConfigOverride.mockResolvedValue({
            data: {configKey: 'DAYZ_SERVER_CFG', advanced: true, content: 'raw config'},
        });
        renderWithPermissions(
            <EditDayZServerSettingsForm server={baseServer} onSubmit={onSubmit} onCancel={jest.fn()}/>,
            bothPermissions,
        );
        await user.click(screen.getByRole('switch', {name: 'Advanced edit (server.cfg)'}));
        await user.click(screen.getByRole('button', {name: /enable/i}));
        await waitFor(() => {
            expect(screen.queryByText(/Enable advanced config editing/i)).not.toBeInTheDocument();
        });
        await user.click(screen.getByRole('button', {name: /submit/i}));
        expect(onSubmit).toHaveBeenCalledWith(
            expect.objectContaining({
                configOverrides: expect.arrayContaining([
                    expect.objectContaining({configKey: 'DAYZ_SERVER_CFG', advanced: true}),
                ]),
            }),
        );
    });

    it('shows revert modal with clipboard when switch toggled off', async () => {
        const user = userEvent.setup();
        mockSeedConfigOverride.mockResolvedValue({
            data: {configKey: 'DAYZ_SERVER_CFG', advanced: true, content: 'raw content to copy'},
        });
        renderWithPermissions(
            <EditDayZServerSettingsForm {...defaultProps}/>,
            bothPermissions,
        );
        await user.click(screen.getByRole('switch', {name: 'Advanced edit (server.cfg)'}));
        await user.click(screen.getByRole('button', {name: /enable/i}));
        await waitFor(() => screen.getByDisplayValue('raw content to copy'));
        await waitFor(() => {
            expect(screen.queryByText(/Enable advanced config editing/i)).not.toBeInTheDocument();
        });
        await user.click(screen.getByRole('switch', {name: 'Advanced edit (server.cfg)'}));
        expect(screen.getByText(/Copy raw config to clipboard/i)).toBeInTheDocument();
    });

    it('removes override on revert, shows fields again', async () => {
        const user = userEvent.setup();
        mockSeedConfigOverride.mockResolvedValue({
            data: {configKey: 'DAYZ_SERVER_CFG', advanced: true, content: 'raw config'},
        });
        renderWithPermissions(
            <EditDayZServerSettingsForm {...defaultProps}/>,
            bothPermissions,
        );
        await user.click(screen.getByRole('switch', {name: 'Advanced edit (server.cfg)'}));
        await user.click(screen.getByRole('button', {name: /enable/i}));
        await waitFor(() => screen.getByDisplayValue('raw config'));
        await waitFor(() => {
            expect(screen.queryByText(/Enable advanced config editing/i)).not.toBeInTheDocument();
        });
        await user.click(screen.getByRole('switch', {name: 'Advanced edit (server.cfg)'}));
        await user.click(screen.getByRole('button', {name: /revert/i}));
        await waitFor(() => {
            expect(screen.getByRole('spinbutton', {name: /max players/i})).toBeInTheDocument();
        });
    });
});
