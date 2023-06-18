import {FormikState} from "formik";
import {Arma3ServerDto} from "../../../dtos/ServerDto.ts";
import {FormikHandlers} from "formik/dist/types";
import {getValueByKeyPath} from "../../../util/formUtils.ts";
import {FormControlLabel, Switch} from "@mui/material";

type SwitchFieldProps = {
    id: string,
    label: string,
    formik: FormikState<Arma3ServerDto> & FormikHandlers
}
export const SwitchField = ({id, label, formik}: SwitchFieldProps) => {
    const checked = !!getValueByKeyPath(formik.values, id);
    return <FormControlLabel
        control={
            <Switch checked={checked}
                    onChange={formik.handleChange}
                    name={id}
                    id={id}/>
        }
        label={label}
    />;
}
