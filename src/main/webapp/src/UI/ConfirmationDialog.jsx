import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import DialogContent from "@mui/material/DialogContent";
import {Button, DialogContentText} from "@mui/material";
import DialogActions from "@mui/material/DialogActions";

export default function ConfirmationDialog(props) {
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
                    <Button onClick={props.onClose}>Cancel</Button>
                    <Button color={props.actionButtonColor ?? "error"} onClick={props.onConfirm} autoFocus>
                        {props.actionLabel}
                    </Button>
                </DialogActions>
            </Dialog>
    );
}