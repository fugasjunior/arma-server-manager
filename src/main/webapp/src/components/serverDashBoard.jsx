import React, {Component} from "react";
import {
    getServerSettings,
    getServerStatus,
    queryServer,
    restartServer,
    startServer,
    stopServer,
    updateServer
} from "../services/serverService";
import ServerStatus from "./serverStatus";
import {getSystemInfo} from "../services/systemService";
import {humanFileSize} from "../util/util";
import {toast} from "react-toastify";

class ServerDashBoard extends Component {

    state = {
        serverStatus: {},
        serverSettings: {},
        systemInfo: {},
        alive: false
    };

    async componentDidMount() {
        const {data: serverSettings} = await getServerSettings();
        const {data: alive} = await getServerStatus();
        const {data: systemInfo} = await getSystemInfo();
        this.setState({alive, serverSettings, systemInfo});

        await this.updateServerStatus();
        this.interval = setInterval(this.updateServerStatus, 10000);
    };

    componentWillUnmount() {
        clearInterval(this.interval);
    };

    updateServerStatus = async () => {
        const {data: systemInfo} = await getSystemInfo();
        const {data: alive} = await getServerStatus();
        const {data: serverStatus} = await queryServer();
        this.setState({serverStatus, alive, systemInfo});
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

    handleUpdate = async () => {
        const systemInfo = {...this.state.systemInfo};
        systemInfo.updating = true;
        this.setState({systemInfo});

        try {
            await updateServer();
        } catch (e) {
            console.error(e);
            toast.error("Server could not be updated");
        }
    };

    renderControlButtons = () => {
        const {alive} = this.state;
        return (
            <React.Fragment>
                {alive ?
                    <button className="btn btn-lg btn-danger m-3"
                            onClick={this.handleStop}>
                        Stop
                    </button>
                    :
                    <button className="btn btn-lg btn-primary m-3"
                            onClick={this.handleStart}>
                        Start
                    </button>
                }
                <button className="btn btn-lg btn-secondary m-3"
                        onClick={this.handleRestart}
                >
                    Restart
                </button>
                <button className="btn btn-lg btn-warning m-3"
                        onClick={this.handleUpdate}
                >
                    Update
                </button>
            </React.Fragment>
        );
    };

    getSpaceProgressBarClassname = (ratio) => {
        const spaceWarning = ratio > 75;
        const spaceDanger = ratio > 90;

        if (spaceDanger) return "progress-bar bg-danger";
        if (spaceWarning) return "progress-bar bg-warning"
        return "progress-bar";
    }

    render() {
        const {serverStatus, alive, systemInfo, serverSettings} = this.state;
        const {maxPlayers} = serverSettings;
        const {hostName, port, spaceLeft, spaceTotal, updating} = systemInfo;

        const spaceRatio = Math.ceil(100 - (spaceLeft / spaceTotal) * 100);

        return (
            <React.Fragment>
                <h2>Server Dashboard</h2>
                {updating ?
                    <div className="text-center">
                        <h3>Server is updating, please wait...</h3>
                        <div className="spinner-border mt-3"
                             role="status">
                            <span className="sr-only">Loading...</span>
                        </div>
                    </div>
                    :
                    <div className="row">
                        <div className="col-sm-6">
                            {this.renderControlButtons()}
                            {systemInfo &&
                            <div>
                                <h3>System Info</h3>
                                <p>Server IP: {hostName + ":" + port}</p>
                                <p>
                                    Space
                                    left: {spaceLeft && humanFileSize(spaceLeft)} / {spaceTotal && humanFileSize(spaceTotal)}
                                    <div className="progress">
                                        <div className={this.getSpaceProgressBarClassname(spaceRatio)}
                                             role="progressbar"
                                             style={{width: spaceRatio + "%"}}
                                             aria-valuenow={spaceRatio}
                                             aria-valuemin="0"
                                             aria-valuemax="100"/>
                                    </div>
                                </p>
                            </div>
                            }
                        </div>
                        <div className="col-6">
                            <ServerStatus status={serverStatus} maxPlayers={maxPlayers} alive={alive}/>
                        </div>
                    </div>
                }
            </React.Fragment>
        )
    };
}

export default ServerDashBoard;