import { Add, Delete, KeyboardArrowDown, KeyboardArrowUp } from "@mui/icons-material";
import { AutocompleteValue, Button, Card, Grid, IconButton, List, ListItem, ListItemText, Stack, Typography } from "@mui/material";
import { FormikProps } from "formik";
import { useState } from "react";
import { Arma3ScenarioDto } from "../../dtos/Arma3ScenarioDto";
import { Arma3ServerDto } from "../../dtos/ServerDto";
import { Arma3ScenariosAutocomplete } from "./Arma3ScenariosAutocomplete";

type Arma3ScenarioRotationFormProps = {
    formik: FormikProps<Arma3ServerDto>
}

export const Arma3ScenarioRotationForm = ({ formik }: Arma3ScenarioRotationFormProps) => {
    const [selectedScenario, setSelectedScenario] = useState<string>();

    const setScenarioAutocomplete = (_: any, value: AutocompleteValue<Arma3ScenarioDto | string, false, false, true> | null): void => {
        if (!value) {
            setSelectedScenario("");
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

    const shiftScenarioUp = (index: number) => {
        if (index <= 0 || index >= formik.values.scenarios.length) {
            return;
        }

        formik.setFieldValue("scenarios", [...formik.values.scenarios].map((scenario, i, scenarios) => {
            if (i === index) return scenarios[index - 1];
            if (i === index - 1) return scenarios[index];
            return scenario;
        }));
    }

    const shiftScenarioDown = (index: number) => {
        if (index < 0 || index >= formik.values.scenarios.length - 1) {
            return;
        }

        formik.setFieldValue("scenarios", [...formik.values.scenarios].map((scenario, i, scenarios) => {
            if (i === index) return scenarios[index + 1];
            if (i === index + 1) return scenarios[index];
            return scenario;
        }));
    }

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
                    <Arma3ScenariosAutocomplete onChange={setScenarioAutocomplete} />
                    <Button
                        variant="contained"
                        startIcon={<Add />}
                        onClick={() => addScenario(selectedScenario ?? "")}
                        disabled={selectedScenario ? Boolean(formik.values.scenarios.find((scenario) => scenario === selectedScenario)) : true}>
                        Add
                    </Button>
                </Stack>
            </Grid>
            <Grid item xs={12}>
                {formik.values.scenarios.length > 0 &&
                    <Card>
                        <List>
                            {formik.values.scenarios.map((value, i) => {
                                return (
                                    <ListItem key={value}
                                        secondaryAction={
                                            <Stack direction="row">
                                                <IconButton
                                                    disabled={i <= 0}
                                                    aria-label="shift-up"
                                                    onClick={() => shiftScenarioUp(i)}>
                                                    <KeyboardArrowUp />
                                                </IconButton>
                                                <IconButton
                                                    disabled={i >= formik.values.scenarios.length - 1}
                                                    aria-label="shift-down"
                                                    onClick={() => shiftScenarioDown(i)}>
                                                    <KeyboardArrowDown />
                                                </IconButton>
                                                <IconButton
                                                    aria-label="delete"
                                                    onClick={() => removeScenario(value)}>
                                                    <Delete />
                                                </IconButton>
                                            </Stack>
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

