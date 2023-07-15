import {List, ListItemButton, Stack, TextField, Typography} from "@mui/material";
import styles from "./ListBuilder.module.css";
import {ChangeEvent} from "react";
import {ListBuilderElement} from "./ListBuilder.tsx";

type ListBuilderListProps<T> = {
    selectedOptions: Array<T>,
    typeLabel: string,
    itemLabel: string,
    itemsLabel: string,
    showFilter?: boolean,
    onFilterChange?: (event: ChangeEvent<HTMLInputElement>) => void,
    onClickItem: (element: T) => void,
}

export default function ListBuilderList<T extends ListBuilderElement>(props: ListBuilderListProps<T>) {

    function getHeadingText() {
        const headingText = props.typeLabel.toLowerCase();
        return headingText.charAt(0).toUpperCase() + headingText.slice(1) + " " + props.itemLabel;
    }

    return (
        <Stack>
            <Stack direction="row" spacing={4} mb={2} alignItems="flex-end" minHeight={40}>
                <Typography variant="h5">{getHeadingText()}</Typography>
                {props.showFilter &&
                    <TextField label="Filter" type="search" variant="outlined" size="small" margin="none"
                               id={`${props.typeLabel}-filter`} onChange={props.onFilterChange}/>
                }
            </Stack>
            <List className={styles.list} sx={{p: 0}}>
                {props.selectedOptions.length === 0 &&
                    <Typography m={2}>No {props.itemsLabel} {props.typeLabel.toLowerCase()}</Typography>
                }
                {props.selectedOptions.length > 0 && props.selectedOptions.map(opt =>
                    <ListItemButton
                        id={`${props.typeLabel}-${props.itemsLabel}-${opt.id}`}
                        className={`${props.typeLabel}-list-item`}
                        key={opt.id}
                        onClick={() => props.onClickItem(opt)}
                        divider>
                        {opt.name}
                    </ListItemButton>
                )}
            </List>
        </Stack>
    );
}