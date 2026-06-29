import {Avatar, Button, Checkbox, Divider, FormControlLabel, Stack, Typography} from "@mui/material";
import {additionalServersApi} from "../api/client";
import {AdditionalServerDto, ServerStatus} from "../api/generated";
import PermissionGuard from "../components/auth/PermissionGuard";
import Paper from "@mui/material/Paper";
import config from "../config";
import TableSkeletons from "../UI/TableSkeletons";
import {useAdditionalServers} from "../hooks/queries/useAdditionalServers";
import {useQueryClient} from "@tanstack/react-query";
import {queryKeys} from "../api/queryKeys";
import {usePermission} from "../hooks/usePermission";
import ServerStatusIndicator from "../UI/ServerStatusIndicator.tsx";

const AdditionalServersPage = () => {
    const queryClient = useQueryClient();
    const canView = usePermission('ADDITIONAL_SERVER_VIEW');
    const {data: additionalServers = [], isLoading} = useAdditionalServers({enabled: canView});

    const handleStart = async (id: number) => {
        queryClient.setQueryData(queryKeys.additionalServers, (old: AdditionalServerDto[] = []) =>
            old.map(s => s.id === id ? {...s, alive: true, status: ServerStatus.Starting} : s)
        );
        await additionalServersApi.startAdditionalServer({serverId: id});
        await queryClient.invalidateQueries({queryKey: queryKeys.additionalServers});
    };

    const handleStop = async (id: number) => {
        queryClient.setQueryData(queryKeys.additionalServers, (old: AdditionalServerDto[] = []) =>
            old.map(s => s.id === id ? {...s, alive: false, startedAt: undefined, status: ServerStatus.Off} : s)
        );
        await additionalServersApi.stopAdditionalServer({serverId: id});
        await queryClient.invalidateQueries({queryKey: queryKeys.additionalServers});
    };

    const handleToggleAutoStart = async (id: number, enabled: boolean) => {
        queryClient.setQueryData(queryKeys.additionalServers, (old: AdditionalServerDto[] = []) =>
            old.map(s => s.id === id ? {...s, autoStart: enabled} : s)
        );
        await additionalServersApi.setAdditionalServerAutoStart({serverId: id, autoStartDto: {enabled}});
        await queryClient.invalidateQueries({queryKey: queryKeys.additionalServers});
    };

    return (
        <PermissionGuard permission="ADDITIONAL_SERVER_VIEW">
            <>
                {!isLoading && additionalServers.length === 0 &&
                    <div>
                        <Typography variant="h5" align="center">No additional servers set up</Typography>
                        <Typography variant="body1" align="center">You can set them up manually through
                            database</Typography>
                    </div>
                }
                {!isLoading && additionalServers.length > 0 && <Paper>
                    <TableSkeletons count={8} spacing={6} display={isLoading}/>
                    {!isLoading && <Stack spacing={2} divider={<Divider orientation="horizontal" flexItem/>} sx={{p: 3}}>
                        {additionalServers.map(server => (
                            <Stack key={server.id} direction="row" sx={{width: "100%", justifyContent: "space-between", alignItems: "center"}}>
                                <Stack direction="row" spacing={3} sx={{alignItems: "center"}}>
                                    <Avatar src={server.imageUrl} alt={`${server.name} icon`}/>
                                    <p>{server.name}</p>
                                </Stack>
                                <Stack direction="row" spacing={2} sx={{alignItems: "center"}}>
                                    {server.startedAt &&
                                        <Typography variant="body2" color="text.secondary">
                                            Started at: {new Date(server.startedAt).toLocaleString(undefined, config.dateFormat)}
                                        </Typography>
                                    }
                                    <ServerStatusIndicator status={server.status}/>
                                </Stack>
                                <PermissionGuard permission="ADDITIONAL_SERVER_OPERATE">
                                    <Stack direction="row" spacing={1} sx={{alignItems: "center"}}>
                                        <FormControlLabel
                                            label="Auto start"
                                            control={
                                                <Checkbox
                                                    checked={!!server.autoStart}
                                                    onChange={(e) => handleToggleAutoStart(server.id!, e.target.checked)}
                                                    size="small"
                                                />
                                            }
                                        />
                                        {server.alive
                                            ? <Button variant="contained" color="error"
                                                      onClick={() => handleStop(server.id!)}>
                                                Stop
                                            </Button>
                                            : <Button variant="contained" color="primary"
                                                      disabled={server.status === ServerStatus.Starting}
                                                      onClick={() => handleStart(server.id!)}>
                                                Start
                                            </Button>
                                        }
                                    </Stack>
                                </PermissionGuard>
                            </Stack>
                        ))}
                    </Stack>
                    }
                </Paper>
                }
            </>
        </PermissionGuard>
    );
};

export default AdditionalServersPage;
