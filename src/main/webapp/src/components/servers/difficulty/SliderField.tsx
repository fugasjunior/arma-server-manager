import {ReactNode} from "react";
import {FormikState} from "formik";
import {Arma3ServerDto} from "../../../dtos/ServerDto.ts";
import {FormikHandlers} from "formik/dist/types";
import {getValueByKeyPath} from "../../../util/formUtils.ts";
import {Grid, Slider, Typography} from "@mui/material";

type SliderFieldProps = {
    id: string,
    label: string,
    min: number,
    max: number,
    step: number
    icon?: ReactNode
    formik: FormikState<Arma3ServerDto> & FormikHandlers
}

export function SliderField({id, label, min, max, step, icon, formik}: SliderFieldProps) {
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
}