import {ToastContainer} from "material-react-toastify";
import './App.css';
import Navbar from "./components/Navbar.jsx";
import {Route, Routes} from "react-router-dom";
import Login from "./components/auth/Login.jsx";
import ProtectedRoute from "./components/ProtectedRoute.jsx";
import ServersPage from "./pages/ServersPage.jsx";
import ServerSettingsPage from "./pages/ServerSettingsPage.jsx";
import ModsPage from "./pages/ModsPage.jsx";
import NewServerPage from "./pages/NewServerPage.jsx";
import {Container} from "@mui/material";
import 'material-react-toastify/dist/ReactToastify.css'
import ScenariosPage from "./pages/ScenariosPage.jsx";
import AppConfigPage from "./pages/AppConfigPage.jsx";
import AdditionalServersPage from "./pages/AdditionalServersPage.jsx";
import DashboardPage from "./pages/DashboardPage.jsx";

const App = () => {
    return (
            <>
                <Navbar/>
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
                        <Route path="config" element={
                            <ProtectedRoute><AppConfigPage/></ProtectedRoute>
                        }/>
                        <Route path="additionalServers" element={
                            <ProtectedRoute><AdditionalServersPage/></ProtectedRoute>
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
