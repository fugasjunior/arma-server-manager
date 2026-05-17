import {useEffect, useState} from "react";
import Paper from "@mui/material/Paper";
import {Divider, LinearProgress, Stack} from "@mui/material";
import CircularProgressWithLabel from "../../UI/CircularProgressWithLabel";
import {systemApi} from "../../api/client";
import {useInterval} from "../../hooks/use-interval";
import {humanFileSize} from "../../util/util";
import {CircularProgressProps} from "@mui/material/CircularProgress";

const SystemResourcesMonitor = () => {
    const [infoLoaded, setInfoLoaded] = useState(false);
    const [cpuUsage, setCpuUsage] = useState(0);
    const [memoryLeft, setMemoryLeft] = useState(0);
    const [memoryTotal, setMemoryTotal] = useState(0);
    const [storageLeft, setStorageLeft] = useState(0);
    const [storageTotal, setStorageTotal] = useState(0);
    const [cpuCount, setCpuCount] = useState(0);
    const [osName, setOsName] = useState("");

    useEffect(() => {
        fetchSystemResourceUsage();
    }, []);

    useInterval(() => fetchSystemResourceUsage(), 3000);

    const fetchSystemResourceUsage = async () => {
        const {data: systemInfo} = await systemApi.getSystemDetails();
        setCpuUsage(Math.round((systemInfo.cpuUsage ?? 0) * 100));
        setMemoryLeft(systemInfo.memoryLeft ?? 0);
        setMemoryTotal(systemInfo.memoryTotal ?? 0);
        setStorageLeft(systemInfo.spaceLeft ?? 0);
        setStorageTotal(systemInfo.spaceTotal ?? 0);
        setCpuCount(systemInfo.cpuCount ?? 0);
        setOsName(systemInfo.osName ?? "");
        setInfoLoaded(true);
    }

    const memoryUsedPercent = Math.round(((memoryTotal - memoryLeft) / memoryTotal) * 100);
    const storageUsedPercent = Math.round(((storageTotal - storageLeft) / storageTotal) * 100);

    const evaluateColor = (percent: number): CircularProgressProps["color"] => {
        if (percent < 70) {
            return "primary";
        }
        if (percent < 90) {
            return "warning";
        }
        return "error";
    }

    return (
            <Paper>
                {!infoLoaded && <LinearProgress /> }
                {infoLoaded &&
                        <Stack direction="row" divider={<Divider orientation="vertical" flexItem/>} spacing={3}
                               sx={{p: 4, alignItems: "center", justifyContent: "center"}}>
                            <Stack direction="row" divider={<Divider orientation="vertical" flexItem/>} spacing={3}
                                   sx={{alignItems: "center", justifyContent: "center"}}
                            >
                                <Stack spacing={2} sx={{alignItems: "center", justifyContent: "center"}}>
                                    <p>CPU usage</p>
                                    <div>
                                        <CircularProgressWithLabel value={cpuUsage} color={evaluateColor(cpuUsage)}/>
                                    </div>
                                </Stack>

                                <Stack spacing={2} sx={{alignItems: "center", justifyContent: "center"}}>
                                    <p>Memory</p>
                                    <div>
                                        <CircularProgressWithLabel
                                                value={memoryUsedPercent}
                                                color={evaluateColor(memoryUsedPercent)}
                                        />
                                    </div>
                                </Stack>

                                <Stack spacing={2} sx={{alignItems: "center", justifyContent: "center"}}>
                                    <p>Storage</p>
                                    <div>
                                        <CircularProgressWithLabel
                                                value={storageUsedPercent}
                                                color={evaluateColor(storageUsedPercent)}
                                        />
                                    </div>
                                </Stack>
                            </Stack>
                            <Stack>
                                <p>Memory used: <strong>{humanFileSize(memoryTotal - memoryLeft)}</strong></p>
                                <p>Total memory: <strong>{humanFileSize(memoryTotal)}</strong></p>
                            </Stack>
                            <Stack>
                                <p>Storage used: <strong>{humanFileSize(storageTotal - storageLeft)}</strong></p>
                                <p>Total storage: <strong>{humanFileSize(storageTotal)}</strong></p>
                            </Stack>
                            <Stack>
                                <p>OS: <strong>{osName}</strong></p>
                                <p>CPU count: <strong>{cpuCount}</strong></p>
                            </Stack>
                        </Stack>
                }
            </Paper>
    );
};

export default SystemResourcesMonitor