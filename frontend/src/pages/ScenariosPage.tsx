import {ChangeEvent, useEffect, useState} from "react";
import {deleteScenario, downloadScenario, getScenarios, uploadScenarios} from "../services/scenarioService";
import {toast} from "material-react-toastify";
import {ScenariosTable} from "../components/scenarios/ScenariosTable";
import {Arma3ScenarioDto} from "../dtos/Arma3ScenarioDto.ts";

const ScenariosPage = () => {
    const [scenarios, setScenarios] = useState<Array<Arma3ScenarioDto>>([]);
    const [selected, setSelected] = useState<Array<string>>([]);
    const [uploadInProgress, setUploadInProgress] = useState(false);
    const [percentUploaded, setPercentUploaded] = useState(0);

    useEffect(() => {
        refreshScenarios();
    }, []);

    const refreshScenarios = async () => {
        const {data: scenariosDto} = await getScenarios();
        const scenarios = scenariosDto.scenarios.map((scenario: Arma3ScenarioDto) => {
            const createdOn = scenario.createdOn ? new Date(scenario.createdOn) : "";
            return {
                ...scenario,
                createdOn
            };
        });
        setScenarios(scenarios);
    };

    const handleProgress = (e: ProgressEvent) => {
        const percentCompleted = Math.round((e.loaded * 100) / e.total);
        setPercentUploaded(percentCompleted);
    }

    const handleFileChange = async (e: ChangeEvent<HTMLInputElement>) => {
        e.preventDefault();
        if (!e.target.files) {
            return;
        }

        try {
            const formData = new FormData();
            [...e.target.files].forEach(file => {
                formData.append(`file`, file, file.name);
            })
            setUploadInProgress(true);

            await uploadScenarios(formData, {onUploadProgress: handleProgress});
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
                    return prevState.filter(scenario => selected.indexOf(scenario.name) === -1)
                }
            );

            for (const scenario of selected) {
                await deleteScenario(scenario);
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
            const newSelected = scenarios.map((n) => n.name);
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