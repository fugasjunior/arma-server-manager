import {Box, Grid} from "@mui/material";
import styles from "./ListBuilder.module.css";
import React from "react";

export default function ListBuilderContainer(props) {

    function getGridItemColumns(index) {
        const elementsEven = !!(props.children.length % 2);
        if (elementsEven && index === 0) {
            return 12;
        }
        return 6;
    }

    return (
            <Box className={styles.box} overflow="auto" sx={{
                bgcolor: 'background.paper',
                boxShadow: 24,
                p: 4,
            }}>
                <Grid container spacing={4} p={4}>
                    {props.children.map((element, i) => (
                            <Grid item key={i} xs={getGridItemColumns(i)}>
                                {element}
                            </Grid>
                    ))}
                </Grid>
            </Box>
    );
}