import React, {Component} from "react";
import ServerStatus from "./serverStatus";
import {humanFileSize} from "../util/util";
import {toast} from "react-toastify";

class ServerDashBoard extends Component {

    state = {
        serverSettings: {},
    };

    renderControlButtons = () => {
        const {alive} = this.props;
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

    getProgressBarClassname = (ratio) => {
        const spaceWarning = ratio > 75;
        const spaceDanger = ratio > 90;

        if (spaceDanger) return "progress-bar bg-danger";
        if (spaceWarning) return "progress-bar bg-warning"
        return "progress-bar";
    }

    render() {
        const {alive, serverStatus, systemInfo} = this.props;
        const {serverSettings} = this.state;
        const {maxPlayers} = serverSettings;
        const {hostName, port, spaceLeft, spaceTotal, memoryLeft, memoryTotal, cpuUsage, updating} = systemInfo;

        const spaceUsed = spaceTotal - spaceLeft;
        const memoryUsed = memoryTotal - memoryLeft;

        const spaceRatio = Math.ceil((spaceUsed / spaceTotal) * 100);
        const memoryRatio = Math.ceil((memoryUsed / memoryTotal) * 100);
        const cpuRatio = Math.ceil(cpuUsage * 100);

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
                                <div>
                                    CPU usage: {cpuRatio}%
                                    <div className="progress">
                                        <div className={this.getProgressBarClassname(cpuRatio)}
                                             role="progressbar"
                                             style={{width: cpuRatio + "%"}}
                                             aria-valuenow={cpuRatio}
                                             aria-valuemin="0"
                                             aria-valuemax="100"/>
                                    </div>
                                </div>
                                <div>
                                    Memory: {memoryLeft && humanFileSize(memoryUsed)} / {memoryTotal && humanFileSize(memoryTotal)}
                                    <div className="progress">
                                        <div className={this.getProgressBarClassname(memoryRatio)}
                                             role="progressbar"
                                             style={{width: memoryRatio + "%"}}
                                             aria-valuenow={memoryRatio}
                                             aria-valuemin="0"
                                             aria-valuemax="100"/>
                                    </div>
                                </div>
                                <div>
                                    Storage
                                    used: {spaceLeft && humanFileSize(spaceUsed)} / {spaceTotal && humanFileSize(spaceTotal)}
                                    <div className="progress">
                                        <div className={this.getProgressBarClassname(spaceRatio)}
                                             role="progressbar"
                                             style={{width: spaceRatio + "%"}}
                                             aria-valuenow={spaceRatio}
                                             aria-valuemin="0"
                                             aria-valuemax="100"/>
                                    </div>
                                </div>
                            </div>
                            }
                        </div>
                        <div className="col-6">
                            <ServerStatus status={serverStatus}
                                          maxPlayers={maxPlayers}
                                          alive={alive}/>
                        </div>
                    </div>
                }
            </React.Fragment>
        )
    };
}

export default ServerDashBoard;