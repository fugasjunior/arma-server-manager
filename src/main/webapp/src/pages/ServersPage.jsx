import {deleteServer, getServers, restartServer, startServer, stopServer} from "../services/serversService"
import {Fragment, useEffect, useState} from "react";
import {Link} from "react-router-dom";
import {useInterval} from "../hooks/use-interval";
import "./ServersPage.css";
import ServerListEntry from "../components/serverListEntry";

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

    const isServerWithSamePortRunning = server => {
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
            <>
                <Link to="/servers/new" className="btn btn-lg btn-success mb-4">Create new server</Link>
                <div className="servers">
                    <table className="table servers__list">
                        <tbody>
                        {servers.map(server =>
                                <ServerListEntry key={server.id}
                                                 server={server}
                                                 onStartServer={handleStartServer}
                                                 onStopServer={handleStopServer}
                                                 onRestartServer={handleRestartServer}
                                                 onDeleteServer={handleDeleteServer}
                                                 serverWithSamePortRunning={isServerWithSamePortRunning(server)}
                                />
                        )}
                        </tbody>
                    </table>
                </div>
            </>
    )
}

export default ServersPage;