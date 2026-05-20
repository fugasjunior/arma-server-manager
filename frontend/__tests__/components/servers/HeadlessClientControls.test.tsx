import React from 'react';
import {render, screen, fireEvent, waitFor} from '@testing-library/react';
import '@testing-library/jest-dom';
import {HeadlessClientControls} from '../../../src/components/servers/serverListEntry/HeadlessClientControls.tsx';
import {headlessClientApi} from '../../../src/api/client';
import {ServerInstanceInfoDto} from '../../../src/api/generated';
import renderWithPermissions from '../../helpers/renderWithPermissions';

jest.mock('../../../src/api/client', () => ({
    headlessClientApi: {
        setHeadlessClientsTarget: jest.fn()
    }
}));

const mockedApi = jest.mocked(headlessClientApi);

const aliveStatus = (headlessClientsCount: number): ServerInstanceInfoDto => ({
    alive: true,
    headlessClientsCount,
    playersOnline: 0,
    maxPlayers: 32,
    startedAt: '',
    version: '',
    map: '',
    description: ''
});

beforeEach(() => {
    jest.clearAllMocks();
    mockedApi.setHeadlessClientsTarget.mockResolvedValue({} as any);
});

test('renders running/target display when server is alive', () => {
    render(
        <HeadlessClientControls
            serverId={1}
            serverStatus={aliveStatus(2)}
            targetCount={3}
        />
    );
    expect(screen.getByText('2 / 3')).toBeInTheDocument();
});

test('renders only target count when server is not alive', () => {
    render(
        <HeadlessClientControls
            serverId={1}
            serverStatus={null}
            targetCount={2}
        />
    );
    expect(screen.getByText('2')).toBeInTheDocument();
});

test('minus button is disabled when target is zero', () => {
    renderWithPermissions(
        <HeadlessClientControls
            serverId={1}
            serverStatus={null}
            targetCount={0}
        />,
        ['SERVER_MODIFY']
    );
    expect(screen.getByRole('button', {name: /remove headless client/i})).toBeDisabled();
});

test('minus button is enabled when target is greater than zero', () => {
    renderWithPermissions(
        <HeadlessClientControls
            serverId={1}
            serverStatus={null}
            targetCount={1}
        />,
        ['SERVER_MODIFY']
    );
    expect(screen.getByRole('button', {name: /remove headless client/i})).toBeEnabled();
});

test('plus button is enabled when server is stopped', () => {
    renderWithPermissions(
        <HeadlessClientControls
            serverId={1}
            serverStatus={null}
            targetCount={0}
        />,
        ['SERVER_MODIFY']
    );
    expect(screen.getByRole('button', {name: /add headless client/i})).toBeEnabled();
});

test('plus button calls setHeadlessClientsTarget with target+1', async () => {
    renderWithPermissions(
        <HeadlessClientControls
            serverId={42}
            serverStatus={null}
            targetCount={2}
        />,
        ['SERVER_MODIFY']
    );
    fireEvent.click(screen.getByRole('button', {name: /add headless client/i}));

    await waitFor(() =>
        expect(mockedApi.setHeadlessClientsTarget).toHaveBeenCalledWith({
            id: 42,
            setHeadlessClientsTargetRequest: {targetHeadlessClientsCount: 3}
        })
    );
});

test('minus button calls setHeadlessClientsTarget with target-1', async () => {
    renderWithPermissions(
        <HeadlessClientControls
            serverId={42}
            serverStatus={aliveStatus(2)}
            targetCount={2}
        />,
        ['SERVER_MODIFY']
    );
    fireEvent.click(screen.getByRole('button', {name: /remove headless client/i}));

    await waitFor(() =>
        expect(mockedApi.setHeadlessClientsTarget).toHaveBeenCalledWith({
            id: 42,
            setHeadlessClientsTargetRequest: {targetHeadlessClientsCount: 1}
        })
    );
});

test('reverts optimistic update on API failure', async () => {
    mockedApi.setHeadlessClientsTarget.mockRejectedValue(new Error('Network error'));

    renderWithPermissions(
        <HeadlessClientControls
            serverId={1}
            serverStatus={null}
            targetCount={2}
        />,
        ['SERVER_MODIFY']
    );
    fireEvent.click(screen.getByRole('button', {name: /add headless client/i}));

    // optimistic update shows 3 immediately
    expect(screen.getByText('3')).toBeInTheDocument();

    // after rejection, reverts to original 2
    await waitFor(() => expect(screen.getByText('2')).toBeInTheDocument());
});
