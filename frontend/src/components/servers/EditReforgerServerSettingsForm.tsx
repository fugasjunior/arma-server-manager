import React from "react";
import {useForm, FormProvider} from "react-hook-form";
import {AutocompleteValue, FormGroup, Grid} from "@mui/material";
import {ReforgerScenarioDto} from "../../api/generated";
import {ReforgerServerDto} from "../../api/serverModels";
import {ReforgerScenariosAutocomplete} from "./ReforgerScenariosAutocomplete.tsx";
import {SwitchField} from "../../UI/Form/SwitchField.tsx";
import {CustomTextField} from "../../UI/Form/CustomTextField.tsx";
import {ServerSettingsFormControls} from "./ServerSettingsFormControls.tsx";
import {CustomLaunchParametersInput} from "./CustomLaunchParametersInput.tsx";
import {useEffect, useState} from "react";

type EditReforgerServerSettingsFormProps = {
    server: ReforgerServerDto,
    isServerRunning?: boolean,
    onSubmit: (values: ReforgerServerDto) => void,
    onCancel: () => void
}

export default function EditReforgerServerSettingsForm(props: EditReforgerServerSettingsFormProps) {

    const [launchParameters, setLaunchParameters] = useState([...(props.server.customLaunchParameters ?? [])]);
    const methods = useForm<ReforgerServerDto>({
        defaultValues: props.server
    });

    useEffect(() => {
        methods.reset(props.server);
    }, [props.server, methods]);

    function handleSubmit(values: ReforgerServerDto) {
        values.customLaunchParameters = [...launchParameters];
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
        <div>
            <FormProvider {...methods}>
                <form onSubmit={methods.handleSubmit(handleSubmit)}>
                    <Grid container spacing={3}>
                        <CustomTextField name='name' label='Server name' required/>
                        <CustomTextField name='description' label='Description'/>
                        <CustomTextField name='port' label='Port' type='number'/>
                        <CustomTextField name='queryPort' label='Query port' type='number'/>
                        <Grid size={{xs: 12, md: 6}}>
                            <ReforgerScenariosAutocomplete onChange={setScenario}/>
                        </Grid>
                        <CustomTextField name='maxPlayers' label='Max players' required type='number'/>
                        <CustomTextField name='password' label='Password'/>
                        <CustomTextField name='adminPassword' label='Admin password' required/>
                        <Grid size={12}>
                            <FormGroup>
                                <SwitchField name='battlEye' label='BattlEye enabled'/>
                                <SwitchField name='thirdPersonViewEnabled' label='Third person view enabled'/>
                            </FormGroup>
                        </Grid>
                        <Grid size={12}>
                            <CustomLaunchParametersInput
                                valueDelimiter=' '
                                parameters={launchParameters}
                                onParametersChange={setLaunchParameters}
                            />
                        </Grid>
                        <ServerSettingsFormControls serverRunning={props.isServerRunning} onCancel={props.onCancel}/>
                    </Grid>
                </form>
            </FormProvider>
        </div>
    );
}
