import React, {Component} from "react";
import {getMods, installMod, refreshMods, setMultipleActive, uninstallMod} from "../services/modsService";
import {toast} from "react-toastify";
import ModInstallForm from "./modInstallForm";
import ModsTable from "./modsTable";
import {getSystemInfo} from "../services/systemService";
import {humanFileSize} from "../util/util";

class Mods extends Component {

    state = {
        mods: [],
        systemInfo: {},
        refreshAutomatically: true,
    };

    async componentDidMount() {
        await this.refreshModList();
        await this.toggleAutoRefresh(true);
    };

    componentWillUnmount() {
        clearInterval(this.refreshInterval);
    };

    refreshModList = async () => {
        try {
            const {data: mods} = await getMods();
            const {data: systemInfo} = await getSystemInfo();

            mods.sort((a, b) => a.name.localeCompare(b.name));
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
        return (
            <div>
                <h2>Installed mods</h2>
                <div className="row">
                    <div className="col-4">
                        <button className="btn btn-primary m-2"
                                onClick={this.handleRefreshList}>Refresh
                        </button>
                        <button className="btn btn-secondary m-2"
                                onClick={this.handleUpdateAll}>Update all
                        </button>
                    </div>
                    <div className="col-4">
                        <span>Free space: {humanFileSize(this.state.systemInfo.spaceLeft)}</span>
                    </div>
                    <div className="col-4">
                        <div className="form-check">
                            <input className="form-check-input"
                                   type="checkbox"
                                   name="refresh" id="refresh"
                                   onChange={this.handleRefreshChange}
                                   checked={this.state.refreshAutomatically}
                            />
                            <label htmlFor="refresh" className="form-check-label">
                                Refresh automatically
                            </label>
                        </div>
                    </div>
                </div>
                <ModsTable mods={this.state.mods}
                           onUninstallClicked={this.handleUninstall}
                           onUpdateClicked={this.handleInstall}
                           onActiveChange={this.handleActiveChange}
                           onApplyClicked={this.handleApplyActive}
                />

                <ModInstallForm onSubmit={this.handleInstall}/>
            </div>
        )
    };
}

export default Mods;