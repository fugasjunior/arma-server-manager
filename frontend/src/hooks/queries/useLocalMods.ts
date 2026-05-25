import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {localModsApi} from "../../api/client";
import {queryKeys} from "../../api/queryKeys";
import {LocalModDto, ServerType} from "../../api/generated";

export function useLocalMods(filter?: ServerType, opts?: {enabled?: boolean}) {
    return useQuery({
        queryKey: queryKeys.localMods(filter),
        queryFn: async () => {
            const {data} = await localModsApi.getLocalMods(filter ? {filter} : undefined);
            return (data.localMods ?? []) as LocalModDto[];
        },
        enabled: opts?.enabled ?? true,
    });
}

export function useSetLocalModServerOnly() {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({id, serverOnly}: {id: number; serverOnly: boolean}) =>
            localModsApi.setLocalModServerOnly({id, serverOnlyDto: {serverOnly}}),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: queryKeys.localMods()});
        },
    });
}
