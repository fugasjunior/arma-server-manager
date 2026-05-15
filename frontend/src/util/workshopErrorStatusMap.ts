import {ErrorStatus} from "../api/generated";

const workshopErrorStatusMap = new Map<ErrorStatus, String>([
    [ErrorStatus.Generic, "Unidentified error. Please contact the system administrator."],
    [ErrorStatus.Io, "File system I/O error. Please contact the system administrator.",],
    [ErrorStatus.NoMatch, "The mod was not found on the Workshop.",],
    [ErrorStatus.NoSubscription, "The given Steam account doesn't have correct subscription and cannot download the mod.",],
    [ErrorStatus.Timeout, "The request timed out, please retry.",],
    [ErrorStatus.WrongAuth, "Incorrect Steam authorization. Please check username, password and Steam Guard token.",],
    [ErrorStatus.RateLimit, "Too many incorrect login attempts. Please try again later (no sooner than 30 minutes after last attempt)."],
    [ErrorStatus.Interrupted, "The installation was interrupted. Please try again."]
]);

export default workshopErrorStatusMap;
