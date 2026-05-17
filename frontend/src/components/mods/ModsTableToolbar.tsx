import Tooltip from "@mui/material/Tooltip";
import IconButton from "@mui/material/IconButton";
import DeleteIcon from "@mui/icons-material/Delete";
import {Box, Tab, Tabs} from "@mui/material";
import UpdateIcon from '@mui/icons-material/Update';
import PlaylistAddIcon from '@mui/icons-material/PlaylistAdd';

type ModsTableToolbarProps = {
    selectedModsCount: number,
    filter: string
    arma3ModsCount: number
    dayZModsCount: number
    mixedModsSelected: boolean
    onUpdateClicked: () => void,
    onCreatePresetClicked: () => void,
    onUninstallClicked: () => void,
    onFilterChange: (_: any, newValue: string) => void,
}

function ModsTableToolbar(
    {
        selectedModsCount,
        filter,
        onFilterChange,
        onUpdateClicked,
        arma3ModsCount,
        dayZModsCount,
        onCreatePresetClicked,
        onUninstallClicked,
        mixedModsSelected
    }: ModsTableToolbarProps)
{
    return (
        <>
            <Box>
                <Tabs value={filter} onChange={onFilterChange}>
                    <Tab value="" label="All"/>
                    <Tab value="ARMA3" label="Arma 3" data-testid="mods-tab-arma3" disabled={arma3ModsCount === 0}/>
                    <Tab value="DAYZ" label="DayZ" data-testid="mods-tab-dayz" disabled={dayZModsCount === 0}/>
                </Tabs>
            </Box>

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
            <Tooltip title="Delete">
                        <span>
                            <IconButton data-testid="mod-uninstall-btn" disabled={selectedModsCount === 0} onClick={onUninstallClicked}>
                                <DeleteIcon/>
                            </IconButton>
                        </span>
            </Tooltip>
        </>
    );
}

export default ModsTableToolbar;