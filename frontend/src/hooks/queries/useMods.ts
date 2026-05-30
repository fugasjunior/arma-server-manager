import {useQuery} from "@tanstack/react-query";
import {modsApi} from "../../api/client";
import {queryKeys} from "../../api/queryKeys";
import {ModDto, ServerType} from "../../api/generated";

export function useMods(filter?: ServerType, opts?: {enabled?: boolean; refetchInterval?: number | false}) {
    return useQuery({
        queryKey: queryKeys.mods(filter),
        queryFn: async () => {
            const {data} = await modsApi.getMods(filter ? {filter} : undefined);
            return (data.workshopMods ?? [])
                .map((mod: ModDto) => ({...mod, lastUpdated: mod.lastUpdated ? new Date(mod.lastUpdated) : ""} as ModDto))
                .sort((a: ModDto, b: ModDto) => (a.name ?? "").localeCompare(b.name ?? ""));
        },
        enabled: opts?.enabled ?? true,
        refetchInterval: opts?.refetchInterval,
    });
}
