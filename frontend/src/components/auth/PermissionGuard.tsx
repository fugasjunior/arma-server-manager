import {ReactNode} from "react";
import {usePermission} from "../../hooks/usePermission";

interface PermissionGuardProps {
    permission: string;
    children: ReactNode;
}

const PermissionGuard = ({permission, children}: PermissionGuardProps) => {
    const hasPermission = usePermission(permission);
    if (!hasPermission) return null;
    return <>{children}</>;
};

export default PermissionGuard;
