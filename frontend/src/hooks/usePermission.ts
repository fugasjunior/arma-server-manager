import {useContext} from "react";
import {AuthContext} from "../store/auth-context";

export const usePermission = (permission: string): boolean => {
    const authCtx = useContext(AuthContext);
    return authCtx.hasPermission(permission);
};
