import {ModDto} from "../../dtos/ModDto.ts";
import {CircularProgress} from "@mui/material";
import Tooltip from "@mui/material/Tooltip";
import workshopErrorStatusMap from "../../util/workshopErrorStatusMap.ts";
import {ErrorStatus} from "../../dtos/Status.ts";
import ReportProblemIcon from "@mui/icons-material/ReportProblem";
import CheckIcon from "@mui/icons-material/Check";
import {MouseEvent} from "react";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import Checkbox from "@mui/material/Checkbox";
import SERVER_NAMES from "../../util/serverNames.ts";
import {ServerType} from "../../dtos/ServerDto.ts";
import {humanFileSize} from "../../util/util.ts";
import config from "../../config.ts";

type ModsTableRowProps = {
    row: ModDto,
    labelId: string,
    onClick: (event: MouseEvent<HTMLTableRowElement>) => void,
    ariaChecked: boolean
}

export const ModsTableRow = ({onClick, ariaChecked, labelId, row}: ModsTableRowProps) => {
    const getInstalledIcon = (mod: ModDto) => {
        const status = mod.installationStatus;
        const error = mod.errorStatus;

        if (status === "INSTALLATION_IN_PROGRESS") {
            return <CircularProgress size={20}/>;
        }
        if (status === "ERROR") {
            return <Tooltip
                title={workshopErrorStatusMap.get(ErrorStatus[error as keyof typeof ErrorStatus])}><ReportProblemIcon/></Tooltip>
        }

        if (status === "FINISHED") {
            return <CheckIcon/>;
        }
    };

    return <TableRow
        hover
        onClick={onClick}
        role="checkbox"
        aria-checked={ariaChecked}
        tabIndex={-1}

        selected={ariaChecked}
    >
        <TableCell padding="checkbox">
            <Checkbox
                color="primary"
                checked={ariaChecked}
                inputProps={{
                    "aria-labelledby": labelId,
                }}
            />
        </TableCell>
        <TableCell
            component="th"
            id={labelId}
            scope="row"
            padding="none"
        >
            {row.id}
        </TableCell>
        <TableCell>{row.name}</TableCell>
        <TableCell>{SERVER_NAMES.get(ServerType[row.serverType as keyof typeof ServerType])}</TableCell>
        <TableCell>{humanFileSize(row.fileSize)}</TableCell>
        <TableCell>
            {row.lastUpdated && row.lastUpdated.toLocaleDateString(undefined,
                config.dateFormat)}
        </TableCell>
        <TableCell>{getInstalledIcon(row)}</TableCell>
    </TableRow>
};
