import React from "react";
import workshopErrorStatusMap from "../util/workshopErrorStatusMap";

const ModsErrorAlertMessage = (props) => {
    const {mods} = props;

    const onlyUnique = (value, index, self) => self.indexOf(value) === index;

    const errors = mods.filter(mod => mod.installationStatus === "ERROR").map(mod => mod.errorStatus).filter(
            onlyUnique);

    return (
            <div className="alert alert-danger" role="alert">
                <p className="m-0 p-1">
                    Installation of some mods failed. Review the errors below:
                </p>
                <ul>
                    {errors.map(error =>
                            <li key={error}>{workshopErrorStatusMap[error]}</li>
                    )}
                </ul>
            </div>
    )
}

export default ModsErrorAlertMessage;