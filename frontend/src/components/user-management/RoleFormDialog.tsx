import {Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField, Typography} from "@mui/material";
import {useState, useEffect} from "react";
import {useMutation, useQueryClient} from "@tanstack/react-query";
import {rolesApi} from "../../api/client";
import {queryKeys} from "../../api/queryKeys";
import {toast} from "react-toastify";
import {RoleDto, PermissionDto} from "../../api/generated";
import ListBuilder from "../../UI/ListBuilder/ListBuilder";

type RoleFormDialogProps = {
    open: boolean;
    role: RoleDto | null;
    permissions: PermissionDto[];
    onClose: () => void;
};

export default function RoleFormDialog({open, role, permissions, onClose}: RoleFormDialogProps) {
    const queryClient = useQueryClient();
    const [roleName, setRoleName] = useState("");
    const [roleDescription, setRoleDescription] = useState("");
    const [rolePermissions, setRolePermissions] = useState<string[]>([]);

    useEffect(() => {
        if (role) {
            setRoleName(role.name ?? "");
            setRoleDescription(role.description ?? "");
            setRolePermissions(role.permissions ?? []);
        } else {
            setRoleName("");
            setRoleDescription("");
            setRolePermissions([]);
        }
    }, [role, open]);

    const toPermElem = (p: PermissionDto) => ({
        id: p.code!,
        name: p.description ?? p.code!,
        subtitle: p.code!,
    });

    const allPermElems = permissions.map(toPermElem).sort((a, b) => (a.id as string).localeCompare(b.id as string));
    const selectedPermElems = rolePermissions.map(code => {
        const p = permissions.find(x => x.code === code);
        return toPermElem(p ?? {code, description: code});
    }).sort((a, b) => (a.id as string).localeCompare(b.id as string));
    const availablePermElems = allPermElems.filter(e => !rolePermissions.includes(e.id as string));

    const createRoleMutation = useMutation({
        mutationFn: () => rolesApi.createRole({
            createRoleRequest: {
                name: roleName,
                description: roleDescription,
                permissions: rolePermissions,
            }
        }),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: queryKeys.roles});
            toast.success("Role created.");
            onClose();
        },
    });

    const updateRoleMutation = useMutation({
        mutationFn: () => rolesApi.updateRole({
            id: role!.id!,
            updateRoleRequest: {
                description: roleDescription,
                permissions: rolePermissions,
            }
        }),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: queryKeys.roles});
            toast.success("Role updated.");
            onClose();
        },
    });

    const isCreate = !role;
    const isLoading = createRoleMutation.isPending || updateRoleMutation.isPending;

    const handleConfirm = () => {
        if (isCreate && !roleName) return;
        if (isCreate) {
            createRoleMutation.mutate();
        } else {
            updateRoleMutation.mutate();
        }
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="lg" fullWidth>
            <DialogTitle>{isCreate ? "Create role" : `Edit role: ${role?.name}`}</DialogTitle>
            <DialogContent sx={{pt: 2}}>
                {isCreate && (
                    <TextField
                        label="Name"
                        value={roleName}
                        onChange={e => setRoleName(e.target.value)}
                        fullWidth
                        size="small"
                        sx={{mb: 2}}
                        disabled={isLoading}
                    />
                )}
                {!isCreate && (
                    <Typography variant="body2" color="text.secondary" sx={{mb: 2}}>
                        Role name cannot be changed
                    </Typography>
                )}
                <TextField
                    label="Description"
                    value={roleDescription}
                    onChange={e => setRoleDescription(e.target.value)}
                    fullWidth
                    size="small"
                    multiline
                    rows={2}
                    sx={{mb: 2}}
                    disabled={isLoading}
                />
                <Typography variant="h6" sx={{mb: 1}}>
                    Permissions
                </Typography>
                <ListBuilder
                    embedded
                    availableOptions={availablePermElems}
                    selectedOptions={selectedPermElems}
                    itemsLabel="permissions"
                    showFilter
                    onSelect={(el) => !isLoading && setRolePermissions([...rolePermissions, el.id as string])}
                    onDeselect={(el) => !isLoading && setRolePermissions(rolePermissions.filter(p => p !== el.id))}
                />
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose} disabled={isLoading}>Cancel</Button>
                <Button
                    variant="contained"
                    onClick={handleConfirm}
                    disabled={isLoading || (isCreate && !roleName)}
                >
                    {isCreate ? "Create" : "Save"}
                </Button>
            </DialogActions>
        </Dialog>
    );
}
