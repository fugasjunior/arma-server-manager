import React, {Component} from "react";
import {getServers, startServer, stopServer} from "../services/additionalServersService"

class AdditionalServers extends Component {

    state = {
        servers: []
    };

    async componentDidMount() {
        await this.updateServers();
        this.refreshInterval = setInterval(this.updateServers, 7500);
    };

    componentWillUnmount() {
        clearInterval(this.refreshInterval);
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

    renderButton = (server) => (
        <button className={"btn btn-block btn-" + (server.alive ? "danger" : "primary")}
                onClick={server.alive ? () => this.handleStop(server.id) : () => this.handleStart(server.id)}
        >
            {server.alive ? "Stop" : "Start"}
        </button>
    )

    render() {
        const {servers} = this.state;

        return (
            <div className="row row-cols-1 row-cols-md-2">
                {servers.map(s => (
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
        )
    };
}

export default AdditionalServers;