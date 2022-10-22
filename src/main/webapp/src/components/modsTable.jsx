import React from "react";
import {humanFileSize} from "../util/util";
import ModsTableEntry from "./modsTableEntry";
import ModsErrorAlertMessage from "./modsErrorAlertMessage";

const ModsTable = props => {
    const {mods, onUpdateClicked, onUninstallClicked} = props;

    return (
            <>
                {mods.some(m => m.installationStatus === "ERROR") && <ModsErrorAlertMessage mods={mods}/>}
                <div className="mods-table__wrapper">
                    <table className="table">
                        <thead>
                        <tr>
                            <th scope="col">Id</th>
                            <th scope="col">Name</th>
                            <th scope="col">File size</th>
                            <th scope="col" className="text-center">Installed</th>
                            <th scope="col"/>
                            <th scope="col"/>
                        </tr>
                        </thead>
                        <tbody>
                        {mods.map(mod => (
                                        <ModsTableEntry mod={mod}
                                                        onUpdateClicked={onUpdateClicked}
                                                        onUninstallClicked={onUninstallClicked}
                                        />
                                )
                        )}
                        </tbody>
                    </table>
                </div>
            </>
    );
};

export default ModsTable;