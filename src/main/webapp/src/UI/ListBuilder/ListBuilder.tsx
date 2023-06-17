import {ChangeEvent, useState} from "react";
import ListBuilderList from "./ListBuilderList";
import ListBuilderHeader from "./ListBuilderHeader";
import Fuse from "fuse.js";
import {Box, Grid} from "@mui/material";
import styles from "./ListBuilder.module.css";
import {ModPresetDto} from "../../dtos/ModPresetDto.ts";

export type ListBuilderElement = { id: number | string, name: string };

type ListBuilderProps<T> = {
    availableOptions: Array<T>,
    selectedOptions: Array<T>,
    itemsLabel: string,
    withControls?: boolean,
    confirmDisabled?: boolean,
    showFilter?: boolean,
    presets?: Array<ModPresetDto>
    selectedPreset?: string,
    onPresetChange?: (event: ChangeEvent<HTMLInputElement>) => void,
    onSelect: (element: T) => void,
    onDeselect: (element: T) => void,
    onConfirm?: () => void,
    onCancel?: () => void
}

export default function ListBuilder<T extends ListBuilderElement>(props: ListBuilderProps<T>) {
    const [filter, setFilter] = useState("");

    function handleFilterChange(e: ChangeEvent<HTMLInputElement>) {
        setFilter(e.target.value);
    }

    const filterAvailableOptions = () => {
        if (!filter) {
            return props.availableOptions;
        }
        return new Fuse<ListBuilderElement>(props.availableOptions, {keys: ['name']}).search(filter).map(o => o.item);
    }

    const itemsLabel = props.itemsLabel ?? "options";

    return (
        <Box className={styles.box} overflow="auto" sx={{
            bgcolor: 'background.paper',
            boxShadow: 24,
            p: 4,
        }}>
            <Grid container spacing={4} p={4}>
                <Grid item xs={12}>
                    <ListBuilderHeader
                        presets={props.presets}
                        itemsLabel={props.itemsLabel}
                        selectedPreset={props.selectedPreset}
                        onPresetChange={props.onPresetChange}
                        onConfirm={props.onConfirm}
                        onCancel={props.onCancel}
                        withControls={props.withControls}
                        confirmDisabled={props.confirmDisabled}
                    />
                </Grid>
                <Grid item xs={6}>
                    <ListBuilderList
                        itemLabel={itemsLabel} typeLabel="available"
                        selectedOptions={filterAvailableOptions()} onClickItem={props.onSelect}
                        itemsLabel={props.itemsLabel}
                        showFilter onFilterChange={handleFilterChange}
                    />
                </Grid>
                <Grid item xs={6}>
                    <ListBuilderList
                        itemLabel={itemsLabel} typeLabel="selected"
                        itemsLabel={props.itemsLabel}
                        selectedOptions={props.selectedOptions} onClickItem={props.onDeselect}
                    />
                </Grid>
            </Grid>
        </Box>
    );
}