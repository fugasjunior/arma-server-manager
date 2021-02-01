import React, {Component} from "react";
import {deleteScenario, downloadScenario, getScenarios, uploadScenario} from "../services/scenarioService";
import {humanFileSize} from "../util/util";
import {toast} from "react-toastify";
import Pagination from "./pagination/pagination";
import {paginate} from "../util/paginate";
import PageSizeSelect from "./pagination/pageSizeSelect";

class Scenarios extends Component {

    state = {
        currentPage: 1,
        pageSize: 10,
        scenarios: [],
        file: null,
        upload: {
            inProgress: false,
            status: 0,
        }
    };

    async componentDidMount() {
        await this.refreshScenarios();
    }

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
        } catch (ex) {
            toast.error("Error during scenario upload");
        }
    };

    handleDownload = async name => {
        try {
            downloadScenario(name);
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

    handlePageChange = page => {
        this.setState({currentPage: page});
    }

    handlePageSizeChange = e => {
        this.setState({pageSize: e.target.value, currentPage: 1});
    }

    render() {
        const {currentPage, pageSize, file, upload, scenarios} = this.state;

        return (
                <React.Fragment>
                    <h2>Scenarios</h2>

                    <div className="row">
                        <div className="col-9">
                            {scenarios.length > 0 ? (
                                    <React.Fragment>
                                        <button className="btn btn-primary mb-1"
                                                onClick={this.handleRefresh}>
                                            Refresh
                                        </button>
                                        <div className="float-right">
                                            <PageSizeSelect
                                                    pageSize={pageSize}
                                                    onPageSizeChange={this.handlePageSizeChange}/>
                                        </div>
                                        <table className="table">
                                            <thead>
                                            <tr>
                                                <th scope="col">
                                                    File name
                                                </th>
                                                <th scope="col">
                                                    Size
                                                </th>
                                                <th/>
                                                <th/>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            {paginate(scenarios, currentPage, pageSize).map(s =>
                                                    <tr key={s.name}>
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
                                                    </tr>
                                            )}
                                            </tbody>
                                        </table>
                                        <Pagination itemsCount={scenarios.length}
                                                    currentPage={currentPage}
                                                    pageSize={pageSize}
                                                    onPageChange={this.handlePageChange}/>
                                    </React.Fragment>
                            ) : <p>There are no scenarios.</p>}

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
                                            disabled={!file}
                                            className="btn btn-primary btn-sm mt-2">
                                        Upload
                                    </button>
                                </div>
                            </form>
                            {upload.inProgress &&
                            <div className="progress">
                                <div className="progress-bar"
                                     role="progressbar"
                                     style={{width: upload.status + "%"}}
                                     aria-valuenow="0"
                                     aria-valuemin="0"
                                     aria-valuemax="100"/>
                            </div>
                            }
                        </div>
                    </div>
                </React.Fragment>
        )
    }
}

export default Scenarios;