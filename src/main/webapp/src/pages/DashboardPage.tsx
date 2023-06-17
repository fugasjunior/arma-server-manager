import {Stack} from "@mui/material";
import SystemResourcesMonitor from "../components/dashboard/SystemResourcesMonitor";
import ServerInstallations from "../components/dashboard/ServerInstallations";

const DashboardPage = () => {
    return (
        <Stack spacing={4}>
            <SystemResourcesMonitor/>
            <ServerInstallations/>
        </Stack>
    )
}

export default DashboardPage;