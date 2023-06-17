import {useContext, useEffect} from 'react';
import {useNavigate} from "react-router-dom";
import {AuthContext} from "../store/auth-context";

const ProtectedRoute = ({children}: any) => {
    const authCtx = useContext(AuthContext);
    const navigate = useNavigate();

    useEffect(() => {
        if (!authCtx.isLoggedIn) {
            navigate("/login");
        }
    }, [authCtx.isLoggedIn]);

    if (!authCtx.isLoggedIn) {
        return;
    }

    return children;
};

export default ProtectedRoute;