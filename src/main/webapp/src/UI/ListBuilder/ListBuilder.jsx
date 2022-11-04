import React, {useState} from "react";
import ListBuilderList from "./ListBuilderList";
import ListBuilderContainer from "./ListBuilderContainer";
import ListBuilderHeader from "./ListBuilderHeader";
import Fuse from "fuse.js";

export default function ListBuilder(props) {
    const [filter, setFilter] = useState("");

    function handleFilterChange(e) {
        setFilter(e.target.value);
    }

    const filterAvailableOptions = () => {
        if (!filter) {
            return props.availableOptions;
        }
        return new Fuse(props.availableOptions, {keys: ['name']}).search(filter).map(o => o.item);
    }

    const itemsLabel = props.itemsLabel ?? "options";

    return (
            <ListBuilderContainer>
                <ListBuilderHeader
                        presets={props.presets}
                        itemsLabel={props.itemsLabel}
                        selectedPreset={props.selectedPreset}
                        onPresetChange={props.onPresetChange}
                />
                <ListBuilderList
                        itemLabel={itemsLabel} typeLabel="available"
                        selectedOptions={filterAvailableOptions()} onClickItem={props.onSelect}
                        itemsLabel={props.itemsLabel}
                        showFilter onFilterChange={handleFilterChange}
                />
                <ListBuilderList
                        itemLabel={itemsLabel} typeLabel="selected"
                        itemsLabel={props.itemsLabel}
                        selectedOptions={props.selectedOptions} onClickItem={props.onDeselect}
                />
            </ListBuilderContainer>
    );
}