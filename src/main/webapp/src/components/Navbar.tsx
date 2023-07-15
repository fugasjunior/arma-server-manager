import {useContext} from "react";
import {NavLink, useNavigate} from "react-router-dom";
import {AppBar, Avatar, Button, CssBaseline, Stack, Toolbar, Typography} from "@mui/material";
import {AuthContext} from "../store/auth-context";
import logo from "../img/logo_48x48.png"

const Navbar = () => {
    const authCtx = useContext(AuthContext);
    const isLoggedIn = authCtx.isLoggedIn;
    const navigate = useNavigate();

    const handleLogout = () => {
        authCtx.logout();
        navigate("/login");
    }

    return (
            <>
                {isLoggedIn && <AppBar position="static" sx={{mb: 4}}>
                    <CssBaseline/>
                    <Toolbar>
                        <Avatar
                                alt="Forgotten Empire Logo"
                                src={logo}
                                sx={{width: 52, height: 52, mr: 2}}
                        />
                        <Typography component="h1" variant="h5">
                            Arma Server Manager
                        </Typography>
                        <Stack marginLeft={4}
                               direction="row"
                               justifyContent="flex-start"
                               alignItems="center"
                               spacing={1}>
                            <Button color="success" component={NavLink} to="/" sx={{color: '#fff'}}>
                                Dashboard
                            </Button>
                            <Button component={NavLink} to="/servers" sx={{color: '#fff'}}>
                                Servers
                            </Button>
                            <Button component={NavLink} to="/mods" sx={{color: '#fff'}}>
                                Mods
                            </Button>
                            <Button component={NavLink} to="/scenarios" sx={{color: '#fff'}}>
                                Scenarios
                            </Button>
                            <Button component={NavLink} to="/config" sx={{color: '#fff'}}>
                                App config
                            </Button>
                            <Button component={NavLink} to="/additionalServers" sx={{color: '#fff'}}>
                                Additional servers
                            </Button>
                            <Button onClick={handleLogout}
                                    sx={{color: '#fff'}}
                            >
                                Log out
                            </Button>
                        </Stack>
                    </Toolbar>
                </AppBar>
                }
            </>
    );
};

export default Navbar;