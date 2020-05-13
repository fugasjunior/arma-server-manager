import React from "react";

const renderServerStatus = (serverUp) => {

    if (serverUp === null || serverUp === undefined) {
        return (
            <span className="badge badge-secondary">
                UNKNOWN
            </span>
        )
    }

    return (
        <span className={"badge badge-" + (serverUp ? "success" : "danger")}>
                {serverUp ? "ONLINE" : "OFFLINE"}
            </span>
    );
};

const ServerStatus = ({status, maxPlayers}) => {
    return (
        <React.Fragment>
            <h3>Server status</h3>
            <h4>
                {renderServerStatus(status.serverUp)}
            </h4>
            {status.serverUp &&
            <table className="table table-borderless">
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
            </table>
            }
        </React.Fragment>
    );
};

export default ServerStatus;