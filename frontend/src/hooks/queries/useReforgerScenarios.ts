import {useQuery} from "@tanstack/react-query";
import {scenariosApi} from "../../api/client";
import {queryKeys} from "../../api/queryKeys";

export function useReforgerScenarios({enabled = true}: {enabled?: boolean} = {}) {
    return useQuery({
        queryKey: queryKeys.reforgerScenarios,
        queryFn: async () => (await scenariosApi.getReforgerScenarios()).data.scenarios ?? [],
        enabled,
    });
}
