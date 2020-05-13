import React from "react";
import {humanFileSize} from "../util/util";

const getWorkshopUrl = modId => "https://steamcommunity.com/sharedfiles/filedetails/?id=" + modId;

const getInstalledIcon = isInstalled => {
    if (isInstalled) return (
        <i className="fa fa-check" aria-hidden="true"></i>
    );

    else return (
        <div className="spinner-border spinner-border-sm" role="status">
            <span className="sr-only">Loading...</span>
        </div>
    );
};

const ModsTable = ({mods, onUpdateClicked, onUninstallClicked, onActiveChange}) => {
    return (
        <table className="table">
            <thead>
            <tr>
                <th scope="col">Id</th>
                <th scope="col">Name</th>
                <th scope="col">File size</th>
                <th scope="col">Installed</th>
                <th scope="col">Last update</th>
                <th scope="col"/>
                <th scope="col"/>
                <th scope="col">Active</th>
            </tr>
            </thead>
            <tbody>
            {mods.map(mod => (
                    <tr key={mod.id}>
                        <td>
                            <a href={getWorkshopUrl(mod.id)}
                               target="_blank"
                               rel="noopener noreferrer"
                            >
                                {mod.id}
                            </a>
                        </td>
                        <td>{mod.name}</td>
                        <td>{mod.fileSize && humanFileSize(mod.fileSize)}</td>
                        <td>{getInstalledIcon(mod.installed)}</td>
                        <td>{mod.lastUpdated}</td>
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
                        <td>
                            <input type="checkbox"
                                   value={mod.id}
                                   checked={mod.active}
                                   onChange={onActiveChange}/>
                        </td>
                    </tr>
                )
            )}
            </tbody>
        </table>
    );
};

export default ModsTable;