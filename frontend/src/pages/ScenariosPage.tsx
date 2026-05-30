import {ChangeEvent, useState} from "react";
import {scenariosApi} from "../api/client";
import {downloadScenario} from "../api/downloads";
import {toast} from "react-toastify";
import {ScenariosTable} from "../components/scenarios/ScenariosTable";
import {useArma3Scenarios} from "../hooks/queries/useArma3Scenarios";
import {useQueryClient} from "@tanstack/react-query";
import {queryKeys} from "../api/queryKeys";
import {usePermission} from "../hooks/usePermission";

const ScenariosPage = () => {
    const queryClient = useQueryClient();
    const canViewScenarios = usePermission("SCENARIO_VIEW");
    const {data: scenarios = []} = useArma3Scenarios({enabled: canViewScenarios});
    const [selected, setSelected] = useState<Array<string>>([]);
    const [uploadInProgress, setUploadInProgress] = useState(false);
    const [percentUploaded, setPercentUploaded] = useState(0);

    const handleFileChange = async (e: ChangeEvent<HTMLInputElement>) => {
        e.preventDefault();
        if (!e.target.files) {
            return;
        }

        try {
            setUploadInProgress(true);
            await scenariosApi.uploadScenarios({file: Array.from(e.target.files)});
            toast.success("Scenarios successfully uploaded");
            await queryClient.invalidateQueries({queryKey: queryKeys.arma3Scenarios});
        } catch (ex: any) {
            console.error(ex);
            toast.error(ex.response.data);
        }
        setUploadInProgress(false);
        setPercentUploaded(0);
    };

    const handleDownload = async (name: string, event: Event) => {
        event.stopPropagation();
        try {
            downloadScenario(name);
        } catch (e) {
            console.error(e);
            toast.error("Error during scenario download");
        }
    };

    const handleDelete = async () => {
        try {
            for (const scenario of selected) {
                await scenariosApi.deleteScenario({name: scenario});
            }
            toast.success("Scenario(s) deleted successfully");
            setSelected([]);
            await queryClient.invalidateQueries({queryKey: queryKeys.arma3Scenarios});
        } catch (e) {
            console.error(e);
            toast.error("Error deleting scenarios");
        }
    };

    const handleSelectAllClick = (event: ChangeEvent<HTMLInputElement>) => {
        if (event.target.checked) {
            const newSelected = scenarios.map((n) => n.name ?? "").filter(Boolean);
            setSelected(newSelected);
            return;
        }
        setSelected([]);
    };

    const handleClick = (id: string | number) => {
        if (typeof id !== "string") {
            return;
        }

        const selectedIndex = selected.indexOf(id);
        let newSelected: Array<string> = [];

        if (selectedIndex === -1) {
            newSelected = newSelected.concat(selected, id);
        } else if (selectedIndex === 0) {
            newSelected = newSelected.concat(selected.slice(1));
        } else if (selectedIndex === selected.length - 1) {
            newSelected = newSelected.concat(selected.slice(0, -1));
        } else if (selectedIndex > 0) {
            newSelected = newSelected.concat(
                selected.slice(0, selectedIndex),
                selected.slice(selectedIndex + 1),
            );
        }

        setSelected(newSelected);
    };

    return (
        <ScenariosTable rows={scenarios} selectedScenarioIds={selected} onSelectAllRowsClick={handleSelectAllClick}
                        onRowClick={handleClick} onDeleteClicked={handleDelete} onFileChange={handleFileChange}
                        percentUploaded={percentUploaded} uploadInProgress={uploadInProgress}
                        onDownloadClicked={handleDownload}
        />
    );
};

export default ScenariosPage;
