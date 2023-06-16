import {useFormik} from "formik";
import {Button, FormControlLabel, FormGroup, Grid, Switch, TextField, useMediaQuery} from "@mui/material";

import Arma3DifficultySettingsForm from "./Arma3DifficultySettingsForm";

const EditArma3ServerSettingsForm = props => {

    const handleSubmit = (values) => {
        props.onSubmit(values);
    }

    const formik = useFormik({
        initialValues: props.server,
        onSubmit: handleSubmit,
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
                            />
                            <FormControlLabel
                                control={
                                    <Switch checked={formik.values.serverFilePatching}
                                            onChange={formik.handleChange}
                                            name="serverFilePatching" id="serverFilePatching"/>
                                }
                                label="Server file patching"
                            />
                            <FormControlLabel
                                control={
                                    <Switch checked={formik.values.verifySignatures}
                                            onChange={formik.handleChange}
                                            name="verifySignatures" id="verifySignatures"/>
                                }
                                label="Verify signatures"
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
                            />
                            <FormControlLabel
                                control={
                                    <Switch checked={formik.values.battlEye} onChange={formik.handleChange}
                                            name="battlEye" id="battlEye"/>
                                }
                                label="BattlEye enabled"
                            />
                            <FormControlLabel
                                control={
                                    <Switch checked={formik.values.persistent} onChange={formik.handleChange}
                                            name="persistent" id="persistent"/>
                                }
                                label="Persistent"
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
                        />
                    </Grid>

                    <Grid item xs={12}>
                        <Arma3DifficultySettingsForm formik={formik}/>
                    </Grid>

                    <Grid item xs={12} md={6}>
                        <Button id="submit-btn"
                                title={props.isServerRunning ? "Stop the server to be able to update the settings."
                                    : ""}
                                fullWidth={!mediaQuery}
                                color="primary" variant="contained" type="submit" size="large">
                            Submit
                        </Button>
                        <Button id="cancel-btn" color="error" variant="outlined"
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