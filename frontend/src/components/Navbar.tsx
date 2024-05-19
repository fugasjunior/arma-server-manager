import {useContext} from "react";
import {NavLink, useNavigate} from "react-router-dom";
import {AppBar, Button, CssBaseline, Stack, Toolbar} from "@mui/material";
import {AuthContext} from "../store/auth-context";
import logo from "../img/asm_logo.png"
import IconButton from "@mui/material/IconButton";
import LightModeIcon from '@mui/icons-material/LightMode';
import DarkModeIcon from '@mui/icons-material/DarkMode';

type NavbarProps = {
    onModeChange: () => void,
    mode: "light" | "dark"
}

const Navbar = ({onModeChange, mode}: NavbarProps) => {
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
                           spacing={1}
                           sx={{flexGrow: 1}}
                    >
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
                        <Button component={NavLink} to="/tools" sx={{color: '#fff'}}>
                            Tools
                        </Button>
                        <Button component={NavLink} to="/settings" sx={{color: '#fff'}}>
                            Settings
                        </Button>
                        <Button component={NavLink} to="/additionalServers" sx={{color: '#fff'}}>
                            Additional servers
                        </Button>
                    </Stack>
                    <Stack direction="row" sx={{flexGrow: 0}}>
                        <IconButton onClick={onModeChange}>
                            {mode === "dark" ? <LightModeIcon/> : <DarkModeIcon style={{color: "white"}}/>}
                        </IconButton>
                        <Button component={NavLink} to="/about" sx={{color: '#fff'}}>
                            About
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