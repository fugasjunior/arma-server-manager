import ListBuilderModEdit from "./ListBuilderModEdit";
import ReforgerModEdit from "./ReforgerModEdit";
import {ReforgerServerDto, ServerDto} from "../../dtos/ServerDto.ts";

type ModEditButtonProps = {
    server: ServerDto
    serverStatus: ServerInstanceInfoDto | null
}

const ModEditButton = ({server, serverStatus}: ModEditButtonProps) => {
    if (server.type === "REFORGER") {
        return <ReforgerModEdit server={server as ReforgerServerDto} serverStatus={serverStatus}/>;
    } else {
        return <ListBuilderModEdit server={server}/>;
    }
}

export default ModEditButton;