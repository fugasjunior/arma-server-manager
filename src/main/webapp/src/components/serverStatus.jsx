import React from "react";

const renderServerStatus = (alive) => {
    return (
        <span className={"badge badge-" + (alive ? "success" : "danger")}>
                {alive ? "ONLINE" : "OFFLINE"}
            </span>
    );
};

const ServerStatus = ({status, maxPlayers, alive}) => {
    return (
        <React.Fragment>
            <h3>Server status</h3>
            <h4>
                {renderServerStatus(alive)}
            </h4>
            {alive && !status.serverUp &&
            <React.Fragment>
                <div className="spinner-border ml-5 mt-3" role="status">
                    <span className="sr-only">Loading...</span>
                </div>
                <p>Loading server info...</p>
            </React.Fragment>
            }
            {alive && status.serverUp &&
            <table className="table table-borderless">
                <tbody>
                <tr>
                    <th scope="row">Players</th>
                    <td>{status.playersOnline} / {Math.min(status.maxPlayers, maxPlayers)}</td>
                </tr>
                <tr>
                    <th scope="row">Description</th>
                    <td>{status.gameDescription}</td>
                </tr>
                {status.mapName &&
                <tr>
                    <th scope="row">Map</th>
                    <td>{status.mapName}</td>
                </tr>
                }
                <tr>
                    <th scope="row">Game version</th>
                    <td>{status.gameVersion}</td>
                </tr>
                </tbody>
            </table>
            }
        </React.Fragment>
    );
};

export default ServerStatus;