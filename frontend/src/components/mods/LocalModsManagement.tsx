import {useEffect, useRef, useState} from "react";
import {
    Box,
    Button,
    CircularProgress,
    Paper,
    Stack,
    Switch,
    Tab,
    Tabs,
    Typography,
} from "@mui/material";
import PermissionGuard from "../auth/PermissionGuard";
import {ServerType} from "../../api/generated";
import {LocalModSyncStatusDtoStatus} from "../../api/generated/models/local-mod-sync-status-dto";
import SERVER_NAMES from "../../util/serverNames";
import {
    useLocalMods,
    useSetLocalModServerOnly
} from "../../hooks/queries/useLocalMods";
import {usePermission} from "../../hooks/usePermission";
import {useSyncLocalMods, useLocalModSyncStatus} from "../../hooks/useLocalModSync";
import {useQueryClient} from "@tanstack/react-query";
import {
    EnhancedTable,
    EnhancedTableCell,
    EnhancedTableHeadCell,
    EnhancedTableRow
} from "../../UI/EnhancedTable/EnhancedTable";

function formatBytes(bytes?: number | null): string {
    if (!bytes) return "—";
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
}

const headCells: EnhancedTableHeadCell[] = [
    {id: "name", label: "Name", sortable: true, searchable: true},
    {id: "serverType", label: "Game", sortable: true},
    {id: "fileSize", label: "Size", sortable: true, type: "numeric"},
    {id: "serverOnly", label: "Server-only", sortable: false},
    {id: "uploadedAt", label: "Uploaded", sortable: true, type: "date"},
];

export default function LocalModsManagement() {
    const canModify = usePermission("MOD_MODIFY");
    const [filter, setFilter] = useState<ServerType | "ALL">("ALL");
    const [isSyncing, setIsSyncing] = useState(false);

    const queryClient = useQueryClient();
    const {data: localMods = [], isLoading} = useLocalMods();
    const serverOnlyMutation = useSetLocalModServerOnly();
    const syncMutation = useSyncLocalMods();
    const syncStatus = useLocalModSyncStatus(isSyncing);
    const seenInProgressRef = useRef(false);

    useEffect(() => {
        const status = syncStatus.data?.status;
        if (!isSyncing) return;
        if (status === LocalModSyncStatusDtoStatus.InProgress) {
            seenInProgressRef.current = true;
        } else if (seenInProgressRef.current && status === LocalModSyncStatusDtoStatus.Finished) {
            seenInProgressRef.current = false;
            setIsSyncing(false);
            queryClient.invalidateQueries({ queryKey: ['localMods'] });
        } else if (seenInProgressRef.current && status === LocalModSyncStatusDtoStatus.Error) {
            seenInProgressRef.current = false;
            setIsSyncing(false);
        }
    }, [syncStatus.data?.status, isSyncing, queryClient]);

    const arma3Count = localMods.filter(m => m.serverType === ServerType.Arma3).length;
    const dayzCount = localMods.filter(m => m.serverType === ServerType.Dayz).length;
    const filteredMods = filter === "ALL" ? localMods : localMods.filter(m => m.serverType === filter);

    function handleFilterChange(_: unknown, newValue: ServerType | "ALL") {
        setFilter(newValue);
    }

    async function handleServerOnlyToggle(id: number, current: boolean) {
        await serverOnlyMutation.mutateAsync({id, serverOnly: !current});
    }

    async function handleSync() {
        seenInProgressRef.current = false;
        setIsSyncing(true);
        try {
            await syncMutation.mutateAsync();
        } catch {
            setIsSyncing(false);
        }
    }

    const rows: EnhancedTableRow[] = filteredMods.map(mod => ({
        id: mod.id!,
        cells: [
            {id: "name", value: mod.name ?? ""} as EnhancedTableCell,
            {
                id: "serverType",
                value: SERVER_NAMES.get(mod.serverType as ServerType) ?? mod.serverType ?? ""
            } as EnhancedTableCell,
            {
                id: "fileSize", value: mod.fileSize ?? 0,
                displayValue: formatBytes(mod.fileSize)
            } as EnhancedTableCell,
            {
                id: "serverOnly", value: mod.serverOnly ?? false,
                displayValue: (
                    <Switch
                        checked={mod.serverOnly ?? false}
                        onChange={() => canModify && handleServerOnlyToggle(mod.id!, mod.serverOnly ?? false)}
                        onClick={(e) => e.stopPropagation()}
                        disabled={!canModify}
                        size="small"
                    />
                )
            } as EnhancedTableCell,
            {
                id: "uploadedAt",
                value: mod.uploadedAt ? new Date(mod.uploadedAt) : new Date(0),
                displayValue: mod.uploadedAt ? new Date(mod.uploadedAt).toLocaleDateString() : "—"
            } as EnhancedTableCell,
        ],
    }));

    const toolbar = (
        <Box>
            <Tabs value={filter} onChange={handleFilterChange}>
                <Tab value="ALL" label="All"/>
                <Tab value={ServerType.Arma3} label="Arma 3" disabled={arma3Count === 0}/>
                <Tab value={ServerType.Dayz} label="DayZ" disabled={dayzCount === 0}/>
            </Tabs>
        </Box>
    );

    return (
        <Box sx={{width: "100%"}}>
            {!isLoading && filteredMods.length === 0 && (
                <Typography color="text.secondary" sx={{textAlign: "center", py: 2}}>
                    No local mods uploaded yet
                </Typography>
            )}
            <Paper sx={{width: "100%", mb: 2}}>
                <EnhancedTable
                    id="local-mods-table"
                    title="Local Mods"
                    headCells={headCells}
                    rows={rows}
                    selectedRowIds={[]}
                    onRowSelect={() => {}}
                    onSelectAllRowsClick={() => {}}
                    loading={isLoading}
                    defaultSortColumnId="name"
                    customTopControls={toolbar}
                    customBottomControls={
                        <PermissionGuard permission="MOD_MODIFY">
                            <Stack direction="row" spacing={1}>
                                <Button
                                    variant="outlined"
                                    size="small"
                                    onClick={handleSync}
                                    disabled={isSyncing}
                                    startIcon={isSyncing ? <CircularProgress size={20}/> : undefined}
                                >
                                    {isSyncing ? 'Syncing...' : 'Sync local mods'}
                                </Button>
                            </Stack>
                        </PermissionGuard>
                    }
                />
            </Paper>

        </Box>
    );
}
