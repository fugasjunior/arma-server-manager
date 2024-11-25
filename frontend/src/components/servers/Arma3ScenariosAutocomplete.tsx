import { Autocomplete, AutocompleteValue, Box, TextField } from "@mui/material";
import React, { useEffect, useState } from "react";
import { Arma3ScenarioDto } from "../../dtos/Arma3ScenarioDto.ts";
import { getScenarios } from "../../services/scenarioService.ts";

type Arma3ScenariosAutocompleteProps = {
    onChange: (_: any, value: AutocompleteValue<Arma3ScenarioDto | string, false, false, true> | null) => void,
};

export function Arma3ScenariosAutocomplete({onChange}: Arma3ScenariosAutocompleteProps) {
    const [scenarios, setScenarios] = useState<Array<Arma3ScenarioDto>>([]);

    useEffect(() => {
        const fetchScenarios = async () => {
            const { data: scenariosDto } = await getScenarios();
            setScenarios(scenariosDto.scenarios);
        };

        fetchScenarios();
        onChange(null, "");
    }, []);

    const getScenarioDisplayName = (scenario: Arma3ScenarioDto) => {
        return scenario.name; // TOOD: Can make pretty later
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
            return option.name;
        }}
        onChange={onChange}
        renderOption={(props, option) => (
            <Box component="li"  {...props}>
                {getScenarioDisplayName(option)}
            </Box>
        )}
        renderInput={(params) => (
            <TextField
                {...params}
                name="scenarioName"
                label="Scenario name"
                inputProps={{
                    ...params.inputProps,
                    autoComplete: 'new-password'
                }}
            />
        )}
    />;
}