import React, {Component, useState} from 'react';
import {isUserAuthenticated, login} from "../../services/authService";
import {Navigate, useNavigate} from "react-router-dom";
import {Box, Button, Stack, TextField, Typography} from "@mui/material";

const Login = () => {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState(false);

    const navigate = useNavigate()

    const handleSubmit = async e => {
        e.preventDefault();
        try {
            await login(username, password);
            navigate("/");
        } catch (e) {
            if (e.response && e.response.status === 401) {
                setError(true);
            }
        }
    };

    const handleUsernameChange = (e) => {
        setUsername(e.target.value);
    }

    const handlePasswordChange = (e) => {
        setPassword(e.target.value);
    }

    if(isUserAuthenticated()) {
        return <Navigate to="/"/>;
    }

    return (
            <Box sx={{
                position: "absolute",
                top: "50%",
                left: "50%",
                transform: "translate(-50%, -50%)",
                textAlign: "center",
                bgcolor: 'background.paper',
                boxShadow: 24,
                p: 4
            }} alignContent="center">

                <form onSubmit={handleSubmit}>
                    <Stack spacing={2}>
                        <Typography variant="h4" component="h2">
                            Log in
                        </Typography>
                        <TextField type="text" id="username" name="username" placeholder="Enter username"
                                   label="User name"
                                   required
                                   value={username}
                                   onChange={handleUsernameChange}/>
                        <TextField type="password" id="password" name="password" placeholder="Password"
                                   label="Password"
                                   required
                                   value={password}
                                   onChange={handlePasswordChange}/>
                        <Button fullWidth variant="contained" size="large" type="submit">Submit</Button>
                    </Stack>
                </form>
            </Box>
    );
};

export default Login;