import React from "react";
import ListBuilderModEdit from "./ListBuilderModEdit";
import ReforgerModEdit from "./ReforgerModEdit";

const ModEditButton = (props) => {
    const server = props.server;
    if (server.type === "REFORGER") {
        return <ReforgerModEdit server={props.server}/>;
    } else {
        return <ListBuilderModEdit server={props.server}/>;
    }
}

export default ModEditButton;