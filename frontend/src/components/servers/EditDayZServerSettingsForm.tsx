import {useFormik} from "formik";
import {FormGroup, Grid} from "@mui/material";
import {DayZServerDto} from "../../dtos/ServerDto.ts";
import {SwitchField} from "../../UI/Form/SwitchField.tsx";
import {CustomTextField} from "../../UI/Form/CustomTextField.tsx";
import {ServerSettingsFormControls} from "./ServerSettingsFormControls.tsx";
import {CustomLaunchParametersInput} from "./CustomLaunchParametersInput.tsx";
import {useState} from "react";

type EditDayZServerSettingsFormProps = {
    server: DayZServerDto,
    isServerRunning?: boolean,
    onSubmit: (values: DayZServerDto) => void,
    onCancel: () => void
}
const EditDayZServerSettingsForm = (props: EditDayZServerSettingsFormProps) => {

    const [launchParameters, setLaunchParameters] = useState([...props.server.customLaunchParameters]);

    const handleSubmit = (values: DayZServerDto) => {
        values.customLaunchParameters = [...launchParameters];
        props.onSubmit(values);
    }

    const formik = useFormik<DayZServerDto>({
        initialValues: props.server,
        onSubmit: handleSubmit,
        enableReinitialize: true,
        validateOnChange: false,
    });

    return (
        <div>
            <form onSubmit={formik.handleSubmit}>
                <Grid container spacing={3}>
                    <CustomTextField id='name' label='Server name' required formik={formik}/>
                    <CustomTextField id='description' label='Description' formik={formik}/>
                    <CustomTextField id='port' label='Port' required type='number' formik={formik}/>
                    <CustomTextField id='queryPort' label='Query port' required type='number' formik={formik}/>
                    <CustomTextField id='maxPlayers' label='Max players' required type='number' formik={formik}/>
                    <CustomTextField id='password' label='Server password' formik={formik}/>
                    <CustomTextField id='adminPassword' label='Admin password' formik={formik}/>
                    <CustomTextField id='respawnTime' label='Respawn time (seconds)' type='number'
                                     formik={formik} inputProps={{min: "0"}}/>
                    <CustomTextField id='timeAcceleration' label='Time acceleration' type='number'
                                     formik={formik} inputProps={{step: "0.1", min: "0.1", max: "64"}}/>
                    <CustomTextField id='nightTimeAcceleration' label='Night time acceleration' type='number'
                                     formik={formik} inputProps={{step: "0.1", min: "0.1", max: "64"}}/>
                    <Grid item xs={6}>
                        <FormGroup>
                            <SwitchField id='vonEnabled' label='VON enabled' formik={formik}/>
                            <SwitchField id='persistent' label='Persistent server time' formik={formik}/>
                            <SwitchField id='clientFilePatching' label='Client file patching' formik={formik}/>
                        </FormGroup>
                    </Grid>
                    <Grid item xs={6}>
                        <FormGroup>
                            <SwitchField id='forceSameBuild' label='Force same build' formik={formik}/>
                            <SwitchField id='thirdPersonViewEnabled' label='Third person view enabled' formik={formik}/>
                            <SwitchField id='crosshairEnabled' label='Crosshair enabled' formik={formik}/>
                        </FormGroup>
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
                    <ServerSettingsFormControls serverRunning={props.isServerRunning} onCancel={props.onCancel}/>
                </Grid>
            </form>
        </div>
    );
};

export default EditDayZServerSettingsForm;