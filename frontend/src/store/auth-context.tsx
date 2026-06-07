import {createContext, useCallback, useEffect, useState} from "react";
import {usersApi} from "../api/client";
import {login as loginService, logout as logoutService} from "../services/authService";
import {useQueryClient} from "@tanstack/react-query";

interface CurrentUser {
    id: number;
    username: string;
    permissions: string[];
    roles: string[];
}

interface AuthContextType {
    isLoggedIn: boolean;
    isLoadingUser: boolean;
    currentUser: CurrentUser | null;
    hasPermission: (permission: string) => boolean;
    login: (username: string, password: string) => Promise<void>;
    logout: () => Promise<void>;
}

export const AuthContext = createContext<AuthContextType>({
    isLoggedIn: false,
    isLoadingUser: true,
    currentUser: null,
    hasPermission: () => false,
    login: async () => undefined,
    logout: async () => undefined,
});

export const AuthContextProvider = ({children}: {children: React.ReactNode}) => {
    const [isLoadingUser, setIsLoadingUser] = useState(true);
    const [currentUser, setCurrentUser] = useState<CurrentUser | null>(null);
    const queryClient = useQueryClient();

    const fetchCurrentUser = useCallback(async () => {
        try {
            setIsLoadingUser(true);
            const response = await usersApi.getCurrentUser();
            setCurrentUser(response.data as CurrentUser);
        } catch {
            setCurrentUser(null);
        } finally {
            setIsLoadingUser(false);
        }
    }, []);

    // Bootstrap: check for an existing session on mount.
    // A 401 from /users/me means no session — the interceptor silently rejects it
    // without redirecting, so fetchCurrentUser just sets currentUser to null.
    useEffect(() => {
        fetchCurrentUser();
    }, [fetchCurrentUser]);

    const loginHandler = useCallback(async (username: string, password: string) => {
        await loginService(username, password);
        await fetchCurrentUser();
    }, [fetchCurrentUser]);

    const logoutHandler = useCallback(async () => {
        await logoutService();
        setCurrentUser(null);
        queryClient.clear();
    }, [queryClient]);

    const hasPermission = useCallback((permission: string): boolean => {
        return currentUser?.permissions.includes(permission) ?? false;
    }, [currentUser]);

    const contextValue: AuthContextType = {
        isLoggedIn: currentUser !== null,
        isLoadingUser,
        currentUser,
        hasPermission,
        login: loginHandler,
        logout: logoutHandler,
    };

    return <AuthContext.Provider value={contextValue}>{children}</AuthContext.Provider>;
};
