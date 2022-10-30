import {Skeleton, Stack} from "@mui/material";
import * as React from "react";

export default function TableGhosts(props) {

    if (!props.display) {
        return;
    }

    const ghostsArray = [];

    for (let i = 0; i < props.count; i++) {
        ghostsArray.push(<Skeleton key={i}/>);
    }

    return (
            <Stack p={2} spacing={1}>
                {ghostsArray.map((item) => (item))}
            </Stack>
    );
}

