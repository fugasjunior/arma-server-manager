import {ChangeEvent, ReactNode, useState} from 'react';
import Box from '@mui/material/Box';
import Paper from '@mui/material/Paper';
import ModsTableToolbar from "./ModsTableToolbar";
import {ModDto} from "../../dtos/ModDto.ts";
import {EnhancedTable, EnhancedTableHeadCell, EnhancedTableRow} from "../../UI/EnhancedTable/EnhancedTable.tsx";
import {Button, CircularProgress, Stack, Switch, TextField} from "@mui/material";
import Tooltip from "@mui/material/Tooltip";
import workshopErrorStatusMap from "../../util/workshopErrorStatusMap.ts";
import {ErrorStatus} from "../../dtos/Status.ts";
import ReportProblemIcon from "@mui/icons-material/ReportProblem";
import CheckIcon from "@mui/icons-material/Check";
import HourglassBottomIcon from '@mui/icons-material/HourglassBottom';
import DownloadDoneIcon from '@mui/icons-material/DownloadDone';
import {ServerType} from "../../dtos/ServerDto.ts";
import SERVER_NAMES from "../../util/serverNames.ts";
import {humanFileSize} from "../../util/util.ts";
import {SteamCmdItemInfoDto, SteamCmdStatus} from "../../dtos/SteamCmdItemInfoDto.ts";

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
        id: 'serverOnly',
        label: 'Server only'
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
    filter: string,
    arma3ModsCount: number,
    dayZModsCount: number,
    mixedModsSelected: boolean,
    loading: boolean,
    steamCmdItemInfo: { [id: number]: SteamCmdItemInfoDto }
    onModInstallClicked: (modId: number) => void,
    onModUpdateClicked: () => void,
    onCreatePresetClicked: () => void,
    onModUninstallClicked: () => void,
    onFilterChange: (_: any, newValue: string) => void,
    onRowClick: (rowId: number | string) => void,
    onSelectAllRowsClick: (event: ChangeEvent<HTMLInputElement>) => void,
    onServerOnlyChanged: (event: ChangeEvent<HTMLInputElement>, id: number) => void,
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

        const modItemInfo = props.steamCmdItemInfo[mod.id];

        if (status === "INSTALLATION_IN_PROGRESS") {
            if (modItemInfo?.status === SteamCmdStatus.IN_QUEUE) {
                return <Tooltip title="In queue"><HourglassBottomIcon/></Tooltip>;
            }
            if (modItemInfo?.status === SteamCmdStatus.FINISHED) {
                return <Tooltip title="Waiting for installation"><DownloadDoneIcon/></Tooltip>;
            }

            return <CircularProgress size={20}/>;
        }
        if (status === "ERROR") {
            return <Tooltip
                title={workshopErrorStatusMap.get(ErrorStatus[error as keyof typeof ErrorStatus])}><ReportProblemIcon/></Tooltip>
        }

        if (status === "FINISHED") {
            return <Tooltip title="Installed"><CheckIcon/></Tooltip>;
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
                        id: "serverOnly",
                        value: "serverOnly",
                        displayValue: <Switch
                            checked={modDto.serverOnly}
                            onClick={(e) => e.stopPropagation()}
                            onChange={(e) => props.onServerOnlyChanged(e, modDto.id)}/>
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
                               id="workshop_mods" title="Workshop mods"
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