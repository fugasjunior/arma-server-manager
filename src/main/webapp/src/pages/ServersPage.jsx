import {getServers} from "../services/serversService"
import {useEffect, useState} from "react";
import {Link} from "react-router-dom";

const ServersPage = () => {
    const [servers, setServers] = useState([]);

    useEffect(async () => {
        const {data: servers} = await getServers();
        setServers(servers);
    })

    return (
            <div>
                {servers.map(server => (
                        <div key={server.id}>
                            <p><img src="./img/arma3_icon.png"  alt="Arma 3 icon"/></p>
                            <p>{server.name}</p>
                            <p>{server.type}</p>
                            <p><Link to={"/server/" + server.id + "/settings"}>Settings</Link></p>
                        </div>))
                }
            </div>
    )
}

export default ServersPage;