import React, {Component} from "react";
import {deleteScenario, getScenarios, uploadScenario} from "../services/scenarioService";
import {humanFileSize} from "../util/util";
import {toast} from "react-toastify";

class Scenarios extends Component {

    state = {
        scenarios: [],
        file: null
    };

    async componentDidMount() {
        await this.refreshScenarios();
    };

    refreshScenarios = async () => {
        const {data: scenarios} = await getScenarios();
        this.setState({scenarios});
    };

    handleRefresh = this.refreshScenarios;

    handleFileChange = e => {
        this.setState({file: e.target.files[0]});
    };

    handleSubmit = async e => {
        e.preventDefault();

        const {file, scenarios} = this.state;

        try {
            const formData = new FormData();
            formData.append("file", file);

            await uploadScenario(formData);

            toast.success("Scenario successfully uploaded");

            const newScenarios = [...scenarios];
            newScenarios.push({name: file.name, fileSize: file.size});

            this.setState({file: null, scenarios: newScenarios});
        } catch (e) {
            toast.error("Error during scenario upload");
        }
    };

    handleDelete = async (name) => {
        const origScenarios = [...this.state.scenarios];
        const scenarios = origScenarios.filter(s => s.name !== name);
        this.setState({scenarios});

        try {
            await deleteScenario(name);
            toast.success("Scenario deleted successfully");
        } catch (e) {
            this.setState({scenarios: origScenarios});
            console.error(e);
            toast.error("Error deleting scenario " + name);
        }
    }

    render() {
        const {scenarios} = this.state;

        return (
            <React.Fragment>
                <h2>Scenarios</h2>

                <div className="row">
                    <div className="col-9">
                        <button className="btn btn-primary mb-1"
                                onClick={this.handleRefresh}>
                            Refresh
                        </button>

                        <table className="table">
                            <thead>
                            <tr>
                                <th scope="col">
                                    Mod name
                                </th>
                                <th scope="col">
                                    File size
                                </th>
                                <th/>
                            </tr>
                            </thead>
                            <tbody>
                            {scenarios.map(s => <tr>
                                <td>{s.name}</td>
                                <td>{s.fileSize && humanFileSize(s.fileSize)}</td>
                                <td>
                                    <button className="btn btn-danger btn-sm"
                                            onClick={() => this.handleDelete(s.name)}>Delete
                                    </button>
                                </td>
                            </tr>)}
                            </tbody>
                        </table>
                    </div>

                    <div className="col-3">
                        <form onSubmit={this.handleSubmit}>
                            <div className="form-group">
                                <label htmlFor="scenario">Upload scenario</label>
                                <input type="file"
                                       className="form-control-file"
                                       id="scenario" name="scenario"
                                       onChange={this.handleFileChange}/>
                                <button type="submit"
                                        disabled={!this.state.file}
                                        className="btn btn-primary btn-sm mt-2">
                                    Upload
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </React.Fragment>
        )
    };
}

export default Scenarios;