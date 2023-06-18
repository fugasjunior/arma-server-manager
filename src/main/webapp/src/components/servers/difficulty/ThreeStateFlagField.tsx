import {FormikState} from "formik";
import {Arma3ServerDto} from "../../../dtos/ServerDto.ts";
import {FormikHandlers} from "formik/dist/types";
import {FormControl, FormControlLabel, FormGroup, FormLabel, Radio, RadioGroup} from "@mui/material";
import {getValueByKeyPath} from "../../../util/formUtils.ts";

type ThreeStateFlagFieldProps = {
    id: string,
    label: string,
    onLabel: string,
    middleLabel: string,
    offLabel: string
    formik: FormikState<Arma3ServerDto> & FormikHandlers
};

export const ThreeStateFlagField = ({id, label, onLabel, middleLabel, offLabel, formik}: ThreeStateFlagFieldProps) => {
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
