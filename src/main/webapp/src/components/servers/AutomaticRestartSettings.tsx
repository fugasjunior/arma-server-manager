import {AutomaticRestartDto} from "../../dtos/AutomaticRestartDto.ts";
import {useEffect, useState} from "react";
import {TimeField} from "@mui/x-date-pickers";
import {setAutomaticRestart} from "../../services/serversService.ts";
import {FormControlLabel, Stack, Switch} from "@mui/material";
import {parseInt} from "lodash";
import dayjs from "dayjs";

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

    const [enabled, setEnabled] = useState<boolean>(props.dto.enabled);
    const [time, setTime] = useState<dayjs.Dayjs | null>(convertTimeStringToDate(props.dto.time));

    useEffect(() => {
        const updateTimeout = setTimeout(async () => {
            await setAutomaticRestart(props.serverId, {enabled, time: time!.toDate().toLocaleTimeString("en-GB")});
        }, 2000)
        return () => clearTimeout(updateTimeout)
    }, [enabled, time])

    return (
        <Stack direction="row">
            <FormControlLabel control={
                <Switch
                    checked={enabled}
                    onChange={(_, checked) => setEnabled(checked)}/>
            } label="Automatic restart"/>
            {enabled &&
                <TimeField
                    disabled={!enabled}
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
