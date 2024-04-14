import {ChangeEvent, ReactNode, useState} from 'react';
import Box from '@mui/material/Box';
import Paper from '@mui/material/Paper';
import ModsTableToolbar from "./ModsTableToolbar";
import {ModDto} from "../../dtos/ModDto.ts";
import {EnhancedTable, EnhancedTableHeadCell, EnhancedTableRow} from "../../UI/EnhancedTable/EnhancedTable.tsx";
import {Button, CircularProgress, Stack, TextField} from "@mui/material";
import Tooltip from "@mui/material/Tooltip";
import workshopErrorStatusMap from "../../util/workshopErrorStatusMap.ts";
import {ErrorStatus} from "../../dtos/Status.ts";
import ReportProblemIcon from "@mui/icons-material/ReportProblem";
import CheckIcon from "@mui/icons-material/Check";
import {ServerType} from "../../dtos/ServerDto.ts";
import SERVER_NAMES from "../../util/serverNames.ts";
import {humanFileSize} from "../../util/util.ts";

const headCells: Array<EnhancedTableHeadCell> = [
    {
        id: 'id',
        label: 'ID',
        type: 'numeric'
    },
    {
        id: 'name',
        label: 'Name',
        searchable: true
    },
    {
        id: 'serverType',
        label: 'For'
    },
    {
        id: 'fileSize',
        label: 'File size',
        type: 'numeric'
    },
    {
        id: 'lastUpdated',
        label: 'Last updated',
        type: 'date'
    },
    {
        id: 'installationStatus',
        label: 'Status'
    }
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
    const [enteredModId, setEnteredModId] = useState("");

    const handleEnteredModIdChange = (e: ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        if (isNaN(+value)) {
            return;
        }
        setEnteredModId(e.target.value);
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
                    {
                        id: "id",
                        value: modDto.id
                    },
                    {
                        id: "name",
                        value: modDto.name
                    },
                    {
                        id: "serverType",
                        value: SERVER_NAMES.get(ServerType[modDto.serverType as keyof typeof ServerType])!
                    },
                    {
                        id: "fileSize",
                        value: modDto.fileSize,
                        displayValue: humanFileSize(modDto.fileSize)
                    },
                    {
                        id: "lastUpdated",
                        value: modDto.lastUpdated
                    },
                    {
                        id: "installationStatus",
                        value: modDto.installationStatus,
                        displayValue: getInstalledIcon(modDto)
                    }
                ]
            };
        })
    }

    return (
        <Box sx={{width: '100%'}}>
            <Paper sx={{
                width: '100%',
                mb: 2
            }}>
                <EnhancedTable rows={mapModDtosToRows()} selectedRowIds={props.selected} headCells={headCells}
                               title="Workshop mods"
                               onRowSelect={props.onRowClick} onSelectAllRowsClick={props.onSelectAllRowsClick}

                               customTopControls={<ModsTableToolbar
                                   selectedModsCount={props.selected.length}
                                   filter={props.filter}
                                   arma3ModsCount={props.arma3ModsCount}
                                   dayZModsCount={props.dayZModsCount}
                                   mixedModsSelected={props.mixedModsSelected}
                                   onUpdateClicked={props.onModUpdateClicked}
                                   onCreatePresetClicked={props.onCreatePresetClicked}
                                   onUninstallClicked={props.onModUninstallClicked}
                                   onFilterChange={props.onFilterChange}
                               />}

                               customBottomControls={
                                   <Stack direction="row" spacing={1}>
                                       <TextField id="mod-install-field" label="Install mod" placeholder="Mod ID"
                                                  size="small"
                                                  variant="filled" value={enteredModId}
                                                  onChange={handleEnteredModIdChange}/>
                                       <Button variant="outlined" size="small" disabled={enteredModId.length === 0}
                                               onClick={() => props.onModInstallClicked(Number(enteredModId))}>Install</Button>
                                   </Stack>
                               }
                />
            </Paper>
        </Box>
    );
}

export default ModsTable;