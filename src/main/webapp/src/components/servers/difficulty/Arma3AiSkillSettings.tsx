import {FormikState} from "formik";
import {Arma3ServerDto} from "../../../dtos/ServerDto.ts";
import {FormikHandlers} from "formik/dist/types";
import {
    FormControl,
    FormControlLabel,
    FormGroup,
    FormLabel,
    Grid,
    Radio,
    RadioGroup,
    Slider,
    Typography
} from "@mui/material";
import SchoolIcon from "@mui/icons-material/School";
import LocationSearchingIcon from "@mui/icons-material/LocationSearching";

type Arma3AiSkillSettingsProps = {
    formik: FormikState<Arma3ServerDto> & FormikHandlers
}

export const Arma3AiSkillSettings = ({formik}: Arma3AiSkillSettingsProps) => {
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