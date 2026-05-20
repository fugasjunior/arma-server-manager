import {useController} from 'react-hook-form';
import {FormControlLabel, Switch} from "@mui/material";

type SwitchFieldProps = {
    name: string,
    label: string,
    disabled?: boolean
}

export const SwitchField = ({name, label, disabled}: SwitchFieldProps) => {
    const {field} = useController({name});

    return <FormControlLabel
        control={
            <Switch
                {...field}
                checked={!!field.value}
                name={name}
                id={name}
                disabled={disabled ?? false}
            />
        }
        label={label}
    />;
}
