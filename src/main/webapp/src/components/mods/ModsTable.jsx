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
import ModsTableToolbar from "./ModsTableToolbar";
import EnhancedTableHead from "../../UI/Table/EnhancedTableHead";
import {humanFileSize} from "../../util/util";
import PendingIcon from "@mui/icons-material/Pending";
import {Button, CircularProgress, Stack, TextField} from "@mui/material";
import ReportProblemIcon from "@mui/icons-material/ReportProblem";
import CheckIcon from "@mui/icons-material/Check";
import Tooltip from "@mui/material/Tooltip";
import workshopErrorStatusMap from "../../util/workshopErrorStatusMap";

function descendingComparator(a, b, orderBy) {
    if (b[orderBy] < a[orderBy]) {
        return -1;
    }
    if (b[orderBy] > a[orderBy]) {
        return 1;
    }
    return 0;
}

function getComparator(order, orderBy) {
    return order === 'desc'
            ? (a, b) => descendingComparator(a, b, orderBy)
            : (a, b) => -descendingComparator(a, b, orderBy);
}

const headCells = [
    {
        id: 'id',
        label: 'ID',
    },
    {
        id: 'name',
        label: 'Name',
    },
    {
        id: 'fileSize',
        label: 'File size',
    },
    {
        id: 'lastUpdated',
        label: 'Last updated',
    },
    {
        id: 'installationStatus',
        label: 'Status',
    }
];

const getInstalledIcon = (mod) => {
    const status = mod.installationStatus;
    const error = mod.errorStatus;

    if (status === "INSTALLATION_QUEUED") {
        return <Tooltip title="Installation queued"><PendingIcon/></Tooltip>
    }
    if (status === "INSTALLATION_IN_PROGRESS") {
        return <CircularProgress size={20}/>;
    }
    if (status === "ERROR") {
        return <Tooltip title={workshopErrorStatusMap[error]}><ReportProblemIcon/></Tooltip>
    }

    if (status === "FINISHED") {
        return <CheckIcon/>;
    }
};

const ModsTable = (props) => {
    const {rows, selected} = props;

    const [order, setOrder] = useState('asc');
    const [orderBy, setOrderBy] = useState('name');
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(15);
    const [enteredModId, setEnteredModId] = useState("");

    const handleRequestSort = (event, property) => {
        const isAsc = orderBy === property && order === 'asc';
        setOrder(isAsc ? 'desc' : 'asc');
        setOrderBy(property);
    };

    const handleEnteredModIdChange = (e) => {
        const value = e.target.value;
        if (isNaN(value)) {
            return;
        }
        setEnteredModId(e.target.value);
    }

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    const isSelected = (id) => selected.indexOf(id) !== -1;

    // Avoid a layout jump when reaching the last page with empty rows.
    const emptyRows = page > 0 ? Math.max(0, (1 + page) * rowsPerPage - rows.length) : 0;

    return (
            <Box sx={{width: '100%'}}>
                <Paper sx={{width: '100%', mb: 2}}>
                    <ModsTableToolbar
                            numSelected={selected.length}
                            title="Workshop mods"
                            onUpdateClicked={props.onUpdateClicked}
                            onUninstallClicked={props.onUninstallClicked}
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
                                {rows.slice().sort(getComparator(order, orderBy))
                                .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                                .map((row, index) => {
                                    const isItemSelected = isSelected(row.id);
                                    const labelId = `enhanced-table-checkbox-${index}`;

                                    return (
                                            <TableRow
                                                    hover
                                                    onClick={(event) => props.onClick(event, row.id)}
                                                    role="checkbox"
                                                    aria-checked={isItemSelected}
                                                    tabIndex={-1}
                                                    key={row.id}
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
                                                    {row.id}
                                                </TableCell>
                                                <TableCell>{row.name}</TableCell>
                                                <TableCell>{humanFileSize(row.fileSize)}</TableCell>
                                                <TableCell>{row.lastUpdated}</TableCell>
                                                <TableCell>{getInstalledIcon(row)}</TableCell>
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
                    <Stack direction="row" justifyContent="space-between" alignItems="start" m={2}>
                        <Stack direction="row">
                            <TextField id="mod-install-field" label="Install mod" placeholder="Mod ID"
                                       variant="standard" value={enteredModId} onChange={handleEnteredModIdChange}/>
                            <Button variant="text" size="small" disabled={enteredModId.length === 0}
                                    onClick={() => props.onInstallClicked(enteredModId)}>Install</Button>
                        </Stack>
                        <TablePagination
                                rowsPerPageOptions={[10, 15, 25, 50]}
                                component="div"
                                count={rows.length}
                                rowsPerPage={rowsPerPage}
                                page={page}
                                onPageChange={handleChangePage}
                                onRowsPerPageChange={handleChangeRowsPerPage}
                        />
                    </Stack>
                </Paper>
            </Box>
    );
}

export default ModsTable;