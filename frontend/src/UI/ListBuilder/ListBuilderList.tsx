import {ChangeEvent, useState} from "react";
import {Button, IconButton, List, ListItem, ListItemButton, ListItemText, Stack, TextField, Typography} from "@mui/material";
import DragIndicatorIcon from "@mui/icons-material/DragIndicator";
import styles from "./ListBuilder.module.css";
import {ListBuilderElement} from "./ListBuilder.tsx";
import Fuse from "fuse.js";
import {
    closestCenter,
    DndContext,
    DragEndEvent,
    KeyboardSensor,
    PointerSensor,
    useSensor,
    useSensors,
} from "@dnd-kit/core";
import {
    arrayMove,
    SortableContext,
    sortableKeyboardCoordinates,
    useSortable,
    verticalListSortingStrategy,
} from "@dnd-kit/sortable";
import {CSS} from "@dnd-kit/utilities";

type ListBuilderListProps<T extends ListBuilderElement> = {
    selectedOptions: Array<T>,
    typeLabel: string,
    itemLabel: string,
    itemsLabel: string,
    showFilter?: boolean,
    onClickItem: (element: T) => void,
    onReorder?: (items: T[]) => void,
    onSelectAll?: () => void,
    onClearAll?: () => void,
}

function SortableItem<T extends ListBuilderElement>({item, typeLabel, itemsLabel, onClickItem}: {
    item: T,
    typeLabel: string,
    itemsLabel: string,
    onClickItem: (element: T) => void,
}) {
    const {attributes, listeners, setNodeRef, transform, transition, isDragging} = useSortable({id: item.id!});

    return (
        <ListItem
            ref={setNodeRef}
            style={{transform: CSS.Transform.toString(transform), transition, opacity: isDragging ? 0.5 : 1}}
            id={`${typeLabel}-${itemsLabel}-${item.id}`}
            className={`${typeLabel}-list-item`}
            disablePadding
            divider
            secondaryAction={
                <IconButton edge="end" size="small" onClick={() => onClickItem(item)} aria-label="remove">✕</IconButton>
            }
        >
            <span className={styles.dragHandle} {...attributes} {...listeners}>
                <DragIndicatorIcon fontSize="small" color="action"/>
            </span>
            <Stack sx={{py: 1, px: 1, flexGrow: 1, userSelect: "none"}}>
                <Typography sx={{fontSize: "inherit"}}>{item.name}</Typography>
                {item.subtitle && <Typography variant="caption" color="text.secondary">{item.subtitle}</Typography>}
            </Stack>
        </ListItem>
    );
}

export default function ListBuilderList<T extends ListBuilderElement>(props: ListBuilderListProps<T>) {
    const [filter, setFilter] = useState("");
    const isSortable = !!props.onReorder;

    const sensors = useSensors(
        useSensor(PointerSensor),
        useSensor(KeyboardSensor, {coordinateGetter: sortableKeyboardCoordinates}),
    );

    function getHeadingText() {
        const t = props.typeLabel.toLowerCase();
        return t.charAt(0).toUpperCase() + t.slice(1) + " " + props.itemLabel;
    }

    function handleDragEnd(event: DragEndEvent) {
        const {active, over} = event;
        if (!over || active.id === over.id) return;
        const oldIndex = props.selectedOptions.findIndex(o => o.id === active.id);
        const newIndex = props.selectedOptions.findIndex(o => o.id === over.id);
        props.onReorder?.(arrayMove(props.selectedOptions, oldIndex, newIndex));
    }

    const filteredOptions: Array<T> = filter
        ? isSortable
            ? props.selectedOptions.filter(o =>
                o.name?.toLowerCase().includes(filter.toLowerCase()) ||
                o.subtitle?.toLowerCase().includes(filter.toLowerCase())
            )
            : new Fuse<T>(props.selectedOptions, {keys: ["name", "subtitle"]}).search(filter).map(o => o.item)
        : props.selectedOptions;

    return (
        <Stack>
            <Stack direction="row" sx={{mb: 1, alignItems: "center", justifyContent: "space-between", minHeight: 40}}>
                <Stack direction="row" spacing={1} sx={{alignItems: "baseline"}}>
                    <Typography variant="h5">{getHeadingText()}</Typography>
                    <Typography variant="body2" color="text.secondary">({props.selectedOptions.length})</Typography>
                </Stack>
                {props.onSelectAll && (
                    <Button size="small" onClick={props.onSelectAll} disabled={props.selectedOptions.length === 0}>
                        Select all
                    </Button>
                )}
                {props.onClearAll && (
                    <Button size="small" color="error" onClick={props.onClearAll}
                            disabled={props.selectedOptions.length === 0}>
                        Clear all
                    </Button>
                )}
            </Stack>
            {props.showFilter && (
                <TextField label="Filter" type="search" variant="outlined" size="small" margin="none"
                           id={`${props.typeLabel}-filter`}
                           onChange={(e: ChangeEvent<HTMLInputElement>) => setFilter(e.target.value)}
                           sx={{mb: 1}}/>
            )}
            <List className={styles.list} sx={{p: 0}}>
                {filteredOptions.length === 0 && (
                    <Typography sx={{m: 2}} color="text.secondary">
                        No {props.itemsLabel} {props.typeLabel.toLowerCase()}
                    </Typography>
                )}
                {isSortable ? (
                    <DndContext sensors={sensors} collisionDetection={closestCenter} onDragEnd={handleDragEnd}>
                        <SortableContext items={filteredOptions.map(o => o.id!)} strategy={verticalListSortingStrategy}>
                            {filteredOptions.map(opt => (
                                <SortableItem key={opt.id} item={opt} typeLabel={props.typeLabel}
                                              itemsLabel={props.itemsLabel} onClickItem={props.onClickItem}/>
                            ))}
                        </SortableContext>
                    </DndContext>
                ) : (
                    filteredOptions.map(opt => (
                        <ListItemButton
                            id={`${props.typeLabel}-${props.itemsLabel}-${opt.id}`}
                            className={`${props.typeLabel}-list-item`}
                            key={opt.id}
                            onClick={() => props.onClickItem(opt)}
                            divider>
                            {opt.subtitle ? (
                                <ListItemText
                                    primary={opt.name}
                                    secondary={opt.subtitle}
                                />
                            ) : (
                                opt.name
                            )}
                        </ListItemButton>
                    ))
                )}
            </List>
        </Stack>
    );
}
