import {deleteServer, getServers, restartServer, startServer, stopServer} from "../services/serversService"
import {Fragment, useEffect, useState} from "react";
import {Link} from "react-router-dom";
import {useInterval} from "../hooks/use-interval";
import "./ServersPage.css";

const ServersPage = () => {
    const [servers, setServers] = useState([]);

    useEffect(() => {
        fetchServers()
    }, [])

    useInterval(async () => {
        fetchServers();
    }, 2000);

    const fetchServers = async () => {
        const {data: servers} = await getServers();
        setServers(servers.servers);
    }

    const isServerRunning = server => (server.instanceInfo && server.instanceInfo.alive);

    const serverWithSamePortRunning = server => {
        const activeServerWithSamePort = servers.filter(s => s !== server)
        .filter(s => s.instanceInfo && s.instanceInfo.alive)
        .filter(s => s.port === server.port || s.queryPort === server.queryPort);
        if (activeServerWithSamePort[0]) {
            return activeServerWithSamePort[0];
        }
        return null;
    }

    const handleStartServer = async id => {
        const newServers = [...servers];
        const server = newServers.find(s => s.id === id);
        server.instanceInfo = {
            alive: true
        }
        setServers(newServers);
        await startServer(id);
    };

    const handleStopServer = async id => {
        const newServers = [...servers];
        const server = newServers.find(s => s.id === id);
        server.instanceInfo = {
            alive: false
        }
        setServers(newServers);
        await stopServer(id);
    };

    const handleRestartServer = async id => {
        const newServers = [...servers];
        const server = newServers.find(s => s.id === id);
        server.instanceInfo = {
            alive: true
        }
        setServers(newServers);
        await restartServer(id);
    };

    const handleDeleteServer = async id => {
        // TODO add confirmation modal
        const newServers = servers.filter(s => s.id !== id);
        setServers(newServers);
        await deleteServer(id);
    }

    return (
            <div className="servers">
                <table className="table servers__list">
                    {servers.map(server => (
                            <tr className="server" key={server.id}>
                                <td><img className="server__icon" src="./img/arma3_icon.png" alt="Arma 3 icon"/></td>
                                <td className="server__info">
                                    <p className="server__name">{server.name}</p>
                                    <p>Port: {server.port} (query: {server.queryPort})</p>
                                    {server.activeMods && <p>Mods active</p>}
                                    {server.activeDLCs && <p>Creator DLC(s) active</p>}
                                </td>
                                <td>{isServerRunning(server) ?
                                        <Fragment>
                                            <button type="button" className="btn btn-danger"
                                                    onClick={() => handleStopServer(server.id)}>Stop
                                            </button>
                                            <button type="button" className="btn btn-secondary"
                                                    onClick={() => handleRestartServer(server.id)}>Restart
                                            </button>
                                        </Fragment>
                                        : <button type="button" className="btn btn-primary"
                                                  disabled={serverWithSamePortRunning(server)}
                                                  onClick={() => handleStartServer(server.id)}>
                                            Start
                                        </button>
                                }
                                    <Link className="btn btn-info" to={"/server/" + server.id}>Settings</Link>
                                </td>
                                <td>
                                    {!isServerRunning(server) &&
                                            <button className="btn btn-danger"
                                                    onClick={() => handleDeleteServer(server.id)}>Delete
                                            </button>}
                                </td>
                                {isServerRunning(server) && <td>
                                    <p>Started on: {server.instanceInfo.startedAt}</p>
                                    <p>Players: {server.instanceInfo.playersOnline} / {server.instanceInfo.maxPlayers}</p>
                                    {server.instanceInfo.version && <p>Version: {server.instanceInfo.version}</p>}
                                    {server.instanceInfo.map && <p>Map: {server.instanceInfo.map}</p>}
                                    {server.instanceInfo.description &&
                                            <p>Description: {server.instanceInfo.description}</p>}
                                </td>
                                }
                            </tr>))
                    }
                </table>
            </div>
    )
}

export default ServersPage;