# Frontend for Arma Server Manager

## Development Setup

### Environment Configuration

The application uses environment variables to configure the API URL:

- In development mode, the application will use the Vite development server with a proxy to the backend
- The proxy is configured in `vite.config.ts` to forward API requests to `http://localhost:8080`
- No manual configuration is needed in `config.ts` anymore

### Running the Application

1. Start the backend server (it should be running on port 8080)
2. Run the frontend development server:
   ```
   npm run dev
   ```
3. Access the application at the URL provided by Vite (typically http://localhost:5173)

## Environment Files

- `.env.development` - Configuration for development environment
- `.env.production` - Configuration for production environment

You can customize these files to change the API URL if needed.