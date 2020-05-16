import React, {Component} from "react";
import {getMods, installMod, refreshMods, setActive, uninstallMod} from "../services/modsService";
import {toast} from "react-toastify";
import ModInstallForm from "./modInstallForm";
import ModsTable from "./modsTable";
import {getSystemInfo} from "../services/systemService";
import {humanFileSize} from "../util/util";

class Mods extends Component {

    state = {
        mods: [],
        systemInfo: {}
    };

    async componentDidMount() {
        await this.refreshModList();
        this.interval = setInterval(this.refreshModList, 7500);
    };

    componentWillUnmount() {
        clearInterval(this.interval);
    };

    refreshModList = async () => {
        try {
            const {data: mods} = await getMods();
            const {data: systemInfo} = await getSystemInfo();
            this.setState({mods, systemInfo})
        } catch (e) {
            toast.error("Error during loading mods");
        }
    };

    handleRefreshList = this.refreshModList;

    handleInstall = async (modId, e) => {
        if (e) e.preventDefault();
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
        } catch (e) {
            toast.error("Error during mod uninstall");
            this.setState({mods: originalMods});
        }
    };

    handleActiveChange = async ({currentTarget: input}) => {
        console.log(input);
        const modId = parseInt(input.value);
        const active = input.checked;

        const newMods = [...this.state.mods];
        newMods.find(mod => mod.id === modId).active = active;
        this.setState({newMods});

        try {
            await setActive(modId, active);
            toast.success("Mod " + modId + " successfully " + (active ? "activated" : "deactivated"));
        } catch (e) {
            toast.error("Error during activating mod " + modId);
        }
    }

    handleUpdateAll = async () => {
        try {
            await refreshMods();
            await this.refreshModList();
        } catch (e) {
            toast.error("Error during updating all mods");
        }
    };

    render() {
        return (
            <div>
                <h2>Installed mods</h2>
                <div className="row">
                    <div className="col-6">
                        <button className="btn btn-primary m-2"
                                onClick={this.handleRefreshList}>Refresh
                        </button>
                        <button className="btn btn-secondary m-2"
                                onClick={this.handleUpdateAll}>Update all
                        </button>
                    </div>
                    <div className="col-6">
                        <span>Free space: {humanFileSize(this.state.systemInfo.spaceLeft)}</span>
                    </div>
                </div>
                <ModsTable mods={this.state.mods}
                           onUninstallClicked={this.handleUninstall}
                           onUpdateClicked={this.handleInstall}
                           onActiveChange={this.handleActiveChange}
                />

                <ModInstallForm onSubmit={this.handleInstall}/>
            </div>
        )
    };
}

export default Mods;