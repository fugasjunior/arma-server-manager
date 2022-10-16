import React, {Component} from "react";
import {getMods, installMod, refreshMods, setMultipleActive, uninstallMod} from "../services/modsService";
import {toast} from "react-toastify";
import {Modal} from "react-bootstrap";
import ModInstallForm from "./modInstallForm";
import ModsTable from "./modsTable";
import {getSystemInfo} from "../services/systemService";
import {createPreset, deletePreset, getPresets} from "../services/modPresetsService";
import {humanFileSize} from "../util/util";
import ModPresets from "./modPresets";

class Mods extends Component {

    state = {
        mods: [],
        presets: [],
        systemInfo: {},
        refreshAutomatically: true,
        newPreset: {
            name: "",
            showModal: false
        }
    };

    async componentDidMount() {
        await this.refreshModList();
        await this.toggleAutoRefresh(true);
    }

    componentWillUnmount() {
        clearInterval(this.refreshInterval);
    }

    refreshModList = async () => {
        try {
            const {data: mods} = await getMods();
            const {data: systemInfo} = await getSystemInfo();
            const {data: presets} = await getPresets();

            mods.sort((a, b) => a.name.localeCompare(b.name));
            this.setState({mods, systemInfo, presets})
        } catch (e) {
            console.error(e);
            toast.error("Error during loading mods");
        }
    };

    handleRefreshList = this.refreshModList;

    handleInstall = async (modId, e) => {
        if (e) {
            e.preventDefault();
        }

        try {
            const {data: mod} = await installMod(modId);

            const existingMods = [...this.state.mods];
            const existingMod = existingMods.find(m => m.id === modId);

            if (!existingMod) {
                const mods = [...existingMods, mod];
                this.setState({mods});
            } else {
                existingMod.failed = false;
                existingMod.installed = false;
                this.setState({existingMods});
            }

            await this.toggleAutoRefresh(true);
        } catch (e) {
            toast.error("Error during mod install");
        }
    };

    handleUninstall = async modId => {
        const originalMods = this.state.mods;
        const mods = originalMods.filter(mod => mod.id !== modId);
        this.setState({mods});

        try {
            await uninstallMod(modId);
            await this.toggleAutoRefresh(true);
        } catch (e) {
            toast.error("Error during mod uninstall");
            this.setState({mods: originalMods});
        }
    };

    handleActiveChange = async ({currentTarget: input}) => {
        const modId = parseInt(input.value);

        const isActive = input.checked;
        const mods = [...this.state.mods];

        mods.find(mod => mod.id === modId).active = isActive;
        this.setState({mods});

        await this.toggleAutoRefresh(false);
    }

    handleUpdateAll = async () => {
        try {
            await refreshMods();
            await this.toggleAutoRefresh(true);
        } catch (e) {
            toast.error("Error during updating all mods");
        }
    };

    handleRefreshChange = ({currentTarget: input}) => {
        this.toggleAutoRefresh(input.checked);
    };

    handleApplyActive = async () => {
        const mods = this.state.mods.reduce((map, mod) => {
            map[mod.id] = mod.active;
            return map;
        }, {});

        await setMultipleActive(mods);
    };

    handlePresetSave = () => {
        const {newPreset} = this.state;
        newPreset.showModal = true;
        this.setState({newPreset});
    };

    handlePresetModalClose = () => {
        const {newPreset} = this.state;
        newPreset.showModal = false;
        newPreset.name = "";
        this.setState({newPreset});
    }

    handlePresetSaveConfirm = async () => {
        const {newPreset, mods} = this.state;

        const modIds = mods.filter(mod => mod.active).map(mod => mod.id);
        await createPreset({name: newPreset.name, modIds});

        newPreset.showModal = false;
        newPreset.name = "";
        const {data: presets} = await getPresets();
        this.setState({presets, newPreset});
    };

    handlePresetLoad = async (presetName) => {
        const {mods, presets} = this.state;
        const preset = presets.find(p => p.name === presetName);
        if (!preset) {
            return;
        }

        await this.toggleAutoRefresh(false); // turn off auto refresh

        // deactivate all mods in list
        const newMods = [...mods];
        newMods.forEach(mod => mod.active = false);

        // activate mods from preset
        for (const {id} of preset.mods) {
            const foundMod = newMods.find(mod => mod.id === id)
            if (foundMod) {
                foundMod.active = true;
            }
        }
        this.setState({mods: newMods});
    };

    handlePresetDelete = async (presetName) => {
        await deletePreset(presetName);
        const {data: presets} = await getPresets();
        this.setState({presets});
    }

    handlePresetNameChange = ({currentTarget: input}) => {
        const {newPreset} = this.state;
        newPreset.name = input.value.trim();
        this.setState({newPreset});
    }

    isPresetNameValid = () => {
        const {name} = this.state.newPreset;
        return name.length > 0 && name.length <= 100;
    }

    toggleAutoRefresh = async isRefreshEnabled => {
        this.setState({refreshAutomatically: isRefreshEnabled});

        if (isRefreshEnabled) {
            this.refreshInterval = setInterval(this.refreshModList, 7500);
            await this.refreshModList();
        } else {
            clearInterval(this.refreshInterval);
        }
    };

    render() {
        const {systemInfo, refreshAutomatically, mods, presets, newPreset} = this.state;

        return (
                <div>
                    <h2>Installed mods</h2>
                    <div className="row">
                        <div className="col-md-4">
                            <button className="btn btn-primary m-2"
                                    onClick={this.handleRefreshList}>Refresh
                            </button>
                            <button className="btn btn-secondary m-2"
                                    onClick={this.handleUpdateAll}>Update all
                            </button>
                            <button className="btn btn-sm btn-secondary"
                                    onClick={this.handlePresetSave}>Save as preset
                            </button>
                        </div>
                        <div className="col-4">
                            <span>Free space: {humanFileSize(systemInfo.spaceLeft)}</span>
                        </div>
                        <div className="col-4">
                            <div className="form-check">
                                <input className="form-check-input"
                                       type="checkbox"
                                       name="refresh" id="refresh"
                                       onChange={this.handleRefreshChange}
                                       checked={refreshAutomatically}
                                />
                                <label htmlFor="refresh"
                                       className="form-check-label">
                                    Refresh automatically
                                </label>
                            </div>
                        </div>
                    </div>
                    <div className="row">
                        <div className="col-md-9">
                            <ModsTable mods={mods}
                                       onUninstallClicked={this.handleUninstall}
                                       onUpdateClicked={this.handleInstall}
                                       onActiveChange={this.handleActiveChange}
                                       onApplyClicked={this.handleApplyActive}
                            />

                            <ModInstallForm onSubmit={this.handleInstall}/>
                        </div>
                        <div className="col-md-3">
                            <ModPresets presets={presets}
                                        onPresetActivate={this.handlePresetLoad}
                                        onPresetDelete={this.handlePresetDelete}/>
                        </div>
                    </div>

                    <Modal show={newPreset.showModal} onHide={this.handlePresetModalClose}>
                        <Modal.Header closeButton>
                            <Modal.Title>Save preset</Modal.Title>
                        </Modal.Header>
                        <Modal.Body>
                            <div className="form-group">
                                <label htmlFor="presetName">Preset name</label>
                                <input className="form-control" id="presetName"
                                       value={newPreset.name}
                                       onChange={this.handlePresetNameChange}/>
                            </div>
                        </Modal.Body>
                        <Modal.Footer>
                            <button className="btn btn-secondary" onClick={this.handlePresetModalClose}>
                                Close
                            </button>
                            <button className="btn btn-primary" disabled={!this.isPresetNameValid()}
                                    onClick={this.handlePresetSaveConfirm}>
                                Save Changes
                            </button>
                        </Modal.Footer>
                    </Modal>
                </div>
        )
    }
}

export default Mods;