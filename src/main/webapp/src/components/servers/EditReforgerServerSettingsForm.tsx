import {useFormik} from "formik";
import {
    Autocomplete,
    Box,
    Button,
    FormControlLabel,
    FormGroup,
    Grid,
    Switch,
    TextField,
    useMediaQuery
} from "@mui/material";
import {useEffect, useState} from "react";
import {getReforgerScenarios} from "../../services/scenarioService";

function renderTextField(name: string, label: string, formik: any, required?: boolean, type?: string, helperText?: string) {
    return (
        <Grid item xs={12} md={6}>
            <TextField
                fullWidth
                required={required}
                id={name}
                name={name}
                label={label}
                type={type ?? "text"}
                value={formik.values[name]}
                onChange={formik.handleChange}
                helperText={helperText}
                inputProps={{autoComplete: 'new-password'}}
            />
        </Grid>
    )
}

function renderSwitch(name: string, label: string, formik: any) {
    return (
        <FormControlLabel
            control={
                <Switch checked={formik.values[name]} onChange={formik.handleChange}
                        name={name} id={name}/>
            }
            label={label}
        />
    )
}

export default function EditReforgerServerSettingsForm(props) {

    const [scenarios, setScenarios] = useState([]);

    useEffect(() => {
        async function fetchScenarios() {
            const {data: scenariosDto} = await getReforgerScenarios();
            setScenarios(scenariosDto.scenarios);
        }

        fetchScenarios();
    }, []);

    function handleSubmit(values) {
        props.onSubmit(values);
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
                            onChange={(_, value) => formik.setFieldValue("scenarioId", value.value)}
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
        </div>
    );
}
