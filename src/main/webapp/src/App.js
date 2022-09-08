import React, {Component} from 'react';
import {ToastContainer} from "react-toastify";
import './App.css';
import Navbar from "./components/navbar";
import Mods from "./components/mods";
import {Navigate, Route, Routes} from "react-router-dom";
import NotFound from "./components/notFound";
import ServerSettingsForm from "./components/serverSettingsForm";
import ServerDashBoard from "./components/serverDashBoard";
import Scenarios from "./components/scenarios";
import AppConfig from "./components/appConfig";
import AdditionalServers from "./components/additionalServers";
import {getSystemInfo} from "./services/systemService";
import {getServerStatus, queryServer} from "./services/serverService";
import Login from "./components/auth/login";
import {isAuthenticated} from "./services/authService";
import ProtectedRoute from "./components/protectedRoute";
import Logout from "./components/auth/logout";
import CreatorDLCs from "./components/creatorDLCs";

class App extends Component {
    state = {
        systemInfo: {},
        serverStatus: {},
        alive: false,
        authenticated: false
    }

    async componentDidMount() {
        await this.updateServerStatus();
        this.interval = setInterval(this.updateServerStatus, 10000);

        const authenticated = isAuthenticated();
        this.setState({authenticated});
    }

    componentWillUnmount() {
        clearInterval(this.interval);
    }

    updateServerStatus = async () => {
        const {data: systemInfo} = await getSystemInfo();
        const {data: alive} = await getServerStatus();
        const {data: serverStatus} = await queryServer();
        this.setState({serverStatus, alive, systemInfo});
    };

    render() {
        const {systemInfo, serverStatus, alive} = this.state;
        return (
                <React.Fragment>
                    <ToastContainer/>
                    <Navbar/>
                    <main role="main" className="container">
                        <Routes>
                            <Route path="/login" exact element={<Login/>}/>
                            <Route path="/logout" exact element={<Logout/>}/>
                            <ProtectedRoute path="/dashboard"
                                            render={() => <ServerDashBoard
                                                    systemInfo={systemInfo}
                                                    serverStatus={serverStatus}
                                                    alive={alive}
                                                    onUpdate={this.updateServerStatus}
                                            />
                                            }
                            />
                            <ProtectedRoute path="/settings" element={<ServerSettingsForm/>}/>
                            <ProtectedRoute path="/scenarios" element={<Scenarios/>}/>
                            <ProtectedRoute path="/mods" element={<Mods/>}/>
                            <ProtectedRoute path="/creatordlcs" element={<CreatorDLCs/>}/>
                            <ProtectedRoute path="/config" element={<AppConfig/>}/>
                            <ProtectedRoute path="/additionalServers" element={<AdditionalServers/>}/>
                            <Route path="/notFound" element={<NotFound/>}/>
                            <Navigate from="/" exact to="/dashboard"/>
                            <Navigate to="/notFound"/>
                        </Routes>
                    </main>
                </React.Fragment>
        );
    }
}

export default App;
