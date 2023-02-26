import React from "react";
import Arma3ModEditButton from "./Arma3ModEdit";

const ModEditButton = (props) => {
    const server = props.server;
    if (server.type === "ARMA3") {
        return <Arma3ModEditButton serverId={props.server.id}/>;
    } else if (server.type === "DAYZ" || server.type === "DAYZ_EXP") {
        // TODO
    } else if (server.type === "REFORGER") {
        // TODO
    }
}

export default ModEditButton;