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
import {ReactNode} from "react";
import {LocationSearching} from "@mui/icons-material";
import {getValueByKeyPath} from "../../../util/formUtils.ts";

type Arma3AiSkillSettingsProps = {
    formik: FormikState<Arma3ServerDto> & FormikHandlers
}

type SliderFieldProps = {
    id: string,
    label: string,
    min: number,
    max: number,
    step: number
    icon?: ReactNode
    formik: FormikState<Arma3ServerDto> & FormikHandlers
}

function SliderField({id, label, min, max, step, icon, formik}: SliderFieldProps) {
    const value = (getValueByKeyPath(formik.values, id) || 0) as number;

    return <>
        <Typography gutterBottom>
            {label}
        </Typography>
        <Grid container spacing={2} alignItems="center">
            {icon && <Grid item>
                {icon}
            </Grid>}
            <Grid item xs>
                <Slider
                    aria-label={label}
                    id={id}
                    name={id}
                    value={value}
                    valueLabelDisplay="auto"
                    onChange={formik.handleChange}
                    step={step}
                    min={min}
                    max={max}
                />
            </Grid>
        </Grid>
    </>;
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
        <SliderField id='difficultySettings.skillAI' label='AI skill' min={0} max={1} step={0.05}
                     icon={<LocationSearching/>} formik={formik}/>
        <SliderField id='difficultySettings.precisionAI' label='AI precision' min={0} max={1} step={0.05}
                     icon={<LocationSearching/>} formik={formik}/>
    </>;
}