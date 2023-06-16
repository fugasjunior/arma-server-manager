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
import React from "react";
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import LocationSearchingIcon from '@mui/icons-material/LocationSearching';
import SchoolIcon from '@mui/icons-material/School';

const Arma3DifficultySettingsForm = ({formik}) => {
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
                        <FormControlLabel
                            control={
                                <Switch checked={formik.values.difficultySettings.reducedDamage}
                                        onChange={formik.handleChange}
                                        name="difficultySettings.reducedDamage"
                                        id="difficultySettings.reducedDamage"/>
                            }
                            label="Reduced damage"
                            error={formik.touched.reducedDamage && Boolean(formik.errors.reducedDamage)}
                        />
                        <FormControlLabel
                            control={
                                <Switch checked={formik.values.difficultySettings.staminaBar}
                                        onChange={formik.handleChange}
                                        name="difficultySettings.staminaBar" id="difficultySettings.staminaBar"/>
                            }
                            label="Stamina bar"
                            error={formik.touched.staminaBar && Boolean(formik.errors.staminaBar)}
                        />
                        <FormControlLabel
                            control={
                                <Switch checked={formik.values.difficultySettings.weaponCrosshair}
                                        onChange={formik.handleChange}
                                        name="difficultySettings.weaponCrosshair"
                                        id="difficultySettings.weaponCrosshair"/>
                            }
                            label="Crosshair"
                            error={formik.touched.weaponCrosshair && Boolean(formik.errors.weaponCrosshair)}
                        />
                        <FormControlLabel
                            control={
                                <Switch checked={formik.values.difficultySettings.visionAid}
                                        onChange={formik.handleChange}
                                        name="difficultySettings.visionAid" id="difficultySettings.visionAid"/>
                            }
                            label="Vision aid"
                            error={formik.touched.visionAid && Boolean(formik.errors.visionAid)}
                        />
                        <FormControlLabel
                            control={
                                <Switch checked={formik.values.difficultySettings.cameraShake}
                                        onChange={formik.handleChange}
                                        name="difficultySettings.cameraShake" id="difficultySettings.cameraShake"/>
                            }
                            label="Camera shake"
                            error={formik.touched.cameraShake && Boolean(formik.errors.cameraShake)}
                        />
                        <FormControlLabel
                            control={
                                <Switch checked={formik.values.difficultySettings.scoreTable}
                                        onChange={formik.handleChange}
                                        name="difficultySettings.scoreTable" id="difficultySettings.scoreTable"/>
                            }
                            label="Score table"
                            error={formik.touched.scoreTable && Boolean(formik.errors.scoreTable)}
                        />
                        <FormControlLabel
                            control={
                                <Switch checked={formik.values.difficultySettings.deathMessages}
                                        onChange={formik.handleChange}
                                        name="difficultySettings.deathMessages"
                                        id="difficultySettings.deathMessages"/>
                            }
                            label="Killed By"
                            error={formik.touched.deathMessages && Boolean(formik.errors.deathMessages)}
                        />
                        <FormControlLabel
                            control={
                                <Switch checked={formik.values.difficultySettings.vonID}
                                        onChange={formik.handleChange}
                                        name="difficultySettings.vonID" id="difficultySettings.vonID"/>
                            }
                            label="VON ID"
                            error={formik.touched.vonID && Boolean(formik.errors.vonID)}
                        />
                        <FormControlLabel
                            control={
                                <Switch checked={formik.values.difficultySettings.tacticalPing}
                                        onChange={formik.handleChange}
                                        name="difficultySettings.tacticalPing"
                                        id="difficultySettings.tacticalPing"/>
                            }
                            label="Tactical ping"
                            error={formik.touched.tacticalPing && Boolean(formik.errors.tacticalPing)}
                        />
                        <FormControlLabel
                            control={
                                <Switch checked={formik.values.difficultySettings.mapContent}
                                        onChange={formik.handleChange}
                                        name="difficultySettings.mapContent" id="difficultySettings.mapContent"/>
                            }
                            label="Extended map content"
                            error={formik.touched.mapContent && Boolean(formik.errors.mapContent)}
                        />
                        <FormControlLabel
                            control={
                                <Switch checked={formik.values.difficultySettings.autoReport}
                                        onChange={formik.handleChange}
                                        name="difficultySettings.autoReport" id="difficultySettings.autoReport"/>
                            }
                            label="Automatic reporting"
                            error={formik.touched.autoReport && Boolean(formik.errors.autoReport)}
                        />
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