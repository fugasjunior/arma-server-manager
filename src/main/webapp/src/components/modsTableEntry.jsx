import React from "react";
import {humanFileSize} from "../util/util";
import workshopErrorStatusMap from "../util/workshopErrorStatusMap";

const getInstalledIcon = (mod) => {
    const status = mod.installationStatus;
    const error = mod.errorStatus;

    if (status === "INSTALLATION_QUEUED") {
        return <i title="Update queued" className="fa fa-clock-o"></i>
    }
    if (status === "INSTALLATION_IN_PROGRESS") {
        return (
                <div title="Installation in progress" className="spinner-border spinner-border-sm" role="status">
                    <span className="sr-only">Loading...</span>
                </div>
        );
    }
    if (status === "ERROR") {
        return <i title={`Error: ${workshopErrorStatusMap[error]}`} className="fa fa-exclamation-triangle"
                  aria-hidden="true"></i>
    }

    if (status === "FINISHED") {
        return (
                <i title={`Last updated: ${mod.lastUpdated}`} className="fa fa-check" aria-hidden="true"></i>
        );
    }
};

const ModsTableEntry = (props) => {
    const {mod, onUpdateClicked, onUninstallClicked} = props;

    const workshopUrl = "https://steamcommunity.com/sharedfiles/filedetails/?id=" + mod.id;

    return (
            <tr className={mod.failed ? "table-danger" : ""}>
                <td>
                    <a href={workshopUrl}
                       target="_blank"
                       rel="noopener noreferrer"
                    >
                        {mod.id}
                    </a>
                </td>
                <td>{mod.name}</td>
                <td>{mod.fileSize && humanFileSize(mod.fileSize)}</td>
                <td className="text-center">{getInstalledIcon(mod)}</td>
                <td>
                    <button className="btn btn-sm btn-primary"
                            onClick={() => onUpdateClicked(mod.id)}
                    >
                        Update
                    </button>
                </td>
                <td>
                    <button className="btn btn-sm btn-danger"
                            onClick={() => onUninstallClicked(mod.id)}
                    >
                        Uninstall
                    </button>
                </td>
            </tr>
    )
};

export default ModsTableEntry;