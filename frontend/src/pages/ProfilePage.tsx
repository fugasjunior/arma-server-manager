import {useContext, useState} from "react";
import {Alert, Box, Button, TextField, Typography} from "@mui/material";
import {AuthContext} from "../store/auth-context";
import {usersApi} from "../api/client";
import {toast} from "react-toastify";
import {useMutation} from "@tanstack/react-query";

const ProfilePage = () => {
    const authCtx = useContext(AuthContext);
    const [currentPassword, setCurrentPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [error, setError] = useState<string | null>(null);

    const changePasswordMutation = useMutation({
        mutationFn: () => usersApi.changeMyPassword({
            changePasswordRequest: {currentPassword, newPassword},
        }),
        onSuccess: () => {
            toast.success("Password changed successfully.");
            setCurrentPassword("");
            setNewPassword("");
            setConfirmPassword("");
            setError(null);
        },
    });

    const handleChangePassword = () => {
        setError(null);
        if (!currentPassword) {
            setError("Current password is required.");
            return;
        }
        if (newPassword !== confirmPassword) {
            setError("New passwords do not match.");
            return;
        }
        if (newPassword.length < 6) {
            setError("New password must be at least 6 characters.");
            return;
        }
        changePasswordMutation.mutate();
    };

    return (
        <>
            <Typography variant="h4" component="h2" sx={{mb: 2}}>Profile</Typography>
            <Typography variant="subtitle1" sx={{mb: 3}}>
                Logged in as: <strong>{authCtx.currentUser?.username}</strong>
            </Typography>
            <Typography variant="h6" sx={{mb: 2}}>Change password</Typography>
            <Box sx={{display: "flex", flexDirection: "column", gap: 2, maxWidth: 400}}>
                {error && <Alert severity="error">{error}</Alert>}
                <TextField
                    label="Current password"
                    type="password"
                    value={currentPassword}
                    onChange={e => setCurrentPassword(e.target.value)}
                    size="small"
                    disabled={changePasswordMutation.isPending}
                />
                <TextField
                    label="New password"
                    type="password"
                    value={newPassword}
                    onChange={e => setNewPassword(e.target.value)}
                    size="small"
                    disabled={changePasswordMutation.isPending}
                />
                <TextField
                    label="Confirm new password"
                    type="password"
                    value={confirmPassword}
                    onChange={e => setConfirmPassword(e.target.value)}
                    size="small"
                    disabled={changePasswordMutation.isPending}
                />
                <Button
                    variant="contained"
                    onClick={handleChangePassword}
                    disabled={changePasswordMutation.isPending || !currentPassword || !newPassword}
                >
                    Change password
                </Button>
            </Box>
        </>
    );
};

export default ProfilePage;
