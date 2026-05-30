import React from 'react';
import {screen} from '@testing-library/react';

jest.mock('../../src/api/client', () => ({}));
jest.mock('../../src/config', () => ({
    __esModule: true,
    default: {
        apiUrl: '/api',
        dateFormat: {year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit'},
        version: 'test',
    },
}));
jest.mock('../../src/hooks/queries/useAdditionalServers');

import '@testing-library/jest-dom';
import AdditionalServersPage from '../../src/pages/AdditionalServersPage';
import {AdditionalServerDto} from '../../src/api/generated';
import renderWithPermissions from '../helpers/renderWithPermissions';
import * as useAdditionalServersModule from '../../src/hooks/queries/useAdditionalServers';

const mockServer: AdditionalServerDto = {
    id: 1,
    name: 'Minecraft Server',
    imageUrl: 'http://example.com/minecraft.png',
    alive: true,
    startedAt: '2024-01-01T12:00:00Z',
};

beforeEach(() => {
    jest.clearAllMocks();
});

describe('AdditionalServersPage permissions', () => {
    it('does not poll API without ADDITIONAL_SERVER_VIEW', () => {
        const mockUseAdditionalServers = jest.spyOn(useAdditionalServersModule, 'useAdditionalServers');
        mockUseAdditionalServers.mockReturnValue({
            data: [],
            isLoading: false,
        } as any);

        renderWithPermissions(
            <AdditionalServersPage/>,
            [],
        );

        expect(mockUseAdditionalServers).toHaveBeenCalledWith({enabled: false});
    });

    it('polls API with ADDITIONAL_SERVER_VIEW', () => {
        const mockUseAdditionalServers = jest.spyOn(useAdditionalServersModule, 'useAdditionalServers');
        mockUseAdditionalServers.mockReturnValue({
            data: [],
            isLoading: false,
        } as any);

        renderWithPermissions(
            <AdditionalServersPage/>,
            ['ADDITIONAL_SERVER_VIEW'],
        );

        expect(mockUseAdditionalServers).toHaveBeenCalledWith({enabled: true});
    });

    it('hides server list without ADDITIONAL_SERVER_VIEW', () => {
        const mockUseAdditionalServers = jest.spyOn(useAdditionalServersModule, 'useAdditionalServers');
        mockUseAdditionalServers.mockReturnValue({
            data: [mockServer],
            isLoading: false,
        } as any);

        renderWithPermissions(
            <AdditionalServersPage/>,
            [],
        );

        expect(screen.queryByText('Minecraft Server')).not.toBeInTheDocument();
    });

    it('shows server list with ADDITIONAL_SERVER_VIEW', () => {
        const mockUseAdditionalServers = jest.spyOn(useAdditionalServersModule, 'useAdditionalServers');
        mockUseAdditionalServers.mockReturnValue({
            data: [mockServer],
            isLoading: false,
        } as any);

        renderWithPermissions(
            <AdditionalServersPage/>,
            ['ADDITIONAL_SERVER_VIEW'],
        );

        expect(screen.getByText('Minecraft Server')).toBeInTheDocument();
    });

    it('hides Stop button without ADDITIONAL_SERVER_OPERATE', () => {
        const mockUseAdditionalServers = jest.spyOn(useAdditionalServersModule, 'useAdditionalServers');
        mockUseAdditionalServers.mockReturnValue({
            data: [mockServer],
            isLoading: false,
        } as any);

        renderWithPermissions(
            <AdditionalServersPage/>,
            ['ADDITIONAL_SERVER_VIEW'],
        );

        expect(screen.queryByRole('button', {name: 'Stop'})).not.toBeInTheDocument();
    });

    it('shows Stop button with ADDITIONAL_SERVER_OPERATE', () => {
        const mockUseAdditionalServers = jest.spyOn(useAdditionalServersModule, 'useAdditionalServers');
        mockUseAdditionalServers.mockReturnValue({
            data: [mockServer],
            isLoading: false,
        } as any);

        renderWithPermissions(
            <AdditionalServersPage/>,
            ['ADDITIONAL_SERVER_VIEW', 'ADDITIONAL_SERVER_OPERATE'],
        );

        expect(screen.getByRole('button', {name: 'Stop'})).toBeInTheDocument();
    });

    it('hides Start button without ADDITIONAL_SERVER_OPERATE', () => {
        const notStartedServer: AdditionalServerDto = {
            ...mockServer,
            startedAt: undefined,
            alive: false,
        };

        const mockUseAdditionalServers = jest.spyOn(useAdditionalServersModule, 'useAdditionalServers');
        mockUseAdditionalServers.mockReturnValue({
            data: [notStartedServer],
            isLoading: false,
        } as any);

        renderWithPermissions(
            <AdditionalServersPage/>,
            ['ADDITIONAL_SERVER_VIEW'],
        );

        expect(screen.queryByRole('button', {name: 'Start'})).not.toBeInTheDocument();
    });

    it('shows Start button with ADDITIONAL_SERVER_OPERATE', () => {
        const notStartedServer: AdditionalServerDto = {
            ...mockServer,
            startedAt: undefined,
            alive: false,
        };

        const mockUseAdditionalServers = jest.spyOn(useAdditionalServersModule, 'useAdditionalServers');
        mockUseAdditionalServers.mockReturnValue({
            data: [notStartedServer],
            isLoading: false,
        } as any);

        renderWithPermissions(
            <AdditionalServersPage/>,
            ['ADDITIONAL_SERVER_VIEW', 'ADDITIONAL_SERVER_OPERATE'],
        );

        expect(screen.getByRole('button', {name: 'Start'})).toBeInTheDocument();
    });
});
