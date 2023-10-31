import {useFormik} from "formik";
import {AutocompleteValue, FormGroup, Grid} from "@mui/material";
import {ReforgerServerDto} from "../../dtos/ServerDto.ts";
import {ReforgerScenarioDto} from "../../dtos/ReforgerScenarioDto.ts";
import {ReforgerScenariosAutocomplete} from "./ReforgerScenariosAutocomplete.tsx";
import {SwitchField} from "../../UI/Form/SwitchField.tsx";
import {CustomTextField} from "../../UI/Form/CustomTextField.tsx";
import {ServerSettingsFormControls} from "./ServerSettingsFormControls.tsx";
import {CustomLaunchParametersInput} from "./CustomLaunchParametersInput.tsx";
import {useState} from "react";

type EditReforgerServerSettingsFormProps = {
    server: ReforgerServerDto,
    isServerRunning?: boolean,
    onSubmit: (values: ReforgerServerDto) => void,
    onCancel: () => void
}

export default function EditReforgerServerSettingsForm(props: EditReforgerServerSettingsFormProps) {

    const [launchParameters, setLaunchParameters] = useState([...props.server.customLaunchParameters]);

    function handleSubmit(values: ReforgerServerDto) {
        values.customLaunchParameters = [...launchParameters];
        props.onSubmit(values);
    }

    const formik = useFormik<ReforgerServerDto>({
        initialValues: props.server,
        onSubmit: handleSubmit,
        enableReinitialize: true
    });

    const setScenario = (_: any, value: AutocompleteValue<ReforgerScenarioDto | string, false, false, true> | null): void => {
        console.log(_, value);
        if (!value) {
            return;
        }
        formik.setFieldValue("scenarioId", typeof value === "string" ? value : value.value);
    };

    return (
        <div>
            <form onSubmit={formik.handleSubmit}>
                <Grid container spacing={3}>
                    <CustomTextField id='name' label='Server name' required formik={formik}/>
                    <CustomTextField id='description' label='Description' formik={formik}/>
                    <CustomTextField id='port' label='Port' type='number' formik={formik}/>
                    <CustomTextField id='queryPort' label='Query port' type='number' formik={formik}/>
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
                        <CustomLaunchParametersInput
                            valueDelimiter=' '
                            parameters={launchParameters}
                            onParametersChange={setLaunchParameters}
                        />
                    </Grid>
                    <ServerSettingsFormControls serverRunning={props.isServerRunning} onCancel={props.onCancel}/>
                </Grid>
            </form>
        </div>
    );
}
