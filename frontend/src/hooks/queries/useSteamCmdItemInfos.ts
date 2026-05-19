import {useQuery} from "@tanstack/react-query";
import {steamCmdApi} from "../../api/client";
import {queryKeys} from "../../api/queryKeys";

export function useSteamCmdItemInfos(opts?: {refetchInterval?: number | false}) {
    return useQuery({
        queryKey: queryKeys.steamCmdItemInfos,
        queryFn: async () => (await steamCmdApi.getSteamCmdItemInfos()).data,
        refetchInterval: opts?.refetchInterval,
    });
}
