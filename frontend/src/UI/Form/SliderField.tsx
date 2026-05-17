import {ReactNode} from "react";
import {useController} from 'react-hook-form';
import {Grid, Slider, Typography} from "@mui/material";

type SliderFieldProps = {
    name: string,
    label: string,
    min: number,
    max: number,
    step: number
    icon?: ReactNode
}

export const SliderField = ({name, label, min, max, step, icon}: SliderFieldProps) => {
    const {field} = useController({name});

    return <>
        <Typography gutterBottom>
            {label}
        </Typography>
        <Grid container spacing={2} sx={{alignItems: "center"}}>
            {icon && <Grid>
                {icon}
            </Grid>}
            <Grid size="grow">
                <Slider
                    aria-label={label}
                    id={name}
                    name={name}
                    value={(field.value as number) || 0}
                    valueLabelDisplay="auto"
                    onChange={(_, value) => field.onChange(value)}
                    step={step}
                    min={min}
                    max={max}
                />
            </Grid>
        </Grid>
    </>;
};