import {useQuery} from "@tanstack/react-query";
import {systemApi} from "../../api/client";
import {queryKeys} from "../../api/queryKeys";

export function useSystemDetails(opts?: {refetchInterval?: number | false}) {
    return useQuery({
        queryKey: queryKeys.systemDetails,
        queryFn: async () => (await systemApi.getSystemDetails()).data,
        refetchInterval: opts?.refetchInterval,
    });
}
