import React from "react";
import workshopErrorStatusMap from "../../util/workshopErrorStatusMap";
import {Alert, AlertTitle} from "@mui/material";

const ModsErrorAlertMessage = (props) => {
    const {mods} = props;

    const onlyUnique = (value, index, self) => self.indexOf(value) === index;

    const errors = mods.filter(mod => mod.installationStatus === "ERROR").map(mod => mod.errorStatus).filter(
            onlyUnique);

    return (
            <Alert severity="error" sx={{mb: 2}}>
                <AlertTitle>Some mods could not be installed</AlertTitle>
                <p className="m-0 p-1">
                    Installation of some mods failed. Review the errors below:
                </p>
                <ul>
                    {errors.map(error =>
                            <li key={error}>{workshopErrorStatusMap[error]}</li>
                    )}
                </ul>
            </Alert>
    )
}

export default ModsErrorAlertMessage;