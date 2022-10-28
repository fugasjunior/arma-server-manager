import {useFormik} from "formik";
import {Box, Button, FormControlLabel, FormGroup, Grid, Modal, Switch, TextField, useMediaQuery} from "@mui/material";
import React, {useEffect, useState} from "react";
import ListBuilder from "../../UI/ListBuilder";

const EditArma3ServerSettingsForm = props => {

    const [modsModalOpen, setModsModalOpen] = useState(false);
    const [dlcsModalOpen, setDlcsModalOpen] = useState(false);
    const [availableMods, setAvailableMods] = useState([]);
    const [selectedMods, setSelectedMods] = useState([]);
    const [availableDlcs, setAvailableDlcs] = useState([]);
    const [selectedDlcs, setSelectedDlcs] = useState([]);

    useEffect(() => {
        setSelectedMods(props.server.activeMods);
        setSelectedDlcs(props.server.activeDLCs);

        const newAvailableMods = props.availableMods.filter(
                mod => !props.server.activeMods.find(searchedMod => searchedMod.id === mod.id));
        const newAvailableDlcs = props.availableDlcs.filter(
                cdlc => !props.server.activeDLCs.find(searchedDlc => searchedDlc.id === cdlc.id));

        setAvailableMods(newAvailableMods);
        setAvailableDlcs(newAvailableDlcs);

    }, [props.availableMods, props.availableDlcs]);

    const handleSubmit = (values) => {
        props.onSubmit(values, selectedMods, selectedDlcs);
    }

    const formik = useFormik({
        initialValues: props.server,
        onSubmit: handleSubmit,
        enableReinitialize: true
    });

    const mediaQuery = useMediaQuery('(min-width:600px)');

    const handleModSelect = option => {
        setAvailableMods((prevState) => {
            return prevState.filter(item => item !== option);

        });
        setSelectedMods((prevState) => {
            return [option, ...prevState].sort((a, b) => a.name.localeCompare(b.name));
        });

    }

    const handleModDeselect = option => {
        setSelectedMods((prevState) => {
            return prevState.filter(item => item !== option);
        });

        setAvailableMods((prevState) => {
            return [option, ...prevState].sort((a, b) => a.name.localeCompare(b.name));
        });
    }

    const handleDlcSelect = option => {
        setAvailableDlcs((prevState) => {
            return prevState.filter(item => item !== option);
        });

        setSelectedDlcs((prevState) => {
            return [option, ...prevState].sort((a, b) => a.name.localeCompare(b.name));
        });
    }

    const handleDlcDeselect = option => {
        setSelectedDlcs((prevState) => {
            return prevState.filter(item => item !== option);
        });

        setAvailableDlcs((prevState) => {
            return [option, ...prevState].sort((a, b) => a.name.localeCompare(b.name));
        });
    }

    const handleToggleModsModal = () => {
        setModsModalOpen(prevState => !prevState);
    }

    const handleToggleDlcsModal = () => {
        setDlcsModalOpen(prevState => !prevState);
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
                        <Grid item xs={6}>
                            <FormGroup>
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
                                <FormControlLabel
                                        control={
                                            <Switch checked={formik.values.serverFilePatching}
                                                    onChange={formik.handleChange}
                                                    name="serverFilePatching" id="serverFilePatching"/>
                                        }
                                        label="Server file patching"
                                        error={formik.touched.serverFilePatching && Boolean(
                                                formik.errors.serverFilePatching)}
                                />
                                <FormControlLabel
                                        control={
                                            <Switch checked={formik.values.verifySignatures}
                                                    onChange={formik.handleChange}
                                                    name="verifySignatures" id="verifySignatures"/>
                                        }
                                        label="Verify signatures"
                                        error={formik.touched.verifySignatures && Boolean(
                                                formik.errors.verifySignatures)}
                                />
                            </FormGroup>
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
                                            <Switch checked={formik.values.battlEye} onChange={formik.handleChange}
                                                    name="battlEye" id="battlEye"/>
                                        }
                                        label="BattlEye enabled"
                                        error={formik.touched.battlEye && Boolean(formik.errors.battlEye)}
                                />
                                <FormControlLabel
                                        control={
                                            <Switch checked={formik.values.persistent} onChange={formik.handleChange}
                                                    name="persistent" id="persistent"/>
                                        }
                                        label="Persistent"
                                        error={formik.touched.persistent && Boolean(formik.errors.persistent)}
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
                            <Button title={props.isServerRunning ? "Stop the server to be able to update the settings." : ""}
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
                <Button onClick={handleToggleModsModal} sx={{mt: 2}}>Manage mods</Button>
                <Button onClick={handleToggleDlcsModal} sx={{mt: 2, ml: 2}}>Manage DLCs</Button>
                <Modal open={modsModalOpen} onClose={handleToggleModsModal}>
                    <Box>
                        <ListBuilder selectedOptions={selectedMods} availableOptions={availableMods}
                                     onSelect={handleModSelect} onDeselect={handleModDeselect}
                                     itemsLabel="mods" showFilter/>
                    </Box>
                </Modal>
                <Modal open={dlcsModalOpen} onClose={handleToggleDlcsModal}>
                    <Box>
                        <ListBuilder selectedOptions={selectedDlcs} availableOptions={availableDlcs}
                                     onSelect={handleDlcSelect} onDeselect={handleDlcDeselect}
                                     itemsLabel="DLCs"/>
                    </Box>
                </Modal>
            </div>
    );
};

export default EditArma3ServerSettingsForm;