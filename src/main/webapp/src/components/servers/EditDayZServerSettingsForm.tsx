import {useFormik} from "formik";
import {Button, FormGroup, Grid, useMediaQuery} from "@mui/material";
import {DayZServerDto} from "../../dtos/ServerDto.ts";
import {SwitchField} from "../../UI/Form/SwitchField.tsx";
import {CustomTextField} from "../../UI/Form/CustomTextField.tsx";

type EditDayZServerSettingsFormProps = {
    server: DayZServerDto,
    isServerRunning?: boolean,
    onSubmit: (values: DayZServerDto) => void,
    onCancel: () => void
}
const EditDayZServerSettingsForm = (props: EditDayZServerSettingsFormProps) => {

    const handleSubmit = (values: DayZServerDto) => {
        props.onSubmit(values);
    }

    const formik = useFormik<DayZServerDto>({
        initialValues: props.server,
        onSubmit: handleSubmit,
        enableReinitialize: true,
        validateOnChange: false,
    });

    const mediaQuery = useMediaQuery('(min-width:600px)');
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
                    <Grid item xs={12} md={6}>
                        <Button
                            title={props.isServerRunning ? "Stop the server to be able to update the settings." : ""}
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

export default EditDayZServerSettingsForm;