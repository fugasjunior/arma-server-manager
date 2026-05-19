import {Avatar, Button, Divider, Stack, Typography} from "@mui/material";
import {additionalServersApi} from "../api/client";
import {AdditionalServerDto} from "../api/generated";
import Paper from "@mui/material/Paper";
import config from "../config";
import TableSkeletons from "../UI/TableSkeletons";
import CircularProgress from "@mui/material/CircularProgress";
import {useAdditionalServers} from "../hooks/queries/useAdditionalServers";
import {useQueryClient} from "@tanstack/react-query";
import {queryKeys} from "../api/queryKeys";

const AdditionalServersPage = () => {
    const queryClient = useQueryClient();
    const {data: additionalServers = [], isLoading} = useAdditionalServers();

    const handleStart = async (id: number) => {
        queryClient.setQueryData(queryKeys.additionalServers, (old: AdditionalServerDto[] = []) =>
            old.map(s => s.id === id ? {...s, alive: true} : s)
        );
        await additionalServersApi.startAdditionalServer({serverId: id});
        await queryClient.invalidateQueries({queryKey: queryKeys.additionalServers});
    };

    const handleStop = async (id: number) => {
        queryClient.setQueryData(queryKeys.additionalServers, (old: AdditionalServerDto[] = []) =>
            old.map(s => s.id === id ? {...s, alive: false, startedAt: undefined} : s)
        );
        await additionalServersApi.stopAdditionalServer({serverId: id});
        await queryClient.invalidateQueries({queryKey: queryKeys.additionalServers});
    };

    return (
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
                        <Stack key={server.id} direction="row" sx={{width: "100%", justifyContent: "space-between"}}>
                            <Stack direction="row" spacing={3}>
                                <Avatar src={server.imageUrl} alt={`${server.name} icon`}/>
                                <p>{server.name}</p>
                            </Stack>
                            {server.startedAt &&
                                <div>
                                    Started at: {new Date(server.startedAt).toLocaleString(undefined,
                                    config.dateFormat)}
                                </div>
                            }
                            {server.startedAt && server.alive &&
                                <Button variant="contained" color="error"
                                        onClick={() => handleStop(server.id!)}>
                                    Stop
                                </Button>}
                            {!server.startedAt &&
                                <Button variant="contained" color="primary"
                                        disabled={!server.startedAt && server.alive}
                                        onClick={() => handleStart(server.id!)}>
                                    {!server.startedAt && server.alive ?
                                        <CircularProgress size={24}/> : "Start"}
                                </Button>}
                        </Stack>
                    ))}
                </Stack>
                }
            </Paper>
            }
        </>
    );
};

export default AdditionalServersPage;
