import React from 'react';
import {Navigate, Route} from "react-router-dom";
import {isAuthenticated} from "../services/authService";

const ProtectedRoute = ({component: Component, render, ...rest}) => {
    return <Route
            {...rest}
            render={props => {
                if (!isAuthenticated()) {
                    return <Navigate to="/login"/>;
                }
                return Component ? <Component {...props} /> : render(props);
            }}/>;
};

export default ProtectedRoute;