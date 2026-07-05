import {useContext, useState} from "react";
import {NavLink, useNavigate} from "react-router-dom";
import {
    AppBar,
    Box,
    Button,
    Divider,
    Drawer,
    IconButton,
    List,
    ListItemButton,
    ListItemText,
    Stack,
    Toolbar,
    Tooltip,
    useMediaQuery,
    useTheme,
} from "@mui/material";
import {AuthContext} from "../store/auth-context";
import logo from "../img/asm_logo.png"
import LightModeIcon from '@mui/icons-material/LightMode';
import DarkModeIcon from '@mui/icons-material/DarkMode';
import FavoriteIcon from '@mui/icons-material/Favorite';
import MenuIcon from '@mui/icons-material/Menu';
import PermissionGuard from "./auth/PermissionGuard";

type NavbarProps = {
    onModeChange: () => void,
    mode: "light" | "dark"
}

const mainNavItems = [
    {label: "Dashboard", to: "/", permission: undefined},
    {label: "Servers", to: "/servers", permission: "SERVER_VIEW"},
    {label: "Mods", to: "/mods", permission: "MOD_VIEW"},
    {label: "Tools", to: "/tools", permission: "APPLICATION_LOGS_VIEW"},
    {label: "Settings", to: "/settings", permission: "MANAGE_APP_SETTINGS"},
    {label: "Additional servers", to: "/additionalServers", permission: "ADDITIONAL_SERVER_VIEW"},
    {label: "Users", to: "/users", permission: "USER_ADMIN"},
];

const Navbar = ({onModeChange, mode}: NavbarProps) => {
    const authCtx = useContext(AuthContext);
    const isLoggedIn = authCtx.isLoggedIn;
    const navigate = useNavigate();
    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("md"));
    const [drawerOpen, setDrawerOpen] = useState(false);

    const handleLogout = async () => {
        await authCtx.logout();
        navigate("/login");
    }

    const closeDrawer = () => setDrawerOpen(false);

    const navLink = (label: string, to: string) => (
        <ListItemButton component={NavLink} to={to} onClick={closeDrawer} sx={{color: "inherit"}}>
            <ListItemText primary={label}/>
        </ListItemButton>
    );

    const mobileDrawer = (
        <Drawer anchor="left" open={drawerOpen} onClose={closeDrawer}>
            <Box sx={{width: 240}} role="presentation">
                <List>
                    {mainNavItems.map(({label, to, permission}) =>
                        permission
                            ? <PermissionGuard key={to} permission={permission}>{navLink(label, to)}</PermissionGuard>
                            : <span key={to}>{navLink(label, to)}</span>
                    )}
                </List>
                <Divider/>
                <List>
                    {navLink("About", "/about")}
                    {navLink(authCtx.currentUser?.username ?? "Profile", "/profile")}
                    <ListItemButton onClick={() => { closeDrawer(); handleLogout(); }}>
                        <ListItemText primary="Log out"/>
                    </ListItemButton>
                </List>
            </Box>
        </Drawer>
    );

    return (
        <>
            {isLoggedIn && <AppBar position="static" sx={{mb: 4}}>
                <Toolbar>
                    {isMobile && (
                        <IconButton color="inherit" edge="start" onClick={() => setDrawerOpen(true)} sx={{mr: 1}}>
                            <MenuIcon/>
                        </IconButton>
                    )}
                    <Box
                        component="img"
                        alt="Arma Server Manager Logo"
                        title="Arma Server Manager"
                        src={logo}
                        sx={{height: 52, display: {xs: "block", md: "none", lg: "block"}}}
                    />
                    {!isMobile && (
                        <Stack direction="row" spacing={1}
                               sx={{marginLeft: 4, justifyContent: "flex-start", alignItems: "center", flexGrow: 1}}
                        >
                            {mainNavItems.map(({label, to, permission}) =>
                                permission
                                    ? <PermissionGuard key={to} permission={permission}>
                                        <Button component={NavLink} to={to} sx={{color: '#fff'}}>{label}</Button>
                                      </PermissionGuard>
                                    : <Button key={to} color="success" component={NavLink} to={to} sx={{color: '#fff'}}>{label}</Button>
                            )}
                        </Stack>
                    )}
                    <Stack direction="row" sx={{flexGrow: isMobile ? 1 : 0, justifyContent: "flex-end"}}>
                        <IconButton onClick={onModeChange} color="inherit">
                            {mode === "dark" ? <LightModeIcon/> : <DarkModeIcon style={{color: "white"}}/>}
                        </IconButton>
                        <Tooltip title="Support this project">
                            <IconButton component={NavLink} to="/about">
                                <FavoriteIcon sx={{color: "#ff5a79"}}/>
                            </IconButton>
                        </Tooltip>
                        {!isMobile && <>
                            <Button component={NavLink} to="/about" sx={{color: '#fff'}}>
                                About
                            </Button>
                            <Button component={NavLink} to="/profile" sx={{color: '#fff'}}>
                                {authCtx.currentUser?.username ?? "Profile"}
                            </Button>
                            <Button onClick={handleLogout} sx={{color: '#fff'}}>
                                Log out
                            </Button>
                        </>}
                    </Stack>
                </Toolbar>
            </AppBar>}
            {isLoggedIn && mobileDrawer}
        </>
    );
};

export default Navbar;
