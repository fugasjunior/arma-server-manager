import {useFormik} from "formik";
import {
    Autocomplete,
    Box,
    Button,
    FormControlLabel,
    FormGroup,
    Grid,
    Modal,
    Stack,
    Switch,
    TextField,
    useMediaQuery
} from "@mui/material";
import React, {useEffect, useState} from "react";
import {getReforgerScenarios} from "../../services/scenarioService";
import IconButton from "@mui/material/IconButton";
import DeleteIcon from "@mui/icons-material/Delete";
import AddIcon from '@mui/icons-material/Add';
import TableContainer from "@mui/material/TableContainer";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import TableHead from "@mui/material/TableHead";

function renderTextField(name, label, formik, required, type, helperText) {
    return (
            <Grid item xs={12} md={6}>
                <TextField
                        fullWidth
                        required={required}
                        id={name}
                        name={name}
                        label={label}
                        type={!!type ? type : "text"}
                        value={formik.values[name]}
                        onChange={formik.handleChange}
                        helperText={helperText}
                        error={formik.touched[name] && Boolean(formik.errors[name])}
                        inputProps={{autoComplete: 'new-password'}}
                />
            </Grid>
    )
}

function renderSwitch(name, label, formik) {
    return (
            <FormControlLabel
                    control={
                        <Switch checked={formik.values[name]} onChange={formik.handleChange}
                                name={name} id={name}/>
                    }
                    label={label}
                    error={formik.touched[name] && Boolean(formik.errors[name])}
            />
    )
}

const modalStyle = {
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: 600,
    bgcolor: 'background.paper',
    border: '2px solid #000',
    boxShadow: 24,
    p: 4,
};

export default function EditReforgerServerSettingsForm(props) {

    const [scenarios, setScenarios] = useState([]);
    const [mods, setMods] = useState(props.server.activeMods);
    const [modsModalOpen, setModsModalOpen] = useState(false);
    const [newModName, setNewModName] = useState("");
    const [newModId, setNewModId] = useState("");

    useEffect(() => {
        async function fetchScenarios() {
            const {data: scenariosDto} = await getReforgerScenarios();
            setScenarios(scenariosDto.scenarios);
        }

        fetchScenarios();
    }, []);

    function handleSubmit(values) {
        props.onSubmit(values, mods);
    }

    function handleToggleModsModal() {
        setModsModalOpen(prevState => !prevState);
    }

    function handleNewModNameChange(event) {
        setNewModName(event.target.value);
    }

    function handleNewModIdChange(event) {
        setNewModId(event.target.value);
    }

    function handleAddNewMod() {
        if (!mods.find(mod => mod.id === newModId)) {
            setMods(prevState => [...prevState, {id: newModId, name: newModName}]);
        }
        setNewModId("");
        setNewModName("");
    }

    function handleDeleteMod(id) {
        setMods(prevState => prevState.filter(mod => mod.id !== id));
    }

    function getScenarioDisplayName(scenario) {
        if (scenario.name) {
            return `${scenario.name} (${scenario.value})`;
        }
        return scenario.value;
    }

    const mediaQuery = useMediaQuery('(min-width:600px)');

    const formik = useFormik({
        initialValues: props.server,
        onSubmit: handleSubmit,
        enableReinitialize: true
    });

    return (
            <div>
                <form onSubmit={formik.handleSubmit}>
                    <Grid container spacing={3}>
                        {renderTextField("name", "Server name", formik, true)}
                        {renderTextField("description", "Description", formik)}
                        {renderTextField("port", "Port", formik, true, "number")}
                        {renderTextField("queryPort", "Query port", formik, true, "number")}
                        {renderTextField("dedicatedServerId", "Dedicated server ID", formik, false, "text",
                                "Leave empty to generate a new ID automatically")}

                        <Grid item xs={12} md={6}>
                            <Autocomplete
                                    id="scenario-select"
                                    fullWidth
                                    options={scenarios}
                                    autoHighlight
                                    getOptionLabel={(option) => option.value}
                                    value={{value: formik.values.scenarioId}}
                                    onChange={(e, value) => formik.setFieldValue("scenarioId", value.value)}
                                    renderOption={(props, option) => (
                                            <Box component="li"  {...props}>
                                                {getScenarioDisplayName(option)}
                                            </Box>
                                    )}
                                    renderInput={(params) => (
                                            <TextField
                                                    {...params}
                                                    required
                                                    id="scenarioId"
                                                    name="scenarioId"
                                                    label="Scenario ID"
                                                    value={formik.values.scenarioId}
                                                    error={formik.touched.scenarioId && Boolean(
                                                            formik.errors.scenarioId)}
                                                    inputProps={{
                                                        ...params.inputProps,
                                                        autoComplete: 'new-password'
                                                    }}
                                            />
                                    )}
                            />
                        </Grid>

                        {renderTextField("maxPlayers", "Max players", formik, true, "number")}
                        {renderTextField("password", "Password", formik)}
                        {renderTextField("adminPassword", "Admin password", formik, true)}
                        <Grid item xs={12}>
                            <FormGroup>
                                {renderSwitch("battlEye", "BattlEye enabled", formik)}
                                {renderSwitch("thirdPersonViewEnabled", "Third person view enabled", formik)}
                            </FormGroup>
                        </Grid>
                        <Grid item xs={12}>
                            <Button title={props.isServerRunning ? "Stop the server to be able to update the settings."
                                    : ""}
                                    fullWidth={!mediaQuery}
                                    color="primary" variant="contained" type="submit" size="large">
                                Submit
                            </Button>
                            <Button color="error" variant="outlined"
                                    fullWidth={!mediaQuery} sx={mediaQuery ? {ml: 1} : {mt: 1}}
                                    onClick={props.onCancel}>
                                Cancel
                            </Button>
                        </Grid>
                    </Grid>
                </form>
                <Button id="manage-mods-btn" onClick={handleToggleModsModal} sx={{mt: 2}}>Manage mods</Button>
                <Modal open={modsModalOpen} onClose={handleToggleModsModal}>
                    <Box sx={modalStyle}>
                        <Stack direction="row" spacing={1} mb={2} justifyItems="center" justifyContent="space-between">
                            <TextField id="mod-id" label="Mod ID" placeholder="Mod ID" size="small" required
                                       variant="standard" value={newModId} onChange={handleNewModIdChange}/>
                            <TextField id="mod-name" label="Mod name" placeholder="Mod name" size="small" required
                                       variant="standard" value={newModName} onChange={handleNewModNameChange}/>
                            <Button variant="contained" startIcon={<AddIcon/>}
                                    onClick={handleAddNewMod}
                                    disabled={newModId.length === 0 || newModName.length === 0}
                            >
                                Add
                            </Button>
                        </Stack>
                        {mods.length > 0 &&
                                <Box overflow="auto" maxHeight={400}>
                                    <TableContainer>
                                        <Table size="small">
                                            <TableHead>
                                                <TableRow>
                                                    <TableCell>ID</TableCell>
                                                    <TableCell>Name</TableCell>
                                                    <TableCell></TableCell>
                                                </TableRow>
                                            </TableHead>
                                            <TableBody>
                                                {mods.map(mod => (
                                                        <TableRow key={mod.id} style={{height: 33}}>
                                                            <TableCell>{mod.id}</TableCell>
                                                            <TableCell>{mod.name}</TableCell>
                                                            <TableCell align="right">
                                                                <IconButton
                                                                        aria-label="delete"
                                                                        onClick={() => handleDeleteMod(mod.id)}>
                                                                    <DeleteIcon color="error"/>
                                                                </IconButton>
                                                            </TableCell>
                                                        </TableRow>
                                                ))}
                                            </TableBody>
                                        </Table>
                                    </TableContainer>
                                </Box>
                        }
                    </Box>
                </Modal>
            </div>
    );
}
