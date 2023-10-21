import {useFormik} from "formik";
import {FormGroup, Grid, Typography} from "@mui/material";
import Arma3DifficultySettingsForm from "./difficulty/Arma3DifficultySettingsForm.tsx";
import {Arma3ServerDto} from "../../dtos/ServerDto.ts";
import {SwitchField} from "../../UI/Form/SwitchField.tsx";
import {CustomTextField} from "../../UI/Form/CustomTextField.tsx";
import {ServerSettingsFormControls} from "./ServerSettingsFormControls.tsx";
import Arma3NetworkSettingsForm from "./Arma3NetworkSettingsForm.tsx";
import {MuiChipsInput} from "mui-chips-input";
import {useState} from "react";

type EditArma3ServerSettingsFormProps = {
    server: Arma3ServerDto,
    isServerRunning?: boolean
    onSubmit: (values: Arma3ServerDto) => void,
    onCancel: () => void
}

const EditArma3ServerSettingsForm = (props: EditArma3ServerSettingsFormProps) => {

    const convertLaunchParameters = () => {
        return props.server.customLaunchParameters
            .map(param => `-${param.name}${param.value ? ('=' + param.value) : ''}`);
    }

    const [launchParameters, setLaunchParameters] = useState(convertLaunchParameters());

    const handleSubmit = (values: Arma3ServerDto) => {
        values.customLaunchParameters = launchParameters.map(param => {
            let split = param.slice(1).split("=");
            return {
                name: split[0],
                value: split.length > 0 ? split[1] : null
            };
        })
        props.onSubmit(values);
    }

    const formik = useFormik<Arma3ServerDto>({
        initialValues: props.server,
        onSubmit: handleSubmit,
        enableReinitialize: true
    });

    const handleParameterInput = (newParameters: Array<string>) => {
        const formattedParameters = newParameters
            .filter(param => param)
            .map(param => {
                const split = param.trim().split(/\s+|=/);
                const name = split[0].startsWith("-") ? split[0] : "-" + split[0];

                if (split.length === 1) {
                    return name;
                }

                return `${name}=${split.slice(1).join(' ')}`;
            });

        setLaunchParameters(formattedParameters);
    };

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
                        <Typography>Additional launch parameters</Typography>
                        <MuiChipsInput value={launchParameters} onChange={handleParameterInput}/>
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