import Tooltip from "@mui/material/Tooltip";
import IconButton from "@mui/material/IconButton";
import DeleteIcon from "@mui/icons-material/Delete";
import UpdateIcon from '@mui/icons-material/Update';
import PlaylistAddIcon from '@mui/icons-material/PlaylistAdd';
import PermissionGuard from "../auth/PermissionGuard";

type ModsTableToolbarProps = {
    selectedModsCount: number,
    mixedModsSelected: boolean
    onUpdateClicked: () => void,
    onCreatePresetClicked: () => void,
    onUninstallClicked: () => void,
}

function ModsTableToolbar(
    {
        selectedModsCount,
        onUpdateClicked,
        onCreatePresetClicked,
        onUninstallClicked,
        mixedModsSelected
    }: ModsTableToolbarProps)
{
    return (
        <>
            <PermissionGuard permission="MOD_MODIFY">
                <Tooltip title="Update">
                            <span>
                                <IconButton data-testid="mod-update-btn" disabled={selectedModsCount === 0} onClick={onUpdateClicked}>
                                    <UpdateIcon/>
                                </IconButton>
                            </span>
                </Tooltip>
                <Tooltip title="Save as preset">
                            <span>
                                <IconButton data-testid="mod-save-preset-btn" disabled={selectedModsCount === 0 || mixedModsSelected}
                                            onClick={onCreatePresetClicked}>
                                    <PlaylistAddIcon/>
                                </IconButton>
                            </span>
                </Tooltip>
            </PermissionGuard>
            <PermissionGuard permission="MOD_DELETE">
                <Tooltip title="Delete">
                            <span>
                                <IconButton data-testid="mod-uninstall-btn" disabled={selectedModsCount === 0} onClick={onUninstallClicked}>
                                    <DeleteIcon/>
                                </IconButton>
                            </span>
                </Tooltip>
            </PermissionGuard>
        </>
    );
}

export default ModsTableToolbar;
