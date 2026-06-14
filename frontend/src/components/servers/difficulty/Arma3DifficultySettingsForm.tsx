import {Accordion, AccordionDetails, AccordionSummary, Box, FormGroup, Grid, Typography} from "@mui/material";

import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import {ThreeStateFlagField} from "../../../UI/Form/ThreeStateFlagField.tsx";
import {Arma3AiSkillSettings} from "./Arma3AiSkillSettings.tsx";
import {SwitchField} from "../../../UI/Form/SwitchField.tsx";
import {BOOLEAN_FIELDS, FOUR_STATE_FLAG_FIELDS, THREE_STATE_FLAG_FIELDS} from "./fieldDefinitions.ts";
import {FourStateFlagField} from "../../../UI/Form/FourStateFlagField.tsx";
import AdvancedConfigToggle from "../AdvancedConfigToggle.tsx";
import AdvancedConfigSection from "../AdvancedConfigSection.tsx";
import {ConfigOverrideDto, ServerDto} from "../../../api/generated";

interface Arma3DifficultySettingsFormProps {
    canModify?: boolean;
    serverDraft: ServerDto;
    override: ConfigOverrideDto | undefined;
    onOverrideChange: (override: ConfigOverrideDto | undefined) => void;
}

const Arma3DifficultySettingsForm = ({canModify, serverDraft, override, onOverrideChange}: Arma3DifficultySettingsFormProps) => {
    return <Accordion>
        <AccordionSummary
            expandIcon={<ExpandMoreIcon/>}
            aria-controls="panel1a-content"
            id="panel1a-header"
        >
            <Typography sx={{flexGrow: 1}}>Custom difficulty settings</Typography>
            <Box onClick={e => e.stopPropagation()}>
                <AdvancedConfigToggle
                    configKey="ARMA3_PROFILE"
                    switchLabel="Advanced edit (server.armaprofile)"
                    enableDialogText="This will replace the difficulty settings form with a raw text editor.
                        Your current difficulty settings will be used to generate an initial profile."
                    serverDraft={serverDraft}
                    override={override}
                    onOverrideChange={onOverrideChange}
                />
            </Box>
        </AccordionSummary>
        <AccordionDetails>
            {!override ? (
                <fieldset disabled={!canModify}
                          style={{border: "none", padding: 0, margin: 0, minInlineSize: "auto"}}>
                    <Grid container>
                        <Grid size={{xs: 12, md: 4}}>
                            <FormGroup>
                                {BOOLEAN_FIELDS.map(field =>
                                    <SwitchField key={field.name} name={field.name} label={field.label}/>
                                )}
                            </FormGroup>
                        </Grid>
                        <Grid size={{xs: 12, md: 4}}>
                            {THREE_STATE_FLAG_FIELDS.map((field, index) =>
                                index < 4 && <ThreeStateFlagField key={field.name} {...field}/>
                            )}
                            <Arma3AiSkillSettings disabled={!canModify}/>
                        </Grid>
                        <Grid size={{xs: 12, md: 4}}>
                            {THREE_STATE_FLAG_FIELDS.map((field, index) =>
                                index >= 4 && <ThreeStateFlagField key={field.name} {...field}/>
                            )}
                            {FOUR_STATE_FLAG_FIELDS.map((field) =>
                                <FourStateFlagField key={field.name} {...field}/>
                            )}
                        </Grid>
                    </Grid>
                </fieldset>
            ) : (
                <AdvancedConfigSection
                    configKey="ARMA3_PROFILE"
                    label="server.armaprofile"
                    override={override}
                    onOverrideChange={onOverrideChange}
                />
            )}
        </AccordionDetails>
    </Accordion>
}

export default Arma3DifficultySettingsForm;
