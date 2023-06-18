import {FormikState} from "formik";
import {FormikHandlers} from "formik/dist/types";
import {FormControl, FormControlLabel, FormGroup, FormLabel, Radio, RadioGroup} from "@mui/material";
import {getValueByKeyPath} from "../../util/formUtils.ts";

type ThreeStateFlagFieldProps<T> = {
    id: string,
    label: string,
    onLabel: string,
    middleLabel: string,
    offLabel: string
    formik: FormikState<T> & FormikHandlers
};

export const ThreeStateFlagField = <T, >(
    {id, label, onLabel, middleLabel, offLabel, formik}: ThreeStateFlagFieldProps<T>
) => {
    const value = (getValueByKeyPath(formik.values, id) || 0) as 0 | 1 | 2;

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
                <FormControlLabel value="2" control={<Radio/>} label={onLabel}/>
                <FormControlLabel value="1" control={<Radio/>} label={middleLabel}/>
                <FormControlLabel value="0" control={<Radio/>} label={offLabel}/>
            </RadioGroup>
        </FormControl>
    </FormGroup>
};
