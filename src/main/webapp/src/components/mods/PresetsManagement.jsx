import * as React from "react";
import {useEffect, useState} from "react";
import {getModPresets} from "../../services/modPresetsService";
import Table from "@mui/material/Table";
import TableContainer from "@mui/material/TableContainer";
import Paper from "@mui/material/Paper";
import TableCell from "@mui/material/TableCell";
import TableRow from "@mui/material/TableRow";
import TableHead from "@mui/material/TableHead";
import TableBody from "@mui/material/TableBody";
import {Box, Toolbar} from "@mui/material";
import Typography from "@mui/material/Typography";
import IconButton from '@mui/material/IconButton';
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import SERVER_NAMES from "../../util/serverNames";

export default function PresetsManagement() {
    const [presets, setPresets] = useState([]);

    useEffect(() => {
        const fetchPresets = async () => {
            const {data: presetsDto} = await getModPresets();
            setPresets(presetsDto.presets);
        }
        fetchPresets();
    });

    const getSummarizedModsList = (mods) => {
        const CUTOFF_LENGTH = 55;
        const modNames = mods.map(mod => mod.shortName);
        modNames.sort((a, b) => a.localeCompare(b));
        let summarizedList = modNames[0];
        let i = 1;
        while (summarizedList.length < CUTOFF_LENGTH && i < modNames.length) {
            summarizedList += ", " + modNames[i++];
        }
        if (summarizedList.length >= CUTOFF_LENGTH) {
            summarizedList += " and " + (modNames.length - i) + " more...";
        }
        return summarizedList;
    }

    return (
            <Box sx={{width: '100%'}}>
                <Paper sx={{width: '100%'}}>
                    <Toolbar sx={{pl: {sm: 2}, pr: {xs: 1, sm: 1}}}>
                        <Typography sx={{flex: '1 1 100%'}} variant="h6" id="tableTitle" component="div">
                            Presets
                        </Typography>
                    </Toolbar>
                    <TableContainer component={Paper}>
                        <Table sx={{minWidth: 650}} aria-label="simple table">
                            <TableHead>
                                <TableRow>
                                    <TableCell>Name</TableCell>
                                    <TableCell>Type</TableCell>
                                    <TableCell>Mods</TableCell>
                                    <TableCell align="right">Mods count</TableCell>
                                    <TableCell></TableCell>
                                    <TableCell></TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {presets.map((preset) => (
                                        <TableRow
                                                key={preset.id}
                                                sx={{'&:last-child td, &:last-child th': {border: 0}}}
                                        >
                                            <TableCell component="th" scope="row">
                                                {preset.name}
                                            </TableCell>
                                            <TableCell>
                                                {SERVER_NAMES[preset.type]}
                                            </TableCell>
                                            <TableCell>
                                                {getSummarizedModsList(preset.mods)}
                                            </TableCell>
                                            <TableCell align="right">
                                                {preset.mods.length}
                                            </TableCell>
                                            <TableCell>
                                                <IconButton aria-label="edit">
                                                    <EditIcon color="primary"/>
                                                </IconButton>
                                            </TableCell>
                                            <TableCell>
                                                <IconButton aria-label="delete">
                                                    <DeleteIcon color="error"/>
                                                </IconButton>
                                            </TableCell>
                                        </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                </Paper>
            </Box>
    )
}