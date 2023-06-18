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

const difficultySettingsBooleanFields = [
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
                        {difficultySettingsBooleanFields.map(booleanField =>
                            renderSwitchField(booleanField.id, booleanField.label, formik)
                        )}
                    </FormGroup>
                </Grid>
                <Grid item xs={12} md={4}>
                    <FormGroup>
                        <FormControl>
                            <FormLabel>Group indicators</FormLabel>
                            <RadioGroup
                                row
                                id="difficultySettings.groupIndicators"
                                name="difficultySettings.groupIndicators"
                                onChange={formik.handleChange}
                                value={formik.values.difficultySettings.groupIndicators}
                            >
                                <FormControlLabel value="2" control={<Radio/>} label="Always"/>
                                <FormControlLabel value="1" control={<Radio/>} label="Limited distance"/>
                                <FormControlLabel value="0" control={<Radio/>} label="Never"/>
                            </RadioGroup>
                        </FormControl>
                    </FormGroup>
                    <FormGroup>
                        <FormControl>
                            <FormLabel>Friendly tags</FormLabel>
                            <RadioGroup
                                row
                                id="difficultySettings.friendlyTags"
                                name="difficultySettings.friendlyTags"
                                onChange={formik.handleChange}
                                value={formik.values.difficultySettings.friendlyTags}
                            >
                                <FormControlLabel value="2" control={<Radio/>} label="Always"/>
                                <FormControlLabel value="1" control={<Radio/>} label="Limited distance"/>
                                <FormControlLabel value="0" control={<Radio/>} label="Never"/>
                            </RadioGroup>
                        </FormControl>
                    </FormGroup>
                    <FormGroup>
                        <FormControl>
                            <FormLabel>Enemy tags</FormLabel>
                            <RadioGroup
                                row
                                id="difficultySettings.enemyTags"
                                name="difficultySettings.enemyTags"
                                onChange={formik.handleChange}
                                value={formik.values.difficultySettings.enemyTags}
                            >
                                <FormControlLabel value="2" control={<Radio/>} label="Always"/>
                                <FormControlLabel value="1" control={<Radio/>} label="Limited distance"/>
                                <FormControlLabel value="0" control={<Radio/>} label="Never"/>
                            </RadioGroup>
                        </FormControl>
                    </FormGroup>
                    <FormGroup>
                        <FormControl>
                            <FormLabel>Detected mines</FormLabel>
                            <RadioGroup
                                row
                                id="difficultySettings.detectedMines"
                                name="difficultySettings.detectedMines"
                                onChange={formik.handleChange}
                                value={formik.values.difficultySettings.detectedMines}
                            >
                                <FormControlLabel value="2" control={<Radio/>} label="Always"/>
                                <FormControlLabel value="1" control={<Radio/>} label="Limited distance"/>
                                <FormControlLabel value="0" control={<Radio/>} label="Never"/>
                            </RadioGroup>
                        </FormControl>
                    </FormGroup>
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
                </Grid>
                <Grid item xs={12} md={4}>
                    <FormGroup>
                        <FormControl>
                            <FormLabel>Commands</FormLabel>
                            <RadioGroup
                                row
                                name="difficultySettings.commands"
                                onChange={formik.handleChange}
                                value={formik.values.difficultySettings.commands}
                            >
                                <FormControlLabel value="2" control={<Radio/>} label="Always"/>
                                <FormControlLabel value="1" control={<Radio/>} label="Fade out"/>
                                <FormControlLabel value="0" control={<Radio/>} label="Never"/>
                            </RadioGroup>
                        </FormControl>
                    </FormGroup>
                    <FormGroup>
                        <FormControl>
                            <FormLabel>Weapon Info</FormLabel>
                            <RadioGroup
                                row
                                name="difficultySettings.weaponInfo"
                                onChange={formik.handleChange}
                                value={formik.values.difficultySettings.weaponInfo}
                            >
                                <FormControlLabel value="2" control={<Radio/>} label="Always"/>
                                <FormControlLabel value="1" control={<Radio/>} label="Fade out"/>
                                <FormControlLabel value="0" control={<Radio/>} label="Never"/>
                            </RadioGroup>
                        </FormControl>
                    </FormGroup>
                    <FormGroup>
                        <FormControl>
                            <FormLabel>Stance indicator</FormLabel>
                            <RadioGroup
                                row
                                name="difficultySettings.stanceIndicator"
                                onChange={formik.handleChange}
                                value={formik.values.difficultySettings.stanceIndicator}
                            >
                                <FormControlLabel value="2" control={<Radio/>} label="Always"/>
                                <FormControlLabel value="1" control={<Radio/>} label="Fade out"/>
                                <FormControlLabel value="0" control={<Radio/>} label="Never"/>
                            </RadioGroup>
                        </FormControl>
                    </FormGroup>
                    <FormGroup>
                        <FormControl>
                            <FormLabel>Third person view</FormLabel>
                            <RadioGroup
                                row
                                name="difficultySettings.thirdPersonView"
                                onChange={formik.handleChange}
                                value={formik.values.difficultySettings.thirdPersonView}
                            >
                                <FormControlLabel value="1" control={<Radio/>} label="Enabled"/>
                                <FormControlLabel value="2" control={<Radio/>} label="Vehicles only"/>
                                <FormControlLabel value="0" control={<Radio/>} label="Disabled"/>
                            </RadioGroup>
                        </FormControl>
                    </FormGroup>
                </Grid>
            </Grid>
        </AccordionDetails>
    </Accordion>
}

export default Arma3DifficultySettingsForm;