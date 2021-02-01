import React, {Component} from "react";
import {getServers, startServer, stopServer} from "../services/additionalServersService"
import PageSizeSelect from "./pagination/pageSizeSelect";
import Pagination from "./pagination/pagination";
import {paginate} from "../util/paginate";

class AdditionalServers extends Component {

    state = {
        servers: [],
        currentPage: 1,
        pageSize: 4,
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
        if(!searchTerm) {
            return servers;
        }
        return servers.filter(s => s.name.toLowerCase().includes(searchTerm.toLowerCase()));
    }

    updateServers = async () => {
        const {data} = await getServers();
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
        if (a.alive && !b.alive) return -1;
        if (!a.alive && b.alive) return 1;
        return a.name.localeCompare(b.name);
    }


    render() {
        const {servers, currentPage, pageSize, searchTerm} = this.state;
        const filteredServers = this.getFilteredServers();
        filteredServers.sort(this.serverComparator);

        return (
                <React.Fragment>
                    <div className="float-right">
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
                                        min={2} max={16} step={2}/>
                    </div>
                    <div className="row row-cols-1 row-cols-md-2">
                        {paginate(filteredServers, currentPage, pageSize).map(s => (
                                <div className="col mb-4">
                                    <div className="card">
                                        {s.imageUrl &&
                                        <img src={s.imageUrl} className="card-img-top" alt={s.name}
                                             style={{maxHeight: "250px"}}
                                        />}
                                        <div className="card-body">
                                            <h5 className="card-title">{s.name}</h5>
                                            {this.renderButton(s)}
                                        </div>
                                    </div>
                                </div>
                        ))}
                    </div>
                    <Pagination itemsCount={servers.length} pageSize={pageSize} currentPage={currentPage}
                                onPageChange={this.handlePageChange}/>
                </React.Fragment>
        )
    }
}

export default AdditionalServers;