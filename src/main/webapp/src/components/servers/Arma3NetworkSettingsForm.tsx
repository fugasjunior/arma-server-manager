import {Accordion, AccordionDetails, AccordionSummary, Grid, InputBaseComponentProps, Typography} from "@mui/material";

import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import {FormikProps} from "formik";
import {Arma3ServerDto} from "../../dtos/ServerDto.ts";
import {CustomTextField} from "../../UI/Form/CustomTextField.tsx";

type Arma3NetworkSettingsFormProps = {
    formik: FormikProps<Arma3ServerDto>
}

type NetworkSettingField = {
    id: string,
    label: string,
    description: string,
    inputProps?: InputBaseComponentProps
}

const networkSettingFields: Array<NetworkSettingField> = [
    {
        id: 'maxMessagesSend',
        label: 'MaxMsgSend',
        description: 'Maximum number of packets that can be sent in one simulation cycle (default 128)'
    },
    {
        id: 'maxSizeGuaranteed',
        label: 'MaxSizeGuaranteed',
        description: 'Maximum size of guaranteed packet in bytes (default 512)'
    },
    {
        id: 'maxSizeNonguaranteed',
        label: 'MaxSizeNonuaranteed',
        description: 'Maximum size of guaranteed packet in bytes (default 256)'
    },
    {
        id: 'minBandwidth',
        label: 'MinBandwidth',
        description: 'Bandwidth the server is guaranteed to have in bps (default 131072)'
    },
    {id: 'maxBandwidth', label: 'MaxBandwidth', description: 'Bandwidth the server is guaranteed to never have in bps'},
    {
        id: 'maxCustomFileSize',
        label: 'MaxCustomFileSize',
        description: 'Users with custom face or custom sound larger than this size are kicked when trying to connect'
    },
    {
        id: 'minErrorToSend',
        label: 'MinErrorToSend',
        description: 'Minimal error to send updates across network (default: 0.001)',
        inputProps: {min: '0', step: '0.001'}
    },
    {
        id: 'minErrorToSendNear',
        label: 'MinErrorToSendNear',
        description: 'Minimal error to send updates across network for near units (default 0.01)',
        inputProps: {min: '0', step: '0.001'}
    },
    {
        id: 'maxPacketSize',
        label: 'MaxPacketSize',
        description: 'Maximal size of packet sent over network (default 1400)'
    }
];

const Arma3DifficultySettingsForm = ({formik}: Arma3NetworkSettingsFormProps) => {
    return <Accordion>
        <AccordionSummary
            expandIcon={<ExpandMoreIcon/>}
            aria-controls="panel1a-content"
            id="panel1a-header"
        >
            <Typography>Custom network settings (advanced)</Typography>
        </AccordionSummary>
        <AccordionDetails>
            <Typography variant='body2' mb={2}>
                For further info, refer to&nbsp;
                <a href='https://community.bistudio.com/wiki/Arma_3:_Basic_Server_Config_File' target="_blank">
                    Community Wiki: Basic Server Config File
                </a>
            </Typography>
            <Grid container spacing={1}>
                {networkSettingFields.map(field =>
                    <CustomTextField id={'networkSettings.' + field.id}
                                     label={field.label}
                                     helperText={field.description}
                                     type='number'
                                     formik={formik}
                                     inputProps={field.inputProps}
                    />
                )}
            </Grid>
        </AccordionDetails>
    </Accordion>
}

export default Arma3DifficultySettingsForm;