import {ChangeEvent, ChangeEventHandler, MouseEventHandler} from 'react';
import Box from '@mui/material/Box';
import Paper from '@mui/material/Paper';
import {humanFileSize} from "../../util/util";
import {EnhancedTable, EnhancedTableHeadCell} from "../../UI/EnhancedTable/EnhancedTable.tsx";
import {Button} from "@mui/material";
import {ScenariosTableToolbar} from "./ScenariosTableToolbar.tsx";
import {Arma3ScenarioDto} from "../../dtos/Arma3ScenarioDto.ts";

const headCells: Array<EnhancedTableHeadCell> = [
    {
        id: "name",
        label: "ID",
        searchable: true
    },
    {
        id: "fileSize",
        label: "File size",
        type: "numeric"
    },
    {
        id: "createdOn",
        label: "Created on",
        type: "date"
    },
];

type ScenariosTableProps = {
    rows: Array<Arma3ScenarioDto>,
    selectedScenarioIds: Array<string>,
    onDeleteClicked: MouseEventHandler,
    onRowClick: (rowId: number | string) => void,
    onSelectAllRowsClick: (event: ChangeEvent<HTMLInputElement>) => void,
    onFileChange: ChangeEventHandler,
    onDownloadClicked: (name: string, event: any) => void,
    uploadInProgress: boolean,
    percentUploaded: number
}

export const ScenariosTable = (
    {
        rows,
        selectedScenarioIds,
        onDeleteClicked,
        onRowClick,
        onSelectAllRowsClick,
        onFileChange,
        onDownloadClicked,
        uploadInProgress,
        percentUploaded
    }: ScenariosTableProps
) => {

    const getRows = () => {
        return rows.map(scenarioDto => {
            return {
                id: scenarioDto.name,
                cells: [
                    {
                        id: "name",
                        value: scenarioDto.name,
                        displayValue: <Button onClick={(e) => onDownloadClicked(scenarioDto.name, e)}>
                            {scenarioDto.name}
                        </Button>
                    },
                    {
                        id: "fileSize",
                        value: scenarioDto.fileSize,
                        displayValue: humanFileSize(scenarioDto.fileSize)
                    },
                    {
                        id: "createdOn",
                        value: scenarioDto.createdOn
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
                <EnhancedTable rows={getRows()} selectedRowIds={selectedScenarioIds} headCells={headCells}
                               onRowSelect={onRowClick} title="Scenarios"
                               onSelectAllRowsClick={onSelectAllRowsClick}
                               defaultSortColumnId="name"
                               customTopControls={<ScenariosTableToolbar
                                   onFileChange={onFileChange}
                                   onDeleteClicked={onDeleteClicked}
                                   percentUploaded={percentUploaded}
                                   selectedScenariosCount={selectedScenarioIds.length}
                                   uploadInProgress={uploadInProgress}/>
                               }
                />
            </Paper>
        </Box>
    );
}