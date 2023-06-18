import {useFormik} from "formik";
import {AutocompleteValue, Button, FormGroup, Grid, useMediaQuery} from "@mui/material";
import {ReforgerServerDto} from "../../dtos/ServerDto.ts";
import {ReforgerScenarioDto} from "../../dtos/ReforgerScenarioDto.ts";
import {ReforgerScenariosAutocomplete} from "./ReforgerScenariosAutocomplete.tsx";
import {SwitchField} from "../../UI/Form/SwitchField.tsx";
import {CustomTextField} from "../../UI/Form/CustomTextField.tsx";

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
                    <CustomTextField id='name' label='Server name' required formik={formik}/>
                    <CustomTextField id='description' label='Description' formik={formik}/>
                    <CustomTextField id='port' label='Port' type='number' formik={formik}/>
                    <CustomTextField id='queryPort' label='Query port' type='number' formik={formik}/>
                    <CustomTextField id='dedicatedServerId' label='Dedicated server ID' type='number' formik={formik}
                                     helperText='Leave empty to generate a new ID automatically'/>
                    <Grid item xs={12} md={6}>
                        <ReforgerScenariosAutocomplete onChange={setScenario} formik={formik}/>
                    </Grid>
                    <CustomTextField id='maxPlayers' label='Max players' required type='number' formik={formik}/>
                    <CustomTextField id='password' label='Password' formik={formik}/>
                    <CustomTextField id='adminPassword' label='Admin password' required formik={formik}/>
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
