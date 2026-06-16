import React from 'react';
import {ToggleButton, ToggleButtonGroup, Tooltip} from "@mui/material";
import {ModFlagsDto, ServerType} from "../../api/generated";

type Props = {
    flags: ModFlagsDto;
    serverType?: ServerType | string;
    disabled: boolean;
    onChange: (flags: ModFlagsDto) => void;
};

export default function ModFlagsToggleGroup({flags, serverType, disabled, onChange}: Props) {
    const selectedValues: string[] = [
        ...(flags.loadOnClient ? ['client'] : []),
        ...(flags.loadOnServer ? ['server'] : []),
        ...(flags.loadOnHeadlessClient ? ['hc'] : []),
    ];

    const handleChange = (_: React.MouseEvent<HTMLElement>, newValues: string[]) => {
        onChange({
            loadOnClient: newValues.includes('client'),
            loadOnServer: newValues.includes('server'),
            loadOnHeadlessClient: newValues.includes('hc'),
        });
    };

    return (
        <ToggleButtonGroup
            value={selectedValues}
            onChange={handleChange}
            size="small"
            color="primary"
            disabled={disabled}
            onClick={(e) => e.stopPropagation()}
        >
            <ToggleButton value="client">
                <Tooltip title="Load on clients" placement="top"><span>C</span></Tooltip>
            </ToggleButton>
            <ToggleButton value="server">
                <Tooltip title="Load on server" placement="top"><span>S</span></Tooltip>
            </ToggleButton>
            {serverType === ServerType.Arma3 && (
                <ToggleButton value="hc">
                    <Tooltip title="Load on headless client" placement="top"><span>H</span></Tooltip>
                </ToggleButton>
            )}
        </ToggleButtonGroup>
    );
}
