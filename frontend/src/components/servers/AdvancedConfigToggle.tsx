import {ReactNode, useState} from "react";
import {Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, FormControlLabel, Switch} from "@mui/material";
import {ConfigOverrideDto, ServerDto} from "../../api/generated";
import {usePermission} from "../../hooks/usePermission";
import {serversApi} from "../../api/client";
import {toast} from "react-toastify";

type Props = {
    configKey: string;
    switchLabel: string;
    enableDialogText: ReactNode;
    serverDraft: ServerDto;
    override: ConfigOverrideDto | undefined;
    onOverrideChange: (override: ConfigOverrideDto | undefined) => void;
};

export default function AdvancedConfigToggle({
    configKey,
    switchLabel,
    enableDialogText,
    serverDraft,
    override,
    onOverrideChange,
}: Props) {
    const hasAdvancedPermission = usePermission('ADVANCED_CONFIG_EDIT') && usePermission('SERVER_SECRETS_VIEW');
    const [dialogType, setDialogType] = useState<'enable' | 'revert' | null>(null);
    const [isSeeding, setIsSeeding] = useState(false);

    if (!hasAdvancedPermission) {
        return null;
    }

    const handleToggleClick = () => {
        setDialogType(override ? 'revert' : 'enable');
    };

    const handleEnableConfirm = async () => {
        setIsSeeding(true);
        try {
            const response = await serversApi.seedConfigOverride({
                seedConfigOverrideRequest: {configKey, server: serverDraft},
            });
            onOverrideChange({
                configKey,
                advanced: true,
                content: response.data.content ?? '',
            });
            setDialogType(null);
        } catch (e: any) {
            toast.error(e?.response?.data?.message ?? "Failed to seed config content");
        } finally {
            setIsSeeding(false);
        }
    };

    const handleRevertConfirm = () => {
        onOverrideChange(undefined);
        setDialogType(null);
    };

    const handleCopyToClipboard = async () => {
        try {
            await navigator.clipboard.writeText(override?.content ?? '');
            toast.success("Config copied to clipboard");
        } catch {
            toast.error("Failed to copy to clipboard");
        }
    };

    return (
        <>
            <FormControlLabel
                control={<Switch checked={!!override} onChange={handleToggleClick}/>}
                label={switchLabel}
            />

            <Dialog open={dialogType === 'enable'} onClose={() => setDialogType(null)}>
                <DialogTitle>Enable advanced config editing?</DialogTitle>
                <DialogContent>
                    <DialogContentText component="div">
                        {enableDialogText}
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setDialogType(null)}>Cancel</Button>
                    <Button onClick={handleEnableConfirm} disabled={isSeeding} autoFocus>
                        {isSeeding ? 'Seeding...' : 'Enable'}
                    </Button>
                </DialogActions>
            </Dialog>

            <Dialog open={dialogType === 'revert'} onClose={() => setDialogType(null)}>
                <DialogTitle>Revert advanced config?</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Reverting discards your advanced config and restores the form fields.
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCopyToClipboard}>Copy raw config to clipboard</Button>
                    <Button onClick={() => setDialogType(null)}>Cancel</Button>
                    <Button color="error" onClick={handleRevertConfirm} autoFocus>Revert</Button>
                </DialogActions>
            </Dialog>
        </>
    );
}
