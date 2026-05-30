import {useForm, FormProvider} from "react-hook-form";
import {FormGroup, Grid, Box} from "@mui/material";
import Arma3DifficultySettingsForm from "./difficulty/Arma3DifficultySettingsForm.tsx";
import {Arma3NetworkSettingsDto} from "../../api/generated";
import {Arma3ServerDto} from "../../api/serverModels";
import {SwitchField} from "../../UI/Form/SwitchField.tsx";
import {CustomTextField} from "../../UI/Form/CustomTextField.tsx";
import {ServerSettingsFormControls} from "./ServerSettingsFormControls.tsx";
import Arma3NetworkSettingsForm from "./Arma3NetworkSettingsForm.tsx";
import {useEffect, useState} from "react";
import PermissionGuard from "../auth/PermissionGuard";
import {CustomLaunchParametersInput} from "./CustomLaunchParametersInput.tsx";
import {usePermission} from "../../hooks/usePermission";

type EditArma3ServerSettingsFormProps = {
    server: Arma3ServerDto,
    isServerRunning?: boolean
    onSubmit: (values: Arma3ServerDto) => void,
    onCancel: () => void
}

const EditArma3ServerSettingsForm = (props: EditArma3ServerSettingsFormProps) => {

    const canModify = usePermission("SERVER_MODIFY");
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
        <Box sx={{mt: 3}}>
            <FormProvider {...methods}>
                <form onSubmit={methods.handleSubmit(handleSubmit)}>
                    <Grid container spacing={3}>
                            <CustomTextField name='name' label='Server name' required disabled={!canModify}/>
                            <CustomTextField name='description' label='Description' disabled={!canModify}/>
                            <CustomTextField name='port' label='Port' required type='number' disabled={!canModify}/>
                            <CustomTextField name='maxPlayers' label='Max players' required type='number' disabled={!canModify}/>
                            <PermissionGuard permission="SERVER_SECRETS_VIEW">
                                <CustomTextField name='password' label='Server password' disabled={!canModify}/>
                                <CustomTextField name='adminPassword' label='Admin password' disabled={!canModify}/>
                            </PermissionGuard>
                            <Grid size={6}>
                                <FormGroup>
                                    <SwitchField name='clientFilePatching' label='Client file patching' disabled={!canModify}/>
                                    <SwitchField name='serverFilePatching' label='Server file patching' disabled={!canModify}/>
                                    <SwitchField name='verifySignatures' label='Verify signatures' disabled={!canModify}/>
                                </FormGroup>
                            </Grid>
                            <Grid size={6}>
                                <FormGroup>
                                    <SwitchField name='vonEnabled' label='VON enabled' disabled={!canModify}/>
                                    <SwitchField name='battlEye' label='BattlEye enabled' disabled={!canModify}/>
                                    <SwitchField name='persistent' label='Persistent' disabled={!canModify}/>
                                </FormGroup>
                            </Grid>
                            <CustomTextField name='targetHeadlessClientsCount' label='Target headless clients'
                                             type='number'
                                             containerMd={6} disabled={!canModify}/>
                            <CustomTextField name='additionalOptions' label='Additional options' multiline
                                             containerMd={12} disabled={!canModify}/>
                        <Grid size={12}>
                            <CustomLaunchParametersInput
                                valueDelimiter='='
                                parameters={launchParameters}
                                onParametersChange={setLaunchParameters}
                                canModify={canModify}
                            />
                        </Grid>
                        <Grid size={12}>
                            <Arma3DifficultySettingsForm canModify={canModify}/>
                        </Grid>
                        <Grid size={12}>
                            <Arma3NetworkSettingsForm canModify={canModify}/>
                        </Grid>
                    </Grid>
                    <Grid container spacing={3} sx={{mt: 3}}>
                        <ServerSettingsFormControls serverRunning={props.isServerRunning} onCancel={props.onCancel}
                                                    canModify={canModify}/>
                    </Grid>
                </form>
            </FormProvider>
        </Box>
    );
};

export default EditArma3ServerSettingsForm;