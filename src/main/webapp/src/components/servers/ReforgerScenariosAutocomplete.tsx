import {ReforgerScenarioDto} from "../../dtos/ReforgerScenarioDto.ts";
import {Autocomplete, AutocompleteValue, Box, TextField} from "@mui/material";
import {useEffect, useState} from "react";
import {getReforgerScenarios} from "../../services/scenarioService.ts";
import {FormikState} from "formik";
import {ReforgerServerDto} from "../../dtos/ServerDto.ts";

type ReforgerScenariosAutocompleteProps = {
    onChange: (_: any, value: AutocompleteValue<ReforgerScenarioDto, false, false, false> | null) => void,
    formik: FormikState<ReforgerServerDto>
};

export function ReforgerScenariosAutocomplete({onChange, formik}: ReforgerScenariosAutocompleteProps) {
    const [scenarios, setScenarios] = useState<Array<ReforgerScenarioDto>>([]);

    useEffect(() => {
        const fetchScenarios = async () => {
            const {data: scenariosDto} = await getReforgerScenarios();
            setScenarios(scenariosDto.scenarios);
        };

        fetchScenarios();
    }, []);

    const findSelectedScenario = (): ReforgerScenarioDto => {
        let selectedScenarioId = formik.values.scenarioId;
        const scenario = scenarios.find(scenario => scenario.value === selectedScenarioId);
        if (!scenario) {
            return {name: "", value: selectedScenarioId, isOfficial: true};
        }
        return scenario;
    };

    const getScenarioDisplayName = (scenario: ReforgerScenarioDto) => {
        if (scenario.name) {
            return `${scenario.name} (${scenario.value})`;
        }
        return scenario.value;
    };

    return <Autocomplete
        id="scenario-select"
        fullWidth
        options={scenarios}
        autoHighlight
        getOptionLabel={(option) => option.value}
        value={findSelectedScenario()}
        onChange={onChange}
        renderOption={(props, option) => (
            <Box component="li"  {...props}>
                {getScenarioDisplayName(option)}
            </Box>
        )}
        renderInput={(params) => (
            <TextField
                {...params}
                required
                id="scenarioId"
                name="scenarioId"
                label="Scenario ID"
                value={formik.values.scenarioId}
                error={formik.touched.scenarioId && Boolean(
                    formik.errors.scenarioId)}
                inputProps={{
                    ...params.inputProps,
                    autoComplete: 'new-password'
                }}
            />
        )}
    />;
}