import {useNavigate, useParams} from "react-router-dom";
import {serversApi} from "../api/client";
import {toast} from "react-toastify";
import {Typography} from "@mui/material";
import EditArma3ServerSettingsForm from "../components/servers/EditArma3ServerSettingsForm";
import EditDayZServerSettingsForm from "../components/servers/EditDayZServerSettingsForm";
import SERVER_NAMES from "../util/serverNames";
import EditReforgerServerSettingsForm from "../components/servers/EditReforgerServerSettingsForm";
import {ServerDto, ServerType} from "../api/generated";
import {Arma3ServerDto, DayZServerDto, ReforgerServerDto} from "../api/serverModels";
import {useServer} from "../hooks/queries/useServer";
import {useServerStatus} from "../hooks/queries/useServerStatus";
import {useQueryClient} from "@tanstack/react-query";
import {queryKeys} from "../api/queryKeys";

const ServerSettingsPage = () => {
    const {id} = useParams();
    const numericId = Number(id);
    const navigate = useNavigate();
    const queryClient = useQueryClient();

    const {data: server, isLoading} = useServer(numericId);
    const {data: status} = useServerStatus(numericId, {refetchInterval: false});

    const handleSubmit = async (values: Arma3ServerDto | DayZServerDto | ReforgerServerDto) => {
        if (!server) {
            return;
        }

        const request = {
            ...server,
            ...values,
            type: server.type,
            queryPort: server.type === ServerType.Arma3 ? (values.port ?? 0) + 1 : values.queryPort
        };

        await serversApi.updateServer({id: numericId, serverDto: request as ServerDto});
        toast.success("Server successfully updated");
        await queryClient.invalidateQueries({queryKey: queryKeys.servers});
        navigate("/servers");
    };

    const handleCancel = () => {
        navigate("/servers");
    };

    return (
        <>
            {isLoading && <h2>Loading server data...</h2>}
            {!isLoading && !!server &&
                <>
                    <Typography variant="h4" sx={{mb: 2}}>Server Settings
                        ({SERVER_NAMES.get(server.type as ServerType)})</Typography>
                    {server.type === ServerType.Arma3 &&
                        <EditArma3ServerSettingsForm server={server as Arma3ServerDto} onSubmit={handleSubmit}
                                                     onCancel={handleCancel}
                                                     isServerRunning={status && status.alive}
                        />
                    }
                    {(server.type === ServerType.Dayz || server.type === ServerType.DayzExp) &&
                        <EditDayZServerSettingsForm server={server as DayZServerDto} onSubmit={handleSubmit}
                                                    onCancel={handleCancel}
                                                    isServerRunning={status && status.alive}
                        />
                    }
                    {(server.type === ServerType.Reforger) &&
                        <EditReforgerServerSettingsForm server={server as ReforgerServerDto} onSubmit={handleSubmit}
                                                        onCancel={handleCancel}
                                                        isServerRunning={status && status.alive}
                        />
                    }
                </>}
        </>
    );
};

export default ServerSettingsPage;
