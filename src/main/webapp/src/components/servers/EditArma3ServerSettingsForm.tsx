import {useFormik} from "formik";
import {Button, FormGroup, Grid, TextField, useMediaQuery} from "@mui/material";
import Arma3DifficultySettingsForm from "./difficulty/Arma3DifficultySettingsForm.tsx";
import {Arma3ServerDto} from "../../dtos/ServerDto.ts";
import {SwitchField} from "../../UI/Form/SwitchField.tsx";
import {CustomTextField} from "../../UI/Form/CustomTextField.tsx";

type EditArma3ServerSettingsFormProps = {
    server: Arma3ServerDto,
    isServerRunning?: boolean
    onSubmit: (values: Arma3ServerDto) => void,
    onCancel: () => void
}

const EditArma3ServerSettingsForm = (props: EditArma3ServerSettingsFormProps) => {

    const handleSubmit = (values: Arma3ServerDto) => {
        props.onSubmit(values);
    }

    const formik = useFormik<Arma3ServerDto>({
        initialValues: props.server,
        onSubmit: handleSubmit,
        enableReinitialize: true
    });

    const mediaQuery = useMediaQuery('(min-width:600px)');

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
                    <CustomTextField id='additionalOptions' label='Additional options' multiline
                                     formik={formik} containerMd={12}/>

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