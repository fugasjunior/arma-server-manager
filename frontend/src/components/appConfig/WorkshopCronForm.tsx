import {Button, FormControlLabel, Grid, Switch, Typography} from "@mui/material";
import {TimeField} from "@mui/x-date-pickers";
import {useEffect, useState} from "react";
import {useForm, FormProvider} from "react-hook-form";
import {appSettingsApi} from "../../api/client";
import dayjs from "dayjs";
import {toast} from "react-toastify";

interface FormValues {
    enabled: boolean;
    time: dayjs.Dayjs | null;
}

const parseTimeToDayjs = (time: string | null): dayjs.Dayjs | null => {
    if (!time) return null;
    const parts = time.split(":");
    return dayjs().hour(parseInt(parts[0])).minute(parseInt(parts[1] ?? "0")).second(0);
};

const WorkshopCronForm = () => {
    const [loaded, setLoaded] = useState(false);
    const methods = useForm<FormValues>({
        defaultValues: {enabled: true, time: parseTimeToDayjs("03:00")},
    });

    const {reset, watch} = methods;
    const enabled = watch("enabled");

    useEffect(() => {
        const fetchSettings = async () => {
            try {
                const {data} = await appSettingsApi.getAppSettings();
                reset({
                    enabled: data.automaticModUpdateEnabled ?? true,
                    time: parseTimeToDayjs(data.automaticModUpdateTime ?? null),
                });
            } catch {
                toast.error("Could not fetch workshop update settings.");
            }
            setLoaded(true);
        };
        void fetchSettings();
    }, [reset]);

    const handleSave = async (values: FormValues) => {
        if (values.enabled && !values.time) {
            toast.error("Update time is required when automatic update is enabled.");
            return;
        }
        try {
            const timeStr = values.enabled ? values.time?.format("HH:mm") ?? "03:00" : "03:00";
            await appSettingsApi.updateAppSettings({
                appSettingsDto: {
                    automaticModUpdateEnabled: values.enabled,
                    automaticModUpdateTime: timeStr,
                },
            });
            toast.success("Workshop update settings saved.");
        } catch {
            toast.error("Failed to save workshop update settings.");
        }
    };

    if (!loaded) return null;

    return (
        <>
            <Typography variant="h5" component="h3" sx={{mb: 1}}>Workshop Mod Auto-Update</Typography>
            <Typography variant='body1' sx={{mb: 2}}>
                Schedule automatic daily updates for all workshop mods.
            </Typography>

            <FormProvider {...methods}>
                <form onSubmit={methods.handleSubmit(handleSave)}>
                    <Grid container spacing={3}>
                        <Grid size={12}>
                            <FormControlLabel
                                control={
                                    <Switch
                                        {...methods.register("enabled")}
                                        checked={methods.watch("enabled")}
                                        onChange={(_, checked) => methods.setValue("enabled", checked)}
                                    />
                                }
                                label="Enable automatic update"
                            />
                        </Grid>
                        <Grid size={12}>
                            <TimeField
                                label="Update time"
                                format="HH:mm"
                                size="small"
                                value={methods.watch("time")}
                                disabled={!enabled}
                                required={enabled}
                                onChange={(newValue) => methods.setValue("time", newValue, {shouldValidate: true})}
                            />
                        </Grid>
                        <Grid size={12}>
                            <Button fullWidth variant="contained" type="submit">
                                Save
                            </Button>
                        </Grid>
                    </Grid>
                </form>
            </FormProvider>
        </>
    );
};

export default WorkshopCronForm;
