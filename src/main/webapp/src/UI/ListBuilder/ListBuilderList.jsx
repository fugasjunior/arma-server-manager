import {List, ListItemButton, Stack, TextField, Typography} from "@mui/material";
import styles from "./ListBuilder.module.css";
import React from "react";

export default function ListBuilderList(props) {

    function getHeadingText() {
        const headingText = props.typeLabel.toLowerCase();
        return headingText.charAt(0).toUpperCase() + headingText.slice(1) + " " + props.itemLabel;
    }

    return (
            <Stack>
                <Stack direction="row" spacing={4} mb={2} alignItems="flex-end">
                    <Typography variant="h5">{getHeadingText()}</Typography>
                    {props.showFilter &&
                            <TextField label="Filter" type="search" variant="outlined" size="small" margin="none"
                                       onChange={props.onFilterChange}/>
                    }
                </Stack>
                <List className={styles.list}>
                    {props.selectedOptions.length === 0 &&
                            <Typography m={2}>No {props.itemsLabel} {props.typeLabel.toLowerCase()}</Typography>
                    }
                    {props.selectedOptions.length > 0 && props.selectedOptions.map(opt =>
                            <ListItemButton key={opt.id} onClick={() => props.onClickItem(opt)} divider>
                                {opt.name}
                            </ListItemButton>
                    )}
                </List>
            </Stack>
    );
}