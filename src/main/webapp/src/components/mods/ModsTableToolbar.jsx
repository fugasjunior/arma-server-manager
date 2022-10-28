import Toolbar from "@mui/material/Toolbar";
import {alpha} from "@mui/material/styles";
import Typography from "@mui/material/Typography";
import Tooltip from "@mui/material/Tooltip";
import IconButton from "@mui/material/IconButton";
import DeleteIcon from "@mui/icons-material/Delete";
import * as React from "react";
import PropTypes from "prop-types";
import {Box, Divider, Stack, Tab, Tabs} from "@mui/material";
import UpdateIcon from '@mui/icons-material/Update';

function ModsTableToolbar(props) {
    const {numSelected, title, filter, onFilterChange} = props;

    return (
            <Toolbar
                    sx={{
                        pl: {sm: 2},
                        pr: {xs: 1, sm: 1},
                        ...(numSelected > 0 && {
                            bgcolor: (theme) =>
                                    alpha(theme.palette.primary.main, theme.palette.action.activatedOpacity),
                        }),
                    }}
            >
                {numSelected > 0 ? (
                        <Typography
                                sx={{flex: '1 1 100%'}}
                                color="inherit"
                                variant="subtitle1"
                                component="div"
                        >
                            {numSelected} selected
                        </Typography>
                ) : (
                        <Typography
                                sx={{flex: '1 1 100%'}}
                                variant="h6"
                                id="tableTitle"
                                component="div"
                        >
                            {title}
                        </Typography>
                )}

                <Box al>
                    <Tabs value={filter} onChange={onFilterChange}>
                        <Tab value="" label="All"/>
                        <Tab value="ARMA3" label="Arma 3" disabled={props.arma3ModsCount === 0}/>
                        <Tab value="DAYZ" label="DayZ" disabled={props.dayZModsCount === 0}/>
                    </Tabs>
                </Box>


                <Stack direction="row" spacing={2} divider={<Divider orientation={"vertical"} flexItem/>}>
                    <Tooltip title="Update">
                        <span>
                            <IconButton disabled={numSelected === 0} onClick={props.onUpdateClicked}>
                                <UpdateIcon/>
                            </IconButton>
                        </span>
                    </Tooltip>
                    <Tooltip title="Delete">
                        <span>
                            <IconButton disabled={numSelected === 0} onClick={props.onUninstallClicked}>
                                <DeleteIcon/>
                            </IconButton>
                        </span>
                    </Tooltip>
                </Stack>
            </Toolbar>
    );
}

ModsTableToolbar.propTypes = {
    numSelected: PropTypes.number.isRequired,
};

export default ModsTableToolbar;