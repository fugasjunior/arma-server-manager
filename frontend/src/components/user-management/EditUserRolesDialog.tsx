import {Autocomplete, Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField} from "@mui/material";
import {useState, useEffect} from "react";
import {useMutation, useQueryClient} from "@tanstack/react-query";
import {usersApi} from "../../api/client";
import {queryKeys} from "../../api/queryKeys";
import {toast} from "react-toastify";
import {UserDto, RoleDto} from "../../api/generated";

type EditUserRolesDialogProps = {
    open: boolean;
    user: UserDto | null;
    roles: RoleDto[];
    onClose: () => void;
};

export default function EditUserRolesDialog({open, user, roles, onClose}: EditUserRolesDialogProps) {
    const queryClient = useQueryClient();
    const [editRoleIds, setEditRoleIds] = useState<RoleDto[]>([]);

    useEffect(() => {
        if (user) {
            setEditRoleIds(user.roles ?? []);
        }
    }, [user, open]);

    const setRolesMutation = useMutation({
        mutationFn: ({id, roleIds}: {id: number; roleIds: number[]}) =>
            usersApi.setUserRoles({id, setUserRolesRequest: {roleIds}}),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: queryKeys.users});
            toast.success("Roles updated.");
            onClose();
        },
    });

    const handleSave = () => {
        if (!user) return;
        setRolesMutation.mutate({id: user.id!, roleIds: editRoleIds.map(r => r.id!)});
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
            <DialogTitle>Edit roles: {user?.username}</DialogTitle>
            <DialogContent sx={{pt: 3}}>
                <Autocomplete<RoleDto, true, false, false>
                    disabled={setRolesMutation.isPending}
                    multiple
                    options={roles}
                    getOptionLabel={(r: RoleDto) => r.name ?? ''}
                    isOptionEqualToValue={(o: RoleDto, v: RoleDto) => o.id === v.id}
                    value={editRoleIds}
                    onChange={(_, val: RoleDto[]) => setEditRoleIds(val)}
                    sx={{mt: 1}}
                    renderInput={(params) => <TextField {...params} label="Roles" fullWidth/>}
                />
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose} disabled={setRolesMutation.isPending}>Cancel</Button>
                <Button
                    variant="contained"
                    onClick={handleSave}
                    disabled={setRolesMutation.isPending}
                >
                    Save
                </Button>
            </DialogActions>
        </Dialog>
    );
}
