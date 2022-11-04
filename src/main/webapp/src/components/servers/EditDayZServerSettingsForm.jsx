import {useFormik} from "formik";
import {Box, Button, FormControlLabel, FormGroup, Grid, Modal, Switch, TextField, useMediaQuery} from "@mui/material";
import React, {useEffect, useState} from "react";
import ListBuilder from "../../UI/ListBuilder/ListBuilder";
import {getModPresets} from "../../services/modPresetsService";

const EditDayZServerSettingsForm = props => {

    const [modsModalOpen, setModsModalOpen] = useState(false);
    const [availableMods, setAvailableMods] = useState([]);
    const [selectedMods, setSelectedMods] = useState([]);
    const [presets, setPresets] = useState([]);
    const [selectedPreset, setSelectedPreset] = useState("");

    useEffect(() => {
        async function fetchPresets() {
            const {data: presetsDto} = await getModPresets("DAYZ");
            setPresets(presetsDto.presets);
        }

        fetchPresets();

        setSelectedMods(props.server.activeMods);
        const newAvailableMods = props.availableMods.filter(
                mod => !props.server.activeMods.find(searchedMod => searchedMod.id === mod.id));
        setAvailableMods(newAvailableMods);
    }, [props.availableMods]);

    const handleSubmit = (values) => {
        props.onSubmit(values, selectedMods);
    }

    const formik = useFormik({
        initialValues: props.server,
        onSubmit: handleSubmit,
        enableReinitialize: true,
        validateOnChange: false,
    });

    const mediaQuery = useMediaQuery('(min-width:600px)');

    const handleModSelect = option => {
        setSelectedPreset("");
        setAvailableMods((prevState) => {
            return prevState.filter(item => item !== option);

        });
        setSelectedMods((prevState) => {
            return [option, ...prevState].sort((a, b) => a.name.localeCompare(b.name));
        });

    }

    const handleModDeselect = option => {
        setSelectedPreset("");
        setSelectedMods((prevState) => {
            return prevState.filter(item => item !== option);
        });

        setAvailableMods((prevState) => {
            return [option, ...prevState].sort((a, b) => a.name.localeCompare(b.name));
        });
    }

    const handleToggleModsModal = () => {
        setModsModalOpen(prevState => !prevState);
    }

    const handlePresetChange = (e) => {
        const presetId = e.target.value;
        const preset = presets.find(preset => preset.id === presetId);
        if (!preset) {
            return;
        }

        const newAvailableMods = [...props.availableMods];
        const newSelectedMods = [];
        for (const mod of preset.mods) {
            const selectedMod = newAvailableMods.find(m => m.id === mod.id);
            const index = newAvailableMods.indexOf(selectedMod);
            newSelectedMods.push(selectedMod);
            newAvailableMods.splice(index, 1);
        }

        setAvailableMods(newAvailableMods);
        setSelectedMods(newSelectedMods);
        setSelectedPreset(presetId);
    }

    return (
            <div>
                <form onSubmit={formik.handleSubmit}>
                    <Grid container spacing={3}>
                        <Grid item xs={12} md={6}>
                            <TextField
                                    fullWidth
                                    required
                                    id="name"
                                    name="name"
                                    label="Server name"
                                    value={formik.values.name}
                                    onChange={formik.handleChange}
                                    error={formik.touched.name && Boolean(formik.errors.name)}
                            />
                        </Grid>
                        <Grid item xs={12} md={6}>
                            <TextField
                                    fullWidth
                                    id="description"
                                    name="description"
                                    label="Description"
                                    value={formik.values.description}
                                    onChange={formik.handleChange}
                                    error={formik.touched.description && Boolean(formik.errors.description)}
                            />
                        </Grid>
                        <Grid item xs={12} md={6}>
                            <TextField
                                    fullWidth
                                    required
                                    id="port"
                                    name="port"
                                    label="Port"
                                    type="number"
                                    value={formik.values.port}
                                    onChange={formik.handleChange}
                                    error={formik.touched.port && Boolean(formik.errors.port)}
                            />
                        </Grid>
                        <Grid item xs={12} md={6}>
                            <TextField
                                    fullWidth
                                    required
                                    id="queryPort"
                                    name="queryPort"
                                    label="Query Port"
                                    type="number"
                                    value={formik.values.queryPort}
                                    onChange={formik.handleChange}
                                    error={formik.touched.queryPort && Boolean(formik.errors.queryPort)}
                            />
                        </Grid>
                        <Grid item xs={12} md={6}>
                            <TextField
                                    fullWidth
                                    required
                                    id="maxPlayers"
                                    name="maxPlayers"
                                    label="Max players"
                                    type="number"
                                    value={formik.values.maxPlayers}
                                    onChange={formik.handleChange}
                                    error={formik.touched.maxPlayers && Boolean(formik.errors.maxPlayers)}
                            />
                        </Grid>
                        <Grid item xs={12} md={6}>
                            <TextField
                                    fullWidth
                                    id="password"
                                    name="password"
                                    label="Server password"
                                    value={formik.values.password}
                                    onChange={formik.handleChange}
                                    error={formik.touched.password && Boolean(formik.errors.password)}
                            />
                        </Grid>
                        <Grid item xs={12} md={6}>
                            <TextField
                                    fullWidth
                                    id="adminPassword"
                                    name="adminPassword"
                                    label="Admin password"
                                    value={formik.values.adminPassword}
                                    onChange={formik.handleChange}
                                    error={formik.touched.adminPassword && Boolean(formik.errors.adminPassword)}
                            />
                        </Grid>
                        <Grid item xs={12} md={6}>
                            <TextField
                                    fullWidth
                                    id="respawnTime"
                                    name="respawnTime"
                                    label="Respawn time"
                                    type="number"
                                    inputProps={{
                                        min: "0"
                                    }}
                                    value={formik.values.respawnTime}
                                    onChange={formik.handleChange}
                                    error={formik.touched.respawnTime && Boolean(formik.errors.respawnTime)}
                            />
                        </Grid>
                        <Grid item xs={12} md={6}>
                            <TextField
                                    fullWidth
                                    id="timeAcceleration"
                                    name="timeAcceleration"
                                    label="Time acceleration"
                                    type="number"
                                    inputProps={{
                                        step: "0.1",
                                        min: "0.1",
                                        max: "64"
                                    }}
                                    value={formik.values.timeAcceleration}
                                    onChange={formik.handleChange}
                                    error={formik.touched.timeAcceleration && Boolean(formik.errors.timeAcceleration)}
                            />
                        </Grid>
                        <Grid item xs={12} md={6}>
                            <TextField
                                    fullWidth
                                    id="nightTimeAcceleration"
                                    name="nightTimeAcceleration"
                                    label="Night time acceleration"
                                    type="number"
                                    inputProps={{
                                        step: "0.1",
                                        min: "0.1",
                                        max: "64"
                                    }}
                                    value={formik.values.nightTimeAcceleration}
                                    onChange={formik.handleChange}
                                    error={formik.touched.nightTimeAcceleration && Boolean(
                                            formik.errors.nightTimeAcceleration)}
                            />
                        </Grid>
                        <Grid item xs={6}>
                            <FormGroup>
                                <FormControlLabel
                                        control={
                                            <Switch checked={formik.values.vonEnabled} onChange={formik.handleChange}
                                                    name="vonEnabled" id="vonEnabled"/>
                                        }
                                        label="VON enabled"
                                        error={formik.touched.vonEnabled && Boolean(formik.errors.vonEnabled)}
                                />
                                <FormControlLabel
                                        control={
                                            <Switch checked={formik.values.persistent} onChange={formik.handleChange}
                                                    name="persistent" id="persistent"/>
                                        }
                                        label="Persistent server time"
                                        error={formik.touched.persistent && Boolean(formik.errors.persistent)}
                                />
                                <FormControlLabel
                                        control={
                                            <Switch checked={formik.values.clientFilePatching}
                                                    onChange={formik.handleChange}
                                                    name="clientFilePatching" id="clientFilePatching"/>
                                        }
                                        label="Client file patching"
                                        error={formik.touched.clientFilePatching && Boolean(
                                                formik.errors.clientFilePatching)}
                                />
                            </FormGroup>
                        </Grid>
                        <Grid item xs={6}>
                            <FormGroup>
                                <FormControlLabel
                                        control={
                                            <Switch checked={formik.values.forceSameBuild}
                                                    onChange={formik.handleChange}
                                                    name="forceSameBuild" id="forceSameBuild"/>
                                        }
                                        label="Force same build"
                                        error={formik.touched.forceSameBuild && Boolean(formik.errors.forceSameBuild)}
                                />
                                <FormControlLabel
                                        control={
                                            <Switch checked={formik.values.thirdPersonViewEnabled}
                                                    onChange={formik.handleChange}
                                                    name="thirdPersonViewEnabled" id="thirdPersonViewEnabled"/>
                                        }
                                        label="Third person view enabled"
                                        error={formik.touched.thirdPersonViewEnabled && Boolean(
                                                formik.errors.thirdPersonViewEnabled)}
                                />
                                <FormControlLabel control={
                                    <Switch checked={formik.values.crosshairEnabled}
                                            onChange={formik.handleChange}
                                            name="crosshairEnabled" id="crosshairEnabled"/>
                                }
                                                  label="Crosshair enabled"
                                                  error={formik.touched.crosshairEnabled && Boolean(
                                                          formik.errors.crosshairEnabled)}
                                />
                            </FormGroup>
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                    fullWidth
                                    id="additionalOptions"
                                    name="additionalOptions"
                                    label="Additional options"
                                    multiline
                                    value={formik.values.additionalOptions}
                                    onChange={formik.handleChange}
                                    error={formik.touched.additionalOptions && Boolean(formik.errors.additionalOptions)}
                            />
                        </Grid>
                        <Grid item xs={12} md={6}>
                            <Button title={props.isServerRunning
                                    && "Stop the server to be able to update the settings."}
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
                <>
                    <Button onClick={handleToggleModsModal} sx={{mt: 2}}>Manage mods</Button>
                    <Modal open={modsModalOpen} onClose={handleToggleModsModal}>
                        <Box>
                            <ListBuilder selectedOptions={selectedMods} availableOptions={availableMods}
                                         onSelect={handleModSelect} onDeselect={handleModDeselect}
                                         itemsLabel="mods" showFilter selectedPreset={selectedPreset}
                                         presets={presets} onPresetChange={handlePresetChange}/>
                        </Box>
                    </Modal>
                </>
            </div>
    );
};

export default EditDayZServerSettingsForm;