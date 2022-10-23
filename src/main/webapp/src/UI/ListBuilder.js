import React, {useState} from "react";
import styles from "./ListBuilder.module.css";
import {Box, Grid, List, ListItemButton, TextField, Typography} from "@mui/material";

const ListBuilder = props => {
    const {showFilter, availableOptions, selectedOptions, onSelect, onDeselect} = props;

    const [filter, setFilter] = useState("");

    const handleFilterChange = (e) => {
        setFilter(e.target.value);
    }

    const filteredAvailableOptions = availableOptions.filter(
            option => option.name.toLowerCase().startsWith(filter.toLowerCase()));

    const itemsLabel = props.itemsLabel ?? "options";

    return (
            <Box className={styles.box} sx={{
                bgcolor: 'background.paper',
                boxShadow: 24,
                p: 4,
            }}>
                <Typography id="transition-modal-title" variant="h6">
                    Select mods
                </Typography>
                <TextField label="Filter" type="search" variant="standard" onChange={handleFilterChange}/>
                <Grid container spacing={4}>
                    <Grid item xs={6}>
                        <Typography variant="h6">Available {itemsLabel}</Typography>
                        <List className={styles.list}>
                            {filteredAvailableOptions.length === 0 && <span>No {itemsLabel} available</span>}
                            {filteredAvailableOptions.map(opt =>
                                    <ListItemButton key={opt.id} onClick={() => onSelect(opt)}>
                                        {opt.name}
                                    </ListItemButton>
                            )}
                        </List>
                    </Grid>
                    <Grid item xs={6}>
                        <Typography variant="h6">Enabled {itemsLabel}</Typography>
                        <List className={styles.list}>
                            {selectedOptions.length === 0 && <span>No {itemsLabel} selected</span>}
                            {selectedOptions.map(opt =>
                                    <ListItemButton key={opt.id} onClick={() => onDeselect(opt)}>
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