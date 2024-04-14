import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import Checkbox from "@mui/material/Checkbox";
import config from "../../config.ts";
import TableBody from "@mui/material/TableBody";
import {EnhancedTableRow} from "./EnhancedTable.tsx";

type EnhancedTableBodyProps = {
    rows: Array<EnhancedTableRow>
    selectedRowIds: Array<string | number>,
    emptyRowsCount: number,
    onRowSelect: (rowId: string | number) => void,
}

export const EnhancedTableBody = (
    {
        rows,
        selectedRowIds,
        emptyRowsCount,
        onRowSelect
    }: EnhancedTableBodyProps
) => {
    const isSelected = (rowId: number | string) => selectedRowIds.indexOf(rowId) !== -1;

    return <TableBody>
        {rows.map((row) => {
            const selected = isSelected(row.id);
            return (
                <TableRow key={row.id} hover onClick={() => onRowSelect(row.id)} role="checkbox"
                          aria-checked={selected} tabIndex={-1} selected={selected}>
                    <TableCell padding="checkbox">
                        <Checkbox checked={selected} color="primary"
                                  inputProps={{
                                      "aria-labelledby": String(row.id),
                                  }}
                        />
                    </TableCell>
                    {row.cells.map((cell, index) => {
                        const valueToShow = cell.displayValue ?? cell.value;
                        if (valueToShow instanceof Date) {
                            return <TableCell key={index}>
                                {valueToShow.toLocaleDateString(undefined, config.dateFormat)}
                            </TableCell>;
                        }
                        return <TableCell key={index}>{valueToShow}</TableCell>;
                    })}
                </TableRow>
            );
        })}
        {emptyRowsCount > 0 && (
            <TableRow style={{height: 33 * emptyRowsCount}}>
                <TableCell colSpan={6}/>
            </TableRow>
        )}
    </TableBody>
}