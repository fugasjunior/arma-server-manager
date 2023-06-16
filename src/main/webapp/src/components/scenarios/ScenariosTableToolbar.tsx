import Toolbar from "@mui/material/Toolbar";
import {alpha} from "@mui/material/styles";
import Typography from "@mui/material/Typography";
import Tooltip from "@mui/material/Tooltip";
import IconButton from "@mui/material/IconButton";
import DeleteIcon from "@mui/icons-material/Delete";
import PropTypes from "prop-types";
import {Button, CircularProgress, Divider, Stack, TextField} from "@mui/material";

function ScenariosTableToolbar(props) {
    const {numSelected, title} = props;

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

                <Stack direction="row" spacing={2} divider={<Divider orientation={"vertical"} flexItem/>}>
                    <TextField label="Search" type="search" variant="standard" value={props.search}
                               sx={{minWidth: "120px"}} id="search-field" onChange={props.onSearchChange}
                    />
                    {!props.uploadInProgress && <Button variant="contained" component="label">
                        Upload
                        <input hidden multiple type="file" onChange={props.onFileChange}/>
                    </Button>}
                    {props.uploadInProgress && <CircularProgress variant="determinate" value={props.percentUploaded}/>}
                    <Tooltip title="Delete">
                        <span>
                            <IconButton disabled={numSelected === 0} onClick={props.onDeleteClicked}>
                                <DeleteIcon/>
                            </IconButton>
                        </span>
                    </Tooltip>
                </Stack>
            </Toolbar>
    );
}

ScenariosTableToolbar.propTypes = {
    numSelected: PropTypes.number.isRequired,
};

export default ScenariosTableToolbar;