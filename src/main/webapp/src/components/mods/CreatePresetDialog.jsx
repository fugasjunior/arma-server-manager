import {useState} from "react";
import Accordion from '@mui/material/Accordion';
import AccordionSummary from '@mui/material/AccordionSummary';
import AccordionDetails from '@mui/material/AccordionDetails';
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import Typography from '@mui/material/Typography';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';

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
                    <DialogContentText>
                        <Accordion>
                            <AccordionSummary
                                    expandIcon={<ExpandMoreIcon/>}
                                    aria-controls="panel1a-content"
                                    id="panel1a-header"
                            >
                                <Typography>Show selected mods</Typography>
                            </AccordionSummary>
                            <AccordionDetails>
                                <ul>
                                    {props.selectedMods.map(mod => (
                                            <li key={mod.id}>{mod.name}</li>
                                    ))}
                                </ul>
                            </AccordionDetails>
                        </Accordion>
                    </DialogContentText>
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