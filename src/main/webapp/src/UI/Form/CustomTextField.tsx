import {HTMLInputTypeAttribute} from "react";
import {Grid, InputBaseComponentProps, TextField} from "@mui/material";
import {FormikState} from "formik";
import {FormikHandlers} from "formik/dist/types";
import {getValueByKeyPath} from "../../util/formUtils.ts";

type CustomTextFieldProps<T> = {
    id: string,
    label: string,
    required?: boolean,
    type?: HTMLInputTypeAttribute,
    helperText?: string
    inputProps?: InputBaseComponentProps
    multiline?: boolean
    containerXs?: number
    containerMd?: number
    formik: FormikState<T> & FormikHandlers
}
export const CustomTextField = <T, >(
    {
        id, label, required, type, helperText, inputProps, multiline, containerXs, containerMd, formik
    }: CustomTextFieldProps<T>
) => {
    const value = getValueByKeyPath(formik.values, id) || '';

    return <Grid item xs={containerXs ?? 12} md={containerMd ?? 6}>
        <TextField
            fullWidth
            required={required}
            id={id}
            name={id}
            label={label}
            type={type ?? "text"}
            value={value}
            onChange={formik.handleChange}
            helperText={helperText}
            inputProps={{autoComplete: 'new-password', ...inputProps}}
            multiline={multiline}
        />
    </Grid>
};