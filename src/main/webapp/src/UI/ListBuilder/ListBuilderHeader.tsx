import {Button, FormControl, InputLabel, Select, SelectChangeEvent, Stack, Typography} from "@mui/material";
import MenuItem from "@mui/material/MenuItem";
import {ModPresetDto} from "../../dtos/ModPresetDto.ts";

type ListBuilderHeaderProps = {
    itemsLabel: string,
    withControls?: boolean,
    confirmDisabled?: boolean,
    selectedPreset?: string,
    presets?: Array<ModPresetDto>,
    onPresetChange?: (event: SelectChangeEvent) => void
    onConfirm?: () => void,
    onCancel?: () => void,
}

export default function ListBuilderHeader(props: ListBuilderHeaderProps) {

    return (
        <Stack direction="row" justifyContent="space-between">
            <Stack>
                <Typography id="transition-modal-title" variant="h4">
                    Select {props.itemsLabel}
                </Typography>
                <Typography variant="body1">
                    Click an item to move it into the second column
                </Typography>
            </Stack>
            <Stack direction="row" alignItems="center">
                {props.withControls &&
                    <>
                        <Button color="primary" variant="contained" sx={{mr: 1}}
                                onClick={props.onConfirm} disabled={props.confirmDisabled}>
                            Confirm
                        </Button>
                        <Button color="error" variant="outlined" onClick={props.onCancel}>Cancel</Button>
                    </>
                }
                {props.presets && <FormControl sx={{m: 1, minWidth: 150}}>
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
        </Stack>
    );
}