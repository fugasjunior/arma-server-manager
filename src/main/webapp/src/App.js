import React, {Component} from 'react';
import {ToastContainer} from "react-toastify";
import './App.css';
import Navbar from "./components/navbar";
import Mods from "./components/mods";
import {Redirect, Route, Switch} from "react-router-dom";
import NotFound from "./components/notFound";
import ServerSettingsForm from "./components/serverSettingsForm";
import ServerDashBoard from "./components/serverDashBoard";
import Scenarios from "./components/scenarios";
import AppConfig from "./components/appConfig";
import AdditionalServers from "./components/additionalServers";
import {getSystemInfo} from "./services/systemService";
import {getServerStatus, queryServer} from "./services/serverService";
import Login from "./components/login";
import {isAuthenticated} from "./services/authService";
import ProtectedRoute from "./components/protectedRoute";
import Logout from "./components/logout";

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
                        <Switch>
                            <Route path="/login" exact component={Login}/>
                            <Route path="/logout" exact component={Logout}/>
                            <ProtectedRoute path="/dashboard"
                                            render={() => <ServerDashBoard
                                                    systemInfo={systemInfo}
                                                    serverStatus={serverStatus}
                                                    alive={alive}
                                                    onUpdate={this.updateServerStatus}
                                            />
                                            }
                            />
                            <ProtectedRoute path="/settings" component={ServerSettingsForm}/>
                            <ProtectedRoute path="/scenarios" component={Scenarios}/>
                            <ProtectedRoute path="/mods" component={Mods}/>
                            <ProtectedRoute path="/config" component={AppConfig}/>
                            <ProtectedRoute path="/additionalServers" component={AdditionalServers}/>
                            <Route path="/notFound" component={NotFound}/>
                            <Redirect from="/" exact to="/dashboard"/>
                            <Redirect to="/notFound"/>
                        </Switch>
                    </main>
                </React.Fragment>
        );
    }
}

export default App;
