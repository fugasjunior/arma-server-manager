import {useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {localModsApi} from "../../api/client";
import {queryKeys} from "../../api/queryKeys";
import {LocalModDto, ModFlagsDto, ServerType} from "../../api/generated";

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

export function useSetLocalModFlags() {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({id, flags}: {id: number; flags: ModFlagsDto}) =>
            localModsApi.setLocalModFlags({id, modFlagsDto: flags}),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: queryKeys.localMods()});
        },
    });
}
