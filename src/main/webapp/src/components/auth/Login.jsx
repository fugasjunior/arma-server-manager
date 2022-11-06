import React, {useContext, useState} from 'react';
import {login} from "../../services/authService";
import {useNavigate} from "react-router-dom";
import {Box, Button, Stack, TextField, Typography} from "@mui/material";
import {AuthContext} from "../../store/auth-context";

const Login = () => {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState(false);

    const authCtx = useContext(AuthContext);

    const navigate = useNavigate()

    const handleSubmit = async e => {
        e.preventDefault();
        try {
            const data = await login(username, password);
            console.log(data);
            authCtx.login(data.token, data.expiresIn * 1000);
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

    if (authCtx.isLoggedIn) {
        navigate("/");
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