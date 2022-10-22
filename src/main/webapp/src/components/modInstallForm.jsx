import React, {Component, useState} from "react";

const ModInstallForm = (props) => {

    const [enteredModId, setEnteredModId] = useState("");

    const handleModIdChanged = (e) => {
        setEnteredModId(e.target.value.trim().replace(/\D/g, ''));
    }

    const handleInstallClicked = () => {
        props.onInstallClicked(enteredModId);
        setEnteredModId("");
    }

    return (
            <>
                <h3>Install new mod</h3>

                <div className="form-inline">
                    <div className="form-group">
                        <label htmlFor="modId" className="sr-only">Steam Workshop Mod ID</label>
                        <input type="text"
                               className="form-control"
                               id="modId" name="modId"
                               placeholder="Workshop mod ID"
                               value={enteredModId}
                               onChange={handleModIdChanged}
                        />
                    </div>
                    <button
                            onClick={handleInstallClicked}
                            className="btn btn-primary ml-2"
                            disabled={enteredModId.length < 1}>
                        Install
                    </button>
                </div>
            </>
    );
}

export default ModInstallForm;

