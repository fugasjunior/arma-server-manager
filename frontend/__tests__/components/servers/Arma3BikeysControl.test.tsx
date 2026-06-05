import React from 'react';
import {screen, fireEvent, waitFor} from '@testing-library/react';
import '@testing-library/jest-dom';
import Arma3BikeysControl from '../../../src/components/servers/Arma3BikeysControl.tsx';
import {ServerDto, ServerInstanceInfoDto, ServerType} from '../../../src/api/generated';
import renderWithPermissions from '../../helpers/renderWithPermissions';
import {useServerKeys} from '../../../src/hooks/queries/useServerKeys';
import {toast} from 'react-toastify';

jest.mock('../../../src/hooks/queries/useServerKeys');
jest.mock('../../../src/api/client', () => ({
    keysApi: {
        uploadServerKeys: jest.fn().mockResolvedValue({}),
        deleteServerKey: jest.fn().mockResolvedValue({}),
    },
}));
jest.mock('react-toastify', () => ({toast: {success: jest.fn(), error: jest.fn()}}));
jest.mock('../../../src/config', () => ({
    __esModule: true,
    default: {
        apiUrl: '/api',
        dateFormat: {year: 'numeric', month: '2-digit', day: 'numeric', hour: '2-digit', minute: '2-digit'},
        version: 'test',
    },
}));

const mockUseServerKeys = jest.mocked(useServerKeys);

const server: ServerDto = {id: 1, name: 'TestServer', type: ServerType.Arma3, port: 2302, queryPort: 2303};
const stoppedStatus: ServerInstanceInfoDto = {alive: false};
const runningStatus: ServerInstanceInfoDto = {alive: true};

beforeEach(() => {
    jest.clearAllMocks();
    mockUseServerKeys.mockReturnValue({data: []} as any);
});

describe('Arma3BikeysControl', () => {
    it('shows Bikeys button', () => {
        renderWithPermissions(<Arma3BikeysControl server={server} status={null}/>, ['BIKEY_VIEW']);
        expect(screen.getByText('Bikeys')).toBeInTheDocument();
    });

    it('queries keys lazily — only after button click', () => {
        renderWithPermissions(<Arma3BikeysControl server={server} status={stoppedStatus}/>, ['BIKEY_VIEW']);
        expect(mockUseServerKeys).toHaveBeenCalledWith(1, expect.objectContaining({enabled: false}));

        fireEvent.click(screen.getByText('Bikeys'));
        expect(mockUseServerKeys).toHaveBeenCalledWith(1, expect.objectContaining({enabled: true}));
    });

    it('disables Upload and Delete when server is running', async () => {
        mockUseServerKeys.mockReturnValue({data: [{name: 'test.bikey', fileSize: 100}]} as any);

        renderWithPermissions(
            <Arma3BikeysControl server={server} status={runningStatus}/>,
            ['BIKEY_VIEW', 'BIKEY_MODIFY', 'BIKEY_DELETE'],
        );

        fireEvent.click(screen.getByText('Bikeys'));

        await waitFor(() => {
            // Upload renders as <label> (MUI component="label"); MUI sets aria-disabled on it
            expect(screen.getByTestId('key-upload-btn')).toHaveAttribute('aria-disabled', 'true');
            // Delete renders as <button>; native disabled applies
            expect(screen.getByTestId('key-delete-btn')).toBeDisabled();
        });
    });

    it('enables Upload when server is stopped', async () => {
        mockUseServerKeys.mockReturnValue({data: [{name: 'test.bikey', fileSize: 100}]} as any);

        renderWithPermissions(
            <Arma3BikeysControl server={server} status={stoppedStatus}/>,
            ['BIKEY_VIEW', 'BIKEY_MODIFY', 'BIKEY_DELETE'],
        );

        fireEvent.click(screen.getByText('Bikeys'));

        await waitFor(() => {
            expect(screen.getByTestId('key-upload-btn')).not.toHaveAttribute('aria-disabled', 'true');
        });
    });

    it('calls uploadServerKeys and shows toast on file upload', async () => {
        const {keysApi} = jest.requireMock('../../../src/api/client');

        renderWithPermissions(
            <Arma3BikeysControl server={server} status={stoppedStatus}/>,
            ['BIKEY_VIEW', 'BIKEY_MODIFY'],
        );

        fireEvent.click(screen.getByText('Bikeys'));

        const file = new File(['some_prefix_RSA1'], 'community.bikey', {type: 'application/octet-stream'});
        const input = await waitFor(() => screen.getByTestId('key-upload-input'));
        fireEvent.change(input, {target: {files: [file]}});

        await waitFor(() => {
            expect(keysApi.uploadServerKeys).toHaveBeenCalledWith(
                expect.objectContaining({id: 1, file: [file]})
            );
            expect(toast.success).toHaveBeenCalledWith('Bikey(s) successfully uploaded');
        });
    });

    it('calls deleteServerKey and shows toast on delete', async () => {
        const {keysApi} = jest.requireMock('../../../src/api/client');
        mockUseServerKeys.mockReturnValue({data: [{name: 'community.bikey', fileSize: 512}]} as any);

        renderWithPermissions(
            <Arma3BikeysControl server={server} status={stoppedStatus}/>,
            ['BIKEY_VIEW', 'BIKEY_DELETE'],
        );

        fireEvent.click(screen.getByText('Bikeys'));

        // Select the row checkbox
        const checkbox = await waitFor(() =>
            screen.getByTestId('row-community.bikey').querySelector('input[type="checkbox"]')!
        );
        fireEvent.click(checkbox);

        // Click delete
        const deleteBtn = screen.getByTestId('key-delete-btn');
        fireEvent.click(deleteBtn);

        await waitFor(() => {
            expect(keysApi.deleteServerKey).toHaveBeenCalledWith({id: 1, name: 'community.bikey'});
            expect(toast.success).toHaveBeenCalledWith('Bikey(s) deleted successfully');
        });
    });
});
