import {ReactNode} from "react";
import {alpha} from "@mui/material/styles";
import Typography from "@mui/material/Typography";
import Toolbar from "@mui/material/Toolbar";
import {Box, Divider, Stack, TextField} from "@mui/material";

type EnhancedTableTopControlsProps = {
    title: string,
    selectedRowsCount: number,
    searchTerm?: string,
    onSearchChange: (searchTerm: string) => void
    customControls?: ReactNode
    customLeadingControls?: ReactNode
    customFilterControls?: ReactNode
}

export const EnhancedTableTopControls = (
    {
        title,
        selectedRowsCount,
        searchTerm,
        onSearchChange,
        customControls,
        customLeadingControls,
        customFilterControls
    }: EnhancedTableTopControlsProps
) => {
    const twoRow = !!customLeadingControls || !!customFilterControls;

    const titleNode = selectedRowsCount > 0 ? (
        <Typography sx={{flexShrink: 0}} color="inherit" variant="subtitle1" component="div">
            {selectedRowsCount} selected
        </Typography>
    ) : (
        <Typography sx={{flexShrink: 0}} variant="h6" id="tableTitle" component="div">
            {title}
        </Typography>
    );

    const searchNode = (
        <TextField label="Search" type="search" variant="standard" value={searchTerm}
                   sx={{minWidth: "120px"}} id="search-field" onChange={(e) => onSearchChange(e.target.value)}
        />
    );

    const highlightSx = selectedRowsCount > 0
        ? {bgcolor: (theme: any) => alpha(theme.palette.primary.main, theme.palette.action.activatedOpacity)}
        : {};

    if (!twoRow) {
        return (
            <Toolbar sx={{pl: {sm: 2}, pr: {xs: 1, sm: 1}, flexWrap: 'wrap', ...highlightSx}}>
                {titleNode}
                <Box sx={{flexGrow: 1}}/>
                <Stack direction="row" spacing={2} divider={<Divider orientation="vertical" flexItem/>}>
                    {searchNode}
                    {customControls}
                </Stack>
            </Toolbar>
        );
    }

    return (
        <Box sx={highlightSx}>
            <Toolbar sx={{pl: {sm: 2}, pr: {xs: 1, sm: 1}, flexWrap: 'wrap'}}>
                {titleNode}
                <Box sx={{flexGrow: 1}}/>
                {customLeadingControls && (
                    <Box sx={{flexShrink: 0}}>
                        {customLeadingControls}
                    </Box>
                )}
            </Toolbar>
            <Box sx={{
                display: 'flex',
                alignItems: 'center',
                flexWrap: 'wrap',
                pl: {sm: 2},
                pr: {xs: 1, sm: 1},
                pb: 1,
                gap: 1,
            }}>
                {customFilterControls && <Box>{customFilterControls}</Box>}
                <Box sx={{flexGrow: 1}}/>
                <Stack direction="row" spacing={2} divider={<Divider orientation="vertical" flexItem/>}>
                    {searchNode}
                    {customControls}
                </Stack>
            </Box>
        </Box>
    );
};
