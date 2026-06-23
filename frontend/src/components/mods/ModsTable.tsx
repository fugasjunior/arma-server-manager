import {ChangeEvent, ReactNode, useState} from 'react';
import PermissionGuard from "../auth/PermissionGuard";
import Box from '@mui/material/Box';
import Paper from '@mui/material/Paper';
import ModsTableToolbar from "./ModsTableToolbar";
import {ErrorStatus, ModDto, ModFlagsDto, ServerType, SteamCmdItemInfoDto, SteamCmdStatus} from "../../api/generated";
import {EnhancedTable, EnhancedTableHeadCell, EnhancedTableRow} from "../../UI/EnhancedTable/EnhancedTable.tsx";
import {Button, CircularProgress, Stack, TextField} from "@mui/material";
import Tooltip from "@mui/material/Tooltip";
import ModFlagsControl from "./ModFlagsControl";
import workshopErrorStatusMap from "../../util/workshopErrorStatusMap.ts";
import ReportProblemIcon from "@mui/icons-material/ReportProblem";
import CheckIcon from "@mui/icons-material/Check";
import HourglassBottomIcon from '@mui/icons-material/HourglassBottom';
import DownloadDoneIcon from '@mui/icons-material/DownloadDone';
import SERVER_NAMES from "../../util/serverNames.ts";
import {humanFileSize} from "../../util/util.ts";
import {usePermission} from "../../hooks/usePermission.ts";

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
        id: 'loadedOn',
        label: 'Loaded on'
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
    onFlagsChange: (id: number, flags: ModFlagsDto) => void,
}

const ModsTable = (props: ModsTableProps) => {
    const [enteredModId, setEnteredModId] = useState("");
    const canModify = usePermission("MOD_MODIFY");

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

        const modItemInfo = props.steamCmdItemInfo[mod.id!];

        if (status === "INSTALLATION_IN_PROGRESS") {
            if (modItemInfo?.status === SteamCmdStatus.InQueue) {
                return <Tooltip title="In queue"><HourglassBottomIcon/></Tooltip>;
            }
            if (modItemInfo?.status === SteamCmdStatus.Finished) {
                return <Tooltip title="Waiting for installation"><DownloadDoneIcon/></Tooltip>;
            }

            return <CircularProgress size={20}/>;
        }
        if (status === "ERROR") {
            return <Tooltip
                title={workshopErrorStatusMap.get(error as ErrorStatus)}><ReportProblemIcon/></Tooltip>
        }

        if (status === "FINISHED") {
            return <Tooltip title="Installed"><CheckIcon/></Tooltip>;
        }
    };

    const mapModDtosToRows = (): Array<EnhancedTableRow> => {
        return props.rows.map(modDto => {
            return {
                id: modDto.id!,
                cells: [
                    {
                        id: "id",
                        value: modDto.id ?? 0
                    },
                    {
                        id: "name",
                        value: modDto.name ?? ""
                    },
                    {
                        id: "serverType",
                        value: SERVER_NAMES.get(modDto.serverType as ServerType) ?? ""
                    },
                    {
                        id: "fileSize",
                        value: modDto.fileSize ?? 0,
                        displayValue: humanFileSize(modDto.fileSize ?? 0)
                    },
                    {
                        id: "loadedOn",
                        value: "loadedOn",
                        displayValue: (
                            <span data-testid={`mod-flags-${modDto.id}`}>
                                <ModFlagsControl
                                    flags={{
                                        loadOnClient: modDto.loadOnClient ?? true,
                                        loadOnServer: modDto.loadOnServer ?? true,
                                        loadOnHeadlessClient: modDto.loadOnHeadlessClient ?? true,
                                    }}
                                    serverType={modDto.serverType as ServerType}
                                    disabled={!canModify}
                                    onChange={(flags: ModFlagsDto) => props.onFlagsChange(modDto.id!, flags)}
                                />
                            </span>
                        )
                    },
                    {
                        id: "lastUpdated",
                        value: modDto.lastUpdated ?? "",
                        displayValue: modDto.lastUpdated
                            ? <Tooltip title={new Date(modDto.lastUpdated).toLocaleString()} placement="top">
                                <span>{new Date(modDto.lastUpdated).toLocaleDateString()}</span>
                              </Tooltip>
                            : ""
                    },
                    {
                        id: "installationStatus",
                        value: modDto.installationStatus ?? "",
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
                                   <PermissionGuard permission="MOD_MODIFY">
                                       <Stack direction="row" spacing={1}>
                                           <TextField id="mod-install-field" label="Install mod" placeholder="Mod ID"
                                                      size="small" slotProps={{htmlInput: {"data-testid": "mod-install-input"}}}
                                                      variant="filled" value={enteredModId}
                                                      onChange={handleEnteredModIdChange}/>
                                           <Button variant="outlined" size="small" disabled={enteredModId.length === 0}
                                                   data-testid="mod-install-submit"
                                                   onClick={() => props.onModInstallClicked(Number(enteredModId))}>Install</Button>
                                       </Stack>
                                   </PermissionGuard>
                               }
                />
            </Paper>
        </Box>
    );
}

export default ModsTable;
