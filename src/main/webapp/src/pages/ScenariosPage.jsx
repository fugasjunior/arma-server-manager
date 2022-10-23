import React, {useEffect, useState} from "react";
import {deleteScenario, downloadScenario, getScenarios, uploadScenario} from "../services/scenarioService";
import {toast} from "material-react-toastify";
import ScenariosTable from "../components/scenarios/ScenariosTable";

const ScenariosPage = () => {
    const [scenarios, setScenarios] = useState([]);
    const [selected, setSelected] = useState([]);
    const [uploadInProgress, setUploadInProgress] = useState(false);
    const [percentUploaded, setPercentUploaded] = useState(0);

    useEffect(() => {
        refreshScenarios();
    }, []);

    const refreshScenarios = async () => {
        const {data: scenarios} = await getScenarios();
        setScenarios(scenarios);
    };

    const handleProgress = e => {
        const percentCompleted = Math.round((e.loaded * 100) / e.total);
        console.log(percentCompleted);
        setPercentUploaded(percentCompleted);
    }

    const handleFileChange = async e => {
        e.preventDefault();
        const file = e.target.files[0];
        try {
            const formData = new FormData();
            formData.append("file", file);
            setUploadInProgress(true);

            await uploadScenario(formData, {onUploadProgress: handleProgress});
            await refreshScenarios();
            toast.success("Scenario successfully uploaded");
        } catch (ex) {
            console.error(ex);
            toast.error(ex.response.data);
        }
        setUploadInProgress(false);
        setPercentUploaded(0);
    };

    const handleDownload = async (name, e) => {
        e.stopPropagation();
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

    const handleSelectAllClick = (event) => {
        if (event.target.checked) {
            const newSelected = scenarios.map((n) => n.name);
            setSelected(newSelected);
            return;
        }
        setSelected([]);
    };

    const handleClick = (event, name) => {
        const selectedIndex = selected.indexOf(name);
        let newSelected = [];

        if (selectedIndex === -1) {
            newSelected = newSelected.concat(selected, name);
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
            <ScenariosTable rows={scenarios} selected={selected} onSelectAllClick={handleSelectAllClick}
                            onClick={handleClick} onDeleteClicked={handleDelete} onFileChange={handleFileChange}
                            percentUploaded={percentUploaded} uploadInProgress={uploadInProgress}
                            onDownloadClicked={handleDownload}
            />
    )
}

export default ScenariosPage;