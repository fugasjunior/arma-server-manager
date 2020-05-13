import React, {Component} from "react";
import {getMods, installMod, refreshMods, setActive, uninstallMod} from "../services/modsService";
import {toast} from "react-toastify";
import ModInstallForm from "./modInstallForm";
import ModsTable from "./modsTable";

class Mods extends Component {

    state = {
        mods: [],
    };

    async componentDidMount() {
        await this.refreshModList();
        this.interval = setInterval(this.refreshModList, 5000);
    };

    componentWillUnmount() {
        clearInterval(this.interval);
    };

    refreshModList = async () => {
        try {
            const {data: mods} = await getMods();
            this.setState({mods})
        } catch (e) {
            toast.error("Error during loading mods");
        }
    };

    handleRefreshList = this.refreshModList;

    handleInstall = async (modId, e) => {
        if (e) e.preventDefault();
        try {
            const {data: mod} = await installMod(modId);
            await this.refreshModList();

            if (this.state.mods.filter(m => m.id === modId).length === 0) {
                const mods = [mod, ...this.state.mods];
                this.setState({mods});
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
        const mod = newMods.find(mod => mod.id === modId).active = active;
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
                <button className="btn btn-primary m-2"
                        onClick={this.handleRefreshList}>Refresh
                </button>
                <button className="btn btn-secondary m-2"
                        onClick={this.handleUpdateAll}>Update all
                </button>
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