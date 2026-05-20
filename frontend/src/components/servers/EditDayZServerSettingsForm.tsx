import {useForm, FormProvider} from "react-hook-form";
import {FormGroup, Grid, Box} from "@mui/material";
import {DayZServerDto} from "../../api/serverModels";
import {SwitchField} from "../../UI/Form/SwitchField.tsx";
import {CustomTextField} from "../../UI/Form/CustomTextField.tsx";
import {ServerSettingsFormControls} from "./ServerSettingsFormControls.tsx";
import {CustomLaunchParametersInput} from "./CustomLaunchParametersInput.tsx";
import {useEffect, useState} from "react";
import PermissionGuard from "../auth/PermissionGuard";
import {usePermission} from "../../hooks/usePermission";

type EditDayZServerSettingsFormProps = {
    server: DayZServerDto,
    isServerRunning?: boolean,
    onSubmit: (values: DayZServerDto) => void,
    onCancel: () => void
}

const EditDayZServerSettingsForm = (props: EditDayZServerSettingsFormProps) => {

    const canModify = usePermission("SERVER_MODIFY");
    const [launchParameters, setLaunchParameters] = useState([...(props.server.customLaunchParameters ?? [])]);
    const methods = useForm<DayZServerDto>({
        defaultValues: props.server
    });

    useEffect(() => {
        methods.reset(props.server);
    }, [props.server, methods]);

    const handleSubmit = (values: DayZServerDto) => {
        values.customLaunchParameters = [...launchParameters];
        props.onSubmit(values);
    }

    return (
        <Box sx={{mt: 3}}>
            <FormProvider {...methods}>
                <form onSubmit={methods.handleSubmit(handleSubmit)}>
                    <fieldset disabled={!canModify} style={{border: "none", padding: 0, margin: 0, minInlineSize: "auto"}}>
                        <Grid container spacing={3}>
                            <CustomTextField name='name' label='Server name' required/>
                            <CustomTextField name='description' label='Description'/>
                            <CustomTextField name='port' label='Port' required type='number'/>
                            <CustomTextField name='queryPort' label='Query port' required type='number'/>
                            <CustomTextField name='maxPlayers' label='Max players' required type='number'/>
                            <PermissionGuard permission="SERVER_SECRETS_VIEW">
                                <CustomTextField name='password' label='Server password'/>
                                <CustomTextField name='adminPassword' label='Admin password'/>
                            </PermissionGuard>
                            <CustomTextField name='respawnTime' label='Respawn time (seconds)' type='number'
                                             inputProps={{min: "0"}}/>
                            <CustomTextField name='timeAcceleration' label='Time acceleration' type='number'
                                             inputProps={{step: "0.1", min: "0.1", max: "64"}}/>
                            <CustomTextField name='nightTimeAcceleration' label='Night time acceleration' type='number'
                                             inputProps={{step: "0.1", min: "0.1", max: "64"}}/>
                            <Grid size={6}>
                                <FormGroup>
                                    <SwitchField name='vonEnabled' label='VON enabled'/>
                                    <SwitchField name='persistent' label='Persistent server time'/>
                                    <SwitchField name='clientFilePatching' label='Client file patching'/>
                                </FormGroup>
                            </Grid>
                            <Grid size={6}>
                                <FormGroup>
                                    <SwitchField name='forceSameBuild' label='Force same build'/>
                                    <SwitchField name='thirdPersonViewEnabled' label='Third person view enabled'/>
                                    <SwitchField name='crosshairEnabled' label='Crosshair enabled'/>
                                </FormGroup>
                            </Grid>
                            <CustomTextField name='additionalOptions' label='Additional options' multiline
                                             containerMd={12}/>
                            <Grid size={12}>
                                <CustomLaunchParametersInput
                                    valueDelimiter='='
                                    parameters={launchParameters}
                                    onParametersChange={setLaunchParameters}
                                    canModify={canModify}
                                />
                            </Grid>
                        </Grid>
                    </fieldset>
                    <Grid container spacing={3} sx={{mt: 3}}>
                        <ServerSettingsFormControls serverRunning={props.isServerRunning} onCancel={props.onCancel} canModify={canModify}/>
                    </Grid>
                </form>
            </FormProvider>
        </Box>
    );
};

export default EditDayZServerSettingsForm;