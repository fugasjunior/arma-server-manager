import {createContext, useState} from "react";
import http from "../services/httpService";

interface AuthContextType {
    token: string,
    isLoggedIn: boolean,
    login: (token: string, expiresIn?: number) => void,
    logout: () => void,
}

export const AuthContext = createContext<AuthContextType>({
        token: "",
        isLoggedIn: false,
        login: () => undefined,
        logout: () => undefined
    }
);
export const AuthContextProvider = (props: any) => {
    const tokenData = retrieveStoredToken();
    let initialToken: string;
    if (tokenData) {
        initialToken = tokenData.storedToken;
    }
    const [token, setToken] = useState(initialToken);
    http.setJwt(token);

    const userIsLoggedIn = !!token;

    const loginHandler = (token: string, expiresIn: number) => {
        localStorage.setItem('token', token);
        localStorage.setItem('expirationTime', calculateExpirationTime(expiresIn).toISOString());
        setToken(token);
    };

    const logoutHandler = () => {
        http.setJwt("");
        setToken(null);
        localStorage.removeItem('token');
        localStorage.removeItem('expirationTime');
    };

    const contextValue: AuthContextType = {
        token,
        isLoggedIn: userIsLoggedIn,
        login: loginHandler,
        logout: logoutHandler
    };

    return <AuthContext.Provider value={contextValue}>{props.children}</AuthContext.Provider>;
}

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
        return null;
    }

    return {storedToken, remainingTime};
}

const calculateRemainingTime = (time: Date) => {
    const currentDate = new Date();
    return +time - +currentDate;
}

const calculateExpirationTime = (millis: number) => {
    let date = new Date();
    date = new Date(+date + (millis / 1000));
    return date;
}
