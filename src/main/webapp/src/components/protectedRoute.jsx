import React from 'react';
import {Redirect, Route} from "react-router-dom";
import {isAuthenticated} from "../services/authService";

const ProtectedRoute = ({component: Component, render, ...rest}) => {
    return <Route
            {...rest}
            render={props => {
                if (!isAuthenticated()) {
                    return <Redirect to="/login"/>;
                }
                return Component ? <Component {...props} /> : render(props);
            }}/>;
};

export default ProtectedRoute;