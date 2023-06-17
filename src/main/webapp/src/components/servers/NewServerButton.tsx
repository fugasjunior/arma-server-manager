import React, {KeyboardEvent} from 'react';
import {useContext} from 'react';
import Button from '@mui/material/Button';
import ClickAwayListener from '@mui/material/ClickAwayListener';
import Grow from '@mui/material/Grow';
import Paper from '@mui/material/Paper';
import Popper from '@mui/material/Popper';
import MenuItem from '@mui/material/MenuItem';
import MenuList from '@mui/material/MenuList';
import {Link} from "react-router-dom";
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline';
import {OsContext} from "../../store/os-context";

export default function NewServerButton() {
    const [open, setOpen] = React.useState(false);
    const anchorRef = React.useRef(null);

    const osCtx = useContext(OsContext);

    const handleToggle = () => {
        setOpen((prevOpen) => !prevOpen);
    };

    const handleClose = (event: Event) => {
        if (anchorRef.current && anchorRef.current.contains(event.target)) {
            return;
        }

        setOpen(false);
    };

    function handleListKeyDown(event: KeyboardEvent<HTMLUListElement>) {
        if (event.key === 'Tab') {
            event.preventDefault();
            setOpen(false);
        } else if (event.key === 'Escape') {
            setOpen(false);
        }
    }

    // return focus to the button when we transitioned from !open -> open
    const prevOpen = React.useRef(open);
    React.useEffect(() => {
        if (prevOpen.current === true && open === false) {
            anchorRef.current.focus();
        }

        prevOpen.current = open;
    }, [open]);

    return (
            <div>
                <Button
                        ref={anchorRef}
                        id="new-server-btn"
                        aria-controls={open ? 'composition-menu' : undefined}
                        aria-expanded={open ? 'true' : undefined}
                        aria-haspopup="true"
                        variant="contained"
                        size="large"
                        sx={{mt: 2, mb: 4}}
                        onClick={handleToggle}
                >
                    <AddCircleOutlineIcon sx={{mr: 1}}/>
                    Create new server
                </Button>
                <Popper
                        open={open}
                        sx={{zIndex: 9999}}
                        anchorEl={anchorRef.current}
                        placement="bottom-start"
                        transition
                        disablePortal
                >
                    {({TransitionProps, placement}) => (
                            <Grow
                                    {...TransitionProps}
                                    style={{
                                        transformOrigin:
                                                placement === 'bottom-start' ? 'left top' : 'left bottom',
                                    }}
                            >
                                <Paper>
                                    <ClickAwayListener onClickAway={handleClose}>
                                        <MenuList
                                                autoFocusItem={open}
                                                id="composition-menu"
                                                aria-labelledby="composition-button"
                                                onKeyDown={handleListKeyDown}
                                        >
                                            <MenuItem id="new-arma3-server-btn"
                                                      component={Link} to="/servers/new/ARMA3">
                                                Arma 3 server
                                            </MenuItem>
                                            <MenuItem id="new-reforger-server-btn"
                                                      component={Link} to="/servers/new/REFORGER">
                                                Arma Reforger server
                                            </MenuItem>
                                            {osCtx.os !== "LINUX" &&
                                                    <MenuItem id="new-dayz-server-btn"
                                                              component={Link}
                                                              to="/servers/new/DAYZ">
                                                        DayZ server
                                                    </MenuItem>
                                            }
                                            <MenuItem id="new-dayzexp-server-btn"
                                                      component={Link} to="/servers/new/DAYZ_EXP">
                                                DayZ Experimental server
                                            </MenuItem>
                                        </MenuList>
                                    </ClickAwayListener>
                                </Paper>
                            </Grow>
                    )}
                </Popper>
            </div>
    );
}