import {useQuery} from "@tanstack/react-query";
import {serverInstallationApi} from "../../api/client";
import {queryKeys} from "../../api/queryKeys";
import {ServerInstallationDto} from "../../api/generated";

export function useServerInstallations(opts?: {refetchInterval?: number | false, enabled?: boolean}) {
    return useQuery({
        queryKey: queryKeys.serverInstallations,
        queryFn: async () => {
            const {data} = await serverInstallationApi.getServerInstallations();
            return (data.serverInstallations ?? []).sort(
                (a: ServerInstallationDto, b: ServerInstallationDto) => (a.type ?? "").localeCompare(b.type ?? "")
            );
        },
        refetchInterval: opts?.refetchInterval,
        enabled: opts?.enabled,
    });
}
