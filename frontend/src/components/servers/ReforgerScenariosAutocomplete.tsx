import {ReforgerScenarioDto} from "../../api/generated";
import {scenariosApi} from "../../api/client";
import {Autocomplete, AutocompleteValue, Box, TextField} from "@mui/material";
import React, {SyntheticEvent, useEffect, useState} from "react";
import {FormikState} from "formik";
import {ReforgerServerDto} from "../../api/serverModels";

type ReforgerScenariosAutocompleteProps = {
    onChange: (_: SyntheticEvent | null, value: AutocompleteValue<ReforgerScenarioDto, false, false, true> | null) => void,
    formik: FormikState<ReforgerServerDto>
};

export function ReforgerScenariosAutocomplete({onChange, formik}: ReforgerScenariosAutocompleteProps) {
    const [scenarios, setScenarios] = useState<Array<ReforgerScenarioDto>>([]);

    useEffect(() => {
        const fetchScenarios = async () => {
            const {data: scenariosDto} = await scenariosApi.getReforgerScenarios();
            setScenarios(scenariosDto.scenarios ?? []);
        };

        fetchScenarios();
    }, []);
    const getScenarioDisplayName = (scenario: ReforgerScenarioDto) => {
        if (scenario.name) {
            return `${scenario.name} (${scenario.value})`;
        }
        return scenario.value;
    };

    const handleBlur = (e: React.FocusEvent<HTMLInputElement>) => {
        onChange(null, e.target.value);
    };

    return <Autocomplete
        id="scenario-select"
        fullWidth
        freeSolo
        selectOnFocus
        onBlur={handleBlur}
        handleHomeEndKeys
        options={scenarios}
        autoHighlight
        getOptionLabel={(option) => {
            if (typeof option === "string")
                return option;
            return option.value ?? '';
        }}
        value={formik.values.scenarioId}
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