import React from 'react';
import {render, screen, waitFor, act} from '@testing-library/react';
import {MemoryRouter, Routes, Route} from 'react-router-dom';
import {QueryClient, QueryClientProvider} from '@tanstack/react-query';
import {AuthContextProvider} from '../../src/store/auth-context';
import ProtectedRoute from '../../src/components/ProtectedRoute';
import PermissionRoute from '../../src/components/auth/PermissionRoute';
import {usersApi} from '../../src/api/client';

jest.mock('../../src/api/client', () => ({
    usersApi: {getCurrentUser: jest.fn()},
}));
jest.mock('../../src/services/authService', () => ({
    login: jest.fn(),
    logout: jest.fn(),
}));

const renderApp = () => render(
    <QueryClientProvider client={new QueryClient({defaultOptions: {queries: {retry: false}}})}>
        <AuthContextProvider>
            <MemoryRouter initialEntries={['/servers']}>
                <Routes>
                    <Route path="/login" element={<div>LOGIN PAGE</div>}/>
                    <Route path="/servers" element={<ProtectedRoute><div>SERVERS PAGE</div></ProtectedRoute>}/>
                </Routes>
            </MemoryRouter>
        </AuthContextProvider>
    </QueryClientProvider>
);

test('session expiry (auth:unauthorized) redirects a logged-in user to /login', async () => {
    (usersApi.getCurrentUser as jest.Mock).mockResolvedValue({
        data: {id: 1, username: 'admin', permissions: [], roles: []},
    });

    renderApp();

    // Logged in: protected page is shown.
    await waitFor(() => expect(screen.getByText('SERVERS PAGE')).toBeInTheDocument());

    // Session expires mid-use -> interceptor dispatches this event.
    act(() => {
        window.dispatchEvent(new Event('auth:unauthorized'));
    });

    // Clean SPA redirect to login, not a blank page.
    await waitFor(() => expect(screen.getByText('LOGIN PAGE')).toBeInTheDocument());
});

test('expired session at a permission-gated route redirects to /login, not a blank page', async () => {
    (usersApi.getCurrentUser as jest.Mock).mockRejectedValue({response: {status: 401}});

    render(
        <QueryClientProvider client={new QueryClient({defaultOptions: {queries: {retry: false}}})}>
            <AuthContextProvider>
                <MemoryRouter initialEntries={['/settings']}>
                    <Routes>
                        <Route path="/login" element={<div>LOGIN PAGE</div>}/>
                        <Route path="/settings" element={
                            <PermissionRoute permission="STEAM_AUTH_ADMIN"><div>SETTINGS PAGE</div></PermissionRoute>
                        }/>
                    </Routes>
                </MemoryRouter>
            </AuthContextProvider>
        </QueryClientProvider>
    );

    await waitFor(() => expect(screen.getByText('LOGIN PAGE')).toBeInTheDocument());
    expect(screen.queryByText('SETTINGS PAGE')).not.toBeInTheDocument();
});
