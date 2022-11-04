import React, {useState} from "react";
import ListBuilderList from "./ListBuilderList";
import ListBuilderContainer from "./ListBuilderContainer";
import ListBuilderHeader from "./ListBuilderHeader";

export default function ListBuilder(props) {
    const [filter, setFilter] = useState("");

    function handleFilterChange(e) {
        setFilter(e.target.value);
    }

    const filteredAvailableOptions = props.availableOptions.filter(
            option => option.name.toLowerCase().startsWith(filter.toLowerCase()));

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
                        selectedOptions={filteredAvailableOptions} onClickItem={props.onSelect}
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