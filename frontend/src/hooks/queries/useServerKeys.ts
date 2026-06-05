import {useQuery} from "@tanstack/react-query";
import {keysApi} from "../../api/client";
import {queryKeys} from "../../api/queryKeys";

export function useServerKeys(serverId: number, opts?: {enabled?: boolean}) {
    return useQuery({
        queryKey: queryKeys.serverKeys(serverId),
        queryFn: async () => (await keysApi.getServerKeys({id: serverId})).data.keys ?? [],
        enabled: opts?.enabled ?? true,
    });
}
