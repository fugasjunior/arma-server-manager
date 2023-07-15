import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import DialogContent from "@mui/material/DialogContent";
import {Button, ButtonProps, DialogContentText} from "@mui/material";
import DialogActions from "@mui/material/DialogActions";
import {MouseEventHandler} from "react";

type Props = {
    open: boolean,
    onClose: MouseEventHandler,
    onConfirm: MouseEventHandler, // TODO
    title: string,
    description: string,
    actionLabel: string,
    actionButtonColor?: ButtonProps["color"],
}

export default function ConfirmationDialog(props: Props) {
    return (
        <Dialog open={props.open}
                onClose={props.onClose}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
        >
            <DialogTitle id="alert-dialog-title">
                {props.title}
            </DialogTitle>
            <DialogContent>
                <DialogContentText id="alert-dialog-description">
                    {props.description}
                </DialogContentText>
            </DialogContent>
            <DialogActions>
                <Button id="dialog-cancel-btn" onClick={props.onClose}>Cancel</Button>
                <Button id="dialog-confirm-btn"
                        color={props.actionButtonColor ?? "error"} onClick={props.onConfirm} autoFocus>
                    {props.actionLabel}
                </Button>
            </DialogActions>
        </Dialog>
    );
}