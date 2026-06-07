import React, {ReactElement} from 'react';
import {render} from '@testing-library/react';
import {QueryClient, QueryClientProvider} from '@tanstack/react-query';
import {AuthContext} from '../../src/store/auth-context';

const renderWithPermissions = (ui: ReactElement, permissions: string[]) => {
    const queryClient = new QueryClient({defaultOptions: {queries: {retry: false}}});
    return render(
        <QueryClientProvider client={queryClient}>
            <AuthContext.Provider value={{
                hasPermission: (p: string) => permissions.includes(p),
                isLoggedIn: true,
                isLoadingUser: false,
                currentUser: null,
                login: async () => undefined,
                logout: async () => undefined,
            }}>
                {ui}
            </AuthContext.Provider>
        </QueryClientProvider>
    );
};

export default renderWithPermissions;
