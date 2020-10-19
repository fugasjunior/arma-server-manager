import React from "react";

const ModPresets = ({presets, onPresetDelete, onPresetActivate}) => {
    return (
            <div id="modPresets">
                <h3>Mod Presets</h3>

                {presets.length === 0 ? <p>There are no presets</p>
                        : <div className="accordion">
                            {presets.map((preset, idx) =>
                                    <div className="card">
                                        <div className="card-header p-1" id="headingOne" data-toggle="collapse"
                                             data-target={"#collapse-" + idx}>
                                            <h5 className="mb-0 p-2">
                                                {preset.name}
                                            </h5>
                                        </div>

                                        <div id={"collapse-" + idx} className="collapse" data-parent="#accordion">
                                            <div className="card-body p-1">
                                                <ul className="list-group list-group-flush">
                                                    {preset.mods.map(mod =>
                                                            <li className="list-group-item p-1"
                                                                key={mod.id}>{mod.name}</li>
                                                    )}
                                                </ul>
                                                <button className="btn btn-sm btn-primary m-2"
                                                        onClick={() => onPresetActivate(preset.name)}>
                                                    Activate
                                                </button>
                                                <button className="btn btn-sm btn-danger m-2"
                                                        onClick={() => onPresetDelete(preset.name)}>
                                                    Delete
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                            )}
                        </div>}
            </div>);
};

export default ModPresets;