import {Skeleton, Stack} from "@mui/material";

type Props = {
    display: boolean,
    count: number,
    spacing?: number,
}

export default function TableSkeletons(props: Props) {

    if (!props.display) {
        return;
    }

    const skeletonsArray = [];

    for (let i = 0; i < props.count; i++) {
        skeletonsArray.push(<Skeleton key={i}/>);
    }

    return (
            <Stack spacing={props.spacing ?? 1} sx={{p: 2}}>
                {skeletonsArray.map((item) => (item))}
            </Stack>
    );
}

