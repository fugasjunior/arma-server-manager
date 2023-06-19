import {Button, Stack, TextField} from "@mui/material";
import TablePagination from "@mui/material/TablePagination";
import {ChangeEvent} from "react";
import {ModDto} from "../../dtos/ModDto.ts";

type ModsTableControlsProps = {
    modId: string,
    onModIdChange: (e: ChangeEvent<HTMLInputElement>) => void,
    onInstallClicked: () => void,
    modDtos: Array<ModDto>,
    rowsPerPage: number,
    pageNumber: number,
    onPageChange: (_: any, newPage: number) => void,
    onRowsPerPageChange: (event: ChangeEvent<HTMLInputElement>) => void
}

export function ModsTableControls(
    {modId, onModIdChange, onInstallClicked, modDtos, rowsPerPage, pageNumber, onPageChange, onRowsPerPageChange}: ModsTableControlsProps
) {
    return <Stack direction="row" justifyContent="space-between" alignItems="start" m={2}>
        <Stack direction="row" spacing={1}>
            <TextField id="mod-install-field" label="Install mod" placeholder="Mod ID" size="small"
                       variant="filled" value={modId} onChange={onModIdChange}/>
            <Button variant="outlined" size="small" disabled={modId.length === 0}
                    onClick={onInstallClicked}>Install</Button>
        </Stack>
        {modDtos.length > 0 && <TablePagination
            rowsPerPageOptions={[10, 15, 25, 50]}
            component="div"
            count={modDtos.length}
            rowsPerPage={rowsPerPage}
            page={pageNumber}
            onPageChange={onPageChange}
            onRowsPerPageChange={onRowsPerPageChange}
        />}
    </Stack>;
}
