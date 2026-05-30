import {ServerInstallationDto} from "../../api/generated";
import {FormControl, InputLabel, Select, SelectChangeEvent} from "@mui/material";
import MenuItem from "@mui/material/MenuItem";
import {usePermission} from "../../hooks/usePermission.ts";

interface ServerBranchSelectProps {
    installation: ServerInstallationDto;
    onChange: (e: SelectChangeEvent) => Promise<void>;
}

export const ServerBranchSelect = ({installation, onChange}: ServerBranchSelectProps) => {
    const canModify = usePermission("INSTALL_MANAGE");

    return (
        <FormControl sx={{m: 1, minWidth: 120}} size="small">
            <InputLabel id={`${installation}_branch`}>Branch</InputLabel>
            <Select
                labelId={`${installation}_branch`}
                value={installation.branch}
                label="Branch"
                disabled={installation.installationStatus === "INSTALLATION_IN_PROGRESS" || !canModify}
                autoWidth
                onChange={onChange}
            >
                {Array.from(installation.availableBranches ?? []).map(branch => <MenuItem key={branch}
                                                                                          value={branch}>{branch.toLowerCase()}</MenuItem>)}
            </Select>
        </FormControl>);
};