type AppConfig = {
    apiUrl: string,
    dateFormat: Intl.DateTimeFormatOptions,
    version: string
};

const config: AppConfig = {
    apiUrl: "/api",
    // apiUrl: "http://localhost:8080/api",
    // apiUrl: "http://homeserver.lan:8080/api",
    dateFormat: {year: "numeric", month: "2-digit", day: "numeric", hour: "2-digit", minute: "2-digit"},
    version: "v1.3.0"
};

export default config;