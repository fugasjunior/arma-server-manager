import {FormikState} from "formik";
import {FormikHandlers} from "formik/dist/types";
import {getValueByKeyPath} from "../../util/formUtils.ts";
import {FormControlLabel, Switch} from "@mui/material";

type SwitchFieldProps<T> = {
    id: string,
    label: string,
    formik: FormikState<T> & FormikHandlers
}
export const SwitchField = <T, >({id, label, formik}: SwitchFieldProps<T>) => {
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
