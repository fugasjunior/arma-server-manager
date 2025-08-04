import {ChangeEvent, useState} from "react";
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';

type RenamePresetDialogProps = {
    open: boolean,
    onConfirmClicked: (name: string, id: string) => void,
    onClose: () => void,
    presetId: string | null
}

const RenamePresetDialog = (props: RenamePresetDialogProps) => {
    const [presetName, setPresetName] = useState("");

    const handlePresetNameChange = (e: ChangeEvent<HTMLInputElement>) => {
        setPresetName(e.target.value);
    }

    const handleConfirmClicked = () => {
        if (props.presetId) { // Ensure the ID is available
            props.onConfirmClicked(presetName, props.presetId);
            setPresetName(""); // Reset the input field
        }
    };

    return (
        <Dialog open={props.open} onClose={props.onClose} fullWidth maxWidth="xs">
            <DialogTitle>Rename preset</DialogTitle>
            <DialogContent>
                <TextField
                    autoFocus
                    margin="dense"
                    id="name"
                    label="Preset name"
                    fullWidth
                    variant="standard"
                    value={presetName}
                    onChange={handlePresetNameChange}
                />
            </DialogContent>
            <DialogActions>
                <Button onClick={props.onClose}>Cancel</Button>
                <Button onClick={handleConfirmClicked}>Confirm</Button>
            </DialogActions>
        </Dialog>
    );
};

export default RenamePresetDialog;