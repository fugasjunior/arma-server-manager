import ListBuilderModEdit from "./ListBuilderModEdit";
import ReforgerModEdit from "./ReforgerModEdit";
import {ReforgerServerDto, ServerDto, ServerInstanceInfoDto, ConfigOverrideDto} from "../../api/generated";
import {Tooltip} from "@mui/material";

type ModEditButtonProps = {
    server: ServerDto
    serverStatus: ServerInstanceInfoDto | null
}

const ModEditButton = ({server, serverStatus}: ModEditButtonProps) => {
    if (server.type === "REFORGER") {
        const configOverrides = (server as any).configOverrides as ConfigOverrideDto[] | undefined;
        const isAdvanced = (configOverrides ?? []).some(o => o.configKey === 'REFORGER_JSON');
        if (isAdvanced) {
            return (
                <Tooltip title="Mods are managed through the raw JSON config. Disable advanced config editing to use this interface.">
                    <span>
                        <ReforgerModEdit server={server as ReforgerServerDto} serverStatus={serverStatus} disabled={true}/>
                    </span>
                </Tooltip>
            );
        }
        return <ReforgerModEdit server={server as ReforgerServerDto} serverStatus={serverStatus}/>;
    } else {
        return <ListBuilderModEdit status={serverStatus} server={server}/>;
    }
}

export default ModEditButton;