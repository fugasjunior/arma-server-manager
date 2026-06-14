import {useEffect, useState} from "react";
import {ConfigOverrideDto, ServerDto} from "../api/generated";

export function useConfigOverrides(server: Pick<ServerDto, 'configOverrides'>) {
    const [configOverrides, setConfigOverrides] = useState<ConfigOverrideDto[]>(
        server.configOverrides ?? [],
    );

    useEffect(() => {
        setConfigOverrides(server.configOverrides ?? []);
    }, [server.configOverrides]);

    const getOverride = (key: string): ConfigOverrideDto | undefined =>
        configOverrides.find(o => o.configKey === key);

    const setOverride = (key: string, override: ConfigOverrideDto | undefined) => {
        setConfigOverrides(prev => {
            const others = prev.filter(o => o.configKey !== key);
            return override ? [...others, override] : others;
        });
    };

    return {configOverrides, getOverride, setOverride};
}
