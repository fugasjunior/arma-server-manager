import Box from '@mui/material/Box';
import Paper from '@mui/material/Paper';
import Chip from '@mui/material/Chip';
import {EnhancedTable, EnhancedTableHeadCell} from "../../UI/EnhancedTable/EnhancedTable.tsx";
import {Arma3ProvidedKeyDto} from "../../api/generated";

const headCells: Array<EnhancedTableHeadCell> = [
    {
        id: "name",
        label: "Name",
        searchable: true
    },
    {
        id: "source",
        label: "Source",
    },
];

type ActiveKeysTableProps = {
    rows: Array<Arma3ProvidedKeyDto>,
}

export const ActiveKeysTable = ({rows}: ActiveKeysTableProps) => {
    const getRows = () => rows.map((key, idx) => ({
        id: `${key.source}/${key.name}/${idx}`,
        cells: [
            {
                id: "name",
                value: key.name ?? "",
            },
            {
                id: "source",
                value: key.source ?? "",
                displayValue: <Chip label={key.source ?? ""} size="small" variant="outlined" sx={{opacity: 0.75}}/>,
            },
        ]
    }));

    return (
        <Box sx={{width: '100%'}}>
            <Paper sx={{width: '100%', mb: 2}}>
                <EnhancedTable
                    rows={getRows()}
                    headCells={headCells}
                    title="Active Keys"
                    id="active-keys"
                    defaultSortColumnId="name"
                    selectable={false}
                />
            </Paper>
        </Box>
    );
};
