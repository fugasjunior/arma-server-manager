# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

### Added
- Local mods: sync and enable locally stored mod folders directly from the filesystem
- Role-based access control: multiple user accounts with configurable roles and permissions
- Server uninstallation from the dashboard
- Mod drag-to-reorder (load order is reflected in server launch parameters)
- Persistent headless clients (configuration survives manager restarts)
- Automatic daily server restart scheduling (per-server, configurable time)
- Auto-generated admin credentials on first startup when `AUTH_USERNAME`/`AUTH_PASSWORD` are not set in `.env`
- GitHub Actions CI/CD workflows
- Log file rotation

### Fixed
- Workshop mod installation is now async — the UI no longer blocks during downloads
- Server installation status no longer returns a stale cached response

## [1.4.1]

### Added
- Support for Expeditionary Forces CDLC

### Changed
- Workshop mod metadata now fetched via IPublishedFileService Steam API (more reliable than HTML scraping)

### Fixed
- `LazyInitializationException` on mod operations

## [1.4.0]

### Added
- Dark mode
- Server duplicate button
- SteamCMD download reliability improvements

### Fixed
- Error when toggling light/dark mode
- Various dependency updates and security patches

## [1.3.0]

### Added
- Server branch selection (stable vs. experimental)
- Support for installing unlisted Workshop mods
- Tactical ping option (Arma 3)
- Server-only mods flag

### Fixed
- Empty network settings form on edit
- Whitelabel error page replaced with proper error handling
- Incorrect "third person view" option labels

## [1.2.0]

### Added
- About page (shows app version)
- DayZ Experimental server support

### Fixed
- DayZ server config not loading on startup
- Headless client incorrectly available for DayZ and Reforger servers

## [1.1.0]

### Added
- Arma 3 difficulty settings configuration
- Mod preset import/export (Arma Launcher HTML format)
- Access to server logs from the UI
- Docker support

### Fixed
- Various authentication and stability fixes

## [1.0.0]

Initial release.

### Features
- Manage Arma 3, DayZ, and Arma Reforger dedicated servers
- SteamCMD-driven server installation and updates
- Steam Workshop mod download and management
- Community DLCs (cDLCs) selection
- Mod presets
- Scenario upload and management
- Headless client management (Arma 3)
- Additional game server support (Minecraft, etc.)
- System resource dashboard
- JWT-based authentication
