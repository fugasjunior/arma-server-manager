import React from 'react';
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
import ReactTooltip from "react-tooltip";

function App() {
    return (
        <React.Fragment>
            <ReactTooltip/>
            <ToastContainer/>
            <Navbar/>
            <main role="main" className="container">
                <Switch>
                    <Route path="/dashboard" component={ServerDashBoard}/>
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
}

export default App;
