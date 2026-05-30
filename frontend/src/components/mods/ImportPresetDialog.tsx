import {ChangeEvent, useEffect, useState} from "react";
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import {Alert} from "@mui/material";

type ImportPresetDialogProps = {
    open: boolean,
    file: File | null,
    existingPresetNames: string[],
    onConfirmClicked: (name: string) => void,
    onClose: () => void
}

export default function ImportPresetDialog(props: ImportPresetDialogProps) {
    const [presetName, setPresetName] = useState("");
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (!props.open || !props.file) return;

        props.file.text().then(content => {
            const parser = new DOMParser();
            const doc = parser.parseFromString(content, 'text/html');
            const metaTag = doc.querySelector('meta[name="arma:PresetName"]');
            const suggested = metaTag?.getAttribute('content') ?? "";
            setPresetName(suggested);
            if (suggested && props.existingPresetNames.includes(suggested)) {
                setError("This preset name is already in use");
            } else {
                setError(null);
            }
        });
    }, [props.open, props.file, props.existingPresetNames]);

    function handleNameChange(e: ChangeEvent<HTMLInputElement>) {
        const name = e.target.value;
        setPresetName(name);
        setError(name && props.existingPresetNames.includes(name) ? "This preset name is already in use" : null);
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

    return (
        <Dialog open={props.open} onClose={handleClose} fullWidth maxWidth="xs">
            <DialogTitle>Import preset</DialogTitle>
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
                <Button onClick={handleConfirm} disabled={!presetName.trim() || !!error}>
                    Import
                </Button>
            </DialogActions>
        </Dialog>
    );
}
