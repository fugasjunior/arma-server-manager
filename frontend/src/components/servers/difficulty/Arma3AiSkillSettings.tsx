import {useController} from 'react-hook-form';
import {FormControl, FormControlLabel, FormGroup, FormLabel, Radio, RadioGroup} from "@mui/material";
import {LocationSearching, School} from "@mui/icons-material";
import {SliderField} from "../../../UI/Form/SliderField.tsx";

export const Arma3AiSkillSettings = () => {
    const {field} = useController({name: 'difficultySettings.aiLevelPreset'});

    return <>
        <FormGroup>
            <FormControl>
                <FormLabel>AI level preset</FormLabel>
                <RadioGroup
                    row
                    id="difficultySettings.aiLevelPreset"
                    name="difficultySettings.aiLevelPreset"
                    onChange={field.onChange}
                    value={field.value ?? 0}
                >
                    <FormControlLabel value="0" control={<Radio/>} label="Low"/>
                    <FormControlLabel value="1" control={<Radio/>} label="Normal"/>
                    <FormControlLabel value="2" control={<Radio/>} label="High"/>
                    <FormControlLabel value="3" control={<Radio/>} label="Custom"/>
                </RadioGroup>
            </FormControl>
        </FormGroup>
        <SliderField name='difficultySettings.skillAI' label='AI skill' min={0} max={1} step={0.05}
                     icon={<School/>}/>
        <SliderField name='difficultySettings.precisionAI' label='AI precision' min={0} max={1} step={0.05}
                     icon={<LocationSearching/>}/>
    </>;
}