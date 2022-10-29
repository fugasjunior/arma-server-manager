import React, {useState} from "react";
import styles from "./ListBuilder.module.css";
import {Box, FormControl, Grid, InputLabel, List, ListItemButton, Select, TextField, Typography} from "@mui/material";
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
            <Box className={styles.box} sx={{
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
                        <Typography variant="h5">Available {itemsLabel}</Typography>
                        <TextField label="Filter" type="search" variant="standard" onChange={handleFilterChange}/>
                        <List className={styles.list}>
                            {filteredAvailableOptions.length === 0 && <span>No {itemsLabel} available</span>}
                            {filteredAvailableOptions.length > 0 && filteredAvailableOptions.map(opt =>
                                    <ListItemButton key={opt.id} onClick={() => props.onSelect(opt)}>
                                        {opt.name}
                                    </ListItemButton>
                            )}
                        </List>
                    </Grid>
                    <Grid item xs={6}>
                        <Typography variant="h5">Enabled {itemsLabel}</Typography>
                        <List className={styles.list}>
                            {props.selectedOptions.length === 0 && <span>No {itemsLabel} selected</span>}
                            {props.selectedOptions.length > 0 && props.selectedOptions.map(opt =>
                                    <ListItemButton key={opt.id} onClick={() => props.onDeselect(opt)}>
                                        {opt.name}
                                    </ListItemButton>
                            )}
                        </List>
                    </Grid>
                </Grid>
            </Box>
    )
}

export default ListBuilder;