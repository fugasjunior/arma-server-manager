import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import Checkbox from "@mui/material/Checkbox";
import TableSortLabel from "@mui/material/TableSortLabel";
import Box from "@mui/material/Box";
import {visuallyHidden} from "@mui/utils";
import {ChangeEvent} from "react";
import {EnhancedTableHeadCell} from "./EnhancedTable.tsx";

type EnhancedTableHeadProps = {
    numSelected: number,
    onRequestSort: (property: string) => void,
    onSelectAllClick: (event: ChangeEvent<HTMLInputElement>, checked: boolean) => void,
    order: "asc" | "desc",
    orderBy: string,
    rowCount: number,
    search?: string,
    headCells: Array<EnhancedTableHeadCell>,
    selectable?: boolean
};

const EnhancedTableHead = (
    {
        onSelectAllClick,
        order,
        orderBy,
        numSelected,
        rowCount,
        onRequestSort,
        headCells,
        search,
        selectable = true
    }: EnhancedTableHeadProps
) => {
    const createSortHandler = (property: string) => (_event: unknown) => {
        onRequestSort(property);
    };

    return (
        <TableHead>
            <TableRow>
                {selectable && <TableCell padding="checkbox">
                    <Checkbox
                        color="primary"
                        indeterminate={numSelected > 0 && numSelected < rowCount}
                        checked={rowCount > 0 && numSelected === rowCount}
                        onChange={onSelectAllClick}
                        slotProps={{
                            input: {"aria-label": "select all"},
                        }}
                    />
                </TableCell>}
                {headCells.map((headCell) => (
                    <TableCell
                        key={headCell.id}
                        align="left"
                        padding={headCell.disablePadding ? "none" : "normal"}
                        sortDirection={orderBy === headCell.id ? order : false}
                    >
                        <TableSortLabel
                            active={orderBy === headCell.id && !search}
                            direction={orderBy === headCell.id ? order : "asc"}
                            onClick={createSortHandler(headCell.id)}
                        >
                            {headCell.label}
                            {orderBy === headCell.id ? (
                                <Box component="span" sx={visuallyHidden}>
                                    {order === "desc" ? "sorted descending" : "sorted ascending"}
                                </Box>
                            ) : null}
                        </TableSortLabel>
                    </TableCell>
                ))}
            </TableRow>
        </TableHead>
    );
};

export default EnhancedTableHead;
