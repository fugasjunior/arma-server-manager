import {FormControl, InputLabel, Select, Stack, Typography} from "@mui/material";
import React from "react";
import MenuItem from "@mui/material/MenuItem";

export default function ListBuilderHeader(props) {

    const presetAvailable = !!props.presets && props.presets.length > 0;

    return (
            <Stack direction="row" justifyContent="space-between">
                <Typography id="transition-modal-title" variant="h4">
                    Select {props.itemsLabel}
                </Typography>
                {presetAvailable && <FormControl sx={{m: 1, minWidth: 150}}>
                    <InputLabel id="preset-select-label">Select preset</InputLabel>
                    <Select
                            labelId="preset-select-label"
                            id="preset"
                            label="Select preset"
                            name="preset"
                            value={props.selectedPreset}
                            onChange={props.onPresetChange}
                    >
                        {props.presets.map(preset => (
                                <MenuItem key={preset.id} value={preset.id}>
                                    {preset.name}
                                </MenuItem>
                        ))}
                    </Select>
                </FormControl>
                }
            </Stack>
    );
}