import {ChangeEvent, MouseEvent, useState} from 'react';
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
import {Button, CircularProgress, Stack, TextField} from "@mui/material";
import ReportProblemIcon from "@mui/icons-material/ReportProblem";
import CheckIcon from "@mui/icons-material/Check";
import Tooltip from "@mui/material/Tooltip";
import workshopErrorStatusMap from "../../util/workshopErrorStatusMap";
import SERVER_NAMES from "../../util/serverNames";
import TableGhosts from "../../UI/TableSkeletons";
import config from "../../config";
import Fuse from "fuse.js";
import {ModDto} from "../../dtos/ModDto.ts";
import {ErrorStatus} from "../../dtos/Status.ts";
import {ServerType} from "../../dtos/ServerDto.ts";

function getComparator(order: 'asc' | 'desc', orderBy: string) {
    const sortByCell = headCells.find(cell => cell.id === orderBy);
    if (!sortByCell) {
        return;
    }

    if (sortByCell.type === "number" || sortByCell.type === "date") {
        return order === "desc"
            ? (a: any, b: any) => a[orderBy] - b[orderBy]
            : (a: any, b: any) => b[orderBy] - a[orderBy];
    }

    return order === "desc"
        ? (a: any, b: any) => b[orderBy].localeCompare(a[orderBy])
        : (a: any, b: any) => a[orderBy].localeCompare(b[orderBy]);
}

const headCells: { id: string, label: string, type?: string }[] = [
    {
        id: 'id',
        label: 'ID',
        type: 'number'
    },
    {
        id: 'name',
        label: 'Name',
    },
    {
        id: 'serverType',
        label: 'For',
    },
    {
        id: 'fileSize',
        label: 'File size',
        type: 'number'
    },
    {
        id: 'lastUpdated',
        label: 'Last updated',
        type: 'date'
    },
    {
        id: 'installationStatus',
        label: 'Status',
    }
];

const getInstalledIcon = (mod: ModDto) => {
    const status = mod.installationStatus;
    const error = mod.errorStatus;

    if (status === "INSTALLATION_IN_PROGRESS") {
        return <CircularProgress size={20}/>;
    }
    if (status === "ERROR") {
        return <Tooltip
            title={workshopErrorStatusMap.get(ErrorStatus[error as keyof typeof ErrorStatus])}><ReportProblemIcon/></Tooltip>
    }

    if (status === "FINISHED") {
        return <CheckIcon/>;
    }
};

type ModsTableProps = {
    rows: Array<ModDto>,
    selected: Array<number>,
    filter: string
    arma3ModsCount: number
    dayZModsCount: number
    mixedModsSelected: boolean
    loading: boolean
    onInstallClicked: (modId: number) => void,
    onUpdateClicked: () => void,
    onCreatePresetClicked: () => void,
    onUninstallClicked: () => void,
    onFilterChange: (_: any, newValue: string) => void,
    onClick: (e: MouseEvent<HTMLTableRowElement>, rowId: number) => void,
    onSelectAllClick: (event: ChangeEvent<HTMLInputElement>, checked: boolean) => void,
}

const ModsTable = (props: ModsTableProps) => {
    const {rows, selected} = props;

    const [order, setOrder] = useState<'asc' | 'desc'>('asc');
    const [orderBy, setOrderBy] = useState('name');
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(15);
    const [enteredModId, setEnteredModId] = useState("");
    const [search, setSearch] = useState("");

    const handleRequestSort = (_: Event, property: string) => {
        setSearch("");
        const isAsc = orderBy === property && order === 'asc';
        setOrder(isAsc ? 'desc' : 'asc');
        setOrderBy(property);
    };

    const handleEnteredModIdChange = (e: ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        if (isNaN(+value)) {
            return;
        }
        setEnteredModId(e.target.value);
    }

    const handleChangePage = (_: any, newPage: number) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event: ChangeEvent<HTMLInputElement>) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    const handleSearchChange = (event: ChangeEvent<HTMLInputElement>) => {
        setSearch(event.target.value);
    }

    const isSelected = (id: number) => selected.indexOf(id) !== -1;

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
                <ModsTableToolbar
                    numSelected={selected.length}
                    title="Workshop mods"
                    filter={props.filter}
                    arma3ModsCount={props.arma3ModsCount}
                    dayZModsCount={props.dayZModsCount}
                    mixedModsSelected={props.mixedModsSelected}
                    search={search}
                    onUpdateClicked={props.onUpdateClicked}
                    onCreatePresetClicked={props.onCreatePresetClicked}
                    onUninstallClicked={props.onUninstallClicked}
                    onFilterChange={props.onFilterChange}
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
                            search={search}
                        />
                        {!props.loading && <TableBody>
                            {getRows().slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
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
                                            <TableCell>{SERVER_NAMES.get(ServerType[row.serverType as keyof typeof ServerType])}</TableCell>
                                            <TableCell>{humanFileSize(row.fileSize)}</TableCell>
                                            <TableCell>
                                                {row.lastUpdated && row.lastUpdated.toLocaleDateString(undefined,
                                                    config.dateFormat)}
                                            </TableCell>
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
                        </TableBody>}
                    </Table>
                    <TableGhosts display={props.loading} count={15}/>
                </TableContainer>
                <Stack direction="row" justifyContent="space-between" alignItems="start" m={2}>
                    <Stack direction="row" spacing={1}>
                        <TextField id="mod-install-field" label="Install mod" placeholder="Mod ID" size="small"
                                   variant="filled" value={enteredModId} onChange={handleEnteredModIdChange}/>
                        <Button variant="outlined" size="small" disabled={enteredModId.length === 0}
                                onClick={() => props.onInstallClicked(Number(enteredModId))}>Install</Button>
                    </Stack>
                    {rows.length > 0 && <TablePagination
                        rowsPerPageOptions={[10, 15, 25, 50]}
                        component="div"
                        count={rows.length}
                        rowsPerPage={rowsPerPage}
                        page={page}
                        onPageChange={handleChangePage}
                        onRowsPerPageChange={handleChangeRowsPerPage}
                    />}
                </Stack>
            </Paper>
        </Box>
    );
}

export default ModsTable;