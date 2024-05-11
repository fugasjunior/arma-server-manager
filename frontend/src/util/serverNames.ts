import {ServerType} from "../dtos/ServerDto.ts";

const SERVER_NAMES = new Map<ServerType, string>([
    [ServerType.ARMA3, "Arma 3"],
    [ServerType.DAYZ, "DayZ"],
    [ServerType.DAYZ_EXP, "DayZ Experimental"],
    [ServerType.REFORGER, "Arma Reforger"]
]);

export default SERVER_NAMES;