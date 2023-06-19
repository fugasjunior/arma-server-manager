import {ModDto} from "../../dtos/ModDto.ts";
import TableContainer from "@mui/material/TableContainer";
import Table from "@mui/material/Table";
import EnhancedTableHead from "../../UI/Table/EnhancedTableHead.tsx";
import TableBody from "@mui/material/TableBody";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import TableGhosts from "../../UI/TableSkeletons.tsx";
import Fuse from "fuse.js";
import {ModsTableRow} from "./ModsTableRow.tsx";
import {ChangeEvent, MouseEvent} from "react";
import {getComparator} from "../../util/tableUtils.ts";

const headCells = [
    {id: 'id', label: 'ID', type: 'number'},
    {id: 'name', label: 'Name'},
    {id: 'serverType', label: 'For'},
    {id: 'fileSize', label: 'File size', type: 'number'},
    {id: 'lastUpdated', label: 'Last updated', type: 'date'},
    {id: 'installationStatus', label: 'Status'}
];

export function ModsTableBody(props: {
    numbers: Array<number>,
    order: "asc" | "desc",
    orderBy: string,
    onSelectAllRowsClick: (event: ChangeEvent<HTMLInputElement>, checked: boolean) => void,
    onSortColumnClicked: (_: Event, property: string) => void,
    rows: Array<ModDto>,
    searchTerm: string,
    loading: boolean,
    pageNumber: number,
    rowsPerPage: number,
    selected: Array<number>
    onRowClick: (e: MouseEvent<HTMLTableRowElement>, rowId: number) => void
}) {

    const getFilteredRows = () => {
        if (props.searchTerm) {
            const fuse = new Fuse(props.rows, {keys: ["name"]});
            const searched = fuse.search(props.searchTerm);
            return searched.map(o => o.item);
        }
        return props.rows.sort(getComparator(props.order, props.orderBy, headCells));
    }

    const isSelected = (id: number) => props.selected.indexOf(id) !== -1;

    // Avoid a layout jump when reaching the last page with empty rows.
    const emptyRowsCount = props.pageNumber > 0 ? Math.max(0, (1 + props.pageNumber) * props.rowsPerPage - props.rows.length) : 0;

    return <TableContainer>
        <Table
            sx={{minWidth: 750}}
            aria-labelledby="tableTitle"
            size="small"
        >
            <EnhancedTableHead
                numSelected={props.numbers.length}
                order={props.order}
                orderBy={props.orderBy}
                onSelectAllClick={props.onSelectAllRowsClick}
                onRequestSort={props.onSortColumnClicked}
                rowCount={props.rows.length}
                headCells={headCells}
                search={props.searchTerm}
            />
            {!props.loading && <TableBody>
                {getFilteredRows().slice(props.pageNumber * props.rowsPerPage, props.pageNumber * props.rowsPerPage + props.rowsPerPage)
                    .map((row, index) => {
                        return (
                            <ModsTableRow key={row.id} onClick={(event) => props.onRowClick(event, row.id)}
                                          ariaChecked={isSelected(row.id)} labelId={`enhanced-table-checkbox-${index}`} row={row}/>
                        );
                    })}
                {emptyRowsCount > 0 && (
                    <TableRow
                        style={{
                            height: 33 * emptyRowsCount,
                        }}
                    >
                        <TableCell colSpan={6}/>
                    </TableRow>
                )}
            </TableBody>}
        </Table>
        <TableGhosts display={props.loading} count={15}/>
    </TableContainer>;
}