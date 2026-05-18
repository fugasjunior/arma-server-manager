import {useForm, FormProvider} from "react-hook-form";
import {FormGroup, Grid} from "@mui/material";
import Arma3DifficultySettingsForm from "./difficulty/Arma3DifficultySettingsForm.tsx";
import {Arma3NetworkSettingsDto} from "../../api/generated";
import {Arma3ServerDto} from "../../api/serverModels";
import {SwitchField} from "../../UI/Form/SwitchField.tsx";
import {CustomTextField} from "../../UI/Form/CustomTextField.tsx";
import {ServerSettingsFormControls} from "./ServerSettingsFormControls.tsx";
import Arma3NetworkSettingsForm from "./Arma3NetworkSettingsForm.tsx";
import {useEffect, useState} from "react";
import {CustomLaunchParametersInput} from "./CustomLaunchParametersInput.tsx";

type EditArma3ServerSettingsFormProps = {
    server: Arma3ServerDto,
    isServerRunning?: boolean
    onSubmit: (values: Arma3ServerDto) => void,
    onCancel: () => void
}

const EditArma3ServerSettingsForm = (props: EditArma3ServerSettingsFormProps) => {

    const [launchParameters, setLaunchParameters] = useState([...(props.server.customLaunchParameters ?? [])]);
    const methods = useForm<Arma3ServerDto>({
        defaultValues: props.server
    });

    useEffect(() => {
        methods.reset(props.server);
    }, [props.server, methods]);

    const handleSubmit = (values: Arma3ServerDto) => {
        if (areAllPropertiesNull(values.networkSettings)) {
            values.networkSettings = undefined;
        }

        values.customLaunchParameters = [...launchParameters];
        props.onSubmit(values);
    }

    const areAllPropertiesNull = (networkSettings: Arma3NetworkSettingsDto | null | undefined): boolean => {
        if (networkSettings == null) {
            return true;
        }
        return Object.values(networkSettings).every(x => x === null);
    }

    return (
        <div>
            <FormProvider {...methods}>
                <form onSubmit={methods.handleSubmit(handleSubmit)}>
                    <Grid container spacing={3}>
                        <CustomTextField name='name' label='Server name' required/>
                        <CustomTextField name='description' label='Description'/>
                        <CustomTextField name='port' label='Port' required type='number'/>
                        <CustomTextField name='maxPlayers' label='Max players' required type='number'/>
                        <CustomTextField name='password' label='Server password'/>
                        <CustomTextField name='adminPassword' label='Admin password'/>
                        <Grid size={6}>
                            <FormGroup>
                                <SwitchField name='clientFilePatching' label='Client file patching'/>
                                <SwitchField name='serverFilePatching' label='Server file patching'/>
                                <SwitchField name='verifySignatures' label='Verify signatures'/>
                            </FormGroup>
                        </Grid>
                        <Grid size={6}>
                            <FormGroup>
                                <SwitchField name='vonEnabled' label='VON enabled'/>
                                <SwitchField name='battlEye' label='BattlEye enabled'/>
                                <SwitchField name='persistent' label='Persistent'/>
                            </FormGroup>
                        </Grid>
                        <CustomTextField name='targetHeadlessClientsCount' label='Target headless clients' type='number'
                                         containerMd={6}/>
                        <CustomTextField name='additionalOptions' label='Additional options' multiline
                                         containerMd={12}/>

                        <Grid size={12}>
                            <CustomLaunchParametersInput
                                valueDelimiter='='
                                parameters={launchParameters}
                                onParametersChange={setLaunchParameters}
                            />
                        </Grid>
                        <Grid size={12}>
                            <Arma3DifficultySettingsForm/>
                        </Grid>
                        <Grid size={12}>
                            <Arma3NetworkSettingsForm/>
                        </Grid>

                        <ServerSettingsFormControls serverRunning={props.isServerRunning} onCancel={props.onCancel}/>
                    </Grid>
                </form>
            </FormProvider>
        </div>
    );
};

export default EditArma3ServerSettingsForm;