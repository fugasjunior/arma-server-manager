import {ChangeEvent, useEffect, useState} from "react";
import {scenariosApi} from "../api/client";
import {downloadScenario} from "../api/downloads";
import {Arma3ScenarioDto} from "../api/generated";
import {toast} from "react-toastify";
import {ScenariosTable} from "../components/scenarios/ScenariosTable";

const ScenariosPage = () => {
    const [scenarios, setScenarios] = useState<Array<Arma3ScenarioDto>>([]);
    const [selected, setSelected] = useState<Array<string>>([]);
    const [uploadInProgress, setUploadInProgress] = useState(false);
    const [percentUploaded, setPercentUploaded] = useState(0);

    useEffect(() => {
        refreshScenarios();
    }, []);

    const refreshScenarios = async () => {
        const {data: scenariosDto} = await scenariosApi.getArma3Scenarios();
        const scenarios = scenariosDto.scenarios ?? [];
        setScenarios(scenarios);
    };

    const handleFileChange = async (e: ChangeEvent<HTMLInputElement>) => {
        e.preventDefault();
        if (!e.target.files) {
            return;
        }

        try {
            setUploadInProgress(true);

            await scenariosApi.uploadScenarios({file: Array.from(e.target.files)});
            await refreshScenarios();
            toast.success("Scenarios successfully uploaded");
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
            setScenarios(prevState => {
                    return prevState.filter(scenario => selected.indexOf(scenario.name ?? "") === -1)
                }
            );

            for (const scenario of selected) {
                await scenariosApi.deleteScenario({name: scenario});
            }
            toast.success("Scenario(s) deleted successfully");
            setSelected([]);
        } catch (e) {
            console.error(e);
            toast.error("Error deleting scenarios");
        }
    }

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
    )
}

export default ScenariosPage;