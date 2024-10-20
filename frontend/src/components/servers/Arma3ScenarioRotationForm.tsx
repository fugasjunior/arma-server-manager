import { Autocomplete, AutocompleteValue, Avatar, Badge, Box, Button, Card, Grid, IconButton, List, ListItem, ListItemAvatar, ListItemText, Stack, TextField, Typography } from "@mui/material";
import { Arma3ScenarioDto } from "../../dtos/Arma3ScenarioDto";
import { Arma3ServerDto } from "../../dtos/ServerDto";
import { getScenarios } from "../../services/scenarioService";
import { useEffect, useState } from "react";
import { FormikProps } from "formik";
import { Add, Delete, Warning } from "@mui/icons-material";

type Arma3ScenarioRotationFormProps = {
    formik: FormikProps<Arma3ServerDto>
}

export const Arma3ScenarioRotationForm = ({ formik }: Arma3ScenarioRotationFormProps) => {
    const [scenarios, setScenarios] = useState<Array<Arma3ScenarioDto>>([]);
    const [selectedScenario, setSelectedScenario] = useState<string>();

    useEffect(() => {
        const fetchScenarios = async () => {
            const { data: scenariosDto } = await getScenarios();
            setScenarios(scenariosDto.scenarios);
        };

        fetchScenarios();
        setSelectedScenario("");
    }, []);

    const setScenarioAutocomplete = (_: any, value: AutocompleteValue<Arma3ScenarioDto | string, false, false, true> | null): void => {
        if (!value) {
            return;
        }
        setSelectedScenario(typeof value === "string" ? value : value.name);
    };

    const addScenario = (scenarioName: string) => {
        if (!scenarioName) {
            return;
        }

        formik.setFieldValue("scenarios", [...formik.values.scenarios, scenarioName]);
    }

    const removeScenario = (scenarioName: string) => {
        if (!scenarioName) {
            return;
        }

        formik.setFieldValue("scenarios", [...formik.values.scenarios].filter(scenario => scenario !== scenarioName));
    }

    const getScenarioDisplayName = (scenario: Arma3ScenarioDto) => {
        return scenario.name; // TOOD: Can make pretty later
    };

    const handleBlur = (e: React.FocusEvent<HTMLInputElement>) => {
        setScenarioAutocomplete(null, e.target.value);
    };

    return (
        <Grid container spacing={1}>
            <Grid item xs={12}>
                <Typography>Scenario rotation</Typography>
            </Grid>
            <Grid item xs={12}>
                <Stack
                    direction="row"
                    justifyContent="space-between"
                    alignItems="center"
                    spacing={1}
                    sx={{ flexGrow: 1 }}>
                    <Autocomplete // TODO: Move this to it's own component?
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
                        onChange={setScenarioAutocomplete}
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
                    />

                    <Button
                        variant="contained"
                        startIcon={<Add />}
                        onClick={() => addScenario(selectedScenario ?? "")}
                        disabled={Boolean(formik.values.scenarios.find((scenario) => scenario === selectedScenario))}>
                        Add
                    </Button>
                </Stack>
            </Grid>
            <Grid item xs={12}>
                {formik.values.scenarios.length > 0 &&
                    <Card>
                        <List>
                            {formik.values.scenarios.map((value) => {
                                return (
                                    <ListItem key={value}
                                        secondaryAction={
                                            <IconButton
                                                edge="end"
                                                aria-label="delete"
                                                onClick={() => removeScenario(value)}>
                                                <Delete />
                                            </IconButton>
                                        }>
                                        <ListItemText primary={value} />
                                    </ListItem>
                                )
                            })}
                        </List>
                    </Card>
                }
            </Grid>
        </Grid>
    );
};

