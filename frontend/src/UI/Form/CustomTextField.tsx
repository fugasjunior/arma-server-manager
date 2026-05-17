import {HTMLInputTypeAttribute, InputHTMLAttributes} from "react";
import {Grid, TextField} from "@mui/material";
import {useController} from "react-hook-form";

type CustomTextFieldProps = {
    name: string,
    label: string,
    required?: boolean,
    type?: HTMLInputTypeAttribute,
    helperText?: string
    inputProps?: InputHTMLAttributes<HTMLInputElement>
    multiline?: boolean
    containerXs?: number
    containerMd?: number
}

export const CustomTextField = (
    {
        name, label, required, type, helperText, inputProps, multiline, containerXs, containerMd
    }: CustomTextFieldProps
) => {
    const {field} = useController({name});

    return <Grid size={{xs: containerXs ?? 12, md: containerMd ?? 6}}>
        <TextField
            {...field}
            value={field.value ?? ''}
            fullWidth
            required={required}
            id={name}
            label={label}
            type={type ?? "text"}
            helperText={helperText}
            slotProps={{htmlInput: {autoComplete: 'new-password', ...inputProps}}}
            multiline={multiline}
        />
    </Grid>
};