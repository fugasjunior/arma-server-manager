import {Fragment} from "react";
import {Link} from "react-router-dom";

const ServerListEntry = (props) => {
    const {server, onStartServer, onStopServer, onRestartServer, onDeleteServer, serverWithSamePortRunning} = props;

    const serverRunning = server.instanceInfo && server.instanceInfo.alive;

    return (
            <tr className="server" key={server.id}>
                <td><img className="server__icon" src="./img/arma3_icon.png" alt="Arma 3 icon"/></td>
                <td className="server__info">
                    <p className="server__name">{server.name}</p>
                    <p>Port: {server.port} (query: {server.queryPort})</p>
                    {server.description && <p>{server.description}</p>}
                    {server.activeMods.length > 0 && <p>Mods active</p>}
                    {server.activeDLCs.length > 0 && <p>Creator DLC(s) active</p>}
                </td>
                <td>{serverRunning ?
                        <>
                            <button type="button" className="btn btn-danger"
                                    onClick={() => onStopServer(server.id)}>Stop
                            </button>
                            <button type="button" className="btn btn-secondary"
                                    onClick={() => onRestartServer(server.id)}>Restart
                            </button>
                        </>
                        : <button type="button" className="btn btn-primary"
                                  disabled={serverWithSamePortRunning}
                                  onClick={() => onStartServer(server.id)}>
                            Start
                        </button>
                }
                    <Link className="btn btn-info" to={"/servers/" + server.id}>Settings</Link>
                </td>
                <td>
                    {!serverRunning &&
                            <button className="btn btn-danger"
                                    onClick={() => onDeleteServer(server.id)}>Delete
                            </button>}
                </td>
                {serverRunning && <td>
                    <p>Started on: {server.instanceInfo.startedAt}</p>
                    <p>Players: {server.instanceInfo.playersOnline} / {server.instanceInfo.maxPlayers}</p>
                    {server.instanceInfo.version && <p>Version: {server.instanceInfo.version}</p>}
                    {server.instanceInfo.map && <p>Map: {server.instanceInfo.map}</p>}
                    {server.instanceInfo.description &&
                            <p>Description: {server.instanceInfo.description}</p>}
                </td>
                }
            </tr>
    );
};

export default ServerListEntry;