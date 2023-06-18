import {FormikState} from "formik";
import {Arma3ServerDto} from "../../../dtos/ServerDto.ts";
import {FormikHandlers} from "formik/dist/types";
import {FormControl, FormControlLabel, FormGroup, FormLabel, Radio, RadioGroup} from "@mui/material";

type ThreeStateFlagFieldProps = {
    id: string,
    label: string,
    onLabel: string,
    middleLabel: string,
    offLabel: string
    formik: FormikState<Arma3ServerDto> & FormikHandlers
};

export const ThreeStateFlagField = ({id, label, onLabel, middleLabel, offLabel, formik}: ThreeStateFlagFieldProps) => {
    return <FormGroup>
        <FormControl>
            <FormLabel>{label}</FormLabel>
            <RadioGroup
                row
                id={id}
                name={id}
                onChange={formik.handleChange}
                value={formik.values['difficultySettings' + id as keyof Arma3ServerDto]}
            >
                <FormControlLabel value="2" control={<Radio/>} label={onLabel}/>
                <FormControlLabel value="1" control={<Radio/>} label={middleLabel}/>
                <FormControlLabel value="0" control={<Radio/>} label={offLabel}/>
            </RadioGroup>
        </FormControl>
    </FormGroup>
};
