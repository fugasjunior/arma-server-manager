import {Grid, Typography} from "@mui/material";
import {useController} from "react-hook-form";
import RawConfigEditor from "../../components/servers/RawConfigEditor";

type Props = {
    name: string;
    label: string;
    containerMd?: number;
};

export const CustomCodeEditorField = ({name, label, containerMd}: Props) => {
    const {field} = useController({name});
    return (
        <Grid size={{xs: 12, md: containerMd ?? 12}}>
            <Typography variant="subtitle2" gutterBottom>{label}</Typography>
            <RawConfigEditor language="cpp" value={field.value ?? ''} onChange={field.onChange}/>
        </Grid>
    );
};
