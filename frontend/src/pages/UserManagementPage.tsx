import {useState} from "react";
import {Stack, Tab, Tabs, Typography} from "@mui/material";
import {useQuery} from "@tanstack/react-query";
import {usersApi, rolesApi} from "../api/client";
import {queryKeys} from "../api/queryKeys";
import UsersTab from "../components/user-management/UsersTab";
import RolesTab from "../components/user-management/RolesTab";

const UserManagementPage = () => {
    const {data: users = []} = useQuery({
        queryKey: queryKeys.users,
        queryFn: async () => (await usersApi.getUsers()).data,
    });

    const {data: roles = []} = useQuery({
        queryKey: queryKeys.roles,
        queryFn: async () => (await rolesApi.getRoles()).data,
    });

    const [tab, setTab] = useState(0);

    return (
        <>
            <Stack direction="row" sx={{alignItems: "center", mb: 2}} spacing={2}>
                <Typography variant="h4" component="h2">User Management</Typography>
            </Stack>

            <Tabs value={tab} onChange={(_, value) => setTab(value)} sx={{mb: 2}}>
                <Tab label="Users"/>
                <Tab label="Roles"/>
            </Tabs>

            {tab === 0 && <UsersTab users={users} roles={roles} />}
            {tab === 1 && <RolesTab roles={roles} />}
        </>
    );
};

export default UserManagementPage;
