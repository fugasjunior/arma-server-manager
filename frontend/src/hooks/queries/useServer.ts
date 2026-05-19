import {useQuery} from "@tanstack/react-query";
import {serversApi} from "../../api/client";
import {queryKeys} from "../../api/queryKeys";

export function useServer(id: number | undefined, opts?: {enabled?: boolean}) {
    return useQuery({
        queryKey: queryKeys.server(id!),
        queryFn: async () => (await serversApi.getServer({id: id!})).data,
        enabled: id != null && (opts?.enabled ?? true),
    });
}
