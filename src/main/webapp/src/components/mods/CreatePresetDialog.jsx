import {useState} from "react";
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';

const CreatePresetDialog = (props) => {
    const [presetName, setPresetName] = useState("");

    const handlePresetNameChange = e => {
        setPresetName(e.target.value);
    }

    const handleConfirmClicked = () => {
        const name = presetName;
        setPresetName("");
        props.onConfirmClicked(name);
    }

    return (
            <Dialog open={props.open} onClose={props.onClose} fullWidth maxWidth="xs">
                <DialogTitle>Create preset</DialogTitle>
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

export default CreatePresetDialog;