import * as React from 'react';
import {useState} from 'react';
import Box from '@mui/material/Box';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TablePagination from '@mui/material/TablePagination';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import Checkbox from '@mui/material/Checkbox';
import ScenariosTableToolbar from "./ScenariosTableToolbar";
import EnhancedTableHead from "../../UI/Table/EnhancedTableHead";
import {humanFileSize} from "../../util/util";
import {Button} from "@mui/material";
import Fuse from "fuse.js";
import config from "../../config";

function getComparator(order, orderBy) {
    const sortByCell = headCells.find(cell => cell.id === orderBy);

    if (sortByCell.type === "number" || sortByCell.type === "date") {
        return order === "desc"
                ? (a, b) => a[orderBy] - b[orderBy]
                : (a, b) => b[orderBy] - a[orderBy];
    }

    return order === "desc"
            ? (a, b) => b[orderBy].localeCompare(a[orderBy])
            : (a, b) => a[orderBy].localeCompare(b[orderBy]);
}

const headCells = [
    {
        id: 'name',
        label: 'Name',
    },
    {
        id: 'fileSize',
        label: 'File size',
        type: 'number'
    },
    {
        id: 'createdOn',
        label: 'Created on',
        type: 'date'
    }

];

const ScenariosTable = (props) => {
    const {rows, selected} = props;

    const [order, setOrder] = useState('asc');
    const [orderBy, setOrderBy] = useState('name');
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(15);
    const [search, setSearch] = useState("");

    const handleRequestSort = (event, property) => {
        setSearch("");
        const isAsc = orderBy === property && order === 'asc';
        setOrder(isAsc ? 'desc' : 'asc');
        setOrderBy(property);
    };

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    const handleSearchChange = (event) => {
        setSearch(event.target.value);
    }

    const isSelected = (name) => selected.indexOf(name) !== -1;

    // Avoid a layout jump when reaching the last page with empty rows.
    const emptyRows = page > 0 ? Math.max(0, (1 + page) * rowsPerPage - rows.length) : 0;

    const getRows = () => {
        if (search) {
            const fuse = new Fuse(props.rows, {keys: ["name"]});
            const searched = fuse.search(search);
            return searched.map(o => o.item);
        }
        return props.rows.sort(getComparator(order, orderBy));
    }

    return (
            <Box sx={{width: '100%'}}>
                <Paper sx={{width: '100%', mb: 2}}>
                    <ScenariosTableToolbar
                            numSelected={selected.length}
                            onDeleteClicked={props.onDeleteClicked}
                            onFileChange={props.onFileChange}
                            uploadInProgress={props.uploadInProgress}
                            percentUploaded={props.percentUploaded}
                            search={search}
                            title="Scenarios"
                            onSearchChange={handleSearchChange}
                    />
                    <TableContainer>
                        <Table
                                sx={{minWidth: 750}}
                                aria-labelledby="tableTitle"
                                size='small'
                        >
                            <EnhancedTableHead
                                    numSelected={selected.length}
                                    order={order}
                                    orderBy={orderBy}
                                    onSelectAllClick={props.onSelectAllClick}
                                    onRequestSort={handleRequestSort}
                                    rowCount={rows.length}
                                    headCells={headCells}
                            />
                            <TableBody>
                                {getRows().slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                                .map((row, index) => {
                                    const isItemSelected = isSelected(row.name);
                                    const labelId = `enhanced-table-checkbox-${index}`;

                                    return (
                                            <TableRow
                                                    hover
                                                    onClick={(event) => props.onClick(event, row.name)}
                                                    role="checkbox"
                                                    aria-checked={isItemSelected}
                                                    tabIndex={-1}
                                                    key={row.name}
                                                    selected={isItemSelected}
                                            >
                                                <TableCell padding="checkbox">
                                                    <Checkbox
                                                            color="primary"
                                                            checked={isItemSelected}
                                                            inputProps={{
                                                                'aria-labelledby': labelId,
                                                            }}
                                                    />
                                                </TableCell>
                                                <TableCell
                                                        component="th"
                                                        id={labelId}
                                                        scope="row"
                                                        padding="none"
                                                >
                                                    <Button onClick={(e) => props.onDownloadClicked(row.name, e)}>
                                                        {row.name}
                                                    </Button>
                                                </TableCell>
                                                <TableCell>
                                                    {humanFileSize(row.fileSize)}
                                                </TableCell>
                                                <TableCell>
                                                    {row.createdOn.toLocaleString(undefined, config.dateFormat)}
                                                </TableCell>
                                            </TableRow>
                                    );
                                })}
                                {emptyRows > 0 && (
                                        <TableRow
                                                style={{
                                                    height: 33 * emptyRows,
                                                }}
                                        >
                                            <TableCell colSpan={6}/>
                                        </TableRow>
                                )}
                            </TableBody>
                        </Table>
                    </TableContainer>
                    <TablePagination
                            rowsPerPageOptions={[10, 15, 25, 50]}
                            component="div"
                            count={rows.length}
                            rowsPerPage={rowsPerPage}
                            page={page}
                            onPageChange={handleChangePage}
                            onRowsPerPageChange={handleChangeRowsPerPage}
                    />
                </Paper>
            </Box>
    );
}

export default ScenariosTable;