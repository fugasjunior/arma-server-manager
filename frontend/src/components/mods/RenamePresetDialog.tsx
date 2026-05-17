import {ChangeEvent, useEffect, useState} from "react";
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import {Alert} from "@mui/material";

type RenamePresetDialogProps = {
    open: boolean,
    currentName: string,
    existingPresetNames: string[],
    onConfirmClicked: (name: string) => void,
    onClose: () => void
}

export default function RenamePresetDialog(props: RenamePresetDialogProps) {
    const [presetName, setPresetName] = useState("");
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (props.open) {
            setPresetName(props.currentName);
            setError(null);
        }
    }, [props.open, props.currentName]);

    function handleNameChange(e: ChangeEvent<HTMLInputElement>) {
        const name = e.target.value;
        setPresetName(name);
        if (name !== props.currentName && props.existingPresetNames.includes(name)) {
            setError("This preset name is already in use");
        } else {
            setError(null);
        }
    }

    function handleConfirm() {
        props.onConfirmClicked(presetName);
        setPresetName("");
        setError(null);
    }

    function handleClose() {
        setPresetName("");
        setError(null);
        props.onClose();
    }

    const isUnchanged = presetName === props.currentName;
    const isDisabled = isUnchanged || !presetName.trim() || !!error;

    return (
        <Dialog open={props.open} onClose={handleClose} fullWidth maxWidth="xs">
            <DialogTitle>Rename preset</DialogTitle>
            <DialogContent>
                {error && <Alert severity="error" sx={{mb: 2}}>{error}</Alert>}
                <TextField
                    autoFocus
                    margin="dense"
                    label="Preset name"
                    fullWidth
                    variant="standard"
                    value={presetName}
                    onChange={handleNameChange}
                    error={!!error}
                />
            </DialogContent>
            <DialogActions>
                <Button onClick={handleClose}>Cancel</Button>
                <Button onClick={handleConfirm} disabled={isDisabled}>
                    Rename
                </Button>
            </DialogActions>
        </Dialog>
    );
}
