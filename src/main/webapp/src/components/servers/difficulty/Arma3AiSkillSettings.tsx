import {FormikState} from "formik";
import {Arma3ServerDto} from "../../../dtos/ServerDto.ts";
import {FormikHandlers} from "formik/dist/types";
import {FormControl, FormControlLabel, FormGroup, FormLabel, Radio, RadioGroup} from "@mui/material";
import {LocationSearching} from "@mui/icons-material";
import {SliderField} from "../../../UI/Form/SliderField.tsx";

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
        <SliderField id='difficultySettings.skillAI' label='AI skill' min={0} max={1} step={0.05}
                     icon={<LocationSearching/>} formik={formik}/>
        <SliderField id='difficultySettings.precisionAI' label='AI precision' min={0} max={1} step={0.05}
                     icon={<LocationSearching/>} formik={formik}/>
    </>;
}