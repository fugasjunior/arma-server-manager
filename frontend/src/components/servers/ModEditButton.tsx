import ListBuilderModEdit from "./ListBuilderModEdit";
import ReforgerModEdit from "./ReforgerModEdit";
import {ReforgerServerDto, ServerDto, ServerInstanceInfoDto} from "../../api/generated";

type ModEditButtonProps = {
    server: ServerDto
    serverStatus: ServerInstanceInfoDto | null
}

const ModEditButton = ({server, serverStatus}: ModEditButtonProps) => {
    if (server.type === "REFORGER") {
        return <ReforgerModEdit server={server as ReforgerServerDto} serverStatus={serverStatus}/>;
    } else {
        return <ListBuilderModEdit status={serverStatus} server={server}/>;
    }
}

export default ModEditButton;