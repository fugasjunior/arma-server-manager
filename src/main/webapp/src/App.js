import React, {Component} from 'react';
import {ToastContainer} from "react-toastify";
import './App.css';
import Navbar from "./components/navbar";
import Mods from "./components/mods";
import {Route, Routes} from "react-router-dom";
import NotFound from "./components/notFound";
import ServerDashBoard from "./components/serverDashBoard";
import Scenarios from "./components/scenarios";
import AppConfig from "./components/appConfig";
import AdditionalServers from "./components/additionalServers";
import Login from "./components/auth/login";
import {isAuthenticated} from "./services/authService";
import ProtectedRoute from "./components/protectedRoute";
import Logout from "./components/auth/logout";
import CreatorDLCs from "./components/creatorDLCs";
import ServersPage from "./pages/ServersPage";
import ServerSettingsPage from "./pages/ServerSettingsPage";

class App extends Component {
    state = {
        systemInfo: {},
        serverStatus: {},
        alive: false,
        authenticated: false
    }

    async componentDidMount() {
        const authenticated = isAuthenticated();
        this.setState({authenticated});
    }

    componentWillUnmount() {
        clearInterval(this.interval);
    }

    render() {
        const {systemInfo, serverStatus, alive} = this.state;
        return (
                <React.Fragment>
                    <ToastContainer/>
                    <Navbar/>
                    <main role="main" className="container">
                        <Routes>
                            <Route index element={
                                <ProtectedRoute><ServersPage/></ProtectedRoute>
                            }/>
                            <Route path="login" exact element={<Login/>}/>
                            <Route path="logout" exact element={<Logout/>}/>
                            <Route path="servers/:id" element={
                                <ProtectedRoute><ServerSettingsPage/></ProtectedRoute>
                            }/>
                            <Route path="scenarios" element={
                                <ProtectedRoute><Scenarios/></ProtectedRoute>
                            }/>
                            <Route path="mods" element={
                                <ProtectedRoute><Mods/></ProtectedRoute>
                            }/>
                            <Route path="creatordlcs" element={
                                <ProtectedRoute><CreatorDLCs/></ProtectedRoute>
                            }/>
                            <Route path="config" element={
                                <ProtectedRoute><AppConfig/></ProtectedRoute>
                            }/>
                            <Route path="additionalServers" element={
                                <ProtectedRoute><AdditionalServers/></ProtectedRoute>
                            }/>
                            <Route path="notFound" element={<NotFound/>}/>
                        </Routes>
                    </main>
                </React.Fragment>
        );
    }
}

export default App;
