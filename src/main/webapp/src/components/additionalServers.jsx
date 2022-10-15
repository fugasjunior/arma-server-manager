import React, {Component} from "react";
import {getServers, startServer, stopServer} from "../services/additionalServersService"
import PageSizeSelect from "./pagination/pageSizeSelect";
import Pagination from "./pagination/pagination";
import {paginate} from "../util/paginate";
import "./additionalServers.css";

class AdditionalServers extends Component {

    state = {
        servers: [],
        currentPage: 1,
        pageSize: 10,
        searchTerm: ""
    };

    async componentDidMount() {
        await this.updateServers();
        this.refreshInterval = setInterval(this.updateServers, 7500);
    }

    componentWillUnmount() {
        clearInterval(this.refreshInterval);
    }

    getFilteredServers = () => {
        const {servers, searchTerm} = this.state;
        if (!searchTerm) {
            return servers;
        }
        return servers.filter(s => s.name.toLowerCase().includes(searchTerm.toLowerCase()));
    }

    updateServers = async () => {
        const {data} = await getServers();
        console.log(data);
        this.setState({servers: data.servers});
    };

    handleStart = async (id) => {
        await startServer(id);
        await this.updateServers();
    };

    handleStop = async (id) => {
        await stopServer(id);
        await this.updateServers();
    };

    handlePageSizeChange = e => {
        this.setState({
            currentPage: 1,
            pageSize: parseInt(e.target.value)
        });
    }

    handlePageChange = page => {
        this.setState({currentPage: page});
    }

    handleSearch = e => {
        this.setState({currentPage: 1, searchTerm: e.target.value});
    }

    renderButton = (server) => (
            <button className={"btn btn-block btn-" + (server.alive ? "danger" : "primary")}
                    onClick={server.alive ? () => this.handleStop(server.id) : () => this.handleStart(server.id)}
            >
                {server.alive ? "Stop" : "Start"}
            </button>
    )

    serverComparator = (a, b) => {
        return a.name.localeCompare(b.name);
    }

    render() {
        const {servers, currentPage, pageSize, searchTerm} = this.state;
        const filteredServers = this.getFilteredServers();
        filteredServers.sort(this.serverComparator);

        return (
                <React.Fragment>
                    <div className="row">
                        <div className="col-9">
                            <table className="servers-table table table-hover">
                                <thead>
                                <tr>
                                    <th></th>
                                    <th>Name</th>
                                    <th>Started at</th>
                                    <th></th>
                                </tr>
                                </thead>
                                <tbody>
                                {paginate(filteredServers, currentPage, pageSize).map(s => (
                                        <tr>
                                            <td><img className="servers-table__icon" src={s.imageUrl}
                                                     alt={s.name + " icon"}/></td>
                                            <td className="servers-table__name">{s.name}</td>
                                            <td className="servers-table__started-at">{s.startedAt}</td>
                                            <td className="servers-table__start-stop">{this.renderButton(s)}</td>
                                        </tr>
                                ))}
                                </tbody>
                            </table>
                            <Pagination itemsCount={servers.length} pageSize={pageSize} currentPage={currentPage}
                                        onPageChange={this.handlePageChange}/>
                        </div>
                        <div className="col-3">
                            <div className="mb-3">
                                <label htmlFor="search" className="sr-only">Search</label>
                                <div className="input-group">
                                    <div className="input-group-prepend">
                                        <div className="input-group-text">
                                            <i className="fa fa-search"></i>
                                        </div>
                                    </div>
                                    <input className="form-control" id="search"
                                           placeholder="Search"
                                           value={searchTerm}
                                           onChange={this.handleSearch}
                                    />
                                </div>
                            </div>

                            <PageSizeSelect onPageSizeChange={this.handlePageSizeChange} pageSize={pageSize}
                                            min={5} max={50} step={5}/>
                        </div>
                    </div>
                </React.Fragment>
        )
    }
}

export default AdditionalServers;