import React, {Component} from "react";
import {
    getServerSettings,
    getServerStatus,
    queryServer,
    restartServer,
    startServer,
    stopServer
} from "../services/serverService";
import ServerStatus from "./serverStatus";

class ServerDashBoard extends Component {

    state = {
        serverStatus: {},
        serverSettings: {},
        alive: false
    };

    async componentDidMount() {
        const {data: serverSettings} = await getServerSettings();
        const {data: alive} = await getServerStatus();
        this.setState({alive, serverSettings});

        await this.updateServerStatus();
        this.interval = setInterval(this.updateServerStatus, 10000);
    };

    componentWillUnmount() {
        clearInterval(this.interval);
    };

    updateServerStatus = async () => {
        const {data: alive} = await getServerStatus();
        const {data: serverStatus} = await queryServer();
        this.setState({serverStatus, alive});
    };

    handleStart = async () => {
        await startServer();
        const {data: alive} = await getServerStatus();
        this.setState({alive});
    };

    handleStop = async () => {
        await stopServer();
        this.setState({alive: false});
    };

    handleRestart = async () => {
        await restartServer();
        const serverStatus = {...this.state.serverStatus};
        serverStatus.serverUp = false;
        this.setState({serverStatus});
    };

    renderControlButtons = () => {
        const {alive} = this.state;
        return (
            <React.Fragment>
                {alive ?
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
        const {serverStatus, alive} = this.state;
        const {maxPlayers} = this.state.serverSettings;

        return (
            <React.Fragment>
                <h2>Server Dashboard</h2>
                <div className="row">
                    <div className="col-sm-6">
                        {this.renderControlButtons()}
                    </div>
                    <div className="col-6">
                        <ServerStatus status={serverStatus} maxPlayers={maxPlayers} alive={alive}/>
                    </div>
                </div>
            </React.Fragment>
        )
    };
}

export default ServerDashBoard;