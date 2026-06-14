import {useEffect, useState} from "react";
import {Box, Button, Stack, Typography} from "@mui/material";
import {ConfigOverrideDto} from "../../api/generated";
import {usePermission} from "../../hooks/usePermission";
import RawConfigEditor from "./RawConfigEditor";
import {toast} from "react-toastify";

type Props = {
    configKey: string;
    label: string;
    override: ConfigOverrideDto | undefined;
    onOverrideChange: (override: ConfigOverrideDto | undefined) => void;
};

const languageFor = (configKey: string): 'json' | 'cpp' =>
    configKey === 'REFORGER_JSON' ? 'json' : 'cpp';

export default function AdvancedConfigSection({configKey, label, override, onOverrideChange}: Props) {
    const hasAdvancedPermission = usePermission('ADVANCED_CONFIG_EDIT') && usePermission('SERVER_SECRETS_VIEW');
    const [editorContent, setEditorContent] = useState(override?.content ?? '');
    const language = languageFor(configKey);

    useEffect(() => {
        setEditorContent(override?.content ?? '');
    }, [override?.content]);

    if (!hasAdvancedPermission) {
        return (
            <Box sx={{p: 2, bgcolor: 'action.hover', borderRadius: 1}}>
                <Typography variant="body2" color="text.secondary">
                    This config is in advanced mode and is managed by an authorized user.
                    These settings do not apply.
                </Typography>
            </Box>
        );
    }

    const handleFormat = () => {
        try {
            const formatted = JSON.stringify(JSON.parse(editorContent), null, 2);
            setEditorContent(formatted);
            onOverrideChange({configKey, advanced: true, content: formatted});
        } catch {
            toast.error('Invalid JSON — cannot format');
        }
    };

    return (
        <Box>
            <Stack direction="row" sx={{alignItems: 'center', justifyContent: 'space-between', mb: 0.5}}>
                <Typography variant="subtitle2">{label}</Typography>
                {language === 'json' && (
                    <Button size="small" onClick={handleFormat}>Format JSON</Button>
                )}
            </Stack>
            <RawConfigEditor
                language={language}
                value={editorContent}
                onChange={(content) => {
                    setEditorContent(content);
                    onOverrideChange({configKey, advanced: true, content});
                }}
            />
        </Box>
    );
}
