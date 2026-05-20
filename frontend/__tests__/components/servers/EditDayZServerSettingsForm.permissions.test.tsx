import React from 'react';
import '@testing-library/jest-dom';
import {screen} from '@testing-library/react';
import {CustomLaunchParametersInput} from '../../../src/components/servers/CustomLaunchParametersInput';

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

import EditDayZServerSettingsForm from '../../../src/components/servers/EditDayZServerSettingsForm';
import {DayZServerDto} from '../../../src/api/serverModels';
import renderWithPermissions from '../../helpers/renderWithPermissions';
import {ServerType} from "../../../src/api/generated";

const mockCustomLaunchParametersInput = jest.mocked(CustomLaunchParametersInput);

const server: DayZServerDto = {
    name: 'Test',
    port: 2302,
    queryPort: 27016,
    maxPlayers: 32,
    type: ServerType.Dayz,
};

const defaultProps = {
    server,
    onSubmit: jest.fn(),
    onCancel: jest.fn(),
};

beforeEach(() => jest.clearAllMocks());

describe('EditDayZServerSettingsForm permissions', () => {
    it('hides password fields without SERVER_SECRETS_VIEW', () => {
        renderWithPermissions(<EditDayZServerSettingsForm {...defaultProps}/>, ['SERVER_VIEW', 'SERVER_MODIFY']);
        expect(screen.queryByLabelText('Server password')).not.toBeInTheDocument();
        expect(screen.queryByLabelText('Admin password')).not.toBeInTheDocument();
    });

    it('shows password fields with SERVER_SECRETS_VIEW', () => {
        renderWithPermissions(
            <EditDayZServerSettingsForm {...defaultProps}/>,
            ['SERVER_VIEW', 'SERVER_MODIFY', 'SERVER_SECRETS_VIEW'],
        );
        expect(screen.getByLabelText('Server password')).toBeInTheDocument();
        expect(screen.getByLabelText('Admin password')).toBeInTheDocument();
    });

    it('hides submit button without SERVER_MODIFY', () => {
        renderWithPermissions(<EditDayZServerSettingsForm {...defaultProps}/>, ['SERVER_VIEW']);
        expect(screen.queryByRole('button', {name: /submit/i})).not.toBeInTheDocument();
    });

    it('shows submit button with SERVER_MODIFY', () => {
        renderWithPermissions(<EditDayZServerSettingsForm {...defaultProps}/>, ['SERVER_VIEW', 'SERVER_MODIFY']);
        expect(screen.getByRole('button', {name: /submit/i})).toBeInTheDocument();
    });

    it('disables name input without SERVER_MODIFY', () => {
        renderWithPermissions(<EditDayZServerSettingsForm {...defaultProps}/>, ['SERVER_VIEW']);
        expect(screen.getByRole('textbox', {name: /server name/i})).toBeDisabled();
    });

    it('enables name input with SERVER_MODIFY', () => {
        renderWithPermissions(<EditDayZServerSettingsForm {...defaultProps}/>, ['SERVER_VIEW', 'SERVER_MODIFY']);
        expect(screen.getByRole('textbox', {name: /server name/i})).not.toBeDisabled();
    });

    it('passes canModify:false to CustomLaunchParametersInput without SERVER_MODIFY', () => {
        renderWithPermissions(<EditDayZServerSettingsForm {...defaultProps}/>, ['SERVER_VIEW']);
        expect(mockCustomLaunchParametersInput).toHaveBeenCalledWith(
            expect.objectContaining({canModify: false}),
            undefined,
        );
    });

    it('passes canModify:true to CustomLaunchParametersInput with SERVER_MODIFY', () => {
        renderWithPermissions(<EditDayZServerSettingsForm {...defaultProps}/>, ['SERVER_VIEW', 'SERVER_MODIFY']);
        expect(mockCustomLaunchParametersInput).toHaveBeenCalledWith(
            expect.objectContaining({canModify: true}),
            undefined,
        );
    });
});
