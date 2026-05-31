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
    onDeleteClicked: MouseEventHandler,
    disabled?: boolean
}

export const ScenariosTableToolbar = (
    {
        selectedScenariosCount,
        onFileChange,
        percentUploaded,
        onDeleteClicked,
        uploadInProgress,
        disabled = false
    }: ScenariosTableToolbarProps
) => <>
    <PermissionGuard permission="SCENARIO_MODIFY">
        {!uploadInProgress && <Button variant="contained" component="label" data-testid="scenario-upload-btn" disabled={disabled}>
            Upload
            <input hidden multiple type="file" data-testid="scenario-upload-input" onChange={onFileChange} disabled={disabled}/>
        </Button>}
        {uploadInProgress && <CircularProgress variant="determinate" value={percentUploaded}/>}
    </PermissionGuard>
    <PermissionGuard permission="SCENARIO_DELETE">
        <Tooltip title={disabled ? "Stop the server before deleting scenarios" : "Delete"}>
                            <span>
                                <IconButton data-testid="scenario-delete-btn" disabled={selectedScenariosCount === 0 || disabled} onClick={onDeleteClicked}>
                                    <DeleteIcon/>
                                </IconButton>
                            </span>
        </Tooltip>
    </PermissionGuard>
</>;
