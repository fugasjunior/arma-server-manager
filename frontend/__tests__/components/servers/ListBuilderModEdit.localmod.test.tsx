import React from 'react';
import {screen, fireEvent, waitFor} from '@testing-library/react';
import '@testing-library/jest-dom';
import ListBuilderModEdit from '../../../src/components/servers/ListBuilderModEdit';
import {useServer} from '../../../src/hooks/queries/useServer';
import {useMods} from '../../../src/hooks/queries/useMods';
import {useLocalMods} from '../../../src/hooks/queries/useLocalMods';
import {usePresets} from '../../../src/hooks/queries/usePresets';
import {ServerDto, ServerType} from '../../../src/api/generated';
import renderWithPermissions from '../../helpers/renderWithPermissions';

jest.mock('../../../src/hooks/queries/useServer');
jest.mock('../../../src/hooks/queries/useMods');
jest.mock('../../../src/hooks/queries/useLocalMods');
jest.mock('../../../src/hooks/queries/usePresets');
jest.mock('../../../src/api/client', () => ({
    serversApi: {updateServer: jest.fn().mockResolvedValue({data: {}})},
}));
jest.mock('react-toastify', () => ({toast: {success: jest.fn(), error: jest.fn()}}));

const mockUseServer = jest.mocked(useServer);
const mockUseMods = jest.mocked(useMods);
const mockUseLocalMods = jest.mocked(useLocalMods);
const mockUsePresets = jest.mocked(usePresets);

const server: ServerDto = {
    id: 1,
    name: 'Test',
    type: ServerType.Arma3,
    port: 2302,
    queryPort: 2303,
};

const permissions = ['MOD_VIEW', 'MOD_MODIFY'];

beforeEach(() => {
    jest.clearAllMocks();
    mockUseServer.mockReturnValue({data: server, isLoading: false} as any);
    mockUseMods.mockReturnValue({data: [], isLoading: false} as any);
    mockUseLocalMods.mockReturnValue({data: [], isLoading: false} as any);
    mockUsePresets.mockReturnValue({data: [], isLoading: false} as any);
});

describe('ListBuilderModEdit local mods', () => {
    it('shows local mod with (LOCAL) subtitle in available list', async () => {
        mockUseLocalMods.mockReturnValue({
            data: [{id: 1, name: '@MyLocalMod', serverType: ServerType.Arma3, serverOnly: false}],
            isLoading: false,
        } as any);

        renderWithPermissions(
            <ListBuilderModEdit server={server} status={null}/>,
            permissions,
        );
        fireEvent.click(screen.getByRole('button', {name: /mods/i}));

        await waitFor(() => expect(screen.getByText('@MyLocalMod')).toBeInTheDocument());
        expect(screen.getByText('(LOCAL)')).toBeInTheDocument();
    });

    it('shows workshop and local mods together in available list', async () => {
        mockUseMods.mockReturnValue({
            data: [{id: 111, name: 'WorkshopMod', serverType: 'ARMA3'}],
            isLoading: false,
        } as any);
        mockUseLocalMods.mockReturnValue({
            data: [{id: 1, name: '@LocalMod', serverType: ServerType.Arma3, serverOnly: false}],
            isLoading: false,
        } as any);

        renderWithPermissions(
            <ListBuilderModEdit server={server} status={null}/>,
            permissions,
        );
        fireEvent.click(screen.getByRole('button', {name: /mods/i}));

        await waitFor(() => expect(screen.getByText('WorkshopMod')).toBeInTheDocument());
        expect(screen.getByText('@LocalMod')).toBeInTheDocument();
        expect(screen.getByText('(LOCAL)')).toBeInTheDocument();
    });

    it('pre-selects active local mods from server data', async () => {
        const serverWithLocalMod = {
            ...server,
            activeMods: [],
            activeLocalMods: [{id: 1, name: '@ActiveLocal'}],
        };
        mockUseServer.mockReturnValue({data: serverWithLocalMod, isLoading: false} as any);

        renderWithPermissions(
            <ListBuilderModEdit server={server} status={null}/>,
            permissions,
        );
        fireEvent.click(screen.getByRole('button', {name: /mods/i}));

        await waitFor(() => expect(screen.getByText('@ActiveLocal')).toBeInTheDocument());
        // Should appear in the selected (right) list
        const selectedList = screen.getAllByText('@ActiveLocal');
        expect(selectedList.length).toBeGreaterThan(0);
    });

    it('sends activeLocalMods separately when saving', async () => {
        const {serversApi} = require('../../../src/api/client');
        const serverWithLocalMod = {
            ...server,
            activeMods: [],
            activeLocalMods: [{id: 5, name: '@SelectedLocal'}],
        };
        mockUseServer.mockReturnValue({data: serverWithLocalMod, isLoading: false} as any);

        renderWithPermissions(
            <ListBuilderModEdit server={server} status={null}/>,
            permissions,
        );
        fireEvent.click(screen.getByRole('button', {name: /mods/i}));
        await waitFor(() => expect(screen.getByRole('button', {name: /confirm/i})).not.toBeDisabled());

        fireEvent.click(screen.getByRole('button', {name: /confirm/i}));

        await waitFor(() => expect(serversApi.updateServer).toHaveBeenCalledWith(
            expect.objectContaining({
                serverDto: expect.objectContaining({
                    activeLocalMods: [expect.objectContaining({id: 5, name: '@SelectedLocal'})],
                }),
            })
        ));
    });
});
