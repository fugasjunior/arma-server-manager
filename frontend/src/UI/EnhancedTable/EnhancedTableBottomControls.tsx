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

export const EnhancedTableBottomControls = (
    {
        totalRowsCount,
        rowsPerPage,
        pageNumber,
        onPageChange,
        onRowsPerPageChange,
        customControls
    }: EnhancedTableControlsProps
) => {
    return <Grid container sx={{padding: 1, justifyContent: "space-between"}}>
        <Grid>
            {customControls}
        </Grid>
        <Grid sx={{alignContent: "flex-end", alignSelf: "flex-end"}}>
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
