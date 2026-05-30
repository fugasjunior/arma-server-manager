import React from 'react';
import {screen, fireEvent, waitFor} from '@testing-library/react';
import '@testing-library/jest-dom';
import ReforgerModEdit from '../../../src/components/servers/ReforgerModEdit';
import {useServer} from '../../../src/hooks/queries/useServer';
import {ReforgerServerDto, ServerType} from '../../../src/api/generated';
import renderWithPermissions from '../../helpers/renderWithPermissions';

jest.mock('../../../src/hooks/queries/useServer');
jest.mock('../../../src/api/client', () => ({
    serversApi: {updateServer: jest.fn()},
}));
jest.mock('react-toastify', () => ({toast: {success: jest.fn(), error: jest.fn()}}));

const mockUseServer = jest.mocked(useServer);

const server: ReforgerServerDto = {
    id: 1,
    name: 'Test',
    type: ServerType.Reforger,
    port: 2302,
    queryPort: 2303,
    activeMods: [{id: 'mod-1', name: 'Mod One'}],
};

beforeEach(() => {
    jest.clearAllMocks();
    mockUseServer.mockReturnValue({data: server, isLoading: false} as any);
});

const openModal = () => {
    fireEvent.click(screen.getByRole('button', {name: /mods/i}));
};

describe('ReforgerModEdit permissions', () => {
    it('disables mod ID input without MOD_MODIFY', async () => {
        renderWithPermissions(
            <ReforgerModEdit server={server} serverStatus={null}/>,
            ['MOD_VIEW'],
        );
        openModal();
        await waitFor(() => expect(screen.getByLabelText(/mod id/i)).toBeInTheDocument());
        expect(screen.getByLabelText(/mod id/i)).toBeDisabled();
    });

    it('enables mod ID input with MOD_MODIFY', async () => {
        renderWithPermissions(
            <ReforgerModEdit server={server} serverStatus={null}/>,
            ['MOD_VIEW', 'MOD_MODIFY'],
        );
        openModal();
        await waitFor(() => expect(screen.getByLabelText(/mod id/i)).toBeInTheDocument());
        expect(screen.getByLabelText(/mod id/i)).not.toBeDisabled();
    });

    it('hides delete buttons without MOD_MODIFY', async () => {
        renderWithPermissions(
            <ReforgerModEdit server={server} serverStatus={null}/>,
            ['MOD_VIEW'],
        );
        openModal();
        await waitFor(() => expect(screen.getByText('Mod One')).toBeInTheDocument());
        expect(screen.queryByRole('button', {name: /delete/i})).not.toBeInTheDocument();
    });

    it('shows delete buttons with MOD_MODIFY', async () => {
        renderWithPermissions(
            <ReforgerModEdit server={server} serverStatus={null}/>,
            ['MOD_VIEW', 'MOD_MODIFY'],
        );
        openModal();
        await waitFor(() => expect(screen.getByText('Mod One')).toBeInTheDocument());
        expect(screen.getByRole('button', {name: /delete/i})).toBeInTheDocument();
    });

    it('disables confirm button without MOD_MODIFY', async () => {
        renderWithPermissions(
            <ReforgerModEdit server={server} serverStatus={null}/>,
            ['MOD_VIEW'],
        );
        openModal();
        await waitFor(() => expect(screen.getByRole('button', {name: /confirm/i})).toBeInTheDocument());
        expect(screen.getByRole('button', {name: /confirm/i})).toBeDisabled();
    });

    it('enables confirm button with MOD_MODIFY when server not running', async () => {
        renderWithPermissions(
            <ReforgerModEdit server={server} serverStatus={null}/>,
            ['MOD_VIEW', 'MOD_MODIFY'],
        );
        openModal();
        await waitFor(() => expect(screen.getByRole('button', {name: /confirm/i})).toBeInTheDocument());
        expect(screen.getByRole('button', {name: /confirm/i})).not.toBeDisabled();
    });
});
