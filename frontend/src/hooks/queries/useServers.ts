import {useQuery} from "@tanstack/react-query";
import {serversApi} from "../../api/client";
import {queryKeys} from "../../api/queryKeys";

export function useServers(opts?: { enabled?: boolean }) {
    return useQuery({
        queryKey: queryKeys.servers,
        queryFn: async () => (await serversApi.getServers()).data.servers ?? [],
        enabled: opts?.enabled ?? true,
    });
}
