import React from 'react';
import {screen} from '@testing-library/react';

jest.mock('../../../src/api/client', () => ({}));
jest.mock('../../../src/config', () => ({
    __esModule: true,
    default: {
        apiUrl: '/api',
        dateFormat: {year: 'numeric', month: '2-digit', day: 'numeric', hour: '2-digit', minute: '2-digit'},
        version: 'test',
    },
}));
import '@testing-library/jest-dom';
import ServerInstallationItem from '../../../src/components/dashboard/ServerInstallationItem';
import {
    InstallationBranch,
    InstallationStatus,
    ServerInstallationDto,
    ServerType,
} from '../../../src/api/generated';
import renderWithPermissions from '../../helpers/renderWithPermissions';

const idleNotInstalled: ServerInstallationDto = {
    type: ServerType.Arma3,
    installationStatus: undefined,
    lastUpdatedAt: undefined,
    branch: InstallationBranch.Public,
    availableBranches: new Set([InstallationBranch.Public]),
    errorStatus: undefined,
};

const installed: ServerInstallationDto = {
    ...idleNotInstalled,
    lastUpdatedAt: '2024-01-01T00:00:00Z',
    version: '2.14',
};

const installing: ServerInstallationDto = {
    ...idleNotInstalled,
    installationStatus: InstallationStatus.InstallationInProgress,
};

const defaultProps = {
    onUpdateClicked: jest.fn(),
    onBranchChanged: jest.fn(),
    onUninstallConfirmed: jest.fn(),
    steamCmdItemInfo: undefined,
};

beforeEach(() => {
    jest.clearAllMocks();
});

describe('ServerInstallationItem permissions', () => {
    it('hides install button without INSTALL_MANAGE when idle', () => {
        renderWithPermissions(
            <ServerInstallationItem installation={idleNotInstalled} {...defaultProps}/>,
            ['INSTALL_VIEW'],
        );
        expect(screen.queryByTestId('install-update-btn-ARMA3')).not.toBeInTheDocument();
    });

    it('shows install button with INSTALL_MANAGE when idle', () => {
        renderWithPermissions(
            <ServerInstallationItem installation={idleNotInstalled} {...defaultProps}/>,
            ['INSTALL_VIEW', 'INSTALL_MANAGE'],
        );
        expect(screen.getByTestId('install-update-btn-ARMA3')).toBeInTheDocument();
    });

    it('shows disabled progress button during install without INSTALL_MANAGE', () => {
        renderWithPermissions(
            <ServerInstallationItem installation={installing} {...defaultProps}/>,
            ['INSTALL_VIEW'],
        );
        expect(screen.getByRole('button', {name: /installing\.\.\.|updating\.\.\./i})).toBeDisabled();
    });

    it('hides more-options button without INSTALL_MANAGE', () => {
        renderWithPermissions(
            <ServerInstallationItem installation={installed} {...defaultProps}/>,
            ['INSTALL_VIEW'],
        );
        expect(screen.queryByTestId('install-menu-btn-ARMA3')).not.toBeInTheDocument();
    });

    it('shows more-options button with INSTALL_MANAGE', () => {
        renderWithPermissions(
            <ServerInstallationItem installation={installed} {...defaultProps}/>,
            ['INSTALL_VIEW', 'INSTALL_MANAGE'],
        );
        expect(screen.getByTestId('install-menu-btn-ARMA3')).toBeInTheDocument();
    });
});
