import React from "react";
import {useForm, FormProvider} from "react-hook-form";
import {AutocompleteValue, Box, FormGroup, Grid, Typography} from "@mui/material";
import {ReforgerScenarioDto, ServerDto} from "../../api/generated";
import {ReforgerServerDto} from "../../api/serverModels";
import {ReforgerScenariosAutocomplete} from "./ReforgerScenariosAutocomplete.tsx";
import {SwitchField} from "../../UI/Form/SwitchField.tsx";
import {CustomTextField} from "../../UI/Form/CustomTextField.tsx";
import {ServerSettingsFormControls} from "./ServerSettingsFormControls.tsx";
import {CustomLaunchParametersInput} from "./CustomLaunchParametersInput.tsx";
import {useEffect, useState} from "react";
import PermissionGuard from "../auth/PermissionGuard";
import {usePermission} from "../../hooks/usePermission";
import AdvancedConfigSection from "./AdvancedConfigSection";
import AdvancedConfigToggle from "./AdvancedConfigToggle";
import {useConfigOverrides} from "../../hooks/useConfigOverrides";

type EditReforgerServerSettingsFormProps = {
    server: ReforgerServerDto,
    isServerRunning?: boolean,
    onSubmit: (values: ReforgerServerDto) => void,
    onCancel: () => void
}

export default function EditReforgerServerSettingsForm(props: EditReforgerServerSettingsFormProps) {

    const canModify = usePermission("SERVER_MODIFY");
    const [launchParameters, setLaunchParameters] = useState([...(props.server.customLaunchParameters ?? [])]);
    const methods = useForm<ReforgerServerDto>({
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
    const configOverride = getOverride('REFORGER_JSON');

    useEffect(() => {
        methods.reset(props.server);
    }, [props.server, methods]);

    function handleSubmit(values: ReforgerServerDto) {
        values.customLaunchParameters = [...launchParameters];
        values.configOverrides = configOverrides.length > 0 ? configOverrides : undefined;
        props.onSubmit(values);
    }

    const setScenario = (_: React.SyntheticEvent | null, value: AutocompleteValue<ReforgerScenarioDto | string, false, false, true> | null): void => {
        console.log(_, value);
        if (!value) {
            return;
        }
        methods.setValue("scenarioId", typeof value === "string" ? value : value.value);
    };

    return (
        <Box sx={{mt: 3}}>
            <FormProvider {...methods}>
                <form onSubmit={methods.handleSubmit(handleSubmit)}>
                    <fieldset disabled={!canModify} style={{border: "none", padding: 0, margin: 0, minInlineSize: "auto"}}>
                        <Box sx={{display: 'flex', justifyContent: 'flex-end', mb: 2}}>
                            <AdvancedConfigToggle
                                configKey="REFORGER_JSON"
                                switchLabel="Advanced edit (server.json)"
                                enableDialogText="This will replace the configuration form with a raw JSON text editor.
                                    Your current form settings will be used to generate an initial config.
                                    You will be responsible for keeping the raw config consistent with
                                    app-level settings (port, query port)."
                                serverDraft={serverDraft}
                                override={configOverride}
                                onOverrideChange={o => setOverride('REFORGER_JSON', o)}
                            />
                        </Box>
                        <Grid container spacing={3}>
                            <CustomTextField name='name' label='Server name' required/>
                            <CustomTextField name='description' label='Description'/>
                            <CustomTextField name='port' label='Port' type='number'/>
                            <CustomTextField name='queryPort' label='Query port' type='number'/>
                            {!configOverride ? (
                                <>
                                    <Grid size={{xs: 12, md: 6}}>
                                        <ReforgerScenariosAutocomplete onChange={setScenario}/>
                                    </Grid>
                                    <CustomTextField name='maxPlayers' label='Max players' required type='number'/>
                                    <PermissionGuard permission="SERVER_SECRETS_VIEW">
                                        <CustomTextField name='password' label='Password'/>
                                        <CustomTextField name='adminPassword' label='Admin password'/>
                                    </PermissionGuard>
                                    <Grid size={12}>
                                        <FormGroup>
                                            <SwitchField name='battlEye' label='BattlEye enabled'/>
                                            <SwitchField name='thirdPersonViewEnabled' label='Third person view enabled'/>
                                        </FormGroup>
                                    </Grid>
                                </>
                            ) : (
                                <>
                                    <Grid size={12}>
                                        <AdvancedConfigSection
                                            configKey="REFORGER_JSON"
                                            label="server.json"
                                            override={configOverride}
                                            onOverrideChange={o => setOverride('REFORGER_JSON', o)}
                                        />
                                    </Grid>
                                    <Grid size={12}>
                                        <Typography variant="body2" color="text.secondary" sx={{mt: 1}}>
                                            Mods are managed through the raw JSON config (game.mods).
                                            The mod management interface is disabled while advanced config is active.
                                            Port and query port are read from the fields above for launch parameters;
                                            keep them consistent with bindPort and a2s.port values in the JSON.
                                        </Typography>
                                    </Grid>
                                </>
                            )}
                            <Grid size={12}>
                                <CustomLaunchParametersInput
                                    valueDelimiter=' '
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
}
