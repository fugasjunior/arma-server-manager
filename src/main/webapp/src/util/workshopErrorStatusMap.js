const workshopErrorStatusMap = {
    GENERIC: "Unidentified error. Please contact the system administrator.",
    IO: "File system I/O error. Please contact the system administrator.",
    NO_MATCH: "The mod was not found on the Workshop.",
    NO_SUBSCRIPTION: "The given Steam account doesn't have correct subscription and cannot download the mod.",
    TIMEOUT: "The request timed out, please retry.",
    WRONG_AUTH: "Incorrect Steam authorization. Please check username, password and Steam Guard token.",
    RATE_LIMIT: "Too many incorrect login attempts. Please try again later (no sooner than 30 minutes after last attempt)."
}

export default workshopErrorStatusMap;