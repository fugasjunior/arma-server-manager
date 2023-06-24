import {ChangeEvent, ChangeEventHandler, MouseEventHandler, useState} from 'react';
import Box from '@mui/material/Box';
import Paper from '@mui/material/Paper';
import ScenariosTableToolbar from "./ScenariosTableToolbar";
import {humanFileSize} from "../../util/util";
import {EnhancedTable, EnhancedTableHeadCell} from "../../UI/EnhancedTable/EnhancedTable.tsx";

const headCells: Array<EnhancedTableHeadCell> = [
    {
        id: 'name',
        label: 'ID',
        searchable: true
    },
    {
        id: 'fileSize',
        label: 'File size',
        type: "numeric"
    },
    {
        id: 'createdOn',
        label: 'Created on',
        type: "date"
    },
];

type ScenariosTableProps = {
    rows: Array<{
        name: string,
        fileSize: number,
        createdOn: Date
    }>,
    selected: Array<string>,
    onDeleteClicked: MouseEventHandler,
    onRowClick: (rowId: number | string) => void,
    onSelectAllRowsClick: (event: ChangeEvent<HTMLInputElement>) => void,
    onFileChange: ChangeEventHandler,
    onDownloadClicked: (name: string, event: any) => void,
    uploadInProgress: boolean,
    percentUploaded: number
}

const ScenariosTable = (props: ScenariosTableProps) => {
    const {
        rows,
        selected
    } = props;

    const [search, setSearch] = useState("");
    const handleSearchChange = (event: ChangeEvent<HTMLInputElement>) => {
        setSearch(event.target.value);
    }

    const getRows = () => {
        return rows.map(scenarioDto => {
            return {
                id: scenarioDto.name,
                cells: [
                    {
                        id: "name",
                        value: scenarioDto.name
                    },
                    {
                        id: "createdOn",
                        value: scenarioDto.createdOn
                    },
                    {
                        id: "fileSize",
                        value: scenarioDto.fileSize,
                        displayValue: humanFileSize(scenarioDto.fileSize)
                    },
                ]
            };
        })
    }

    return (
        <Box sx={{width: '100%'}}>
            <Paper sx={{
                width: '100%',
                mb: 2
            }}>
                <ScenariosTableToolbar
                    numSelected={selected.length}
                    onDeleteClicked={props.onDeleteClicked}
                    onFileChange={props.onFileChange}
                    uploadInProgress={props.uploadInProgress}
                    percentUploaded={props.percentUploaded}
                    search={search}
                    title="Scenarios"
                    onSearchChange={handleSearchChange}
                />
                <EnhancedTable rows={getRows()} selectedRowIds={selected} headCells={headCells}
                               onRowSelect={props.onRowClick}
                               onSelectAllRowsClick={props.onSelectAllRowsClick} searchTerm={search}
                               defaultSortColumnId="name"/>
            </Paper>
        </Box>
    );
}

export default ScenariosTable;