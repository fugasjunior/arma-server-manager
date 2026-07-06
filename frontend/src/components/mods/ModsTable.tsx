import {ChangeEvent, ReactNode, useState} from 'react';
import PermissionGuard from "../auth/PermissionGuard";
import Box from '@mui/material/Box';
import Paper from '@mui/material/Paper';
import ModsTableToolbar from "./ModsTableToolbar";
import {ErrorStatus, ModDto, ModFlagsDto, ServerType, SteamCmdItemInfoDto, SteamCmdStatus} from "../../api/generated";
import {EnhancedTable, EnhancedTableHeadCell, EnhancedTableRow} from "../../UI/EnhancedTable/EnhancedTable.tsx";
import {Button, CircularProgress, Stack, Tab, Tabs, TextField} from "@mui/material";
import Tooltip from "@mui/material/Tooltip";
import ModFlagsControl from "./ModFlagsControl";
import workshopErrorStatusMap from "../../util/workshopErrorStatusMap.ts";
import ReportProblemIcon from "@mui/icons-material/ReportProblem";
import CheckIcon from "@mui/icons-material/Check";
import HourglassBottomIcon from '@mui/icons-material/HourglassBottom';
import DownloadDoneIcon from '@mui/icons-material/DownloadDone';
import SystemUpdateAltIcon from '@mui/icons-material/SystemUpdateAlt';
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
        label: 'Loaded on',
        sortable: false
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
    onModInstallClicked: (modId: number) => Promise<void>,
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
                        id: "installationStatus",
                        value: `${modDto.installationStatus !== "FINISHED" || modDto.updateAvailable ? "0" : "1"}${(modDto.name ?? "").toLowerCase()}`,
                        displayValue: (
                            <Stack direction="row" spacing={0.5} sx={{alignItems: 'center'}}>
                                {getInstalledIcon(modDto)}
                                {modDto.updateAvailable && (
                                    <Tooltip title={[
                                        'Update available',
                                        modDto.installedUpdate ? `installed: ${new Date(modDto.installedUpdate).toLocaleString()}` : null,
                                        modDto.latestUpdate ? `latest: ${new Date(modDto.latestUpdate).toLocaleString()}` : null,
                                    ].filter(Boolean).join(' — ')}>
                                        <SystemUpdateAltIcon color="warning" fontSize="small"
                                                             data-testid={`update-available-${modDto.id}`}/>
                                    </Tooltip>
                                )}
                            </Stack>
                        )
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
                               id="workshop_mods" title="Workshop mods" defaultSortColumnId="installationStatus"
                               onRowSelect={props.onRowClick} onSelectAllRowsClick={props.onSelectAllRowsClick}

                               customLeadingControls={
                                   <PermissionGuard permission="MOD_MODIFY">
                                       <Stack direction="row" spacing={1}>
                                           <TextField id="mod-install-field" label="Install mod" placeholder="Mod ID"
                                                      size="small" slotProps={{htmlInput: {"data-testid": "mod-install-input"}}}
                                                      variant="filled" value={enteredModId}
                                                      onChange={handleEnteredModIdChange}/>
                                           <Button variant="outlined" size="small" disabled={enteredModId.length === 0}
                                                   data-testid="mod-install-submit"
                                                   onClick={async () => {
                                                       await props.onModInstallClicked(Number(enteredModId));
                                                       setEnteredModId("");
                                                   }}>Install</Button>
                                       </Stack>
                                   </PermissionGuard>
                               }

                               customFilterControls={
                                   <Tabs value={props.filter} onChange={props.onFilterChange}>
                                       <Tab value="" label="All"/>
                                       <Tab value="ARMA3" label="Arma 3" data-testid="mods-tab-arma3" disabled={props.arma3ModsCount === 0}/>
                                       <Tab value="DAYZ" label="DayZ" data-testid="mods-tab-dayz" disabled={props.dayZModsCount === 0}/>
                                   </Tabs>
                               }

                               customTopControls={<ModsTableToolbar
                                   selectedModsCount={props.selected.length}
                                   mixedModsSelected={props.mixedModsSelected}
                                   onUpdateClicked={props.onModUpdateClicked}
                                   onCreatePresetClicked={props.onCreatePresetClicked}
                                   onUninstallClicked={props.onModUninstallClicked}
                               />}
                />
            </Paper>
        </Box>
    );
}

export default ModsTable;
