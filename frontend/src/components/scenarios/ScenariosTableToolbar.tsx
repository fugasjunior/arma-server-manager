import Tooltip from "@mui/material/Tooltip";
import IconButton from "@mui/material/IconButton";
import DeleteIcon from "@mui/icons-material/Delete";
import {Button, CircularProgress} from "@mui/material";
import {ChangeEventHandler, MouseEventHandler} from "react";
import PermissionGuard from "../auth/PermissionGuard";

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
    <PermissionGuard permission="SCENARIO_MODIFY">
        {!uploadInProgress && <Button variant="contained" component="label" data-testid="scenario-upload-btn">
            Upload
            <input hidden multiple type="file" data-testid="scenario-upload-input" onChange={onFileChange}/>
        </Button>}
        {uploadInProgress && <CircularProgress variant="determinate" value={percentUploaded}/>}
    </PermissionGuard>
    <PermissionGuard permission="SCENARIO_DELETE">
        <Tooltip title="Delete">
                            <span>
                                <IconButton data-testid="scenario-delete-btn" disabled={selectedScenariosCount === 0} onClick={onDeleteClicked}>
                                    <DeleteIcon/>
                                </IconButton>
                            </span>
        </Tooltip>
    </PermissionGuard>
</>;
