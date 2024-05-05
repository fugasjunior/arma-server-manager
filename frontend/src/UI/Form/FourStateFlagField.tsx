import {FormikState} from "formik";
import {FormikHandlers} from "formik/dist/types";
import {FormControl, FormControlLabel, FormGroup, FormLabel, Radio, RadioGroup} from "@mui/material";
import {getValueByKeyPath} from "../../util/formUtils.ts";

type ThreeStateFlagFieldProps<T> = {
    id: string,
    label: string,
    zeroLabel: string,
    oneLabel: string,
    twoLabel: string
    threeLabel: string
    formik: FormikState<T> & FormikHandlers
};

export const FourStateFlagField = <T, >(
    {id, label, zeroLabel, oneLabel, twoLabel, threeLabel, formik}: ThreeStateFlagFieldProps<T>
) => {
    const value = (getValueByKeyPath(formik.values, id) || 0) as 0 | 1 | 2 | 3;

    return <FormGroup>
        <FormControl>
            <FormLabel>{label}</FormLabel>
            <RadioGroup
                row
                id={id}
                name={id}
                onChange={formik.handleChange}
                value={value}
            >
                <FormControlLabel value="3" control={<Radio/>} label={threeLabel}/>
                <FormControlLabel value="2" control={<Radio/>} label={twoLabel}/>
                <FormControlLabel value="1" control={<Radio/>} label={oneLabel}/>
                <FormControlLabel value="0" control={<Radio/>} label={zeroLabel}/>
            </RadioGroup>
        </FormControl>
    </FormGroup>
};
