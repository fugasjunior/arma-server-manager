import React from 'react';
import {ToastContainer} from "material-react-toastify";
import './App.css';
import Navbar from "./components/navbar";
import {Route, Routes} from "react-router-dom";
import AppConfig from "./components/appConfig";
import AdditionalServers from "./components/additionalServers/additionalServers";
import Login from "./components/auth/login";
import ProtectedRoute from "./components/protectedRoute";
import Logout from "./components/auth/logout";
import ServersPage from "./pages/ServersPage";
import ServerSettingsPage from "./pages/ServerSettingsPage";
import ModsPage from "./pages/ModsPage";
import NewServerPage from "./pages/NewServerPage";
import {Container} from "@mui/material";
import 'material-react-toastify/dist/ReactToastify.css'
import ScenariosPage from "./pages/ScenariosPage";

const App = () => {
    return (
            <>
                <Navbar/>
                <Container>
                    <Routes>
                        <Route index element={
                            <ProtectedRoute><ServersPage/></ProtectedRoute>
                        }/>
                        <Route path="login" exact element={<Login/>}/>
                        <Route path="logout" exact element={<Logout/>}/>
                        <Route path="servers" element={
                            <ProtectedRoute><ServersPage/></ProtectedRoute>
                        }/>
                        <Route path="servers/new" element={
                            <ProtectedRoute><NewServerPage/></ProtectedRoute>
                        }/>
                        <Route path="servers/:id" element={
                            <ProtectedRoute><ServerSettingsPage/></ProtectedRoute>
                        }/>
                        <Route path="scenarios" element={
                            <ProtectedRoute><ScenariosPage/></ProtectedRoute>
                        }/>
                        <Route path="mods" element={
                            <ProtectedRoute><ModsPage/></ProtectedRoute>
                        }/>
                        <Route path="config" element={
                            <ProtectedRoute><AppConfig/></ProtectedRoute>
                        }/>
                        <Route path="additionalServers" element={
                            <ProtectedRoute><AdditionalServers/></ProtectedRoute>
                        }/>
                    </Routes>
                </Container>
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
            </>
    );
}

export default App;
