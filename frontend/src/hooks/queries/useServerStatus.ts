import {useQuery} from "@tanstack/react-query";
import {serversApi} from "../../api/client";
import {queryKeys} from "../../api/queryKeys";

export function useServerStatus(id: number | undefined, opts?: {enabled?: boolean; refetchInterval?: number | false}) {
    return useQuery({
        queryKey: queryKeys.serverStatus(id!),
        queryFn: async () => (await serversApi.getServerStatus({id: id!})).data,
        enabled: id != null && (opts?.enabled ?? true),
        refetchInterval: opts?.refetchInterval ?? 10000,
    });
}
