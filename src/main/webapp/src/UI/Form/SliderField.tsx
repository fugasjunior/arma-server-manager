import {ReactNode} from "react";
import {FormikState} from "formik";
import {FormikHandlers} from "formik/dist/types";
import {getValueByKeyPath} from "../../util/formUtils.ts";
import {Grid, Slider, Typography} from "@mui/material";

type SliderFieldProps<T> = {
    id: string,
    label: string,
    min: number,
    max: number,
    step: number
    icon?: ReactNode
    formik: FormikState<T> & FormikHandlers
}

export const SliderField = <T, >({id, label, min, max, step, icon, formik}: SliderFieldProps<T>) => {
    const value = (getValueByKeyPath(formik.values, id) || 0) as number;

    return <>
        <Typography gutterBottom>
            {label}
        </Typography>
        <Grid container spacing={2} alignItems="center">
            {icon && <Grid item>
                {icon}
            </Grid>}
            <Grid item xs>
                <Slider
                    aria-label={label}
                    id={id}
                    name={id}
                    value={value}
                    valueLabelDisplay="auto"
                    onChange={formik.handleChange}
                    step={step}
                    min={min}
                    max={max}
                />
            </Grid>
        </Grid>
    </>;
};