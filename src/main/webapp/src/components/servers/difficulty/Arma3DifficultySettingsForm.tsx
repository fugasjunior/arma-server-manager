import {
    Accordion,
    AccordionDetails,
    AccordionSummary,
    FormControlLabel,
    FormGroup,
    Grid,
    Switch,
    Typography
} from "@mui/material";

import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import {FormikProps, FormikState} from "formik";
import {Arma3ServerDto} from "../../../dtos/ServerDto.ts";
import {FormikHandlers} from "formik/dist/types";
import {ThreeStateFlagField} from "./ThreeStateFlagField.tsx";
import {Arma3AiSkillSettings} from "./Arma3AiSkillSettings.tsx";

const booleanFields = [
    {id: 'difficultySettings.reducedDamage', label: 'Reduced damage'},
    {id: 'difficultySettings.tacticalPing', label: 'Tactical ping'},
    {id: 'difficultySettings.staminaBar', label: 'Stamina bar'},
    {id: 'difficultySettings.weaponCrosshair', label: 'Weapon crosshair'},
    {id: 'difficultySettings.visionAid', label: 'Vision aid'},
    {id: 'difficultySettings.scoreTable', label: 'Score table'},
    {id: 'difficultySettings.deathMessages', label: 'Killed by'},
    {id: 'difficultySettings.vonID', label: 'VON ID'},
    {id: 'difficultySettings.mapContent', label: 'Extended map content'},
    {id: 'difficultySettings.autoReport', label: 'Auto report'},
    {id: 'difficultySettings.cameraShake', label: 'Camera shake'}
];

const threeStateFlagFields = [
    {
        id: 'difficultySettings.groupIndicators',
        label: 'Group indicators',
        onLabel: 'Always',
        middleLabel: 'Limited distance',
        offLabel: 'Never'
    },
    {
        id: 'difficultySettings.friendlyTags',
        label: 'Friendly tags',
        onLabel: 'Always',
        middleLabel: 'Limited distance',
        offLabel: 'Never'
    },
    {
        id: 'difficultySettings.enemyTags',
        label: 'Enemy tags',
        onLabel: 'Always',
        middleLabel: 'Limited distance',
        offLabel: 'Never'
    },
    {
        id: 'difficultySettings.detectedMines',
        label: 'Detected mines',
        onLabel: 'Always',
        middleLabel: 'Limited distance',
        offLabel: 'Never'
    },
    {
        id: 'difficultySettings.commands',
        label: 'Commands',
        onLabel: 'Always',
        middleLabel: 'Fade out',
        offLabel: 'Never'
    },
    {
        id: 'difficultySettings.waypoints',
        label: 'Waypoints',
        onLabel: 'Always',
        middleLabel: 'Fade out',
        offLabel: 'Never'
    },
    {
        id: 'difficultySettings.weaponInfo',
        label: 'Weapon info',
        onLabel: 'Always',
        middleLabel: 'Fade out',
        offLabel: 'Never'
    },
    {
        id: 'difficultySettings.stanceIndicator',
        label: 'Stance indicator',
        onLabel: 'Always',
        middleLabel: 'Fade out',
        offLabel: 'Never'
    },
    {
        id: 'difficultySettings.thirdPersonView',
        label: 'Third person view',
        onLabel: 'Enabled',
        middleLabel: 'Vehicles only',
        offLabel: 'Disabled'
    }
];

type Arma3DifficultySettingsFormProps = {
    formik: FormikProps<Arma3ServerDto>
}

function renderSwitchField(id: string, label: string, formik: FormikState<Arma3ServerDto> & FormikHandlers) {
    return <FormControlLabel
        control={
            <Switch checked={formik.values[id as keyof Arma3ServerDto] as boolean}
                    onChange={formik.handleChange}
                    name={id}
                    id={id}/>
        }
        label={label}
    />;
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
                        {booleanFields.map(booleanField =>
                            renderSwitchField(booleanField.id, booleanField.label, formik)
                        )}
                    </FormGroup>
                </Grid>
                <Grid item xs={12} md={4}>
                    {threeStateFlagFields.map((field, index) =>
                        index < 4 && <ThreeStateFlagField key={field.id} {...field} formik={formik}/>
                    )}
                    <Arma3AiSkillSettings formik={formik}/>
                </Grid>
                <Grid item xs={12} md={4}>
                    {threeStateFlagFields.map((field, index) =>
                        index >= 4 && <ThreeStateFlagField key={field.id} {...field} formik={formik}/>
                    )}
                </Grid>
            </Grid>
        </AccordionDetails>
    </Accordion>
}

export default Arma3DifficultySettingsForm;