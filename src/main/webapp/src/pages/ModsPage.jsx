import React, {useEffect, useState} from "react";
import {getMods, installMod, updateAllMods, uninstallMod} from "../services/modsService";
import {toast} from "react-toastify";
import ModInstallForm from "../components/modInstallForm";
import ModsTable from "../components/modsTable";
import {useInterval} from "../hooks/use-interval";

const ModsPage = () => {
    const [mods, setMods] = useState([]);

    const fetchMods = async () => {
        const {data: modsDto} = await getMods();
        setMods(modsDto.workshopMods.sort((a, b) => a.name.localeCompare(b.name)));
    };

    useEffect(() => {
        fetchMods();
    }, []);

    useInterval(fetchMods, 2000);

    const handleInstall = async (modId) => {
        try {
            const {data: mod} = await installMod(modId);
            setMods(prevState => {
                return [mod, ...prevState].sort((a, b) => a.name.localeCompare(b.name));
            })
        } catch (e) {
            console.error(e);
            toast.error("Error during mod install");
        }
    };

    const handleUpdate = async (modId) => {
        try {
            await installMod(modId);
            setMods(prevState => {
                const newState = [...prevState];
                const updatedMod = newState.find(mod => mod.id === modId);
                if (updatedMod) {
                    updatedMod.installationStatus = "INSTALLATION_IN_PROGRESS";
                    updatedMod.errorStatus = null;
                }
                return newState;
            })
        } catch (e) {
            console.error(e);
            toast.error("Error during mod install");
        }
    };

    const handleUninstall = async (modId) => {
        try {
            setMods(prevState => {
                return prevState.filter(mod => mod.id !== modId);
            })
            await uninstallMod(modId);
        } catch (e) {
            console.error(e);
            toast.error("Error during mod uninstall");
        }
    };

    const handleUpdateAll = async () => {
        try {
            await updateAllMods();
            await fetchMods();
        } catch (e) {
            console.error(e);
            toast.error("Error during updating all mods");
        }
    };

    return (
            <div>
                <h2>Installed mods</h2>
                <div className="row">
                    <div className="col-md-4">
                        <button className="btn btn-secondary m-2"
                                onClick={handleUpdateAll}>Update all
                        </button>
                    </div>
                </div>
                <div className="row">
                    <div className="col-md-12">
                        <ModsTable mods={mods}
                                   onUninstallClicked={handleUninstall}
                                   onUpdateClicked={handleUpdate}
                        />
                        <ModInstallForm onInstallClicked={handleInstall}/>
                    </div>
                </div>

                {/*<Modal show={newPreset.showModal} onHide={this.handlePresetModalClose}>*/}
                {/*    <Modal.Header closeButton>*/}
                {/*        <Modal.Title>Save preset</Modal.Title>*/}
                {/*    </Modal.Header>*/}
                {/*    <Modal.Body>*/}
                {/*        <div className="form-group">*/}
                {/*            <label htmlFor="presetName">Preset name</label>*/}
                {/*            <input className="form-control" id="presetName"*/}
                {/*                   value={newPreset.name}*/}
                {/*                   onChange={this.handlePresetNameChange}/>*/}
                {/*        </div>*/}
                {/*    </Modal.Body>*/}
                {/*    <Modal.Footer>*/}
                {/*        <button className="btn btn-secondary" onClick={this.handlePresetModalClose}>*/}
                {/*            Close*/}
                {/*        </button>*/}
                {/*        <button className="btn btn-primary" disabled={!this.isPresetNameValid()}*/}
                {/*                onClick={this.handlePresetSaveConfirm}>*/}
                {/*            Save Changes*/}
                {/*        </button>*/}
                {/*    </Modal.Footer>*/}
                {/*</Modal>*/}
            </div>
    )
}

export default ModsPage;