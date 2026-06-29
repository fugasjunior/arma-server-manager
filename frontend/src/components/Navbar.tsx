import {useContext} from "react";
import {NavLink, useNavigate} from "react-router-dom";
import {AppBar, Button, Stack, Toolbar, Tooltip} from "@mui/material";
import {AuthContext} from "../store/auth-context";
import logo from "../img/asm_logo.png"
import IconButton from "@mui/material/IconButton";
import LightModeIcon from '@mui/icons-material/LightMode';
import DarkModeIcon from '@mui/icons-material/DarkMode';
import FavoriteIcon from '@mui/icons-material/Favorite';
import PermissionGuard from "./auth/PermissionGuard";

type NavbarProps = {
    onModeChange: () => void,
    mode: "light" | "dark"
}

const Navbar = ({onModeChange, mode}: NavbarProps) => {
    const authCtx = useContext(AuthContext);
    const isLoggedIn = authCtx.isLoggedIn;
    const navigate = useNavigate();

    const handleLogout = async () => {
        await authCtx.logout();
        navigate("/login");
    }

    return (
        <>
            {isLoggedIn && <AppBar position="static" sx={{mb: 4}}>
                <Toolbar>
                    <img
                        alt="Arma Server Manager Logo"
                        title="Arma Server Manager"
                        src={logo}
                        style={{height: 52}}
                    />
                    <Stack direction="row" spacing={1}
                           sx={{marginLeft: 4, justifyContent: "flex-start", alignItems: "center", flexGrow: 1}}
                    >
                        <Button color="success" component={NavLink} to="/" sx={{color: '#fff'}}>
                            Dashboard
                        </Button>
                        <PermissionGuard permission="SERVER_VIEW">
                            <Button component={NavLink} to="/servers" sx={{color: '#fff'}}>
                                Servers
                            </Button>
                        </PermissionGuard>
                        <PermissionGuard permission="MOD_VIEW">
                            <Button component={NavLink} to="/mods" sx={{color: '#fff'}}>
                                Mods
                            </Button>
                        </PermissionGuard>
                        <PermissionGuard permission="APPLICATION_LOGS_VIEW">
                            <Button component={NavLink} to="/tools" sx={{color: '#fff'}}>
                                Tools
                            </Button>
                        </PermissionGuard>
                        <PermissionGuard permission={"MANAGE_APP_SETTINGS"}>
                            <Button component={NavLink} to="/settings" sx={{color: '#fff'}}>
                                Settings
                            </Button>
                        </PermissionGuard>
                        <PermissionGuard permission="ADDITIONAL_SERVER_VIEW">
                            <Button component={NavLink} to="/additionalServers" sx={{color: '#fff'}}>
                                Additional servers
                            </Button>
                        </PermissionGuard>
                        <PermissionGuard permission="USER_ADMIN">
                            <Button component={NavLink} to="/users" sx={{color: '#fff'}}>
                                Users
                            </Button>
                        </PermissionGuard>
                    </Stack>
                    <Stack direction="row" sx={{flexGrow: 0}}>
                        <IconButton onClick={onModeChange}>
                            {mode === "dark" ? <LightModeIcon/> : <DarkModeIcon style={{color: "white"}}/>}
                        </IconButton>
                        <Tooltip title="Support this project">
                            <IconButton component={NavLink} to="/about">
                                <FavoriteIcon sx={{color: "#ff5a79"}}/>
                            </IconButton>
                        </Tooltip>
                        <Button component={NavLink} to="/about" sx={{color: '#fff'}}>
                            About
                        </Button>
                        <Button component={NavLink} to="/profile" sx={{color: '#fff'}}>
                            {authCtx.currentUser?.username ?? "Profile"}
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