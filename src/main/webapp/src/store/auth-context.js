import {createContext, useState} from "react";
import http from "../services/httpService";

export const AuthContext = createContext({
    token: '',
    isLoggedIn: false,
    login: (token) => {
    },
    logout: () => {
    }
});

const calculateExpirationTime = (seconds) => {
    let date = new Date();
    date = new Date(+date + seconds * 1000);
    return date;
}

const calculateRemainingTime = (time) => {
    const currentDate = new Date();
    return +time - +currentDate;
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

export const AuthContextProvider = (props) => {
    const tokenData = retrieveStoredToken();
    let initialToken;
    if (tokenData) {
        initialToken = tokenData.storedToken;
    }
    const [token, setToken] = useState(initialToken);
    http.setJwt(token);

    const userIsLoggedIn = !!token;

    const loginHandler = (token, expiresIn) => {
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

    const contextValue = {
        token,
        isLoggedIn: userIsLoggedIn,
        login: loginHandler,
        logout: logoutHandler
    };

    return <AuthContext.Provider value={contextValue}>{props.children}</AuthContext.Provider>;
}