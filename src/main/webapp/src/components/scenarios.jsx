import React, {Component} from "react";
import {deleteScenario, downloadScenario, getScenarios, uploadScenario} from "../services/scenarioService";
import {humanFileSize} from "../util/util";
import {toast} from "react-toastify";

class Scenarios extends Component {

    state = {
        scenarios: [],
        file: null,
        upload: {
            inProgress: false,
            status: 0,
        }
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

    handleProgress = e => {
        const percentCompleted = Math.round((e.loaded * 100) / e.total);
        this.setState({upload: {inProgress: true, status: percentCompleted}});
    }

    handleSubmit = async e => {
        e.preventDefault();

        const {file, scenarios} = this.state;

        try {
            const formData = new FormData();
            formData.append("file", file);
            this.setState({upload: {inProgress: true}});

            await uploadScenario(formData, {onUploadProgress: this.handleProgress});
            toast.success("Scenario successfully uploaded");

            const newScenarios = [...scenarios];
            newScenarios.push({name: file.name, fileSize: file.size});

            this.setState({
                file: null,
                scenarios: newScenarios,
                upload: {
                    inProgress: false,
                    status: 0
                }
            });
        } catch (e) {
            toast.error("Error during scenario upload");
        }
    };

    handleDownload = async name => {
        try {
            const {data} = await downloadScenario(name);
            const url = window.URL.createObjectURL(new Blob([data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', name);
            document.body.appendChild(link);
            link.click();
        } catch (e) {
            console.error(e);
            toast.error("Error during scenario download");
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
                                <th/>
                            </tr>
                            </thead>
                            <tbody>
                            {scenarios.map(s => <tr>
                                <td>{s.name}</td>
                                <td>{s.fileSize && humanFileSize(s.fileSize)}</td>
                                <td>
                                    <button className="btn btn-primary btn-sm"
                                            onClick={() => this.handleDownload(s.name)}>
                                        <i className="fa fa-download" aria-hidden="true"></i>
                                        &nbsp;
                                        Download
                                    </button>
                                </td>
                                <td>
                                    <button className="btn btn-danger btn-sm"
                                            onClick={() => this.handleDelete(s.name)}>
                                        <i className="fa fa-trash-o" aria-hidden="true"></i>
                                        &nbsp;
                                        Delete
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
                        {this.state.upload.inProgress &&
                        <div className="progress">
                            <div className="progress-bar"
                                 role="progressbar"
                                 style={{width: this.state.upload.status + "%"}}
                                 aria-valuenow="0"
                                 aria-valuemin="0"
                                 aria-valuemax="100"/>
                        </div>
                        }
                    </div>
                </div>
            </React.Fragment>
        )
    };
}

export default Scenarios;