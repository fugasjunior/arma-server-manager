import ListBuilderModEdit from "./ListBuilderModEdit";
import ReforgerModEdit from "./ReforgerModEdit";
import {ReforgerServerDto, ServerDto} from "../../dtos/ServerDto.ts";

type ModEditButtonProps = {
    server: ServerDto
}

const ModEditButton = ({server}: ModEditButtonProps) => {
    if (server.type === "REFORGER") {
        return <ReforgerModEdit server={server as ReforgerServerDto}/>;
    } else {
        return <ListBuilderModEdit server={server}/>;
    }
}

export default ModEditButton;