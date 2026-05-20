import {Box, Button, Chip, IconButton, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Tooltip} from "@mui/material";
import {useState} from "react";
import {useMutation, useQueryClient, useQuery} from "@tanstack/react-query";
import {rolesApi, permissionsApi} from "../../api/client";
import {queryKeys} from "../../api/queryKeys";
import {toast} from "react-toastify";
import {RoleDto} from "../../api/generated";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import AddIcon from "@mui/icons-material/Add";
import ConfirmationDialog from "../../UI/ConfirmationDialog";
import RoleFormDialog from "./RoleFormDialog";

type RolesTabProps = {
    roles: RoleDto[];
};

export default function RolesTab({roles}: RolesTabProps) {
    const queryClient = useQueryClient();

    const {data: permissions = []} = useQuery({
        queryKey: queryKeys.permissions,
        queryFn: async () => (await permissionsApi.getPermissions()).data,
    });

    const [createRoleOpen, setCreateRoleOpen] = useState(false);
    const [editRole, setEditRole] = useState<RoleDto | null>(null);
    const [deleteRole, setDeleteRole] = useState<RoleDto | null>(null);

    const deleteRoleMutation = useMutation({
        mutationFn: (id: number) => rolesApi.deleteRole({id}),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: queryKeys.roles});
            toast.success("Role deleted.");
            setDeleteRole(null);
        },
    });

    return (
        <>
            <Box sx={{mb: 2}}>
                <Button variant="contained" startIcon={<AddIcon/>} onClick={() => setCreateRoleOpen(true)}>
                    New role
                </Button>
            </Box>

            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Name</TableCell>
                            <TableCell>Description</TableCell>
                            <TableCell>Built-in</TableCell>
                            <TableCell>Permissions</TableCell>
                            <TableCell>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {roles.map(role => (
                            <TableRow key={role.id}>
                                <TableCell sx={{fontWeight: 500}}>{role.name}</TableCell>
                                <TableCell>{role.description}</TableCell>
                                <TableCell>
                                    {role.builtIn && <Chip label="Built-in" size="small"/>}
                                </TableCell>
                                <TableCell>
                                    <Box sx={{display: "flex", flexWrap: "wrap", gap: 0.5}}>
                                        {[...(role.permissions ?? [])].sort().slice(0, 3).map(p => (
                                            <Chip key={p} label={p} size="small" variant="outlined"/>
                                        ))}
                                        {(role.permissions ?? []).length > 3 && (
                                            <Tooltip title={<Box sx={{whiteSpace: 'normal'}}>{[...(role.permissions ?? [])].sort().slice(3).join(', ')}</Box>}>
                                                <Chip label={`+${(role.permissions ?? []).length - 3}`} size="small" variant="outlined"/>
                                            </Tooltip>
                                        )}
                                    </Box>
                                </TableCell>
                                <TableCell>
                                    <IconButton title="Edit" onClick={() => setEditRole(role)}>
                                        <EditIcon/>
                                    </IconButton>
                                    <Tooltip title={role.builtIn ? "Cannot delete built-in roles" : "Delete"}>
                                        <span>
                                            <IconButton
                                                color="error"
                                                disabled={role.builtIn}
                                                onClick={() => setDeleteRole(role)}
                                            >
                                                <DeleteIcon/>
                                            </IconButton>
                                        </span>
                                    </Tooltip>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>

            <RoleFormDialog
                open={createRoleOpen}
                role={null}
                permissions={permissions}
                onClose={() => setCreateRoleOpen(false)}
            />
            <RoleFormDialog
                open={!!editRole}
                role={editRole}
                permissions={permissions}
                onClose={() => setEditRole(null)}
            />
            <ConfirmationDialog
                open={!!deleteRole}
                title="Delete role"
                description={`Delete role "${deleteRole?.name}"? This cannot be undone.`}
                actionLabel="Delete"
                actionButtonColor="error"
                onClose={() => setDeleteRole(null)}
                onConfirm={() => deleteRole && deleteRoleMutation.mutate(deleteRole.id!)}
            />
        </>
    );
}
