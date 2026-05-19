import {useQuery} from "@tanstack/react-query";
import {modsApi} from "../../api/client";
import {queryKeys} from "../../api/queryKeys";
import {CreatorDlcDto, ServerType} from "../../api/generated";

export function useCreatorDlcs(filter?: ServerType, opts?: {enabled?: boolean}) {
    return useQuery({
        queryKey: queryKeys.creatorDlcs(filter),
        queryFn: async () => {
            const {data} = await modsApi.getMods(filter ? {filter} : undefined);
            return (data.creatorDlcs ?? []).sort(
                (a: CreatorDlcDto, b: CreatorDlcDto) => (a.name ?? "").localeCompare(b.name ?? "")
            );
        },
        enabled: opts?.enabled ?? true,
    });
}
