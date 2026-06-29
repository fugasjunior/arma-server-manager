import {useQuery} from "@tanstack/react-query";
import {scenariosApi} from "../../api/client";
import {queryKeys} from "../../api/queryKeys";

export function useServerScenarios(serverId: number, opts?: {enabled?: boolean}) {
    return useQuery({
        queryKey: queryKeys.serverScenarios(serverId),
        queryFn: async () => (await scenariosApi.getServerScenarios({id: serverId})).data.scenarios ?? [],
        enabled: opts?.enabled ?? true,
    });
}
