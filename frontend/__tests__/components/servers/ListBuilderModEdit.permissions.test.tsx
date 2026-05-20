import React from 'react';
import {screen, fireEvent, waitFor} from '@testing-library/react';
import '@testing-library/jest-dom';
import ListBuilderModEdit from '../../../src/components/servers/ListBuilderModEdit';
import {useServer} from '../../../src/hooks/queries/useServer';
import {useMods} from '../../../src/hooks/queries/useMods';
import {usePresets} from '../../../src/hooks/queries/usePresets';
import {ServerDto, ServerType} from '../../../src/api/generated';
import renderWithPermissions from '../../helpers/renderWithPermissions';

jest.mock('../../../src/hooks/queries/useServer');
jest.mock('../../../src/hooks/queries/useMods');
jest.mock('../../../src/hooks/queries/usePresets');
jest.mock('../../../src/api/client', () => ({
    serversApi: {updateServer: jest.fn()},
}));
jest.mock('react-toastify', () => ({toast: {success: jest.fn(), error: jest.fn()}}));

const mockUseServer = jest.mocked(useServer);
const mockUseMods = jest.mocked(useMods);
const mockUsePresets = jest.mocked(usePresets);

const server: ServerDto = {
    id: 1,
    name: 'Test',
    type: ServerType.Arma3,
    port: 2302,
    queryPort: 2303,
};

beforeEach(() => {
    jest.clearAllMocks();
    mockUseServer.mockReturnValue({data: server, isLoading: false} as any);
    mockUseMods.mockReturnValue({data: [], isLoading: false} as any);
    mockUsePresets.mockReturnValue({data: [], isLoading: false} as any);
});

describe('ListBuilderModEdit permissions', () => {
    it('confirm button disabled without MOD_MODIFY', async () => {
        renderWithPermissions(
            <ListBuilderModEdit server={server} status={null}/>,
            ['MOD_VIEW'],
        );
        fireEvent.click(screen.getByRole('button', {name: /mods/i}));
        await waitFor(() => expect(screen.getByRole('button', {name: /confirm/i})).toBeInTheDocument());
        expect(screen.getByRole('button', {name: /confirm/i})).toBeDisabled();
    });

    it('confirm button enabled with MOD_MODIFY when server not running', async () => {
        renderWithPermissions(
            <ListBuilderModEdit server={server} status={null}/>,
            ['MOD_VIEW', 'MOD_MODIFY'],
        );
        fireEvent.click(screen.getByRole('button', {name: /mods/i}));
        await waitFor(() => expect(screen.getByRole('button', {name: /confirm/i})).toBeInTheDocument());
        expect(screen.getByRole('button', {name: /confirm/i})).not.toBeDisabled();
    });

    it('hides select-all and clear-all without MOD_MODIFY', async () => {
        renderWithPermissions(
            <ListBuilderModEdit server={server} status={null}/>,
            ['MOD_VIEW'],
        );
        fireEvent.click(screen.getByRole('button', {name: /mods/i}));
        await waitFor(() => expect(screen.getByRole('button', {name: /confirm/i})).toBeInTheDocument());
        expect(screen.queryByRole('button', {name: /select all/i})).not.toBeInTheDocument();
        expect(screen.queryByRole('button', {name: /clear all/i})).not.toBeInTheDocument();
    });

    it('shows select-all and clear-all with MOD_MODIFY', async () => {
        renderWithPermissions(
            <ListBuilderModEdit server={server} status={null}/>,
            ['MOD_VIEW', 'MOD_MODIFY'],
        );
        fireEvent.click(screen.getByRole('button', {name: /mods/i}));
        await waitFor(() => expect(screen.getByRole('button', {name: /confirm/i})).toBeInTheDocument());
        expect(screen.getByRole('button', {name: /select all/i})).toBeInTheDocument();
        expect(screen.getByRole('button', {name: /clear all/i})).toBeInTheDocument();
    });
});
