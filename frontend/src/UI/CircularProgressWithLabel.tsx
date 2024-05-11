import CircularProgress from "@mui/material/CircularProgress";
import {Box, Typography} from "@mui/material";
import {CircularProgressProps} from "@mui/material/CircularProgress/CircularProgress";

type CircularProgressWithLabelProps = {
    value: number
    color: CircularProgressProps['color'];
}

function CircularProgressWithLabel(props: CircularProgressWithLabelProps) {
    return (
        <Box sx={{position: 'relative', display: 'inline-flex'}}>
            <CircularProgress variant="determinate" {...props} />
            <Box
                sx={{
                    top: 0,
                    left: 0,
                    bottom: 0,
                    right: 0,
                    position: 'absolute',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                }}
            >
                <Typography variant="caption" component="div" color="text.secondary">
                    {`${Math.round(props.value)}%`}
                </Typography>
            </Box>
        </Box>
    );
}

export default CircularProgressWithLabel;