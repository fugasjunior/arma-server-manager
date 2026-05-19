import {ReforgerScenarioDto} from "../../api/generated";
import {Autocomplete, AutocompleteValue, Box, TextField} from "@mui/material";
import React, {SyntheticEvent} from "react";
import {useController} from "react-hook-form";
import {useReforgerScenarios} from "../../hooks/queries/useReforgerScenarios";

type ReforgerScenariosAutocompleteProps = {
    onChange: (_: SyntheticEvent | null, value: AutocompleteValue<ReforgerScenarioDto, false, false, true> | null) => void,
};

export function ReforgerScenariosAutocomplete({onChange}: ReforgerScenariosAutocompleteProps) {
    const {field, fieldState} = useController({name: 'scenarioId'});
    const {data: scenarios = []} = useReforgerScenarios();

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
        value={field.value}
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
                value={field.value}
                error={Boolean(fieldState.error)}
                slotProps={{
                    ...params.slotProps,
                    htmlInput: {...(params.slotProps?.htmlInput as object), autoComplete: 'new-password'},
                }}
            />
        )}
    />;
}
