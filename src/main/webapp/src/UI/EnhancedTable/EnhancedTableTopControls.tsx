import {ReactNode} from "react";
import {alpha} from "@mui/material/styles";
import Typography from "@mui/material/Typography";
import Toolbar from "@mui/material/Toolbar";
import {Divider, Stack, TextField} from "@mui/material";

type EnhancedTableTopControlsProps = {
    title: string,
    selectedRowsCount: number,
    searchTerm?: string,
    onSearchChange: (searchTerm: string) => void
    customControls?: ReactNode
}

export const EnhancedTableTopControls = (
    {
        title,
        selectedRowsCount,
        searchTerm,
        onSearchChange,
        customControls
    }: EnhancedTableTopControlsProps
) => {
    return <Toolbar
        sx={{
            pl: {sm: 2},
            pr: {
                xs: 1,
                sm: 1
            },
            ...(selectedRowsCount > 0 && {
                bgcolor: (theme) =>
                    alpha(theme.palette.primary.main, theme.palette.action.activatedOpacity),
            }),
        }}
    >
        {selectedRowsCount > 0 ? (
            <Typography
                sx={{flex: '1 1 100%'}}
                color="inherit"
                variant="subtitle1"
                component="div"
            >
                {selectedRowsCount} selected
            </Typography>
        ) : (
            <Typography
                sx={{flex: '1 1 100%'}}
                variant="h6"
                id="tableTitle"
                component="div"
            >
                {title}
            </Typography>
        )}

        <Stack direction="row" spacing={2} divider={<Divider orientation={"vertical"} flexItem/>}>
            <TextField label="Search" type="search" variant="standard" value={searchTerm}
                       sx={{minWidth: "120px"}} id="search-field" onChange={(e) => onSearchChange(e.target.value)}
            />

            {customControls}
        </Stack>
    </Toolbar>;
};
