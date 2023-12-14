import {useContext} from "react";
import {NavLink, useNavigate} from "react-router-dom";
import {AppBar, Button, CssBaseline, Stack, Toolbar} from "@mui/material";
import {AuthContext} from "../store/auth-context";
import logo from "../img/asm_logo.png"

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
                        <img
                            alt="Arma Server Manager Logo"
                            title="Arma Server Manager"
                            src={logo}
                            style={{height: 52}}
                        />
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