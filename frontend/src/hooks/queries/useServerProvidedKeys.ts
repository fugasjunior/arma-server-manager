import {useQuery} from "@tanstack/react-query";
import {keysApi} from "../../api/client";
import {queryKeys} from "../../api/queryKeys";

export function useServerProvidedKeys(serverId: number, opts?: {enabled?: boolean}) {
    return useQuery({
        queryKey: queryKeys.serverProvidedKeys(serverId),
        queryFn: async () => (await keysApi.getServerProvidedKeys({id: serverId})).data.keys ?? [],
        enabled: opts?.enabled ?? true,
    });
}
