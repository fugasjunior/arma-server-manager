import React from 'react';
import '@testing-library/jest-dom';
import {screen} from '@testing-library/react';
import {useReforgerScenarios} from '../../../src/hooks/queries/useReforgerScenarios';
import {CustomLaunchParametersInput} from '../../../src/components/servers/CustomLaunchParametersInput';

jest.mock('../../../src/hooks/queries/useReforgerScenarios');
jest.mock('../../../src/api/client', () => ({}));
jest.mock('../../../src/components/servers/CustomLaunchParametersInput', () => ({
    CustomLaunchParametersInput: jest.fn(() => null),
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
import {ServerType} from "../../../src/api/generated";

const mockUseReforgerScenarios = jest.mocked(useReforgerScenarios);
const mockCustomLaunchParametersInput = jest.mocked(CustomLaunchParametersInput);

const server: ReforgerServerDto = {
    name: 'Test',
    port: 2001,
    maxPlayers: 32,
    scenarioId: '{ECC61978EDCC2B5A}Missions/23_Campaign.conf',
    type: ServerType.Reforger,
};

const defaultProps = {
    server,
    onSubmit: jest.fn(),
    onCancel: jest.fn(),
};

beforeEach(() => {
    jest.clearAllMocks();
    mockUseReforgerScenarios.mockReturnValue({data: []} as any);
});

describe('EditReforgerServerSettingsForm permissions', () => {
    it('hides password fields without SERVER_SECRETS_VIEW', () => {
        renderWithPermissions(<EditReforgerServerSettingsForm {...defaultProps}/>, ['SERVER_VIEW', 'SERVER_MODIFY']);
        expect(screen.queryByLabelText('Password')).not.toBeInTheDocument();
        expect(screen.queryByLabelText('Admin password')).not.toBeInTheDocument();
    });

    it('shows password fields with SERVER_SECRETS_VIEW', () => {
        renderWithPermissions(
            <EditReforgerServerSettingsForm {...defaultProps}/>,
            ['SERVER_VIEW', 'SERVER_MODIFY', 'SERVER_SECRETS_VIEW'],
        );
        expect(screen.getByLabelText('Password')).toBeInTheDocument();
        expect(screen.getByLabelText('Admin password')).toBeInTheDocument();
    });

    it('hides submit button without SERVER_MODIFY', () => {
        renderWithPermissions(<EditReforgerServerSettingsForm {...defaultProps}/>, ['SERVER_VIEW']);
        expect(screen.queryByRole('button', {name: /submit/i})).not.toBeInTheDocument();
    });

    it('shows submit button with SERVER_MODIFY', () => {
        renderWithPermissions(<EditReforgerServerSettingsForm {...defaultProps}/>, ['SERVER_VIEW', 'SERVER_MODIFY']);
        expect(screen.getByRole('button', {name: /submit/i})).toBeInTheDocument();
    });

    it('disables name input without SERVER_MODIFY', () => {
        renderWithPermissions(<EditReforgerServerSettingsForm {...defaultProps}/>, ['SERVER_VIEW']);
        expect(screen.getByRole('textbox', {name: /server name/i})).toBeDisabled();
    });

    it('enables name input with SERVER_MODIFY', () => {
        renderWithPermissions(<EditReforgerServerSettingsForm {...defaultProps}/>, ['SERVER_VIEW', 'SERVER_MODIFY']);
        expect(screen.getByRole('textbox', {name: /server name/i})).not.toBeDisabled();
    });

    it('passes enabled:false to scenarios hook without SCENARIO_VIEW', () => {
        renderWithPermissions(<EditReforgerServerSettingsForm {...defaultProps}/>, ['SERVER_VIEW', 'SERVER_MODIFY']);
        expect(mockUseReforgerScenarios).toHaveBeenCalledWith(expect.objectContaining({enabled: false}));
    });

    it('passes enabled:true to scenarios hook with SCENARIO_VIEW', () => {
        renderWithPermissions(
            <EditReforgerServerSettingsForm {...defaultProps}/>,
            ['SERVER_VIEW', 'SERVER_MODIFY', 'SCENARIO_VIEW'],
        );
        expect(mockUseReforgerScenarios).toHaveBeenCalledWith(expect.objectContaining({enabled: true}));
    });

    it('passes canModify:false to CustomLaunchParametersInput without SERVER_MODIFY', () => {
        renderWithPermissions(<EditReforgerServerSettingsForm {...defaultProps}/>, ['SERVER_VIEW']);
        expect(mockCustomLaunchParametersInput).toHaveBeenCalledWith(
            expect.objectContaining({canModify: false}),
            undefined,
        );
    });

    it('passes canModify:true to CustomLaunchParametersInput with SERVER_MODIFY', () => {
        renderWithPermissions(<EditReforgerServerSettingsForm {...defaultProps}/>, ['SERVER_VIEW', 'SERVER_MODIFY']);
        expect(mockCustomLaunchParametersInput).toHaveBeenCalledWith(
            expect.objectContaining({canModify: true}),
            undefined,
        );
    });
});
