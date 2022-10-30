import {useFormik} from "formik";
import {Button, FormControlLabel, FormGroup, Grid, Switch, TextField, useMediaQuery} from "@mui/material";
import React from "react";

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

export default function EditReforgerServerSettingsForm(props) {

    const mediaQuery = useMediaQuery('(min-width:600px)');

    const formik = useFormik({
        initialValues: props.server,
        onSubmit: props.onSubmit,
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
                        {renderTextField("scenarioId", "Scenario ID", formik, true)}
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
            </div>
    );
}
