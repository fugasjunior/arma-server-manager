import {Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField} from "@mui/material";
import {useEffect, useState} from "react";
import {useMutation, useQueryClient} from "@tanstack/react-query";
import {usersApi} from "../../api/client";
import {queryKeys} from "../../api/queryKeys";
import {toast} from "react-toastify";

type CreateUserDialogProps = {
    open: boolean;
    onClose: () => void;
};

export default function CreateUserDialog({open, onClose}: CreateUserDialogProps) {
    const queryClient = useQueryClient();
    const [newUsername, setNewUsername] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [passwordMismatch, setPasswordMismatch] = useState(false);

    useEffect(() => {
        if (!open) {
            setNewUsername("");
            setNewPassword("");
            setConfirmPassword("");
            setPasswordMismatch(false);
        }
    }, [open]);

    const createMutation = useMutation({
        mutationFn: () => usersApi.createUser({createUserRequest: {username: newUsername, password: newPassword}}),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: queryKeys.users});
            toast.success("User created.");
            onClose();
            setNewUsername("");
            setNewPassword("");
            setConfirmPassword("");
        },
    });

    const handleCreate = () => {
        if (newPassword !== confirmPassword) {
            setPasswordMismatch(true);
            return;
        }
        createMutation.mutate();
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="xs" fullWidth>
            <DialogTitle>Create user</DialogTitle>
            <DialogContent>
                <TextField
                    label="Username"
                    value={newUsername}
                    onChange={e => setNewUsername(e.target.value)}
                    fullWidth
                    size="small"
                    sx={{mt: 1, mb: 2}}
                    disabled={createMutation.isPending}
                />
                <TextField
                    label="Password"
                    type="password"
                    value={newPassword}
                    onChange={e => setNewPassword(e.target.value)}
                    fullWidth
                    size="small"
                    sx={{mb: 2}}
                    disabled={createMutation.isPending}
                />
                <TextField
                    label="Confirm password"
                    type="password"
                    value={confirmPassword}
                    onChange={e => {
                        setConfirmPassword(e.target.value);
                        setPasswordMismatch(false);
                    }}
                    fullWidth
                    size="small"
                    error={passwordMismatch}
                    helperText={passwordMismatch ? "Passwords do not match" : ""}
                    disabled={createMutation.isPending}
                />
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose} disabled={createMutation.isPending}>Cancel</Button>
                <Button
                    variant="contained"
                    onClick={handleCreate}
                    disabled={createMutation.isPending || !newUsername || !newPassword}
                >
                    Create
                </Button>
            </DialogActions>
        </Dialog>
    );
}
