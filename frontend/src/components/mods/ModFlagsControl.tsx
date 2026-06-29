import {Checkbox, FormControlLabel, MenuItem, Select, SelectChangeEvent, Stack} from "@mui/material";
import {ModFlagsDto, ServerType} from "../../api/generated";

type Props = {
    flags: ModFlagsDto;
    serverType?: ServerType | string;
    disabled: boolean;
    onChange: (flags: ModFlagsDto) => void;
};

type ModRole = 'server_client' | 'server' | 'client' | '';

const ROLE_FLAGS: Record<Exclude<ModRole, ''>, Pick<ModFlagsDto, 'loadOnClient' | 'loadOnServer'>> = {
    server_client: {loadOnClient: true, loadOnServer: true},
    server: {loadOnClient: false, loadOnServer: true},
    client: {loadOnClient: true, loadOnServer: false},
};

function flagsToRole(flags: ModFlagsDto): ModRole {
    if (flags.loadOnServer && flags.loadOnClient) return 'server_client';
    if (flags.loadOnServer && !flags.loadOnClient) return 'server';
    if (!flags.loadOnServer && flags.loadOnClient) return 'client';
    return '';
}

export default function ModFlagsControl({flags, serverType, disabled, onChange}: Props) {
    const role = flagsToRole(flags);
    const isArma3 = serverType === ServerType.Arma3;

    const handleRoleChange = (e: SelectChangeEvent<ModRole>) => {
        const newRole = e.target.value as ModRole;
        if (!newRole) return;
        onChange({
            ...ROLE_FLAGS[newRole],
            loadOnHeadlessClient: newRole === 'server_client' ? true : flags.loadOnHeadlessClient,
        });
    };

    const handleHcChange = (_: unknown, checked: boolean) => {
        onChange({
            loadOnClient: flags.loadOnClient,
            loadOnServer: flags.loadOnServer,
            loadOnHeadlessClient: checked,
        });
    };

    return (
        <Stack direction="row" spacing={1} sx={{alignItems: "center"}}
               onClick={(e) => e.stopPropagation()}>
            <Select<ModRole>
                value={role}
                onChange={handleRoleChange}
                size="small"
                displayEmpty
                disabled={disabled}
                sx={{width: '10rem'}}
            >
                <MenuItem value="" disabled><em>—</em></MenuItem>
                <MenuItem value="server_client">Server + Client</MenuItem>
                <MenuItem value="server">Server only</MenuItem>
                <MenuItem value="client">Client only</MenuItem>
            </Select>
            {isArma3 && (
                <FormControlLabel
                    label="HC"
                    disabled={disabled || role === 'server_client'}
                    control={
                        <Checkbox
                            checked={role === 'server_client' || (flags.loadOnHeadlessClient ?? false)}
                            onChange={handleHcChange}
                            size="small"
                        />
                    }
                />
            )}
        </Stack>
    );
}
