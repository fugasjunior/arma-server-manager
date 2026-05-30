import {useController} from 'react-hook-form';
import {FormControl, FormControlLabel, FormGroup, FormLabel, Radio, RadioGroup} from "@mui/material";

type ThreeStateFlagFieldProps = {
    name: string,
    label: string,
    onLabel: string,
    middleLabel: string,
    offLabel: string
};

export const ThreeStateFlagField = (
    {name, label, onLabel, middleLabel, offLabel}: ThreeStateFlagFieldProps
) => {
    const {field} = useController({name});

    return <FormGroup>
        <FormControl>
            <FormLabel>{label}</FormLabel>
            <RadioGroup
                row
                id={name}
                name={name}
                onChange={field.onChange}
                value={(field.value as number) || 0}
            >
                <FormControlLabel value="2" control={<Radio/>} label={onLabel}/>
                <FormControlLabel value="1" control={<Radio/>} label={middleLabel}/>
                <FormControlLabel value="0" control={<Radio/>} label={offLabel}/>
            </RadioGroup>
        </FormControl>
    </FormGroup>
};
