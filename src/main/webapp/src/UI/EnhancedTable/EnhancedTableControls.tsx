import {Grid} from "@mui/material";
import TablePagination from "@mui/material/TablePagination";
import {ChangeEvent, ReactNode} from "react";

type EnhancedTableControlsProps = {
    totalRowsCount: number,
    rowsPerPage: number,
    pageNumber: number,
    onPageChange: (newPage: number) => void,
    onRowsPerPageChange: (event: ChangeEvent<HTMLInputElement>) => void
    customControls?: ReactNode
}

export const EnhancedTableControls = (
    {
        totalRowsCount,
        rowsPerPage,
        pageNumber,
        onPageChange,
        onRowsPerPageChange,
        customControls
    }: EnhancedTableControlsProps
) => {
    return <Grid container padding={1} justifyContent="space-between">
        <Grid item>
            {customControls}
        </Grid>
        <Grid item alignContent="flex-end" alignSelf="flex-end">
            <TablePagination
                rowsPerPageOptions={[10, 15, 25, 50]}
                component="div"
                count={totalRowsCount}
                rowsPerPage={rowsPerPage}
                page={pageNumber}
                onPageChange={(_, page) => onPageChange(page)}
                onRowsPerPageChange={onRowsPerPageChange}
            />
        </Grid>
    </Grid>;
};
