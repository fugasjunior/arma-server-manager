import React from 'react';
import {screen} from '@testing-library/react';

jest.mock('../../../src/api/client', () => ({}));
import '@testing-library/jest-dom';
import {ServerBranchSelect} from '../../../src/components/dashboard/ServerBranchSelect';
import {InstallationBranch, ServerInstallationDto} from '../../../src/api/generated';
import renderWithPermissions from '../../helpers/renderWithPermissions';

const installation: ServerInstallationDto = {
    branch: InstallationBranch.Public,
    availableBranches: new Set([InstallationBranch.Public, InstallationBranch.Profiling]),
    installationStatus: undefined,
};

describe('ServerBranchSelect permissions', () => {
    it('disables select without INSTALL_MANAGE', () => {
        renderWithPermissions(
            <ServerBranchSelect installation={installation} onChange={jest.fn()}/>,
            [],
        );
        expect(screen.getByRole('combobox')).toHaveAttribute('aria-disabled', 'true');
    });

    it('enables select with INSTALL_MANAGE', () => {
        renderWithPermissions(
            <ServerBranchSelect installation={installation} onChange={jest.fn()}/>,
            ['INSTALL_MANAGE'],
        );
        expect(screen.getByRole('combobox')).not.toHaveAttribute('aria-disabled', 'true');
    });
});
