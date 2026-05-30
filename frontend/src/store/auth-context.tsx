import {createContext, useCallback, useEffect, useState} from "react";
import {setJwt, usersApi} from "../api/client";
import {useQueryClient} from "@tanstack/react-query";

interface CurrentUser {
    id: number;
    username: string;
    permissions: string[];
    roles: string[];
}

interface AuthContextType {
    token: string | null;
    isLoggedIn: boolean;
    isLoadingUser: boolean;
    currentUser: CurrentUser | null;
    hasPermission: (permission: string) => boolean;
    login: (token: string, expiresIn: number) => void;
    logout: () => void;
}

export const AuthContext = createContext<AuthContextType>({
    token: "",
    isLoggedIn: false,
    isLoadingUser: false,
    currentUser: null,
    hasPermission: () => false,
    login: () => undefined,
    logout: () => undefined,
});

export const AuthContextProvider = ({ children }: { children: React.ReactNode }) => {
    const tokenData = retrieveStoredToken();
    let initialToken: string | null = null;
    if (tokenData) {
        initialToken = tokenData.storedToken;
    }

    const [token, setToken] = useState<string | null>(initialToken);
    const [isLoadingUser, setIsLoadingUser] = useState(false);
    const [currentUser, setCurrentUser] = useState<CurrentUser | null>(() => {
        const stored = localStorage.getItem('currentUser');
        return stored ? JSON.parse(stored) : null;
    });
    const queryClient = useQueryClient();

    const fetchCurrentUser = useCallback(async () => {
        try {
            setIsLoadingUser(true);
            const response = await usersApi.getCurrentUser();
            const user = response.data as CurrentUser;
            setCurrentUser(user);
            localStorage.setItem('currentUser', JSON.stringify(user));
        } catch {
            setCurrentUser(null);
            localStorage.removeItem('currentUser');
        } finally {
            setIsLoadingUser(false);
        }
    }, []);

    useEffect(() => {
        if (token) {
            setJwt(token);
        }
    }, [token]);

    useEffect(() => {
        if (token) {
            fetchCurrentUser();
        }
    }, [token, fetchCurrentUser]);

    const userIsLoggedIn = !!token;

    const loginHandler = useCallback((token: string, expiresIn: number) => {
        localStorage.setItem('token', token);
        localStorage.setItem('expirationTime', calculateExpirationTime(expiresIn).toISOString());
        setJwt(token);
        setToken(token);
    }, []);

    const logoutHandler = useCallback(() => {
        setJwt("");
        setToken(null);
        setCurrentUser(null);
        localStorage.removeItem('token');
        localStorage.removeItem('expirationTime');
        localStorage.removeItem('currentUser');
        queryClient.clear();
    }, [queryClient]);

    const hasPermission = useCallback((permission: string): boolean => {
        return currentUser?.permissions.includes(permission) ?? false;
    }, [currentUser]);

    const contextValue: AuthContextType = {
        token,
        isLoggedIn: userIsLoggedIn,
        isLoadingUser,
        currentUser,
        hasPermission,
        login: loginHandler,
        logout: logoutHandler,
    };

    return <AuthContext.Provider value={contextValue}>{children}</AuthContext.Provider>;
};

const retrieveStoredToken = () => {
    const storedToken = localStorage.getItem('token');
    const expirationTime = localStorage.getItem('expirationTime');

    if (!storedToken || !expirationTime) {
        return null;
    }

    const remainingTime = calculateRemainingTime(new Date(expirationTime));
    if (remainingTime < 60000) {
        localStorage.removeItem('token');
        localStorage.removeItem('expirationTime');
        localStorage.removeItem('currentUser');
        return null;
    }

    return {storedToken, remainingTime};
};

const calculateRemainingTime = (time: Date) => {
    const currentDate = new Date();
    return +time - +currentDate;
};

const calculateExpirationTime = (millis: number) => {
    return new Date(Date.now() + millis);
};
