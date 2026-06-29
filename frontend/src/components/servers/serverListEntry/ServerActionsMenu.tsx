import {useState} from "react";
import IconButton from "@mui/material/IconButton";
import Menu from "@mui/material/Menu";
import MenuItem from "@mui/material/MenuItem";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import MoreVertIcon from "@mui/icons-material/MoreVert";
import ContentCopyIcon from "@mui/icons-material/ContentCopy";
import DeleteIcon from "@mui/icons-material/Delete";
import PermissionGuard from "../../auth/PermissionGuard";
import {ServerDto} from "../../../api/generated";

type ServerActionsMenuProps = {
    server: ServerDto,
    onDuplicateServer: (server: ServerDto) => void,
    onDeleteServer: (id: number) => void,
}

export function ServerActionsMenu({server, onDuplicateServer, onDeleteServer}: ServerActionsMenuProps) {
    const [anchor, setAnchor] = useState<null | HTMLElement>(null);
    const open = Boolean(anchor);

    const handleOpen = (e: React.MouseEvent<HTMLElement>) => setAnchor(e.currentTarget);
    const handleClose = () => setAnchor(null);

    return (
        <>
            <IconButton
                data-testid={`server-${server.id}-actions-btn`}
                onClick={handleOpen}
                aria-label="server actions"
            >
                <MoreVertIcon/>
            </IconButton>
            <Menu anchorEl={anchor} open={open} onClose={handleClose}>
                <PermissionGuard permission="SERVER_MODIFY">
                    <MenuItem onClick={() => { onDuplicateServer(server); handleClose(); }}>
                        <ListItemIcon><ContentCopyIcon/></ListItemIcon>
                        <ListItemText>Duplicate</ListItemText>
                    </MenuItem>
                </PermissionGuard>
                <PermissionGuard permission="SERVER_DELETE">
                    <MenuItem
                        id={`server-${server.id}-delete-btn`}
                        onClick={() => { onDeleteServer(server.id as number); handleClose(); }}
                        sx={{color: "error.main"}}
                    >
                        <ListItemIcon sx={{color: "error.main"}}><DeleteIcon/></ListItemIcon>
                        <ListItemText>Delete</ListItemText>
                    </MenuItem>
                </PermissionGuard>
            </Menu>
        </>
    );
}
