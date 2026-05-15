import {useNavigate, useParams} from "react-router-dom";
import {serversApi} from "../api/client";
import {toast} from "material-react-toastify";
import EditArma3ServerSettingsForm from "../components/servers/EditArma3ServerSettingsForm";
import {Typography} from "@mui/material";
import EditDayZServerSettingsForm from "../components/servers/EditDayZServerSettingsForm";
import SERVER_NAMES from "../util/serverNames";
import EditReforgerServerSettingsForm from "../components/servers/EditReforgerServerSettingsForm";
import {arma3ServerInitialState, dayzServerInitialState, reforgerServerInitialState} from "./initialServerStateCreator";
import {ServerType} from "../api/generated";


const NewServerPage = () => {
    const {type} = useParams<{ type: string }>();
    const navigate = useNavigate();

    type FixMeLater = any;

    const handleSubmit = async (values: FixMeLater) => {
        const server = {
            ...values,
            type,
            activeMods: [],
            activeDLCs: [],
        }

        try {
            await serversApi.createServer({serverDto: server});
            toast.success("Server successfully created");
            navigate("/servers");
        } catch (e) {
            console.error(e);
            toast.error("Creating server failed");
        }
    };

    const handleCancel = () => {
        navigate("/servers");
    };

    return (
        <>
            <Typography variant="h4" mb={2}>New {SERVER_NAMES.get(type as ServerType)} server</Typography>
            {type === ServerType.Arma3 &&
                <EditArma3ServerSettingsForm server={arma3ServerInitialState()}
                                             onCancel={handleCancel}
                                             onSubmit={handleSubmit}
                />
            }
            {(type === ServerType.Dayz || type === ServerType.DayzExp) &&
                <EditDayZServerSettingsForm server={{...dayzServerInitialState(), type}}
                                            onCancel={handleCancel}
                                            onSubmit={handleSubmit}
                />
            }
            {(type === ServerType.Reforger) &&
                <EditReforgerServerSettingsForm server={reforgerServerInitialState()}
                                                onCancel={handleCancel}
                                                onSubmit={handleSubmit}
                />
            }
        </>
    );
}

export default NewServerPage;