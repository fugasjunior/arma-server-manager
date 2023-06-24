import Tooltip from "@mui/material/Tooltip";
import IconButton from "@mui/material/IconButton";
import DeleteIcon from "@mui/icons-material/Delete";
import {Button, CircularProgress} from "@mui/material";
import {ChangeEventHandler, MouseEventHandler} from "react";

type ScenariosTableToolbarProps = {
    selectedScenariosCount: number,
    uploadInProgress: boolean,
    percentUploaded: number,
    onFileChange: ChangeEventHandler,
    onDeleteClicked: MouseEventHandler
}

export const ScenariosTableToolbar = (
    {
        selectedScenariosCount,
        onFileChange,
        percentUploaded,
        onDeleteClicked,
        uploadInProgress
    }: ScenariosTableToolbarProps
) => <>
    {!uploadInProgress && <Button variant="contained" component="label">
        Upload
        <input hidden multiple type="file" onChange={onFileChange}/>
    </Button>}
    {uploadInProgress && <CircularProgress variant="determinate" value={percentUploaded}/>}
    <Tooltip title="Delete">
                        <span>
                            <IconButton disabled={selectedScenariosCount === 0} onClick={onDeleteClicked}>
                                <DeleteIcon/>
                            </IconButton>
                        </span>
    </Tooltip>
</>;
