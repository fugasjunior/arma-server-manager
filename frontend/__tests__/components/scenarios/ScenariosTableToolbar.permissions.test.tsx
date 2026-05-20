import React from 'react';
import '@testing-library/jest-dom';
import {screen} from '@testing-library/react';
import {ScenariosTableToolbar} from '../../../src/components/scenarios/ScenariosTableToolbar';
import renderWithPermissions from '../../helpers/renderWithPermissions';

jest.mock('../../../src/api/client', () => ({}));
jest.mock('../../../src/config', () => ({
    __esModule: true,
    default: {
        apiUrl: '/api',
        dateFormat: {year: 'numeric', month: '2-digit', day: 'numeric', hour: '2-digit', minute: '2-digit'},
        version: 'test',
    },
}));

const defaultProps = {
    selectedScenariosCount: 1,
    uploadInProgress: false,
    percentUploaded: 0,
    onFileChange: jest.fn(),
    onDeleteClicked: jest.fn(),
};

beforeEach(() => {
    jest.clearAllMocks();
});

describe('ScenariosTableToolbar permissions', () => {
    it('hides upload button without SCENARIO_MODIFY', () => {
        renderWithPermissions(<ScenariosTableToolbar {...defaultProps}/>, ['SCENARIO_VIEW']);
        expect(screen.queryByTestId('scenario-upload-btn')).not.toBeInTheDocument();
    });

    it('shows upload button with SCENARIO_MODIFY', () => {
        renderWithPermissions(<ScenariosTableToolbar {...defaultProps}/>, ['SCENARIO_VIEW', 'SCENARIO_MODIFY']);
        expect(screen.getByTestId('scenario-upload-btn')).toBeInTheDocument();
    });

    it('hides delete button without SCENARIO_DELETE', () => {
        renderWithPermissions(<ScenariosTableToolbar {...defaultProps}/>, ['SCENARIO_VIEW']);
        expect(screen.queryByTestId('scenario-delete-btn')).not.toBeInTheDocument();
    });

    it('shows delete button with SCENARIO_DELETE', () => {
        renderWithPermissions(<ScenariosTableToolbar {...defaultProps}/>, ['SCENARIO_VIEW', 'SCENARIO_DELETE']);
        expect(screen.getByTestId('scenario-delete-btn')).toBeInTheDocument();
    });

    it('delete button is disabled when no scenarios selected', () => {
        renderWithPermissions(
            <ScenariosTableToolbar {...defaultProps} selectedScenariosCount={0}/>,
            ['SCENARIO_VIEW', 'SCENARIO_DELETE'],
        );
        expect(screen.getByTestId('scenario-delete-btn')).toBeDisabled();
    });

    it('delete button is enabled when scenarios are selected', () => {
        renderWithPermissions(
            <ScenariosTableToolbar {...defaultProps} selectedScenariosCount={2}/>,
            ['SCENARIO_VIEW', 'SCENARIO_DELETE'],
        );
        expect(screen.getByTestId('scenario-delete-btn')).not.toBeDisabled();
    });
});
