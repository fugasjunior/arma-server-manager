import React from 'react';
import '@testing-library/jest-dom';
import ScenariosPage from '../../../src/pages/ScenariosPage';
import {useArma3Scenarios} from '../../../src/hooks/queries/useArma3Scenarios';
import renderWithPermissions from '../../helpers/renderWithPermissions';

jest.mock('../../../src/hooks/queries/useArma3Scenarios');
jest.mock('../../../src/api/client', () => ({
    scenariosApi: {
        getArma3Scenarios: jest.fn(),
        uploadScenarios: jest.fn(),
        deleteScenario: jest.fn(),
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

const mockUseArma3Scenarios = jest.mocked(useArma3Scenarios);

beforeEach(() => {
    jest.clearAllMocks();
    mockUseArma3Scenarios.mockReturnValue({data: []} as any);
});

describe('ScenariosPage permissions', () => {
    it('passes enabled:false to scenarios hook without SCENARIO_VIEW', () => {
        renderWithPermissions(<ScenariosPage/>, []);
        expect(mockUseArma3Scenarios).toHaveBeenCalledWith(expect.objectContaining({enabled: false}));
    });

    it('passes enabled:true to scenarios hook with SCENARIO_VIEW', () => {
        renderWithPermissions(<ScenariosPage/>, ['SCENARIO_VIEW']);
        expect(mockUseArma3Scenarios).toHaveBeenCalledWith(expect.objectContaining({enabled: true}));
    });
});
