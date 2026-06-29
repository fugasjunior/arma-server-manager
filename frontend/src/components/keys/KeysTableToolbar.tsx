import Tooltip from "@mui/material/Tooltip";
import IconButton from "@mui/material/IconButton";
import DeleteIcon from "@mui/icons-material/Delete";
import {Button, CircularProgress} from "@mui/material";
import {ChangeEventHandler, MouseEventHandler} from "react";
import PermissionGuard from "../auth/PermissionGuard";

type KeysTableToolbarProps = {
    selectedKeysCount: number,
    uploadInProgress: boolean,
    onFileChange: ChangeEventHandler,
    onDeleteClicked: MouseEventHandler,
    disabled?: boolean
}

export const KeysTableToolbar = (
    {
        selectedKeysCount,
        onFileChange,
        onDeleteClicked,
        uploadInProgress,
        disabled = false
    }: KeysTableToolbarProps
) => <>
    <PermissionGuard permission="BIKEY_MODIFY">
        {!uploadInProgress && <Button variant="contained" component="label" data-testid="key-upload-btn" disabled={disabled}>
            Upload
            <input hidden multiple type="file" accept=".bikey" data-testid="key-upload-input" onChange={onFileChange} disabled={disabled}/>
        </Button>}
        {uploadInProgress && <CircularProgress/>}
    </PermissionGuard>
    <PermissionGuard permission="BIKEY_DELETE">
        <Tooltip title={disabled ? "Stop the server before deleting keys" : "Delete"}>
            <span>
                <IconButton data-testid="key-delete-btn" disabled={selectedKeysCount === 0 || disabled} onClick={onDeleteClicked}>
                    <DeleteIcon/>
                </IconButton>
            </span>
        </Tooltip>
    </PermissionGuard>
</>;
