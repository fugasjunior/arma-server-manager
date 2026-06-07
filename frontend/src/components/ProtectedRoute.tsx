import {useContext, useEffect} from 'react';
import {useNavigate} from "react-router-dom";
import {AuthContext} from "../store/auth-context";

const ProtectedRoute = ({children}: any) => {
    const authCtx = useContext(AuthContext);
    const navigate = useNavigate();

    useEffect(() => {
        if (!authCtx.isLoadingUser && !authCtx.isLoggedIn) {
            navigate("/login");
        }
    }, [authCtx.isLoadingUser, authCtx.isLoggedIn]);

    if (authCtx.isLoadingUser) return null;
    if (!authCtx.isLoggedIn) return null;

    return children;
};

export default ProtectedRoute;