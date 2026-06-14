import {useForm, FormProvider} from "react-hook-form";
import {Box, FormGroup, Grid} from "@mui/material";
import Arma3DifficultySettingsForm from "./difficulty/Arma3DifficultySettingsForm.tsx";
import {Arma3NetworkSettingsDto, ServerDto} from "../../api/generated";
import {Arma3ServerDto} from "../../api/serverModels";
import {SwitchField} from "../../UI/Form/SwitchField.tsx";
import {CustomTextField} from "../../UI/Form/CustomTextField.tsx";
import {CustomCodeEditorField} from "../../UI/Form/CustomCodeEditorField.tsx";
import {ServerSettingsFormControls} from "./ServerSettingsFormControls.tsx";
import Arma3NetworkSettingsForm from "./Arma3NetworkSettingsForm.tsx";
import {useEffect, useState} from "react";
import PermissionGuard from "../auth/PermissionGuard";
import {CustomLaunchParametersInput} from "./CustomLaunchParametersInput.tsx";
import {usePermission} from "../../hooks/usePermission";
import AdvancedConfigSection from "./AdvancedConfigSection";
import AdvancedConfigToggle from "./AdvancedConfigToggle";
import {useConfigOverrides} from "../../hooks/useConfigOverrides";

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

    const serverDraft = (() => {
        const draft = {...methods.watch()} as unknown as ServerDto;
        if (!draft.id && !draft.name) {
            draft.name = '(new server)';
        }
        return draft;
    })();

    const {configOverrides, getOverride, setOverride} = useConfigOverrides(props.server);
    const serverCfgOverride = getOverride('ARMA3_SERVER_CFG');
    const profileOverride = getOverride('ARMA3_PROFILE');
    const networkOverride = getOverride('ARMA3_NETWORK_CFG');

    useEffect(() => {
        methods.reset(props.server);
    }, [props.server, methods]);

    const handleSubmit = (values: Arma3ServerDto) => {
        if (areAllPropertiesNull(values.networkSettings)) {
            values.networkSettings = undefined;
        }
        values.customLaunchParameters = [...launchParameters];
        values.configOverrides = configOverrides.length > 0 ? configOverrides : undefined;
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
                    <fieldset disabled={!canModify} style={{border: "none", padding: 0, margin: 0, minInlineSize: "auto"}}>
                        <Box sx={{display: 'flex', justifyContent: 'flex-end', mb: 2}}>
                            <AdvancedConfigToggle
                                configKey="ARMA3_SERVER_CFG"
                                switchLabel="Advanced edit (server.cfg)"
                                enableDialogText="This will replace the server config form fields with a raw text editor.
                                    Your current form settings will be used to generate an initial config."
                                serverDraft={serverDraft}
                                override={serverCfgOverride}
                                onOverrideChange={o => setOverride('ARMA3_SERVER_CFG', o)}
                            />
                        </Box>
                        <Grid container spacing={3}>
                            <CustomTextField name='name' label='Server name' required/>
                            <CustomTextField name='description' label='Description'/>
                            <CustomTextField name='port' label='Port' required type='number'/>
                            {!serverCfgOverride ? (
                                <>
                                    <CustomTextField name='maxPlayers' label='Max players' required type='number'/>
                                    <PermissionGuard permission="SERVER_SECRETS_VIEW">
                                        <CustomTextField name='password' label='Server password'/>
                                        <CustomTextField name='adminPassword' label='Admin password'/>
                                    </PermissionGuard>
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
                                    <CustomCodeEditorField name='additionalOptions' label='Additional options' containerMd={12}/>
                                </>
                            ) : (
                                <Grid size={12}>
                                    <AdvancedConfigSection
                                        configKey="ARMA3_SERVER_CFG"
                                        label="server.cfg"
                                        override={serverCfgOverride}
                                        onOverrideChange={o => setOverride('ARMA3_SERVER_CFG', o)}
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
                            <Grid size={12}>
                                <Arma3DifficultySettingsForm
                                    canModify={canModify}
                                    serverDraft={serverDraft}
                                    override={profileOverride}
                                    onOverrideChange={o => setOverride('ARMA3_PROFILE', o)}
                                />
                            </Grid>
                            <Grid size={12}>
                                <Arma3NetworkSettingsForm
                                    canModify={canModify}
                                    serverDraft={serverDraft}
                                    override={networkOverride}
                                    onOverrideChange={o => setOverride('ARMA3_NETWORK_CFG', o)}
                                />
                            </Grid>
                        </Grid>
                    </fieldset>
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
