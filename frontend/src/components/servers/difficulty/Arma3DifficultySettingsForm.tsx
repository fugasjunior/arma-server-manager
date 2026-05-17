import {Accordion, AccordionDetails, AccordionSummary, FormGroup, Grid, Typography} from "@mui/material";

import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import {ThreeStateFlagField} from "../../../UI/Form/ThreeStateFlagField.tsx";
import {Arma3AiSkillSettings} from "./Arma3AiSkillSettings.tsx";
import {SwitchField} from "../../../UI/Form/SwitchField.tsx";
import {BOOLEAN_FIELDS, FOUR_STATE_FLAG_FIELDS, THREE_STATE_FLAG_FIELDS} from "./fieldDefinitions.ts";
import {FourStateFlagField} from "../../../UI/Form/FourStateFlagField.tsx";

const Arma3DifficultySettingsForm = () => {
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
                <Grid size={{xs: 12, md: 4}}>
                    <FormGroup>
                        {BOOLEAN_FIELDS.map(field =>
                            <SwitchField key={field.name} name={field.name} label={field.label}/>
                        )}
                    </FormGroup>
                </Grid>
                <Grid size={{xs: 12, md: 4}}>
                    {THREE_STATE_FLAG_FIELDS.map((field, index) =>
                        index < 4 && <ThreeStateFlagField key={field.name} {...field}/>
                    )}
                    <Arma3AiSkillSettings/>
                </Grid>
                <Grid size={{xs: 12, md: 4}}>
                    {THREE_STATE_FLAG_FIELDS.map((field, index) =>
                        index >= 4 && <ThreeStateFlagField key={field.name} {...field}/>
                    )}
                    {FOUR_STATE_FLAG_FIELDS.map((field) =>
                        <FourStateFlagField key={field.name} {...field}/>
                    )}
                </Grid>
            </Grid>
        </AccordionDetails>
    </Accordion>
}

export default Arma3DifficultySettingsForm;