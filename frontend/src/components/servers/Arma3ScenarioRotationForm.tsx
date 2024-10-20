import { Add, Delete } from "@mui/icons-material";
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

