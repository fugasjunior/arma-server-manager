import {ChangeEvent, useState} from "react";
import ListBuilderList from "./ListBuilderList";
import ListBuilderHeader from "./ListBuilderHeader";
import Fuse from "fuse.js";
import {Box, Grid, SelectChangeEvent} from "@mui/material";
import styles from "./ListBuilder.module.css";
import {PresetResponseDto} from "../../api/generated";

export type ListBuilderElement = { id?: number | string, name?: string };

type ListBuilderProps<T> = {
    availableOptions: Array<T>,
    selectedOptions: Array<T>,
    itemsLabel: string,
    withControls?: boolean,
    confirmDisabled?: boolean,
    showFilter?: boolean,
    presets?: Array<PresetResponseDto>
    selectedPreset?: string,
    onPresetChange?: (event: SelectChangeEvent) => void,
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

    const filterAvailableOptions = (): Array<T> => {
        if (!filter) {
            return props.availableOptions;
        }
        return new Fuse<T>(props.availableOptions, {keys: ['name']}).search(filter).map(o => o.item);
    }

    const itemsLabel = props.itemsLabel ?? "options";

    return (
        <Box className={styles.box} sx={{
            overflow: "auto",
            bgcolor: 'background.paper',
            boxShadow: 24,
            p: 4,
        }}>
            <Grid container spacing={4} sx={{p: 4}}>
                <Grid size={12}>
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
                <Grid size={6}>
                    <ListBuilderList
                        itemLabel={itemsLabel} typeLabel="available"
                        selectedOptions={filterAvailableOptions()} onClickItem={props.onSelect}
                        itemsLabel={props.itemsLabel}
                        showFilter onFilterChange={handleFilterChange}
                    />
                </Grid>
                <Grid size={6}>
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