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
jest.mock('../../../src/components/servers/difficulty/Arma3DifficultySettingsForm', () => {
    const AdvancedConfigToggle = require('../../../src/components/servers/AdvancedConfigToggle').default;
    const AdvancedConfigSection = require('../../../src/components/servers/AdvancedConfigSection').default;
    return {
        __esModule: true,
        default: ({override, onOverrideChange, serverDraft}: any) => (
            <>
                <AdvancedConfigToggle
                    configKey="ARMA3_PROFILE"
                    switchLabel="Edit raw difficulty profile"
                    enableDialogText="Replace difficulty form"
                    serverDraft={serverDraft}
                    override={override}
                    onOverrideChange={onOverrideChange}
                />
                {!override
                    ? <div data-testid="difficulty-form">Difficulty Form</div>
                    : <AdvancedConfigSection configKey="ARMA3_PROFILE" label="server.armaprofile" override={override} onOverrideChange={onOverrideChange}/>
                }
            </>
        ),
    };
});
jest.mock('../../../src/components/servers/Arma3NetworkSettingsForm', () => {
    const AdvancedConfigToggle = require('../../../src/components/servers/AdvancedConfigToggle').default;
    const AdvancedConfigSection = require('../../../src/components/servers/AdvancedConfigSection').default;
    return {
        __esModule: true,
        default: ({override, onOverrideChange, serverDraft}: any) => (
            <>
                <AdvancedConfigToggle
                    configKey="ARMA3_NETWORK_CFG"
                    switchLabel="Edit raw network config"
                    enableDialogText="Replace network form"
                    serverDraft={serverDraft}
                    override={override}
                    onOverrideChange={onOverrideChange}
                />
                {!override
                    ? <div data-testid="network-form">Network Form</div>
                    : <AdvancedConfigSection configKey="ARMA3_NETWORK_CFG" label="basic.cfg" override={override} onOverrideChange={onOverrideChange}/>
                }
            </>
        ),
    };
});
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

import EditArma3ServerSettingsForm from '../../../src/components/servers/EditArma3ServerSettingsForm';
import {Arma3ServerDto} from '../../../src/api/serverModels';
import renderWithPermissions from '../../helpers/renderWithPermissions';
import {ServerType} from '../../../src/api/generated';

const {serversApi} = require('../../../src/api/client');
const mockSeedConfigOverride = serversApi.seedConfigOverride;

const baseServer: Arma3ServerDto = {
    name: 'Test',
    port: 2302,
    maxPlayers: 32,
    type: ServerType.Arma3,
    configOverrides: [],
};

const defaultProps = {
    server: baseServer,
    onSubmit: jest.fn(),
    onCancel: jest.fn(),
};

const bothPermissions = ['SERVER_VIEW', 'SERVER_MODIFY', 'ADVANCED_CONFIG_EDIT', 'SERVER_SECRETS_VIEW'];

beforeEach(() => jest.clearAllMocks());

describe('EditArma3ServerSettingsForm advanced config — server.cfg', () => {
    it('hides server.cfg toggle without ADVANCED_CONFIG_EDIT or SERVER_SECRETS_VIEW', () => {
        renderWithPermissions(
            <EditArma3ServerSettingsForm {...defaultProps}/>,
            ['SERVER_VIEW', 'SERVER_MODIFY'],
        );
        expect(screen.queryByText('Advanced edit (server.cfg)')).not.toBeInTheDocument();
    });

    it('shows server.cfg toggle with both permissions', () => {
        renderWithPermissions(
            <EditArma3ServerSettingsForm {...defaultProps}/>,
            bothPermissions,
        );
        expect(screen.getByText('Advanced edit (server.cfg)')).toBeInTheDocument();
    });

    it('shows enable confirm modal when server.cfg switch clicked', async () => {
        const user = userEvent.setup();
        renderWithPermissions(
            <EditArma3ServerSettingsForm {...defaultProps}/>,
            bothPermissions,
        );
        await user.click(screen.getByRole('switch', {name: 'Advanced edit (server.cfg)'}));
        expect(screen.getByText(/Enable advanced config editing/i)).toBeInTheDocument();
    });

    it('calls seed and shows editor on server.cfg enable confirm', async () => {
        const user = userEvent.setup();
        mockSeedConfigOverride.mockResolvedValue({
            data: {configKey: 'ARMA3_SERVER_CFG', advanced: true, content: 'seeded cfg'},
        });
        renderWithPermissions(
            <EditArma3ServerSettingsForm {...defaultProps}/>,
            bothPermissions,
        );
        await user.click(screen.getByRole('switch', {name: 'Advanced edit (server.cfg)'}));
        await user.click(screen.getByRole('button', {name: /enable/i}));
        await waitFor(() => {
            expect(mockSeedConfigOverride).toHaveBeenCalledWith(
                expect.objectContaining({
                    seedConfigOverrideRequest: expect.objectContaining({configKey: 'ARMA3_SERVER_CFG'}),
                }),
            );
        });
        expect(screen.getByDisplayValue('seeded cfg')).toBeInTheDocument();
    });

    it('hides config-mapped fields and keeps app-level fields when server.cfg override exists', () => {
        const serverWithOverride: Arma3ServerDto = {
            ...baseServer,
            configOverrides: [{configKey: 'ARMA3_SERVER_CFG', advanced: true, content: 'raw cfg'}],
        };
        renderWithPermissions(
            <EditArma3ServerSettingsForm {...defaultProps} server={serverWithOverride}/>,
            bothPermissions,
        );
        expect(screen.queryByLabelText('Max players')).not.toBeInTheDocument();
        expect(screen.queryByLabelText('Server password')).not.toBeInTheDocument();
        expect(screen.queryByLabelText('BattlEye enabled')).not.toBeInTheDocument();
        expect(screen.getByRole('textbox', {name: 'Server name'})).toBeInTheDocument();
        expect(screen.getByRole('spinbutton', {name: 'Port'})).toBeInTheDocument();
    });

    it('shows read-only notice for unauthorized users when server.cfg is advanced', () => {
        const serverWithOverride: Arma3ServerDto = {
            ...baseServer,
            configOverrides: [{configKey: 'ARMA3_SERVER_CFG', advanced: true, content: 'raw cfg'}],
        };
        renderWithPermissions(
            <EditArma3ServerSettingsForm {...defaultProps} server={serverWithOverride}/>,
            ['SERVER_VIEW', 'SERVER_MODIFY'],
        );
        expect(screen.getByText(/advanced mode/i)).toBeInTheDocument();
        expect(screen.getByText(/managed by an authorized user/i)).toBeInTheDocument();
        expect(screen.queryByText('Advanced edit (server.cfg)')).not.toBeInTheDocument();
    });

    it('includes server.cfg override in submit payload', async () => {
        const user = userEvent.setup();
        const onSubmit = jest.fn();
        mockSeedConfigOverride.mockResolvedValue({
            data: {configKey: 'ARMA3_SERVER_CFG', advanced: true, content: 'raw cfg'},
        });
        renderWithPermissions(
            <EditArma3ServerSettingsForm server={baseServer} onSubmit={onSubmit} onCancel={jest.fn()}/>,
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
                    expect.objectContaining({configKey: 'ARMA3_SERVER_CFG', advanced: true}),
                ]),
            }),
        );
    });

    it('shows revert modal when server.cfg switch toggled off', async () => {
        const user = userEvent.setup();
        mockSeedConfigOverride.mockResolvedValue({
            data: {configKey: 'ARMA3_SERVER_CFG', advanced: true, content: 'raw cfg'},
        });
        renderWithPermissions(
            <EditArma3ServerSettingsForm {...defaultProps}/>,
            bothPermissions,
        );
        await user.click(screen.getByRole('switch', {name: 'Advanced edit (server.cfg)'}));
        await user.click(screen.getByRole('button', {name: /enable/i}));
        await waitFor(() => screen.getByDisplayValue('raw cfg'));
        await waitFor(() => {
            expect(screen.queryByText(/Enable advanced config editing/i)).not.toBeInTheDocument();
        });
        await user.click(screen.getByRole('switch', {name: 'Advanced edit (server.cfg)'}));
        expect(screen.getByText(/Copy raw config to clipboard/i)).toBeInTheDocument();
    });

    it('removes server.cfg override on revert, shows fields again', async () => {
        const user = userEvent.setup();
        mockSeedConfigOverride.mockResolvedValue({
            data: {configKey: 'ARMA3_SERVER_CFG', advanced: true, content: 'raw cfg'},
        });
        renderWithPermissions(
            <EditArma3ServerSettingsForm {...defaultProps}/>,
            bothPermissions,
        );
        await user.click(screen.getByRole('switch', {name: 'Advanced edit (server.cfg)'}));
        await user.click(screen.getByRole('button', {name: /enable/i}));
        await waitFor(() => screen.getByDisplayValue('raw cfg'));
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

describe('EditArma3ServerSettingsForm advanced config — difficulty profile', () => {
    it('shows difficulty toggle with both permissions', () => {
        renderWithPermissions(
            <EditArma3ServerSettingsForm {...defaultProps}/>,
            bothPermissions,
        );
        expect(screen.getByText('Edit raw difficulty profile')).toBeInTheDocument();
    });

    it('hides difficulty form and shows editor when ARMA3_PROFILE override exists', () => {
        const serverWithOverride: Arma3ServerDto = {
            ...baseServer,
            configOverrides: [{configKey: 'ARMA3_PROFILE', advanced: true, content: 'raw profile'}],
        };
        renderWithPermissions(
            <EditArma3ServerSettingsForm {...defaultProps} server={serverWithOverride}/>,
            bothPermissions,
        );
        expect(screen.queryByTestId('difficulty-form')).not.toBeInTheDocument();
        expect(screen.getByDisplayValue('raw profile')).toBeInTheDocument();
    });

    it('seeds difficulty profile on enable', async () => {
        const user = userEvent.setup();
        mockSeedConfigOverride.mockResolvedValue({
            data: {configKey: 'ARMA3_PROFILE', advanced: true, content: 'seeded profile'},
        });
        renderWithPermissions(
            <EditArma3ServerSettingsForm {...defaultProps}/>,
            bothPermissions,
        );
        await user.click(screen.getByRole('switch', {name: 'Edit raw difficulty profile'}));
        await user.click(screen.getByRole('button', {name: /enable/i}));
        await waitFor(() => {
            expect(mockSeedConfigOverride).toHaveBeenCalledWith(
                expect.objectContaining({
                    seedConfigOverrideRequest: expect.objectContaining({configKey: 'ARMA3_PROFILE'}),
                }),
            );
        });
        expect(screen.getByDisplayValue('seeded profile')).toBeInTheDocument();
    });
});

describe('EditArma3ServerSettingsForm advanced config — network config', () => {
    it('shows network toggle with both permissions', () => {
        renderWithPermissions(
            <EditArma3ServerSettingsForm {...defaultProps}/>,
            bothPermissions,
        );
        expect(screen.getByText('Edit raw network config')).toBeInTheDocument();
    });

    it('hides network form and shows editor when ARMA3_NETWORK_CFG override exists', () => {
        const serverWithOverride: Arma3ServerDto = {
            ...baseServer,
            configOverrides: [{configKey: 'ARMA3_NETWORK_CFG', advanced: true, content: 'raw network'}],
        };
        renderWithPermissions(
            <EditArma3ServerSettingsForm {...defaultProps} server={serverWithOverride}/>,
            bothPermissions,
        );
        expect(screen.queryByTestId('network-form')).not.toBeInTheDocument();
        expect(screen.getByDisplayValue('raw network')).toBeInTheDocument();
    });

    it('seeds network config on enable', async () => {
        const user = userEvent.setup();
        mockSeedConfigOverride.mockResolvedValue({
            data: {configKey: 'ARMA3_NETWORK_CFG', advanced: true, content: 'seeded network'},
        });
        renderWithPermissions(
            <EditArma3ServerSettingsForm {...defaultProps}/>,
            bothPermissions,
        );
        await user.click(screen.getByRole('switch', {name: 'Edit raw network config'}));
        await user.click(screen.getByRole('button', {name: /enable/i}));
        await waitFor(() => {
            expect(mockSeedConfigOverride).toHaveBeenCalledWith(
                expect.objectContaining({
                    seedConfigOverrideRequest: expect.objectContaining({configKey: 'ARMA3_NETWORK_CFG'}),
                }),
            );
        });
        expect(screen.getByDisplayValue('seeded network')).toBeInTheDocument();
    });
});

describe('EditArma3ServerSettingsForm — toggles are independent', () => {
    it('enabling server.cfg does not hide difficulty or network sections', async () => {
        const user = userEvent.setup();
        mockSeedConfigOverride.mockResolvedValue({
            data: {configKey: 'ARMA3_SERVER_CFG', advanced: true, content: 'raw cfg'},
        });
        renderWithPermissions(
            <EditArma3ServerSettingsForm {...defaultProps}/>,
            bothPermissions,
        );
        await user.click(screen.getByRole('switch', {name: 'Advanced edit (server.cfg)'}));
        await user.click(screen.getByRole('button', {name: /enable/i}));
        await waitFor(() => screen.getByDisplayValue('raw cfg'));
        expect(screen.getByTestId('difficulty-form')).toBeInTheDocument();
        expect(screen.getByTestId('network-form')).toBeInTheDocument();
    });
});
