import {screen, waitFor, act} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import '@testing-library/jest-dom';
import SteamAuthForm from '../../../src/components/appConfig/SteamAuthForm';
import {steamAuthApi} from '../../../src/api/client';
import renderWithPermissions from '../../helpers/renderWithPermissions';

jest.mock('../../../src/api/client', () => ({
    steamAuthApi: {
        getSteamAuth: jest.fn(),
        setSteamAuth: jest.fn(),
        clearSteamAuth: jest.fn(),
    }
}));

jest.mock('react-toastify', () => ({toast: {success: jest.fn(), error: jest.fn()}}));

const mockedSteamAuthApi = jest.mocked(steamAuthApi);

const renderForm = () => renderWithPermissions(<SteamAuthForm/>, ['STEAM_AUTH_ADMIN']);

describe('SteamAuthForm', () => {
    beforeEach(() => {
        jest.clearAllMocks();
        (mockedSteamAuthApi.getSteamAuth as jest.Mock).mockResolvedValue({
            data: {username: 'olduser', password: '', steamGuardToken: ''}
        });
        (mockedSteamAuthApi.setSteamAuth as jest.Mock).mockResolvedValue({});
        (mockedSteamAuthApi.clearSteamAuth as jest.Mock).mockResolvedValue({});
    });

    it('loads and displays saved credentials', async () => {
        renderForm();

        await waitFor(() => {
            expect(screen.getByLabelText(/user name/i)).toHaveValue('olduser');
        });
    });

    it('reflects submitted values in form after save, not stale data', async () => {
        renderForm();

        await waitFor(() => {
            expect(screen.getByLabelText(/user name/i)).toHaveValue('olduser');
        });

        const usernameInput = screen.getByLabelText(/user name/i);
        await userEvent.clear(usernameInput);
        await userEvent.type(usernameInput, 'newuser');

        await act(async () => {
            screen.getByRole('button', {name: /submit/i}).click();
        });

        await waitFor(() => {
            expect(mockedSteamAuthApi.setSteamAuth).toHaveBeenCalledWith(
                expect.objectContaining({
                    steamAuthDto: expect.objectContaining({username: 'newuser'})
                })
            );
        });

        // Form must show the newly submitted username, not the old one
        expect(screen.getByLabelText(/user name/i)).toHaveValue('newuser');
        // Password must be cleared after save
        expect(screen.getByLabelText(/^password$/i)).toHaveValue('');
    });
});
