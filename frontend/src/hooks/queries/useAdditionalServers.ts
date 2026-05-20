import {useQuery} from "@tanstack/react-query";
import {additionalServersApi} from "../../api/client";
import {queryKeys} from "../../api/queryKeys";

export function useAdditionalServers(options?: { enabled?: boolean }) {
    return useQuery({
        queryKey: queryKeys.additionalServers,
        queryFn: async () => (await additionalServersApi.getAdditionalServers()).data.servers ?? [],
        refetchInterval: 2000,
        enabled: options?.enabled,
    });
}
