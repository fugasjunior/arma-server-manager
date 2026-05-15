import workshopErrorStatusMap from "../../util/workshopErrorStatusMap";
import {Alert, AlertTitle} from "@mui/material";
import {ErrorStatus, ModDto} from "../../api/generated";

type ModsErrorAlertMessageProps = {
    mods: Array<ModDto>
}

const ModsErrorAlertMessage = ({mods}: ModsErrorAlertMessageProps) => {
    const onlyUnique = (value: string | null, index: number, self: Array<string | null>) => {
        return value !== null && self.indexOf(value) === index;
    }

    const errors = mods.filter(mod => mod.installationStatus === "ERROR")
        .map(mod => mod.errorStatus ?? null)
        .filter(onlyUnique);

    return (
        <Alert severity="error" sx={{mb: 2}}>
            <AlertTitle>Some mods could not be installed</AlertTitle>
            <p className="m-0 p-1">
                Installation of some mods failed. Review the errors below:
            </p>
            <ul>
                {errors.map(error =>
                    <li key={error}>{workshopErrorStatusMap.get(error as ErrorStatus)}</li>
                )}
            </ul>
        </Alert>
    )
};

export default ModsErrorAlertMessage;