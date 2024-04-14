import {ServerDto} from "../../../dtos/ServerDto.ts";
import {Button, Stack} from "@mui/material";
import PlayCircleFilledIcon from "@mui/icons-material/PlayCircleFilled";

export function SeverControls(props: {
    serverRunning: null | boolean,
    server: ServerDto,
    onStopServer: () => void,
    onRestartServer: () => void,
    disabled: boolean,
    onStartServer: () => void
}) {
    return <Stack direction="row" spacing={1}>
        {props.serverRunning ?
            <>
                <Button id={`server-${props.server.id}-stop-btn`}
                        className="server-stop-btn"
                        variant="contained" color="error"
                        onClick={props.onStopServer}>
                    Stop
                </Button>
                <Button onClick={props.onRestartServer}>Restart
                </Button>
            </>
            : <Button id={`server-${props.server.id}-start-btn`}
                      variant="contained" startIcon={<PlayCircleFilledIcon/>}
                      disabled={props.disabled}
                      onClick={props.onStartServer}>
                Start
            </Button>
        }
    </Stack>;
}
