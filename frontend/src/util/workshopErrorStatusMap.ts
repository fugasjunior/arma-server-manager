import {ErrorStatus} from "../dtos/Status.ts";

const workshopErrorStatusMap = new Map<ErrorStatus, String>([
    [ErrorStatus.GENERIC, "Unidentified error. Please contact the system administrator."],
    [ErrorStatus.IO, "File system I/O error. Please contact the system administrator.",],
    [ErrorStatus.NO_MATCH, "The mod was not found on the Workshop.",],
    [ErrorStatus.NO_SUBSCRIPTION, "The given Steam account doesn't have correct subscription and cannot download the mod.",],
    [ErrorStatus.TIMEOUT, "The request timed out, please retry.",],
    [ErrorStatus.WRONG_AUTH, "Incorrect Steam authorization. Please check username, password and Steam Guard token.",],
    [ErrorStatus.RATE_LIMIT, "Too many incorrect login attempts. Please try again later (no sooner than 30 minutes after last attempt)."],
    [ErrorStatus.INTERRUPTED, "The installation was interrupted. Please try again."]
]);

export default workshopErrorStatusMap;