import React from 'react';
import {screen} from '@testing-library/react';
import '@testing-library/jest-dom';
import SystemResourcesMonitor from '../../../src/components/dashboard/SystemResourcesMonitor';
import {useSystemDetails} from '../../../src/hooks/queries/useSystemDetails';
import renderWithPermissions from '../../helpers/renderWithPermissions';

jest.mock('../../../src/api/client', () => ({}));
jest.mock('../../../src/hooks/queries/useSystemDetails');

const mockUseSystemDetails = jest.mocked(useSystemDetails);

const mockSystemDetails = {
    cpuUsage: 0.5,
    memoryLeft: 4_000_000_000,
    memoryTotal: 8_000_000_000,
    spaceLeft: 100_000_000_000,
    spaceTotal: 500_000_000_000,
    cpuCount: 4,
    osName: 'Linux',
};

beforeEach(() => {
    jest.clearAllMocks();
    mockUseSystemDetails.mockReturnValue({data: undefined, isSuccess: false} as any);
});

describe('SystemResourcesMonitor permissions', () => {
    it('renders nothing without SYSTEM_VIEW', () => {
        const {container} = renderWithPermissions(<SystemResourcesMonitor/>, []);
        expect(container).toBeEmptyDOMElement();
    });

    it('passes enabled:false to hook without SYSTEM_VIEW', () => {
        renderWithPermissions(<SystemResourcesMonitor/>, []);
        expect(mockUseSystemDetails).toHaveBeenCalledWith(expect.objectContaining({enabled: false}));
    });

    it('renders component with SYSTEM_VIEW', () => {
        mockUseSystemDetails.mockReturnValue({data: mockSystemDetails, isSuccess: true} as any);
        renderWithPermissions(<SystemResourcesMonitor/>, ['SYSTEM_VIEW']);
        expect(screen.getByText('CPU usage')).toBeInTheDocument();
    });

    it('passes enabled:true to hook with SYSTEM_VIEW', () => {
        renderWithPermissions(<SystemResourcesMonitor/>, ['SYSTEM_VIEW']);
        expect(mockUseSystemDetails).toHaveBeenCalledWith(expect.objectContaining({enabled: true}));
    });
});
