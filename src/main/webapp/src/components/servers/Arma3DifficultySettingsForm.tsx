import {
    Accordion,
    AccordionDetails,
    AccordionSummary,
    FormControl,
    FormControlLabel,
    FormGroup,
    FormLabel,
    Grid,
    Radio,
    RadioGroup,
    Slider,
    Switch,
    Typography
} from "@mui/material";

import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import LocationSearchingIcon from '@mui/icons-material/LocationSearching';
import SchoolIcon from '@mui/icons-material/School';
import {FormikProps, FormikState} from "formik";
import {Arma3ServerDto} from "../../dtos/ServerDto.ts";
import {FormikHandlers} from "formik/dist/types";

const booleanFields = [
    {id: 'reducedDamage', label: 'Reduced damage'},
    {id: 'tacticalPing', label: 'Tactical ping'},
    {id: 'staminaBar', label: 'Stamina bar'},
    {id: 'weaponCrosshair', label: 'Weapon crosshair'},
    {id: 'visionAid', label: 'Vision aid'},
    {id: 'scoreTable', label: 'Score table'},
    {id: 'deathMessages', label: 'Killed by'},
    {id: 'vonID', label: 'VON ID'},
    {id: 'mapContent', label: 'Extended map content'},
    {id: 'autoReport', label: 'Auto report'},
    {id: 'cameraShake', label: 'Camera shake'}
];

type ThreeStateFlagField = {
    id: string,
    label: string,
    onLabel: string,
    middleLabel: string,
    offLabel: string
}

const threeStateFlagFields: Array<ThreeStateFlagField> = [
    {
        id: 'groupIndicators',
        label: 'Group indicators',
        onLabel: 'Always',
        middleLabel: 'Limited distance',
        offLabel: 'Never'
    },
    {
        id: 'friendlyTags',
        label: 'Friendly tags',
        onLabel: 'Always',
        middleLabel: 'Limited distance',
        offLabel: 'Never'
    },
    {
        id: 'enemyTags',
        label: 'Enemy tags',
        onLabel: 'Always',
        middleLabel: 'Limited distance',
        offLabel: 'Never'
    },
    {
        id: 'detectedMines',
        label: 'Detected mines',
        onLabel: 'Always',
        middleLabel: 'Limited distance',
        offLabel: 'Never'
    },
    {
        id: 'commands',
        label: 'Commands',
        onLabel: 'Always',
        middleLabel: 'Fade out',
        offLabel: 'Never'
    },
    {
        id: 'waypoints',
        label: 'Waypoints',
        onLabel: 'Always',
        middleLabel: 'Fade out',
        offLabel: 'Never'
    },
    {
        id: 'weaponInfo',
        label: 'Weapon info',
        onLabel: 'Always',
        middleLabel: 'Fade out',
        offLabel: 'Never'
    },
    {
        id: 'stanceIndicator',
        label: 'Stance indicator',
        onLabel: 'Always',
        middleLabel: 'Fade out',
        offLabel: 'Never'
    },
    {
        id: 'thirdPersonView',
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
            <Switch checked={formik.values['difficultySettings' + id as keyof Arma3ServerDto] as boolean}
                    onChange={formik.handleChange}
                    name={id}
                    id={id}/>
        }
        label={label}
    />;
}

function renderThreeStateFlagField(field: ThreeStateFlagField, formik: FormikState<Arma3ServerDto> & FormikHandlers) {
    return <FormGroup>
        <FormControl>
            <FormLabel>{field.label}</FormLabel>
            <RadioGroup
                row
                id={field.id}
                name={field.id}
                onChange={formik.handleChange}
                value={formik.values['difficultySettings' + field.id as keyof Arma3ServerDto]}
            >
                <FormControlLabel value="2" control={<Radio/>} label={field.onLabel}/>
                <FormControlLabel value="1" control={<Radio/>} label={field.middleLabel}/>
                <FormControlLabel value="0" control={<Radio/>} label={field.offLabel}/>
            </RadioGroup>
        </FormControl>
    </FormGroup>
}


function renderAiSkillSettings(formik: FormikState<Arma3ServerDto> & FormikHandlers) {
    return <>
        <FormGroup>
            <FormControl>
                <FormLabel>AI level preset</FormLabel>
                <RadioGroup
                    row
                    id="difficultySettings.aiLevelPreset"
                    name="difficultySettings.aiLevelPreset"
                    onChange={formik.handleChange}
                    value={formik.values.difficultySettings.aiLevelPreset}
                >
                    <FormControlLabel value="0" control={<Radio/>} label="Low"/>
                    <FormControlLabel value="1" control={<Radio/>} label="Normal"/>
                    <FormControlLabel value="2" control={<Radio/>} label="High"/>
                    <FormControlLabel value="3" control={<Radio/>} label="Custom"/>
                </RadioGroup>
            </FormControl>
        </FormGroup>
        <Typography gutterBottom>
            AI skill
        </Typography>
        <Grid container spacing={2} alignItems="center">
            <Grid item>
                <SchoolIcon/>
            </Grid>
            <Grid item xs>
                <Slider
                    aria-label="AI skill"
                    id="difficultySettings.skillAI"
                    name="difficultySettings.skillAI"
                    value={formik.values.difficultySettings.skillAI}
                    valueLabelDisplay="auto"
                    onChange={formik.handleChange}
                    step={0.05}
                    min={0}
                    max={1}
                />
            </Grid>
        </Grid>
        <Typography gutterBottom>
            AI precision
        </Typography>
        <Grid container spacing={2} alignItems="center">
            <Grid item>
                <LocationSearchingIcon/>
            </Grid>
            <Grid item xs>
                <Slider
                    aria-label="AI precision"
                    id="difficultySettings.precisionAI"
                    name="difficultySettings.precisionAI"
                    value={formik.values.difficultySettings.precisionAI}
                    valueLabelDisplay="auto"
                    onChange={formik.handleChange}
                    step={0.05}
                    min={0}
                    max={1}
                />
            </Grid>
        </Grid>
    </>;
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
                        index < 4 && renderThreeStateFlagField(field, formik)
                    )}
                    {renderAiSkillSettings(formik)}
                </Grid>
                <Grid item xs={12} md={4}>
                    {threeStateFlagFields.map((field, index) =>
                        index >= 4 && renderThreeStateFlagField(field, formik)
                    )}
                </Grid>
            </Grid>
        </AccordionDetails>
    </Accordion>
}

export default Arma3DifficultySettingsForm;