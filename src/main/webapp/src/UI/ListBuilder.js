import React, {useState} from "react";
import styles from "./ListBuilder.module.css";
import {
    Box,
    FormControl,
    Grid,
    InputLabel,
    List,
    ListItemButton,
    Select,
    Stack,
    TextField,
    Typography
} from "@mui/material";
import MenuItem from "@mui/material/MenuItem";

const ListBuilder = props => {
    const [filter, setFilter] = useState("");

    const handleFilterChange = (e) => {
        setFilter(e.target.value);
    }

    const filteredAvailableOptions = props.availableOptions.filter(
            option => option.name.toLowerCase().startsWith(filter.toLowerCase()));

    const itemsLabel = props.itemsLabel ?? "options";

    const presetAvailable = !!props.presets && props.presets.length > 0;

    return (
            <Box className={styles.box} overflow="auto" sx={{
                bgcolor: 'background.paper',
                boxShadow: 24,
                p: 4,
            }}>
                <Grid container spacing={4} p={4}>
                    <Grid item xs={presetAvailable ? 6 : 12}>
                        <Typography id="transition-modal-title" variant="h4">
                            Select {itemsLabel}
                        </Typography>
                    </Grid>

                    {presetAvailable &&
                            <Grid item xs={6}>
                                <FormControl fullWidth>
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
                            </Grid>
                    }

                    <Grid item xs={6}>
                        <Stack>
                            <Stack direction="row" spacing={4} mb={1}>
                                <Typography variant="h5">Available {itemsLabel}</Typography>
                                <TextField label="Filter" type="search" variant="outlined" size="small" margin="none"
                                           onChange={handleFilterChange}/>
                            </Stack>
                            <List className={styles.list}>
                                {filteredAvailableOptions.length === 0 && <Typography
                                        m={2}>No {itemsLabel} available</Typography>}
                                {filteredAvailableOptions.length > 0 && filteredAvailableOptions.map(opt =>
                                        <ListItemButton key={opt.id} onClick={() => props.onSelect(opt)} divider>
                                            {opt.name}
                                        </ListItemButton>
                                )}
                            </List>
                        </Stack>
                    </Grid>
                    <Grid item xs={6}>
                        <Stack>
                            <Stack direction="row" spacing={4} mb={2} alignItems="flex-end">
                                <Typography variant="h5">Enabled {itemsLabel}</Typography>
                            </Stack>
                            <List className={styles.list}>
                                {props.selectedOptions.length === 0 && <Typography
                                        m={2}>No {itemsLabel} selected</Typography>}
                                {props.selectedOptions.length > 0 && props.selectedOptions.map(opt =>
                                        <ListItemButton key={opt.id} onClick={() => props.onDeselect(opt)} divider>
                                            {opt.name}
                                        </ListItemButton>
                                )}
                            </List>
                        </Stack>
                    </Grid>
                </Grid>
            </Box>
    )
}

export default ListBuilder;