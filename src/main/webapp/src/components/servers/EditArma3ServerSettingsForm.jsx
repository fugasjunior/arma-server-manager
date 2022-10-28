import {useFormik} from "formik";
import {Button, FormControlLabel, FormGroup, Grid, Switch, TextField, useMediaQuery} from "@mui/material";
import React from "react";

const EditArma3ServerSettingsForm = props => {

    const formik = useFormik({
        initialValues: props.server,
        onSubmit: props.onSubmit,
        enableReinitialize: true
    });

    const mediaQuery = useMediaQuery('(min-width:600px)');

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
                                    helperText={formik.touched.name && formik.errors.name}
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
                                    helperText={formik.touched.description && formik.errors.description}
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
                                    helperText={formik.touched.port && formik.errors.port}
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
                                    helperText={formik.touched.maxPlayers && formik.errors.maxPlayers}
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
                                    helperText={formik.touched.password && formik.errors.password}
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
                                    helperText={formik.touched.adminPassword && formik.errors.adminPassword}
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
                                        helperText={formik.touched.clientFilePatching
                                                && formik.errors.clientFilePatching}
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
                                        helperText={formik.touched.serverFilePatching
                                                && formik.errors.serverFilePatching}
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
                                        helperText={formik.touched.verifySignatures && formik.errors.verifySignatures}
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
                                        helperText={formik.touched.vonEnabled && formik.errors.vonEnabled}
                                />
                                <FormControlLabel
                                        control={
                                            <Switch checked={formik.values.battlEye} onChange={formik.handleChange}
                                                    name="battlEye" id="battlEye"/>
                                        }
                                        label="BattlEye enabled"
                                        error={formik.touched.battlEye && Boolean(formik.errors.battlEye)}
                                        helperText={formik.touched.battlEye && formik.errors.battlEye}
                                />
                                <FormControlLabel
                                        control={
                                            <Switch checked={formik.values.persistent} onChange={formik.handleChange}
                                                    name="persistent" id="persistent"/>
                                        }
                                        label="Persistent"
                                        error={formik.touched.persistent && Boolean(formik.errors.persistent)}
                                        helperText={formik.touched.persistent && formik.errors.persistent}
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
                                    helperText={formik.touched.additionalOptions && formik.errors.additionalOptions}
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
            </div>
    );
};

export default EditArma3ServerSettingsForm;