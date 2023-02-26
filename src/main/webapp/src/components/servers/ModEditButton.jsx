import React from "react";
import ListBuilderModEdit from "./ListBuilderModEdit";

const ModEditButton = (props) => {
    const server = props.server;
    if (server.type === "REFORGER") {
        // TODO
    } else {
        return <ListBuilderModEdit server={props.server}/>;
    }
}

export default ModEditButton;