import {ChangeEvent, ReactNode, useState} from 'react';
import Box from '@mui/material/Box';
import Paper from '@mui/material/Paper';
import ModsTableToolbar from "./ModsTableToolbar";
import {ModDto} from "../../dtos/ModDto.ts";
import {ModsTableControls} from "./ModsTableControls.tsx";
import {EnhancedTable, EnhancedTableHeadCell, EnhancedTableRow} from "../../UI/EnhancedTable/EnhancedTable.tsx";
import {CircularProgress} from "@mui/material";
import Tooltip from "@mui/material/Tooltip";
import workshopErrorStatusMap from "../../util/workshopErrorStatusMap.ts";
import {ErrorStatus} from "../../dtos/Status.ts";
import ReportProblemIcon from "@mui/icons-material/ReportProblem";
import CheckIcon from "@mui/icons-material/Check";
import {ServerType} from "../../dtos/ServerDto.ts";
import SERVER_NAMES from "../../util/serverNames.ts";
import {humanFileSize} from "../../util/util.ts";

const headCells: Array<EnhancedTableHeadCell> = [
    {id: 'id', label: 'ID', type: 'numeric'},
    {id: 'name', label: 'Name', searchable: true},
    {id: 'serverType', label: 'For'},
    {id: 'fileSize', label: 'File size', type: 'numeric'},
    {id: 'lastUpdated', label: 'Last updated', type: 'date'},
    {id: 'installationStatus', label: 'Status'}
];

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
    onRowClick: (rowId: number | string) => void,
    onSelectAllRowsClick: (event: ChangeEvent<HTMLInputElement>) => void,
}

const ModsTable = (props: ModsTableProps) => {
    const {rows, selected} = props;

    const [pageNumber, setPageNumber] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(15);
    const [enteredModId, setEnteredModId] = useState("");
    const [searchTerm, setSearchTerm] = useState("");

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

    const getInstalledIcon = (mod: ModDto): ReactNode => {
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

    const mapModDtosToRows = (): Array<EnhancedTableRow> => {
        return props.rows.map(modDto => {
            return {
                id: modDto.id,
                cells: [
                    {id: "id", value: modDto.id},
                    {id: "name", value: modDto.name},
                    {
                        id: "serverType",
                        value: SERVER_NAMES.get(ServerType[modDto.serverType as keyof typeof ServerType])!
                    },
                    {id: "fileSize", value: modDto.fileSize, displayValue: humanFileSize(modDto.fileSize)},
                    {id: "lastUpdated", value: modDto.lastUpdated},
                    {id: "installationStatus", value: modDto.installationStatus, displayValue: getInstalledIcon(modDto)}
                ]
            };
        })
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
                <EnhancedTable rows={mapModDtosToRows()} selectedRowIds={selected} headCells={headCells}
                               searchTerm={searchTerm}
                               onRowSelect={props.onRowClick} onSelectAllRowsClick={props.onSelectAllRowsClick}/>
                <ModsTableControls modId={enteredModId} onModIdChange={handleEnteredModIdChange}
                                   onInstallClicked={() => props.onModInstallClicked(Number(enteredModId))}
                                   modDtos={rows}
                                   rowsPerPage={rowsPerPage} pageNumber={pageNumber} onPageChange={handleChangePage}
                                   onRowsPerPageChange={handleChangeRowsPerPage}/>
            </Paper>
        </Box>
    );
}

export default ModsTable;