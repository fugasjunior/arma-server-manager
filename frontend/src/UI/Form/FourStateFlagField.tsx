import {useController} from 'react-hook-form';
import {FormControl, FormControlLabel, FormGroup, FormLabel, Radio, RadioGroup} from "@mui/material";

type FourStateFlagFieldProps = {
    name: string,
    label: string,
    zeroLabel: string,
    oneLabel: string,
    twoLabel: string
    threeLabel: string
};

export const FourStateFlagField = (
    {name, label, zeroLabel, oneLabel, twoLabel, threeLabel}: FourStateFlagFieldProps
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
                <FormControlLabel value="3" control={<Radio/>} label={threeLabel}/>
                <FormControlLabel value="2" control={<Radio/>} label={twoLabel}/>
                <FormControlLabel value="1" control={<Radio/>} label={oneLabel}/>
                <FormControlLabel value="0" control={<Radio/>} label={zeroLabel}/>
            </RadioGroup>
        </FormControl>
    </FormGroup>
};
