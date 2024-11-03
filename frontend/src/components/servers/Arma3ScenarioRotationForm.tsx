import { Add, Close, KeyboardArrowDown, KeyboardArrowUp } from "@mui/icons-material";
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

    const getSplitScenarios = () => {
        return formik.values.scenarioRotation.split(";").filter(scenario => scenario.trim().length > 0);
    }

    const setScenarios = (scenarios: string[]) => {
        formik.setFieldValue("scenarioRotation", scenarios.join(";"));
    }

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

        setScenarios([...getSplitScenarios(), scenarioName]);
    }

    const removeScenario = (scenarioName: string) => {
        if (!scenarioName) {
            return;
        }

        setScenarios(getSplitScenarios().filter(scenario => scenario !== scenarioName));
    }

    const shiftScenarioUp = (index: number) => {
        let scenarios = getSplitScenarios();
        if (index <= 0 || index >= scenarios.length) {
            return;
        }

        let temp = scenarios[index];
        scenarios[index] = scenarios[index - 1];
        scenarios[index - 1] = temp;

        setScenarios(scenarios);
    }

    const shiftScenarioDown = (index: number) => {
        let scenarios = getSplitScenarios();
        if (index < 0 || index >= scenarios.length - 1) {
            return;
        }

        let temp = scenarios[index];
        scenarios[index] = scenarios[index + 1];
        scenarios[index + 1] = temp;

       setScenarios(scenarios);
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
                        disabled={selectedScenario ? Boolean(getSplitScenarios().find((scenario) => scenario === selectedScenario)) : true}>
                        Add
                    </Button>
                </Stack>
            </Grid>
            <Grid item xs={12}>
                {getSplitScenarios().length > 0 &&
                    <Card>
                        <List>
                            {getSplitScenarios().map((value, i) => {
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
                                                    disabled={i >= getSplitScenarios().length - 1}
                                                    aria-label="shift-down"
                                                    onClick={() => shiftScenarioDown(i)}>
                                                    <KeyboardArrowDown />
                                                </IconButton>
                                                <IconButton
                                                    aria-label="remove"
                                                    onClick={() => removeScenario(value)}>
                                                    <Close />
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

