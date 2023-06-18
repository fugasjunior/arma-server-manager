import {useFormik} from "formik";
import {AutocompleteValue, Button, FormGroup, Grid, TextField, useMediaQuery} from "@mui/material";
import {ReforgerServerDto} from "../../dtos/ServerDto.ts";
import {ReforgerScenarioDto} from "../../dtos/ReforgerScenarioDto.ts";
import {ReforgerScenariosAutocomplete} from "./ReforgerScenariosAutocomplete.tsx";
import {SwitchField} from "../../UI/Form/SwitchField.tsx";

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

type EditReforgerServerSettingsFormProps = {
    server: ReforgerServerDto,
    isServerRunning?: boolean,
    onSubmit: (values: ReforgerServerDto) => void,
    onCancel: () => void
}

export default function EditReforgerServerSettingsForm(props: EditReforgerServerSettingsFormProps) {

    function handleSubmit(values: ReforgerServerDto) {
        props.onSubmit(values);
    }

    const mediaQuery = useMediaQuery('(min-width:600px)');

    const formik = useFormik<ReforgerServerDto>({
        initialValues: props.server,
        onSubmit: handleSubmit,
        enableReinitialize: true
    });

    const setScenario = (_: any, value: AutocompleteValue<ReforgerScenarioDto, false, false, false> | null): void => {
        if (!value) {
            return;
        }
        formik.setFieldValue("scenarioId", value.value);
    };

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
                        <ReforgerScenariosAutocomplete onChange={setScenario} formik={formik}/>
                    </Grid>

                    {renderTextField("maxPlayers", "Max players", formik, true, "number")}
                    {renderTextField("password", "Password", formik)}
                    {renderTextField("adminPassword", "Admin password", formik, true)}
                    <Grid item xs={12}>
                        <FormGroup>
                            <SwitchField id='battlEye' label='BattlEye enabled' formik={formik}/>
                            <SwitchField id='thirdPersonViewEnabled' label='Third person view enabled' formik={formik}/>
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
