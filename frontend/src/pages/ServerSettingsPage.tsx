import {useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import {serversApi} from "../api/client";
import {toast} from "react-toastify";
import {Typography} from "@mui/material";
import EditArma3ServerSettingsForm from "../components/servers/EditArma3ServerSettingsForm";
import EditDayZServerSettingsForm from "../components/servers/EditDayZServerSettingsForm";
import SERVER_NAMES from "../util/serverNames";
import EditReforgerServerSettingsForm from "../components/servers/EditReforgerServerSettingsForm";
import {ServerDto, ServerInstanceInfoDto, ServerType} from "../api/generated";
import {Arma3ServerDto, DayZServerDto, ReforgerServerDto} from "../api/serverModels";

const ServerSettingsPage = () => {
    const {id} = useParams();
    const [server, setServer] = useState<ServerDto>();
    const [status, setStatus] = useState<ServerInstanceInfoDto>();
    const [isLoading, setIsLoading] = useState(true);

    const navigate = useNavigate();

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            setIsLoading(true);
            const {data: fetchedServer} = await serversApi.getServer({id: Number(id)});
            setServer(fetchedServer);

            const {data: serverStatus} = await serversApi.getServerStatus({id: Number(id)});
            setStatus(serverStatus);

            setIsLoading(false);
        } catch (e) {
            console.error(e);
            toast("Error while isLoading server data");
        }
    }

    const handleSubmit = async (values: Arma3ServerDto | DayZServerDto | ReforgerServerDto) => {
        if (!server) {
            return;
        }

        const request = {
            ...server,
            ...values,
            type: server.type,
            queryPort: server.type === ServerType.Arma3 ? (values.port ?? 0) + 1 : values.queryPort
        }

        try {
            await serversApi.updateServer({id: Number(id), serverDto: request});
            toast.success("Server successfully updated");
            navigate("/servers");
        } catch (e) {
            console.error(e);
        }
    }

    const handleCancel = () => {
        navigate("/servers");
    }

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
                                                     isServerRunning={status
                                                         && status.alive}
                        />
                    }
                    {(server.type === ServerType.Dayz || server.type === ServerType.DayzExp) &&
                        <EditDayZServerSettingsForm server={server as DayZServerDto} onSubmit={handleSubmit}
                                                    onCancel={handleCancel}
                                                    isServerRunning={status
                                                        && status.alive}
                        />
                    }
                    {(server.type === ServerType.Reforger) &&
                        <EditReforgerServerSettingsForm server={server as ReforgerServerDto} onSubmit={handleSubmit}
                                                        onCancel={handleCancel}
                                                        isServerRunning={status
                                                            && status.alive}
                        />
                    }
                </>}

        </>
    );
}

export default ServerSettingsPage;