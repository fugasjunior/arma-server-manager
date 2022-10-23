import React from "react";
import {Link, NavLink} from "react-router-dom";
import {isUserAuthenticated} from "../services/authService";
import {AppBar, Box, Button, CssBaseline, MenuItem, Stack, Toolbar, Typography} from "@mui/material";

const Navbar = () => {
    return (
            <AppBar position="static" sx={{mb: 4}}>
                <CssBaseline/>
                <Toolbar>
                    <Typography variant="h4">
                        Arma Server GUI
                    </Typography>
                    <Stack marginLeft={4}
                           direction="row"
                           justifyContent="flex-start"
                           alignItems="center"
                           spacing={2}>
                        <Button color="success" component={NavLink} to="/" sx={{ color: '#fff' }}>
                            Dashboard
                        </Button>
                        <Button component={NavLink} to="/servers" sx={{ color: '#fff' }}>
                            Servers
                        </Button>
                        <Button component={NavLink} to="/mods" sx={{ color: '#fff' }}>
                            Mods
                        </Button>
                        <Button component={NavLink} to="/scenarios" sx={{ color: '#fff' }}>
                            Scenarios
                        </Button>
                        <Button component={NavLink} to="/config" sx={{ color: '#fff' }}>
                            App config
                        </Button>
                        <Button component={NavLink} to="/additionalServers" sx={{ color: '#fff' }}>
                            Additional servers
                        </Button>
                        {isUserAuthenticated() && <Button component={NavLink} to="/logout" sx={{ color: '#fff' }} style={{justifySelf: "flex-end"}}>
                                Log out
                        </Button>}
                    </Stack>
                </Toolbar>
            </AppBar>
    );
};

export default Navbar;