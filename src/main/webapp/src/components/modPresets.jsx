import React from "react";

const ModPresets = ({presets, onPresetDelete, onPresetActivate}) => {
    return (
            <div id="modPresets">
                <h3>Mod Presets</h3>

                {presets.length === 0 ? <p>There are no presets</p>
                        : <ul>
                            {presets.map(preset =>
                                    <li><strong>{preset.name}</strong>
                                        <ul>
                                            {preset.mods.map(mod =>
                                                    <li>{mod.name}</li>
                                            )}
                                        </ul>
                                        <div className="btn-group" role="group">
                                            <button className="btn btn-sm btn-primary"
                                                    onClick={() => onPresetActivate(preset.name)}>
                                                Activate
                                            </button>
                                            <button className="btn btn-sm btn-danger"
                                                    onClick={() => onPresetDelete(preset.name)}>
                                                Delete
                                            </button>
                                        </div>
                                    </li>
                            )}
                        </ul>}
            </div>);
};

export default ModPresets;