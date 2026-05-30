import {useQuery} from "@tanstack/react-query";
import {modPresetsApi} from "../../api/client";
import {queryKeys} from "../../api/queryKeys";
import {ServerType} from "../../api/generated";

export function usePresets(filter?: ServerType, opts?: {enabled?: boolean}) {
    return useQuery({
        queryKey: queryKeys.presets(filter),
        queryFn: async () => (await modPresetsApi.getPresets(filter ? {filter} : undefined)).data.presets ?? [],
        enabled: opts?.enabled ?? true,
    });
}
