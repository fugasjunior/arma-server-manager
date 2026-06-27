import {AutomaticRestartDto} from "../../../api/generated";
import {serversApi} from "../../../api/client";
import {useEffect, useState} from "react";
import {TimeField} from "@mui/x-date-pickers";
import {Checkbox, FormControlLabel, Stack} from "@mui/material";
import {parseInt} from "lodash";
import dayjs from "dayjs";
import {usePermission} from "../../../hooks/usePermission.ts";

const convertTimeStringToDate = (timeString: string | null): dayjs.Dayjs | null => {
    if (timeString === null) {
        return null;
    }

    const date = new Date();
    date.setHours(parseInt(timeString.split(":")[0]));
    date.setMinutes(parseInt(timeString.split(":")[1]));
    date.setSeconds(0);
    return dayjs(date);
}

const AutomaticRestartSettings = (props: { serverId: number, dto: AutomaticRestartDto }) => {

    const [enabled, setEnabled] = useState<boolean>(props.dto.enabled ?? false);
    const [time, setTime] = useState<dayjs.Dayjs | null>(convertTimeStringToDate(props.dto.time ?? null));
    const canModify = usePermission("SERVER_MODIFY");

    useEffect(() => {
        const updateTimeout = setTimeout(async () => {
            await serversApi.setAutoRestart({id: props.serverId, automaticRestartDto: {enabled, time: time!.toDate().toLocaleTimeString("en-GB")}});
        }, 2000)
        return () => clearTimeout(updateTimeout)
    }, [enabled, time])

    return (
        <Stack direction="row">
            <FormControlLabel control={
                <Checkbox
                    checked={enabled}
                    disabled={!canModify}
                    size="small"
                    onChange={(e) => setEnabled(e.target.checked)}/>
            } label="Automatic restart"/>
            {enabled &&
                <TimeField
                    disabled={!enabled || !canModify}
                    value={dayjs(time)}
                    label="Time"
                    format="HH:mm"
                    size="small"
                    onChange={(newTime) => setTime(newTime)}
                />
            }
        </Stack>
    )
}

export default AutomaticRestartSettings;
