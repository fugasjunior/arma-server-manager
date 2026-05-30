import {ServerType} from "../api/generated";

const SERVER_NAMES = new Map<ServerType, string>([
    [ServerType.Arma3, "Arma 3"],
    [ServerType.Dayz, "DayZ"],
    [ServerType.DayzExp, "DayZ Experimental"],
    [ServerType.Reforger, "Arma Reforger"]
]);

export default SERVER_NAMES;
