import React from "react";
import ListBuilderModEdit from "./ListBuilderModEdit";
import ReforgerModEdit from "./ReforgerModEdit";

const ModEditButton = (props) => {
    const server = props.server;
    const serverRunning = server.instanceInfo && server.instanceInfo.alive;
    if (server.type === "REFORGER") {
        return <ReforgerModEdit server={props.server}/>;
    } else {
        return <ListBuilderModEdit server={props.server} confirmDisabled={serverRunning}/>;
    }
}

export default ModEditButton;