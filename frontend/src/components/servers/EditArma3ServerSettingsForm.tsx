import {useFormik} from "formik";
import {FormGroup, Grid} from "@mui/material";
import Arma3DifficultySettingsForm from "./difficulty/Arma3DifficultySettingsForm.tsx";
import {Arma3NetworkSettings, Arma3ServerDto} from "../../dtos/ServerDto.ts";
import {SwitchField} from "../../UI/Form/SwitchField.tsx";
import {CustomTextField} from "../../UI/Form/CustomTextField.tsx";
import {ServerSettingsFormControls} from "./ServerSettingsFormControls.tsx";
import Arma3NetworkSettingsForm from "./Arma3NetworkSettingsForm.tsx";
import {useState} from "react";
import {CustomLaunchParametersInput} from "./CustomLaunchParametersInput.tsx";
import {Arma3ScenarioRotationForm} from "./Arma3ScenarioRotationForm.tsx";

type EditArma3ServerSettingsFormProps = {
    server: Arma3ServerDto,
    isServerRunning?: boolean
    onSubmit: (values: Arma3ServerDto) => void,
    onCancel: () => void
}

const EditArma3ServerSettingsForm = (props: EditArma3ServerSettingsFormProps) => {

    const [launchParameters, setLaunchParameters] = useState([...props.server.customLaunchParameters]);

    const handleSubmit = (values: Arma3ServerDto) => {
        if (areAllPropertiesNull(values.networkSettings)) {
            values.networkSettings = null;
        }

        values.customLaunchParameters = [...launchParameters];
        props.onSubmit(values);
    }

    const formik = useFormik<Arma3ServerDto>({
        initialValues: props.server,
        onSubmit: handleSubmit,
        enableReinitialize: true
    });

    const areAllPropertiesNull = (networkSettings: Arma3NetworkSettings | null): boolean => {
        if (networkSettings === null) {
            return true;
        }
        return Object.values(networkSettings).every(x => x === null);
    }

    return (
        <div>
            <form onSubmit={formik.handleSubmit}>
                <Grid container spacing={3}>
                    <CustomTextField id='name' label='Server name' required formik={formik}/>
                    <CustomTextField id='description' label='Description' formik={formik}/>
                    <CustomTextField id='port' label='Port' required type='number' formik={formik}/>
                    <CustomTextField id='maxPlayers' label='Max players' required type='number' formik={formik}/>
                    <CustomTextField id='password' label='Server password' formik={formik}/>
                    <CustomTextField id='adminPassword' label='Admin password' formik={formik}/>
                    <Grid item xs={6}>
                        <FormGroup>
                            <SwitchField id='clientFilePatching' label='Client file patching' formik={formik}/>
                            <SwitchField id='serverFilePatching' label='Server file patching' formik={formik}/>
                            <SwitchField id='verifySignatures' label='Verify signatures' formik={formik}/>
                        </FormGroup>
                    </Grid>
                    <Grid item xs={6}>
                        <FormGroup>
                            <SwitchField id='vonEnabled' label='VON enabled' formik={formik}/>
                            <SwitchField id='battlEye' label='BattlEye enabled' formik={formik}/>
                            <SwitchField id='persistent' label='Persistent' formik={formik}/>
                        </FormGroup>
                    </Grid>
                    
                    <Grid item xs={6}>
                        <Arma3ScenarioRotationForm formik={formik}/>
                    </Grid>

                    <CustomTextField id='additionalOptions' label='Additional options' multiline
                                     formik={formik} containerMd={12}/>

                    <Grid item xs={12}>
                        <CustomLaunchParametersInput
                            valueDelimiter='='
                            parameters={launchParameters}
                            onParametersChange={setLaunchParameters}
                        />
                    </Grid>
                    <Grid item xs={12}>
                        <Arma3DifficultySettingsForm formik={formik}/>
                    </Grid>
                    <Grid item xs={12}>
                        <Arma3NetworkSettingsForm formik={formik}/>
                    </Grid>

                    <ServerSettingsFormControls serverRunning={props.isServerRunning} onCancel={props.onCancel}/>
                </Grid>
            </form>
        </div>
    );
};

export default EditArma3ServerSettingsForm;