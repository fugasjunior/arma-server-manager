import {Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField} from "@mui/material";
import {useState, useEffect} from "react";
import {useMutation} from "@tanstack/react-query";
import {usersApi} from "../../api/client";
import {toast} from "react-toastify";
import {UserDto} from "../../api/generated";

type ResetPasswordDialogProps = {
    open: boolean;
    user: UserDto | null;
    onClose: () => void;
};

export default function ResetPasswordDialog({open, user, onClose}: ResetPasswordDialogProps) {
    const [resetPassword, setResetPassword] = useState("");

    useEffect(() => {
        if (!open) {
            setResetPassword("");
        }
    }, [open]);

    const resetPasswordMutation = useMutation({
        mutationFn: ({id, password}: {id: number; password: string}) =>
            usersApi.resetUserPassword({id, changePasswordRequest: {newPassword: password}}),
        onSuccess: () => {
            toast.success("Password reset.");
            onClose();
            setResetPassword("");
        },
    });

    const handleReset = () => {
        if (!user) return;
        resetPasswordMutation.mutate({id: user.id!, password: resetPassword});
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="xs" fullWidth>
            <DialogTitle>Reset password: {user?.username}</DialogTitle>
            <DialogContent>
                <TextField
                    label="New password"
                    type="password"
                    value={resetPassword}
                    onChange={e => setResetPassword(e.target.value)}
                    fullWidth
                    size="small"
                    sx={{mt: 1}}
                    disabled={resetPasswordMutation.isPending}
                />
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose} disabled={resetPasswordMutation.isPending}>Cancel</Button>
                <Button
                    variant="contained"
                    onClick={handleReset}
                    disabled={resetPasswordMutation.isPending || !resetPassword}
                >
                    Reset
                </Button>
            </DialogActions>
        </Dialog>
    );
}
