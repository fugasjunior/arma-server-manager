type AppConfig = {
    apiUrl: string,
    dateFormat: Intl.DateTimeFormatOptions,
    version: string
};

// Automatically determine API URL based on environment
function getApiUrl(): string {
    // Use environment variable if available
    if (import.meta.env.VITE_API_URL) {
        return import.meta.env.VITE_API_URL;
    }
    
    // Fallback for development mode
    if (import.meta.env.DEV) {
        return "http://localhost:8080/api";
    }
    
    // Fallback for production mode (when frontend is served by the backend)
    return "/api";
}

const config: AppConfig = {
    apiUrl: getApiUrl(),
    dateFormat: {year: "numeric", month: "2-digit", day: "numeric", hour: "2-digit", minute: "2-digit"},
    version: import.meta.env.VITE_APP_VERSION ?? "dev"
};

export default config;