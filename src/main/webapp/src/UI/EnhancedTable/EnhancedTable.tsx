import TableContainer from "@mui/material/TableContainer";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import TableGhosts from "../TableSkeletons.tsx";
import Fuse from "fuse.js";
import {getComparator} from "../../util/tableUtils.ts";
import {ChangeEvent, ReactNode, useState} from "react";
import Checkbox from "@mui/material/Checkbox";
import config from "../../config.ts";
import EnhancedTableHead2 from "./EnhancedTableHead2.tsx";

export type EnhancedTableRow = {
    id: string | number,
    cells: Array<EnhancedTableCell>
};

export type EnhancedTableCell = {
    id: string,
    value: string | number | boolean | Date
    displayValue?: string | number | boolean | Date | ReactNode
};

export type EnhancedTableHeadCell = {
    id: string,
    label: string,
    type?: "text" | "numeric" | "date",
    disablePadding?: boolean,
    sortable?: boolean
    searchable?: boolean
}

type EnhancedTableProps = {
    rows: Array<EnhancedTableRow>,
    selectedRowIds: Array<string | number>,
    headCells: Array<EnhancedTableHeadCell>,
    onRowSelect: (rowId: string | number) => void,
    onSelectAllRowsClick: (event: ChangeEvent<HTMLInputElement>) => void,
    searchTerm?: string,
    loading?: boolean
    defaultSortColumnId?: string
};

export const EnhancedTable = (
    {
        rows,
        selectedRowIds,
        headCells,
        onRowSelect,
        onSelectAllRowsClick,
        searchTerm,
        loading,
        defaultSortColumnId
    }: EnhancedTableProps
) => {
    // const [pageNumber, setPageNumber] = useState<number>(0);
    // const [rowsPerPage, setRowsPerPage] = useState<number>(5);
    const [orderByColumnId, setOrderByColumnId] = useState<string>(defaultSortColumnId ?? headCells[0].id);
    const [order, setOrder] = useState<"asc" | "desc">("asc");

    const searchableColumnNames = headCells
        .filter(cell => cell.searchable)
        .map(searchableCell => {
            return {
                name: searchableCell.id,
                getFn: (row: EnhancedTableRow) => {
                    const targetCell = row.cells.find(cell => cell.id === searchableCell.id);
                    if (!targetCell) {
                        throw new Error("Cell to search text in was not found.");
                    }
                    return targetCell.value.toString();
                }
            };
        });

    const getFilteredRows = () => {
        if (searchTerm) {
            console.log(searchTerm, searchableColumnNames, rows);
            const fuse = new Fuse(rows, {keys: searchableColumnNames});
            const searched = fuse.search(searchTerm);
            return searched.map(o => o.item);
        }
        return rows.sort(getComparator(order, orderByColumnId, headCells));
    };

    const isSelected = (rowId: number | string) => selectedRowIds.indexOf(rowId) !== -1;

    // Avoid a layout jump when reaching the last page with empty rows.
    // const emptyRowsCount = pageNumber > 0 ? Math.max(0, (1 + pageNumber) * rowsPerPage - rows.length) : 0;

    const handleSortColumnClicked = (columnId: string) => {
        const isAlreadySelectedAndAscending = orderByColumnId === columnId && order === "asc";
        setOrder(isAlreadySelectedAndAscending ? 'desc' : 'asc');
        setOrderByColumnId(columnId);
    };

    return <TableContainer>
        <Table
            sx={{minWidth: 750}}
            aria-labelledby="tableTitle"
            size="small"
        >
            <EnhancedTableHead2
                numSelected={selectedRowIds.length}
                order={order}
                orderBy={orderByColumnId}
                onSelectAllClick={onSelectAllRowsClick}
                onRequestSort={handleSortColumnClicked}
                rowCount={rows.length}
                headCells={headCells}
                search={searchTerm}
            />
            {!loading && <TableBody>
                {getFilteredRows()
                    // .slice(pageNumber * rowsPerPage, pageNumber * rowsPerPage + rowsPerPage)
                    .map((row) => {
                        const selected = isSelected(row.id);
                        return (
                            <TableRow key={row.id} hover onClick={() => onRowSelect(row.id)} role="checkbox"
                                      aria-checked={selected} tabIndex={-1} selected={selected}>
                                <TableCell padding="checkbox">
                                    <Checkbox checked={selected} color="primary"
                                              inputProps={{
                                                  "aria-labelledby": String(row.id),
                                              }}
                                    />
                                </TableCell>
                                {row.cells.map((cell, index) => {
                                    const valueToShow = cell.displayValue ?? cell.value;
                                    if (valueToShow instanceof Date) {
                                        return <TableCell
                                            key={index}>{valueToShow.toLocaleDateString(undefined, config.dateFormat)}</TableCell>;
                                    }
                                    return <TableCell key={index}>{valueToShow}</TableCell>;
                                })}
                            </TableRow>
                        );
                    })}
                {/*{emptyRowsCount > 0 && (*/}
                {/*    <TableRow style={{height: 33 * emptyRowsCount}}>*/}
                {/*        <TableCell colSpan={6}/>*/}
                {/*    </TableRow>*/}
                {/*)}*/}
            </TableBody>}
        </Table>
        <TableGhosts display={!!loading} count={15}/>
    </TableContainer>;
};