import React from 'react';
import {Navigate, Route} from "react-router-dom";
import {isUserAuthenticated} from "../services/authService";

const ProtectedRoute = ({children}) => {
    if (!isUserAuthenticated()) {
        return <Navigate to="/login"/>;
    }
    return children;
};

export default ProtectedRoute;