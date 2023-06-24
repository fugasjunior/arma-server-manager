import TableContainer from "@mui/material/TableContainer";
import Table from "@mui/material/Table";
import TableGhosts from "../TableSkeletons.tsx";
import Fuse from "fuse.js";
import {getComparator} from "../../util/tableUtils.ts";
import {ChangeEvent, ReactNode, useState} from "react";
import EnhancedTableHead from "./EnhancedTableHead.tsx";
import {EnhancedTableControls} from "./EnhancedTableControls.tsx";
import {EnhancedTableBody} from "./EnhancedTableBody.tsx";

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
    defaultSortColumnId?: string,
    customControls?: ReactNode
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
        defaultSortColumnId,
        customControls
    }: EnhancedTableProps
) => {
    const [pageNumber, setPageNumber] = useState<number>(0);
    const [rowsPerPage, setRowsPerPage] = useState<number>(10);
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
            const fuse = new Fuse(rows, {keys: searchableColumnNames});
            const searched = fuse.search(searchTerm);
            return searched.map(o => o.item);
        }
        return rows.sort(getComparator(order, orderByColumnId, headCells));
    };

    const getPaginatedRows = () => {
        return getFilteredRows().slice(pageNumber * rowsPerPage, pageNumber * rowsPerPage + rowsPerPage);
    }

    // Avoid a layout jump when reaching the last page with empty rows.
    const emptyRowsCount = pageNumber > 0 ? Math.max(0, (1 + pageNumber) * rowsPerPage - rows.length) : 0;

    const handleSortColumnClicked = (columnId: string) => {
        const isAlreadySelectedAndAscending = orderByColumnId === columnId && order === "asc";
        setOrder(isAlreadySelectedAndAscending ? "desc" : "asc");
        setOrderByColumnId(columnId);
    };

    const handlePageChange = (newPage: number) => {
        setPageNumber(newPage);
    };

    const handleRowsPerPageChange = (event: ChangeEvent<HTMLInputElement>) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPageNumber(0);
    };

    return <>
        <TableContainer>
            <Table
                sx={{minWidth: 750}}
                aria-labelledby="tableTitle"
                size="small"
            >
                <EnhancedTableHead
                    numSelected={selectedRowIds.length}
                    order={order}
                    orderBy={orderByColumnId}
                    onSelectAllClick={onSelectAllRowsClick}
                    onRequestSort={handleSortColumnClicked}
                    rowCount={rows.length}
                    headCells={headCells}
                    search={searchTerm}
                />
                {!loading && <EnhancedTableBody rows={getPaginatedRows()} selectedRowIds={selectedRowIds}
                                                emptyRowsCount={emptyRowsCount} onRowSelect={onRowSelect}/>}
            </Table>
            <TableGhosts display={!!loading} count={15}/>
        </TableContainer>
        <EnhancedTableControls totalRowsCount={getFilteredRows().length} rowsPerPage={rowsPerPage}
                               pageNumber={pageNumber} onPageChange={handlePageChange}
                               onRowsPerPageChange={handleRowsPerPageChange} customControls={customControls}
        />
    </>;
};