import {ServerInstallationDto} from "../../dtos/ServerInstallationDto.ts";
import {FormControl, InputLabel, Select, SelectChangeEvent} from "@mui/material";
import MenuItem from "@mui/material/MenuItem";

interface ServerBranchSelectProps {
    installation: ServerInstallationDto;
    onChange: (e: SelectChangeEvent) => Promise<void>;
}

export const ServerBranchSelect = ({installation, onChange}: ServerBranchSelectProps) => (
    <FormControl sx={{m: 1, minWidth: 120}} size="small">
        <InputLabel id={`${installation}_branch`}>Branch</InputLabel>
        <Select
            labelId={`${installation}_branch`}
            value={installation.branch}
            label="Branch"
            disabled={installation.installationStatus === "INSTALLATION_IN_PROGRESS"}
            autoWidth
            onChange={onChange}
        >
            {installation.availableBranches.map(branch => <MenuItem value={branch}>{branch.toLowerCase()}</MenuItem>)}
        </Select>
    </FormControl>);