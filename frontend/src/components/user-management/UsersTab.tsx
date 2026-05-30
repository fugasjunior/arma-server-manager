import {Box, Button, IconButton, Paper, Switch, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Chip} from "@mui/material";
import {useContext, useState} from "react";
import {useMutation, useQueryClient} from "@tanstack/react-query";
import {usersApi} from "../../api/client";
import {queryKeys} from "../../api/queryKeys";
import {toast} from "react-toastify";
import {UserDto, RoleDto} from "../../api/generated";
import {AuthContext} from "../../store/auth-context";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import AddIcon from "@mui/icons-material/Add";
import LockResetIcon from "@mui/icons-material/LockReset";
import ConfirmationDialog from "../../UI/ConfirmationDialog";
import CreateUserDialog from "./CreateUserDialog";
import EditUserRolesDialog from "./EditUserRolesDialog";
import ResetPasswordDialog from "./ResetPasswordDialog";

type UsersTabProps = {
    users: UserDto[];
    roles: RoleDto[];
};

export default function UsersTab({users, roles}: UsersTabProps) {
    const queryClient = useQueryClient();
    const authCtx = useContext(AuthContext);

    const [createOpen, setCreateOpen] = useState(false);
    const [editUser, setEditUser] = useState<UserDto | null>(null);
    const [resetUser, setResetUser] = useState<UserDto | null>(null);
    const [deleteUser, setDeleteUser] = useState<UserDto | null>(null);

    const isOwnAccount = (id: number | undefined): boolean => authCtx.currentUser?.id === id;

    const deleteMutation = useMutation({
        mutationFn: (id: number) => usersApi.deleteUser({id}),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: queryKeys.users});
            toast.success("User deleted.");
            setDeleteUser(null);
        },
    });

    const toggleEnabledMutation = useMutation({
        mutationFn: ({id, enabled}: {id: number; enabled: boolean}) =>
            usersApi.updateUser({id, updateUserRequest: {enabled}}),
        onSuccess: () => queryClient.invalidateQueries({queryKey: queryKeys.users}),
    });

    return (
        <>
            <Box sx={{mb: 2}}>
                <Button variant="contained" startIcon={<AddIcon/>} onClick={() => setCreateOpen(true)}>
                    New user
                </Button>
            </Box>

            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Username</TableCell>
                            <TableCell>Roles</TableCell>
                            <TableCell>Enabled</TableCell>
                            <TableCell>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {users.map(user => (
                            <TableRow key={user.id}>
                                <TableCell>{user.username}</TableCell>
                                <TableCell>
                                    <Box sx={{display: "flex", flexWrap: "wrap", gap: 0.5}}>
                                        {(user.roles ?? []).map(r => (
                                            <Chip key={r.id} label={r.name} size="small"/>
                                        ))}
                                    </Box>
                                </TableCell>
                                <TableCell>
                                    <Switch
                                        disabled={isOwnAccount(user.id)}
                                        checked={user.enabled ?? true}
                                        onChange={e => toggleEnabledMutation.mutate({id: user.id!, enabled: e.target.checked})}
                                    />
                                </TableCell>
                                <TableCell>
                                    <IconButton title="Edit roles" onClick={() => setEditUser(user)}>
                                        <EditIcon/>
                                    </IconButton>
                                    <IconButton title="Reset password" onClick={() => setResetUser(user)}>
                                        <LockResetIcon/>
                                    </IconButton>
                                    <IconButton color="error" title="Delete" disabled={isOwnAccount(user.id)} onClick={() => setDeleteUser(user)}>
                                        <DeleteIcon/>
                                    </IconButton>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>

            <CreateUserDialog open={createOpen} onClose={() => setCreateOpen(false)}/>
            <EditUserRolesDialog
                open={!!editUser}
                user={editUser}
                roles={roles}
                onClose={() => setEditUser(null)}
            />
            <ResetPasswordDialog
                open={!!resetUser}
                user={resetUser}
                onClose={() => setResetUser(null)}
            />
            <ConfirmationDialog
                open={!!deleteUser}
                title="Delete user"
                description={`Delete user "${deleteUser?.username}"? This cannot be undone.`}
                actionLabel="Delete"
                actionButtonColor="error"
                onClose={() => setDeleteUser(null)}
                onConfirm={() => deleteUser && deleteMutation.mutate(deleteUser.id!)}
            />
        </>
    );
}
