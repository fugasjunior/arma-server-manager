import { Add, ArrowDropDown, Close, KeyboardArrowDown, KeyboardArrowUp } from "@mui/icons-material";
import { Accordion, AccordionDetails, AccordionSummary, AutocompleteValue, Button, Card, Chip, Grid, IconButton, Stack, TextField, Typography } from "@mui/material";
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
                        {getSplitScenarios().map((value, i) => {
                            return (
                                <Accordion >
                                    <AccordionSummary expandIcon={<ArrowDropDown />} >
                                        <Stack direction="row" alignItems="center" justifyContent="space-between" flex="1" paddingRight={1}>
                                            <Typography>{value}</Typography>
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
                                        </Stack>
                                    </AccordionSummary>
                                    <AccordionDetails>
                                        <Grid container spacing={1}>
                                            <Grid item xs={5}>
                                                <TextField
                                                    fullWidth
                                                    id="param-name"
                                                    type="text"
                                                    label="Param name"
                                                    size="small">
                                                </TextField>
                                            </Grid>
                                            <Grid item xs={5}>
                                                <TextField
                                                    fullWidth
                                                    id="param-value"
                                                    type="number"
                                                    label="Param value"
                                                    size="small">
                                                </TextField>
                                            </Grid>
                                            <Grid item xs={2}>
                                                <Button aria-label="add" variant="contained" startIcon={<Add />}>
                                                    <Typography>Add</Typography>
                                                </Button>
                                            </Grid>
                                            <Grid item xs={12}>
                                                <Stack direction="row" spacing={1} rowGap={1} flexWrap="wrap" >
                                                    {[
                                                        {name: "testParam1", value: 123}, 
                                                        {name: "test2", value: 1}, 
                                                        {name: "testParameterLong3", value: 1512}
                                                    ].map((param) => {
                                                        return (
                                                            <Chip label={`${param.name} = ${param.value}`} onDelete={() => {}} />
                                                        );
                                                    })}
                                                </Stack>
                                            </Grid>
                                        </Grid>
                                    </AccordionDetails>
                                </Accordion>
                            )
                        })}
                    </Card>
                }
            </Grid>
        </Grid>
    );
};

