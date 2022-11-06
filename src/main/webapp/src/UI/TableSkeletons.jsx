import {Skeleton, Stack} from "@mui/material";
import * as React from "react";

export default function TableSkeletons(props) {

    if (!props.display) {
        return;
    }

    const skeletonsArray = [];

    for (let i = 0; i < props.count; i++) {
        skeletonsArray.push(<Skeleton key={i}/>);
    }

    return (
            <Stack p={2} spacing={props.spacing ?? 1}>
                {skeletonsArray.map((item) => (item))}
            </Stack>
    );
}

