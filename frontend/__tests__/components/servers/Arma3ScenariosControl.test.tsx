import React from 'react';
import {screen, fireEvent, waitFor} from '@testing-library/react';
import '@testing-library/jest-dom';
import Arma3ScenariosControl from '../../../src/components/servers/Arma3ScenariosControl.tsx';
import {ServerDto, ServerInstanceInfoDto, ServerType} from '../../../src/api/generated';
import renderWithPermissions from '../../helpers/renderWithPermissions';
import {useServerScenarios} from '../../../src/hooks/queries/useServerScenarios';

jest.mock('../../../src/hooks/queries/useServerScenarios');
jest.mock('../../../src/api/client', () => ({
    scenariosApi: {
        uploadServerScenarios: jest.fn(),
        deleteServerScenario: jest.fn(),
    },
}));
jest.mock('../../../src/api/downloads', () => ({
    downloadScenario: jest.fn(),
}));
jest.mock('../../../src/config', () => ({
    __esModule: true,
    default: {
        apiUrl: '/api',
        dateFormat: {year: 'numeric', month: '2-digit', day: 'numeric', hour: '2-digit', minute: '2-digit'},
        version: 'test',
    },
}));

const mockUseServerScenarios = jest.mocked(useServerScenarios);

const server: ServerDto = {id: 1, name: 'TestServer', type: ServerType.Arma3, port: 2302, queryPort: 2303};
const stoppedStatus: ServerInstanceInfoDto = {alive: false};
const runningStatus: ServerInstanceInfoDto = {alive: true};

beforeEach(() => {
    jest.clearAllMocks();
    mockUseServerScenarios.mockReturnValue({data: []} as any);
});

describe('Arma3ScenariosControl', () => {
    it('shows Scenarios button', () => {
        renderWithPermissions(<Arma3ScenariosControl server={server} status={null}/>, ['SCENARIO_VIEW']);
        expect(screen.getByText('Scenarios')).toBeInTheDocument();
    });

    it('queries scenarios lazily — only after button click', () => {
        renderWithPermissions(<Arma3ScenariosControl server={server} status={stoppedStatus}/>, ['SCENARIO_VIEW']);
        expect(mockUseServerScenarios).toHaveBeenCalledWith(1, expect.objectContaining({enabled: false}));

        fireEvent.click(screen.getByText('Scenarios'));
        expect(mockUseServerScenarios).toHaveBeenCalledWith(1, expect.objectContaining({enabled: true}));
    });

    it('disables Upload and Delete when server is running', async () => {
        mockUseServerScenarios.mockReturnValue({data: [{name: 'test.pbo', fileSize: 100}]} as any);

        renderWithPermissions(
            <Arma3ScenariosControl server={server} status={runningStatus}/>,
            ['SCENARIO_VIEW', 'SCENARIO_MODIFY', 'SCENARIO_DELETE'],
        );

        fireEvent.click(screen.getByText('Scenarios'));

        await waitFor(() => {
            // Upload renders as <label> (MUI component="label"); MUI sets aria-disabled on it
            expect(screen.getByTestId('scenario-upload-btn')).toHaveAttribute('aria-disabled', 'true');
            // Delete renders as <button>; native disabled applies
            expect(screen.getByTestId('scenario-delete-btn')).toBeDisabled();
        });
    });

    it('enables Upload when server is stopped', async () => {
        mockUseServerScenarios.mockReturnValue({data: [{name: 'test.pbo', fileSize: 100}]} as any);

        renderWithPermissions(
            <Arma3ScenariosControl server={server} status={stoppedStatus}/>,
            ['SCENARIO_VIEW', 'SCENARIO_MODIFY', 'SCENARIO_DELETE'],
        );

        fireEvent.click(screen.getByText('Scenarios'));

        await waitFor(() => {
            expect(screen.getByTestId('scenario-upload-btn')).not.toHaveAttribute('aria-disabled', 'true');
        });
    });
});
