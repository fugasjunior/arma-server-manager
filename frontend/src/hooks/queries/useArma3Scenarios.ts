import {useQuery} from "@tanstack/react-query";
import {scenariosApi} from "../../api/client";
import {queryKeys} from "../../api/queryKeys";

export function useArma3Scenarios(opts?: {enabled?: boolean}) {
    return useQuery({
        queryKey: queryKeys.arma3Scenarios,
        queryFn: async () => (await scenariosApi.getArma3Scenarios()).data.scenarios ?? [],
        enabled: opts?.enabled ?? true,
    });
}
