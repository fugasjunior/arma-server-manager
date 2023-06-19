import {ChangeEvent, MouseEvent, useState} from 'react';
import Box from '@mui/material/Box';
import Paper from '@mui/material/Paper';
import ModsTableToolbar from "./ModsTableToolbar";
import {ModDto} from "../../dtos/ModDto.ts";
import {ModsTableControls} from "./ModsTableControls.tsx";
import {ModsTableBody} from "./ModsTableBody.tsx";

type ModsTableProps = {
    rows: Array<ModDto>,
    selected: Array<number>,
    filter: string
    arma3ModsCount: number
    dayZModsCount: number
    mixedModsSelected: boolean
    loading: boolean
    onModInstallClicked: (modId: number) => void,
    onModUpdateClicked: () => void,
    onCreatePresetClicked: () => void,
    onModUninstallClicked: () => void,
    onFilterChange: (_: any, newValue: string) => void,
    onRowClick: (e: MouseEvent<HTMLTableRowElement>, rowId: number) => void,
    onSelectAllRowsClick: (event: ChangeEvent<HTMLInputElement>, checked: boolean) => void,
}

const ModsTable = (props: ModsTableProps) => {
    const {rows, selected} = props;

    const [order, setOrder] = useState<'asc' | 'desc'>('asc');
    const [orderBy, setOrderBy] = useState('name');
    const [pageNumber, setPageNumber] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(15);
    const [enteredModId, setEnteredModId] = useState("");
    const [searchTerm, setSearchTerm] = useState("");

    const handleSortColumnClicked = (_: Event, property: string) => {
        setSearchTerm("");
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
        setPageNumber(newPage);
    };

    const handleChangeRowsPerPage = (event: ChangeEvent<HTMLInputElement>) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPageNumber(0);
    };

    const handleSearchChange = (event: ChangeEvent<HTMLInputElement>) => {
        setSearchTerm(event.target.value);
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
                    searchTerm={searchTerm}
                    onUpdateClicked={props.onModUpdateClicked}
                    onCreatePresetClicked={props.onCreatePresetClicked}
                    onUninstallClicked={props.onModUninstallClicked}
                    onFilterChange={props.onFilterChange}
                    onSearchChange={handleSearchChange}
                />
                <ModsTableBody numbers={selected} order={order} orderBy={orderBy}
                               onSelectAllRowsClick={props.onSelectAllRowsClick} onSortColumnClicked={handleSortColumnClicked}
                               rows={rows} searchTerm={searchTerm} loading={props.loading} pageNumber={pageNumber}
                               rowsPerPage={rowsPerPage} selected={selected} onRowClick={props.onRowClick}/>
                <ModsTableControls modId={enteredModId} onModIdChange={handleEnteredModIdChange}
                                   onInstallClicked={() => props.onModInstallClicked(Number(enteredModId))} modDtos={rows}
                                   rowsPerPage={rowsPerPage} pageNumber={pageNumber} onPageChange={handleChangePage}
                                   onRowsPerPageChange={handleChangeRowsPerPage}/>
            </Paper>
        </Box>
    );
}

export default ModsTable;