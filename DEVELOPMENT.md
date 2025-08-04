# Development Guide for Arma Server Manager

This guide will help you set up the Arma Server Manager project for development.

## Project Structure

The project consists of two main components:

- **Backend**: Spring Boot application (Java 17)
- **Frontend**: React application with TypeScript, Vite, and Jest

## Prerequisites

- JDK 17 or higher
- Node.js 18 or higher
- npm 9 or higher
- Docker and Docker Compose (for database and optional containerized development)
- MySQL 8.x (if not using Docker)
- Git

## Setting Up the Development Environment

### 1. Clone the Repository

```bash
git clone https://github.com/fugasjunior/arma-server-manager.git
cd arma-server-manager
```

### 2. Database Setup

The easiest way to set up the database is using Docker:

```bash
# Create a copy of the example .env file
cp .env.EXAMPLE .env

# Edit the .env file with your preferred settings
# Make sure to set the database credentials and other required variables
```

Start the database using Docker Compose:

```bash
docker-compose -f docker-compose.yml up -d db adminer
```

This will start:
- MySQL database on port 3306
- Adminer (database management tool) on port 8090

### 3. Backend Setup

#### Configure Application Properties

```bash
# Create a copy of the example properties file
cp config/application.properties.EXAMPLE config/application.properties

# Edit the application.properties file with your settings
# Make sure database connection details match those in your .env file
```

Important properties to configure:
- `steam.api.key`: Your Steam API key (get it from https://steamcommunity.com/dev/apikey)
- `steamcmd.path`: Path to SteamCMD executable
- `directory.mods`, `directory.servers`, `directory.logs`: Paths for storing game files
- Database connection details

#### Run the Backend

You can run the backend using Gradle:

```bash
# On Windows
.\gradlew.bat bootRun -DskipFrontendBuild=true

# On Linux/macOS
./gradlew bootRun -DskipFrontendBuild=true
```

The `-DskipFrontendBuild=true` flag prevents the backend build from also building the frontend, which is useful during development.

### 4. Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Start the development server
npm run dev
```

The frontend development server will start on http://localhost:5173 and will proxy API requests to the backend.

## Development Workflow

### Backend Development

- The backend uses Spring Boot and follows a standard layered architecture
- Changes to the database schema are managed with Flyway migrations
- Run tests with `./gradlew test`

### Frontend Development

- The frontend is built with React and TypeScript
- State management is handled with React Context and hooks
- Run tests with `npm test`
- Cypress is set up for end-to-end testing (you may need to install Cypress with `npm install cypress --save-dev` and add a script to package.json)

### Full-Stack Development

When working on both frontend and backend:

1. Start the database with Docker Compose
2. Run the backend with Gradle or from IDE
3. Run the frontend development server
4. Make changes to either component as needed

## Building for Production

To build the entire application:

```bash
# On Windows
.\gradlew.bat install

# On Linux/macOS
./gradlew install
```

This will:
1. Build the frontend
2. Copy the frontend build to the backend resources
3. Build the backend with the frontend included

The resulting JAR file will be in `backend/build/libs/`.

## Docker Development

You can also use Docker for the entire development environment:

1. Uncomment the `armaservermanager` service in `docker-compose-dev.yml`
2. Build and start all services:

```bash
docker-compose -f docker-compose-dev.yml up --build
```

## Troubleshooting

### Common Issues

1. **Database connection errors**: Ensure your database is running and the connection details in `application.properties` match your setup.

2. **SteamCMD issues**: Make sure the path to SteamCMD is correctly set in `application.properties` and that you have the necessary permissions.

3. **Frontend proxy errors**: Verify that the backend is running and accessible at the URL specified in `.env.development`.

### Logs

- Backend logs are available in the console and in the `logs` directory
- Frontend development server logs are displayed in the console

## Contributing

1. Create a feature branch from `dev`
2. Make your changes
3. Write or update tests as necessary
4. Ensure all tests pass
5. Submit a pull request to `dev`

## Additional Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [React Documentation](https://reactjs.org/docs/getting-started.html)
- [Vite Documentation](https://vitejs.dev/guide/)
- [Docker Documentation](https://docs.docker.com/)