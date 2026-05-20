import {ToastContainer} from "react-toastify";
import './App.css';
import Navbar from "./components/Navbar";
import {Route, Routes} from "react-router-dom";
import Login from "./components/auth/Login";
import ProtectedRoute from "./components/ProtectedRoute";
import PermissionRoute from "./components/auth/PermissionRoute";
import ServersPage from "./pages/ServersPage";
import ServerSettingsPage from "./pages/ServerSettingsPage";
import ModsPage from "./pages/ModsPage";
import NewServerPage from "./pages/NewServerPage";
import {Container, createTheme, CssBaseline, ThemeProvider, useMediaQuery} from "@mui/material";
import 'react-toastify/dist/ReactToastify.css'
import ScenariosPage from "./pages/ScenariosPage";
import AppConfigPage from "./pages/AppConfigPage";
import AdditionalServersPage from "./pages/AdditionalServersPage";
import DashboardPage from "./pages/DashboardPage";
import AboutPage from "./pages/AboutPage.tsx";
import ToolsPage from "./pages/ToolsPage.tsx";
import UserManagementPage from "./pages/UserManagementPage.tsx";
import ProfilePage from "./pages/ProfilePage.tsx";
import {useContext, useMemo, useState} from "react";
import SteamAuthWizard from "./components/steamauthwizard/SteamAuthWizard";
import {AuthContext} from "./store/auth-context.tsx";

const getDefaultMode = (prefersDarkMode: boolean): "light" | "dark" => {
    const storedMode = localStorage.getItem("mode");
    if (storedMode === "light" || storedMode === "dark") {
        return storedMode;
    }

    return prefersDarkMode ? "dark" : "light";
}

const App = () => {
    const prefersDarkMode = useMediaQuery("(prefers-color-scheme: dark)");
    const [mode, setMode] = useState<"light" | "dark">(getDefaultMode(prefersDarkMode));
    const authCtx = useContext(AuthContext);

    const theme = useMemo(() => createTheme({
        palette: {
            mode,
        },
    }), [mode]);

    const toggleMode = () => {
        setMode(prevState => {
            const newState = prevState === "light" ? "dark" : "light";
            localStorage.setItem("mode", newState);
            return newState;
        });
    };

    return (
        <>
            <ThemeProvider theme={theme}>
                <CssBaseline/>
                <Navbar mode={mode} onModeChange={toggleMode}/>
                <Container>
                    <Routes>
                        <Route index element={
                            <ProtectedRoute><DashboardPage/></ProtectedRoute>
                        }/>
                        <Route path="login" element={<Login/>}/>
                        <Route path="servers" element={
                            <ProtectedRoute><ServersPage/></ProtectedRoute>
                        }/>
                        <Route path="servers/new/:type" element={
                            <ProtectedRoute><NewServerPage/></ProtectedRoute>
                        }/>
                        <Route path="servers/:id" element={
                            <ProtectedRoute><ServerSettingsPage/></ProtectedRoute>
                        }/>
                        <Route path="scenarios" element={
                            <ProtectedRoute><ScenariosPage/></ProtectedRoute>
                        }/>
                        <Route path="mods/:section" element={
                            <ProtectedRoute><ModsPage/></ProtectedRoute>
                        }/>
                        <Route path="mods" element={
                            <ProtectedRoute><ModsPage/></ProtectedRoute>
                        }/>
                        <Route path="tools" element={
                            <PermissionRoute permission="STEAM_AUTH_ADMIN"><ToolsPage/></PermissionRoute>
                        }/>
                        <Route path="settings" element={
                            <PermissionRoute permission="STEAM_AUTH_ADMIN"><AppConfigPage/></PermissionRoute>
                        }/>
                        <Route path="additionalServers" element={
                            <ProtectedRoute><AdditionalServersPage/></ProtectedRoute>
                        }/>
                        <Route path="about" element={
                            <ProtectedRoute><AboutPage/></ProtectedRoute>
                        }/>
                        <Route path="users" element={
                            <PermissionRoute permission="USER_ADMIN"><UserManagementPage/></PermissionRoute>
                        }/>
                        <Route path="profile" element={
                            <ProtectedRoute><ProfilePage/></ProtectedRoute>
                        }/>
                    </Routes>
                </Container>
                {authCtx.isLoggedIn && <SteamAuthWizard/>}
                <ToastContainer
                    position="bottom-left"
                    autoClose={3000}
                    hideProgressBar
                    newestOnTop={false}
                    closeOnClick
                    rtl={false}
                    pauseOnFocusLoss
                    draggable
                    pauseOnHover
                />
            </ThemeProvider>
        </>
    );
}


export default App;
