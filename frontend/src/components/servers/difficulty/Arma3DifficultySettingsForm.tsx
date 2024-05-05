import {Accordion, AccordionDetails, AccordionSummary, FormGroup, Grid, Typography} from "@mui/material";

import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import {FormikProps} from "formik";
import {Arma3ServerDto} from "../../../dtos/ServerDto.ts";
import {ThreeStateFlagField} from "../../../UI/Form/ThreeStateFlagField.tsx";
import {Arma3AiSkillSettings} from "./Arma3AiSkillSettings.tsx";
import {SwitchField} from "../../../UI/Form/SwitchField.tsx";
import {BOOLEAN_FIELDS, FOUR_STATE_FLAG_FIELDS, THREE_STATE_FLAG_FIELDS} from "./fieldDefinitions.ts";
import {FourStateFlagField} from "../../../UI/Form/FourStateFlagField.tsx";

type Arma3DifficultySettingsFormProps = {
    formik: FormikProps<Arma3ServerDto>
}

const Arma3DifficultySettingsForm = ({formik}: Arma3DifficultySettingsFormProps) => {
    return <Accordion>
        <AccordionSummary
            expandIcon={<ExpandMoreIcon/>}
            aria-controls="panel1a-content"
            id="panel1a-header"
        >
            <Typography>Custom difficulty settings</Typography>
        </AccordionSummary>
        <AccordionDetails>
            <Grid container>
                <Grid item xs={12} md={4}>
                    <FormGroup>
                        {BOOLEAN_FIELDS.map(field =>
                            <SwitchField key={field.id} id={field.id} label={field.label} formik={formik}/>
                        )}
                    </FormGroup>
                </Grid>
                <Grid item xs={12} md={4}>
                    {THREE_STATE_FLAG_FIELDS.map((field, index) =>
                        index < 4 && <ThreeStateFlagField key={field.id} {...field} formik={formik}/>
                    )}
                    <Arma3AiSkillSettings formik={formik}/>
                </Grid>
                <Grid item xs={12} md={4}>
                    {THREE_STATE_FLAG_FIELDS.map((field, index) =>
                        index >= 4 && <ThreeStateFlagField key={field.id} {...field} formik={formik}/>
                    )}
                    {FOUR_STATE_FLAG_FIELDS.map((field) =>
                        <FourStateFlagField key={field.id} {...field} formik={formik}/>
                    )}
                </Grid>
            </Grid>
        </AccordionDetails>
    </Accordion>
}

export default Arma3DifficultySettingsForm;