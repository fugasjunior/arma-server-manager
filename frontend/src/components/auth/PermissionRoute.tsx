import {useContext, useEffect} from "react";
import {useNavigate} from "react-router-dom";
import {AuthContext} from "../../store/auth-context";
import ProtectedRoute from "../ProtectedRoute";

import {ReactNode} from "react";

interface PermissionRouteProps {
    permission: string;
    children: ReactNode;
}

const PermissionRoute = ({permission, children}: PermissionRouteProps) => {
    const authCtx = useContext(AuthContext);
    const navigate = useNavigate();

    const hasPermission = authCtx.hasPermission(permission);

    useEffect(() => {
        if (authCtx.isLoggedIn && !authCtx.isLoadingUser && !hasPermission) {
            navigate("/");
        }
    }, [authCtx.isLoggedIn, authCtx.isLoadingUser, hasPermission, navigate]);

    if (authCtx.isLoadingUser) return null;
    if (!hasPermission) return null;

    return <ProtectedRoute>{children}</ProtectedRoute>;
};

export default PermissionRoute;
