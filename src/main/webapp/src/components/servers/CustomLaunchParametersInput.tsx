import {Typography} from "@mui/material";
import {MuiChipsInput} from "mui-chips-input";

type CustomLaunchParametersInputProps = {
    valueDelimiter: string,
    parameters: Array<{ name: string, value: string | null }>,
    onParametersChange: (values: Array<{ name: string, value: string | null }>) => void,
}

export const CustomLaunchParametersInput = (props: CustomLaunchParametersInputProps) => {
    const {valueDelimiter, parameters, onParametersChange} = props;

    const convertLaunchParameters = () => {
        return parameters
            .map(param => `-${param.name}${param.value ? (valueDelimiter + param.value) : ''}`);
    }

    const handleParameterInput = (newParameters: Array<string>) => {
        const formattedParameters = newParameters
            .filter(param => param)
            .map(param => {
                const split = param.trim().split(/\s+|=/);
                const name = split[0].startsWith("-") ? split[0].slice(1) : split[0];

                if (split.length === 1) {
                    return {name, value: null};
                }

                return {name, value: split.slice(1).join(' ')};
            });

        onParametersChange(formattedParameters);
    };

    return (
        <>
            <Typography>Custom launch parameters</Typography>
            <MuiChipsInput value={convertLaunchParameters()} onChange={handleParameterInput}/>
        </>
    );
};