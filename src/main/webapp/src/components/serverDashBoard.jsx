import React, {Component} from "react";
import {getServerSettings, getStatus, restartServer, startServer, stopServer} from "../services/serverService";
import ServerStatus from "./serverStatus";

class ServerDashBoard extends Component {

    state = {
        serverStatus: {},
        serverSettings: {}
    };

    async componentDidMount() {
        await this.updateServerStatus();

        const {data: serverSettings} = await getServerSettings();
        this.setState({serverSettings});

        this.interval = setInterval(this.updateServerStatus, 10000);
    };

    componentWillUnmount() {
        clearInterval(this.interval);
    };

    updateServerStatus = async () => {
        const {data: serverStatus} = await getStatus();
        this.setState({serverStatus});
    };

    handleStart = async () => {
        await startServer();
    };

    handleStop = async () => {
        await stopServer();
        const serverStatus = {...this.state.serverStatus};
        serverStatus.serverUp = false;
        this.setState({serverStatus});
    };

    handleRestart = async () => {
        await restartServer();
        const serverStatus = {...this.state.serverStatus};
        serverStatus.serverUp = false;
        this.setState({serverStatus});
    };

    renderControlButtons = () => {
        const {serverUp} = this.state.serverStatus;
        return (
            <React.Fragment>
                {(serverUp || serverUp === null || serverUp === undefined) ?
                    <button className="btn btn-lg btn-danger m-2"
                            onClick={this.handleStop}>
                        Stop
                    </button>
                    :
                    <button className="btn btn-lg btn-primary m-2"
                            onClick={this.handleStart}>
                        Start
                    </button>
                }
                <button className="btn btn-lg btn-secondary m-2"
                        onClick={this.handleRestart}
                >
                    Restart
                </button>
            </React.Fragment>
        );
    };

    render() {
        const {serverStatus} = this.state;
        const {maxPlayers} = this.state.serverSettings;

        return (
            <React.Fragment>
                <h2>Server Dashboard</h2>
                <div className="row">
                    <div className="col-sm-6">
                        {this.renderControlButtons()}
                    </div>
                    <div className="col-6">
                        <ServerStatus status={serverStatus} maxPlayers={maxPlayers}/>
                    </div>
                </div>
            </React.Fragment>

        )
    };
}

export default ServerDashBoard;