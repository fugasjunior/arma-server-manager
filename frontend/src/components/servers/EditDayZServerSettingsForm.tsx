import {useForm, FormProvider} from "react-hook-form";
import {Box, FormGroup, Grid} from "@mui/material";
import {DayZServerDto} from "../../api/serverModels";
import {SwitchField} from "../../UI/Form/SwitchField.tsx";
import {CustomTextField} from "../../UI/Form/CustomTextField.tsx";
import {CustomCodeEditorField} from "../../UI/Form/CustomCodeEditorField.tsx";
import {ServerSettingsFormControls} from "./ServerSettingsFormControls.tsx";
import {CustomLaunchParametersInput} from "./CustomLaunchParametersInput.tsx";
import {useEffect, useState} from "react";
import PermissionGuard from "../auth/PermissionGuard";
import {ServerDto} from "../../api/generated";
import AdvancedConfigSection from "./AdvancedConfigSection";
import AdvancedConfigToggle from "./AdvancedConfigToggle";
import {useConfigOverrides} from "../../hooks/useConfigOverrides";
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

    const serverDraft = (() => {
        const draft = {...methods.watch()} as unknown as ServerDto;
        if (!draft.id && !draft.name) {
            draft.name = '(new server)';
        }
        return draft;
    })();

    const {configOverrides, getOverride, setOverride} = useConfigOverrides(props.server);
    const configOverride = getOverride('DAYZ_SERVER_CFG');

    useEffect(() => {
        methods.reset(props.server);
    }, [props.server, methods]);

    const handleSubmit = (values: DayZServerDto) => {
        values.customLaunchParameters = [...launchParameters];
        values.configOverrides = configOverrides.length > 0 ? configOverrides : undefined;
        props.onSubmit(values);
    }

    return (
        <Box sx={{mt: 3}}>
            <FormProvider {...methods}>
                <form onSubmit={methods.handleSubmit(handleSubmit)}>
                    <fieldset disabled={!canModify} style={{border: "none", padding: 0, margin: 0, minInlineSize: "auto"}}>
                        <Box sx={{display: 'flex', justifyContent: 'flex-end', mb: 2}}>
                            <AdvancedConfigToggle
                                configKey="DAYZ_SERVER_CFG"
                                switchLabel="Advanced edit (server.cfg)"
                                enableDialogText="This will replace the configuration form with a raw text editor.
                                    Your current form settings will be used to generate an initial config.
                                    You will be responsible for keeping the raw config consistent with
                                    app-level settings (port, query port)."
                                serverDraft={serverDraft}
                                override={configOverride}
                                onOverrideChange={o => setOverride('DAYZ_SERVER_CFG', o)}
                            />
                        </Box>
                        <Grid container spacing={3}>
                            <CustomTextField name='name' label='Server name' required/>
                            <CustomTextField name='description' label='Description'/>
                            <CustomTextField name='port' label='Port' required type='number'/>
                            <CustomTextField name='queryPort' label='Query port' required type='number'/>
                            {!configOverride ? (
                                <>
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
                                    <CustomCodeEditorField name='additionalOptions' label='Additional options' containerMd={12}/>
                                </>
                            ) : (
                                <Grid size={12}>
                                    <AdvancedConfigSection
                                        configKey="DAYZ_SERVER_CFG"
                                        label="server.cfg"
                                        override={configOverride}
                                        onOverrideChange={o => setOverride('DAYZ_SERVER_CFG', o)}
                                    />
                                </Grid>
                            )}
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
