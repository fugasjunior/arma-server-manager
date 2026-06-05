import {ChangeEvent, ChangeEventHandler, MouseEventHandler} from 'react';
import Box from '@mui/material/Box';
import Paper from '@mui/material/Paper';
import {humanFileSize} from "../../util/util";
import {EnhancedTable, EnhancedTableHeadCell} from "../../UI/EnhancedTable/EnhancedTable.tsx";
import {KeysTableToolbar} from "./KeysTableToolbar.tsx";
import {Arma3KeyDto} from "../../api/generated";

const headCells: Array<EnhancedTableHeadCell> = [
    {
        id: "name",
        label: "Name",
        searchable: true
    },
    {
        id: "fileSize",
        label: "File size",
        type: "numeric"
    },
    {
        id: "createdOn",
        label: "Uploaded on",
        type: "date"
    },
];

type KeysTableProps = {
    rows: Array<Arma3KeyDto>,
    selectedKeyIds: Array<string>,
    onDeleteClicked: MouseEventHandler,
    onRowClick: (rowId: number | string) => void,
    onSelectAllRowsClick: (event: ChangeEvent<HTMLInputElement>) => void,
    onFileChange: ChangeEventHandler,
    uploadInProgress: boolean,
    disabled?: boolean
}

export const KeysTable = (
    {
        rows,
        selectedKeyIds,
        onDeleteClicked,
        onRowClick,
        onSelectAllRowsClick,
        onFileChange,
        uploadInProgress,
        disabled = false
    }: KeysTableProps
) => {

    const getRows = () => {
        return rows.map(keyDto => ({
            id: keyDto.name ?? "",
            cells: [
                {
                    id: "name",
                    value: keyDto.name ?? "",
                },
                {
                    id: "fileSize",
                    value: keyDto.fileSize ?? 0,
                    displayValue: humanFileSize(keyDto.fileSize ?? 0)
                },
                {
                    id: "createdOn",
                    value: keyDto.createdOn ? new Date(keyDto.createdOn) : new Date()
                },
            ]
        }));
    };

    return (
        <Box sx={{width: '100%'}}>
            <Paper sx={{width: '100%', mb: 2}}>
                <EnhancedTable
                    rows={getRows()}
                    selectedRowIds={selectedKeyIds}
                    headCells={headCells}
                    onRowSelect={onRowClick}
                    title="Custom Bikeys"
                    id="keys"
                    onSelectAllRowsClick={onSelectAllRowsClick}
                    defaultSortColumnId="name"
                    customTopControls={
                        <KeysTableToolbar
                            onFileChange={onFileChange}
                            onDeleteClicked={onDeleteClicked}
                            selectedKeysCount={selectedKeyIds.length}
                            uploadInProgress={uploadInProgress}
                            disabled={disabled}
                        />
                    }
                />
            </Paper>
        </Box>
    );
};
