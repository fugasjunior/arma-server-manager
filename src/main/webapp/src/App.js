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
import {getSystemInfo} from "./services/systemService";
import {getServerStatus, queryServer} from "./services/serverService";

class App extends Component {
    state = {
        systemInfo: {},
        serverStatus: {},
        alive: false
    }

    async componentDidMount() {
        await this.updateServerStatus();
        this.interval = setInterval(this.updateServerStatus, 10000);
    };

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
                        <Route path="/dashboard"
                               render={() => <ServerDashBoard
                                   systemInfo={systemInfo}
                                   serverStatus={serverStatus}
                                   alive={alive}
                                   onUpdate={this.updateServerStatus}
                               />
                               }
                        />
                        <Route path="/settings" component={ServerSettingsForm}/>
                        <Route path="/scenarios" component={Scenarios}/>
                        <Route path="/mods" component={Mods}/>
                        <Route path="/config" component={AppConfig}/>
                        <Route path="/not-found" component={NotFound}/>
                        <Redirect from="/" exact to="/dashboard"/>
                        <Redirect to="/not-found"/>
                    </Switch>
                </main>
            </React.Fragment>
        );
    };
}

export default App;
