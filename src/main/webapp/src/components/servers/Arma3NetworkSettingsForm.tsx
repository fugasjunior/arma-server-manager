import {Accordion, AccordionDetails, AccordionSummary, Grid, Typography} from "@mui/material";

import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import {FormikProps} from "formik";
import {Arma3ServerDto} from "../../dtos/ServerDto.ts";
import {CustomTextField} from "../../UI/Form/CustomTextField.tsx";

type Arma3DifficultySettingsFormProps = {
    formik: FormikProps<Arma3ServerDto>
}

const networkSettingFields = [
    ['maxMessagesSend', 'MaxMsgSend', 'Maximum number of packets that can be sent in one simulation cycle (default 128)'],
    ['maxSizeGuaranteed', 'MaxSizeGuaranteed', 'Maximum size of guaranteed packet in bytes (default 512)'],
    ['maxSizeNonguaranteed', 'MaxSizeNonuaranteed', 'Maximum size of guaranteed packet in bytes (default 256)'],
    ['minBandwidth', 'MinBandwidth', 'Bandwidth the server is guaranteed to have in bps (default 131072)'],
    ['maxBandwidth', 'MaxBandwidth', 'Bandwidth the server is guaranteed to never have in bps'],
    ['minErrorToSend', 'MinErrorToSend', 'Minimal error to send updates across network (default: 0.001)'],
    ['minErrorToSendNear', 'MinErrorToSendNear', 'Minimal error to send updates across network for near units (default 0.01)'],
    ['maxPacketSize', 'MaxPacketSize', 'Maximal size of packet sent over network (default 1400)'],
    ['maxCustomFileSize', 'MaxCustomFileSize', 'Users with custom face or custom sound larger than this size are kicked when trying to connect']
];

const Arma3DifficultySettingsForm = ({formik}: Arma3DifficultySettingsFormProps) => {
    return <Accordion>
        <AccordionSummary
            expandIcon={<ExpandMoreIcon/>}
            aria-controls="panel1a-content"
            id="panel1a-header"
        >
            <Typography>Custom network settings (advanced)</Typography>
        </AccordionSummary>
        <AccordionDetails>
            <Typography variant='body2' mb={2}>For further info, refer to <a
                href='https://community.bistudio.com/wiki/Arma_3:_Basic_Server_Config_File' target="_blank">Community
                Wiki: Basic Server Config File</a></Typography>
            <Grid container spacing={1}>
                {networkSettingFields.map(field =>
                    <CustomTextField id={'networkSettings.' + field[0]}
                                     label={field[1]}
                                     helperText={field[2]}
                                     type='number'
                                     formik={formik}
                    />
                )}
            </Grid>
        </AccordionDetails>
    </Accordion>
}

export default Arma3DifficultySettingsForm;