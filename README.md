# Arma Server Manager

Dedicated server administration made easy.

## About
Arma Server Manager is a web administration tool for managing dedicated gaming servers mainly for games 
by Bohemia Interactive, such as **Arma 3**, **DayZ** and **Arma Reforger**. If you're tired of paying expensive game hosting 
providers and would prefer to host your own server, but find the server configuration tedious, then this project is for you.

The manager allows you to **set up, control and monitor multiple game servers** with just a couple of clicks using a 
simple user interface. You can easily **install workshop mods**, **upload scenarios**, **manage headless clients** and more. 
For more comprehensive list of features, see [Features](#features). 

## List of contents
- [About](#about)
- [List of contents](#list-of-contents)
- [Features](#features)
    - [Host multiple game servers](#host-multiple-game-servers-from-bohemia-interactive)
    - [Steam Workshop mods](#steam-workshop-mods)
    - [Community DLCs (cDLCs)](#community-dlcs-cdlcs)
    - [Launcher HTML mod presets](#launcher-html-mod-presets)
    - [System dashboard](#system-dashboard)
    - [Server configuration](#server-configuration)
    - [Headless clients](#headless-clients)
    - [Scenarios](#scenarios)
    - [Additional game servers](#additional-game-servers)
- [Installation](#installation)
    - [Docker setup (recommended)](#docker-setup-recommended)
        - [Prerequisites](#prerequisites)
        - [Installation](#installation)
    - [Custom installation without Docker](#custom-installation-without-docker)
        - [Prerequisites](#prerequisites-1)
        - [Installing SteamCMD](#installing-steamcmd)
        - [Configuring the Admin UI app](#configuring-the-admin-ui-app)
        - [Setting up MySQL database with Docker](#setting-up-mysql-database-with-docker)
        - [Running the Admin UI app](#running-the-admin-ui-app)
- [First time setup](#first-time-setup)
    - [Getting SteamAuth token](#getting-steamauth-token)
        - [Option 1: Obtain Steam Guard token through UI](#option-1-obtain-steam-guard-token-through-ui)
        - [Option 2: Manually logging into SteamCMD](#option-2-manually-logging-into-steamcmd)
    - [Setting up SteamAuth](#setting-up-steamauth)
    - [Installing server files](#installing-server-files)
    - [Configuring your first server](#configuring-your-first-server)
    - [Additional servers](#additional-servers)
- [Discord](#discord)
- [Support](#support)
- [Credits](#credits)

## Features
![Main dashboard screenshot](https://imgur.com/LcvCtlk.jpg)

[More screenshots](https://imgur.com/a/74pWsoO)

### Host multiple game servers

Manage multiple **Arma 3**, **DayZ***, **DayZ Experimental** and **Arma Reforger** servers. With SteamCMD running in 
the background, server installation is fully automated and installing and updating the server takes just a single click.

*\* Unfortunately, DayZ (stable) server is currently only available on Windows and is not available when using Docker 
to run the manager because of this.*

### Steam Workshop mods
The manager allows you to download and automatically install 

### Community DLCs (cDLCs)
Besides workshop mods, you can also select which community DLCs you want to run any of your Arma 3 servers. 

### Launcher HTML mod presets
Organize workshop mods into presets which you can simply activate on the server. To make things even easier, you can
upload HTML preset files generated by the Arma launcher which will automatically install all the mods. This also works
the other way - create a preset in the manager, export it and share with your comrades.

### System dashboard
See basic info about the system resources, such as the current CPU, memory and disk usage.

### Server configuration
You can create multiple servers for each supported game.

Depending on the game, all of the following can be configured with a simple form:
- Basic server information ([server.cfg](https://community.bistudio.com/wiki/Arma_3:_Server_Config_File))
- Difficulty settings ([server.Arma3Profile](https://community.bistudio.com/wiki/Arma_3:_Server_Profile))
- Advanced network settings ([basic.cfg](https://community.bistudio.com/wiki/Arma_3:_Basic_Server_Config_File))

### Headless clients
When running Arma 3 servers, you can launch or stop preconfigured headless clients with a single click.

### Scenarios
You can upload and manage `.pbo` scenarios which will be available to play on your Arma 3 servers.

### Additional game servers
Besides the main supported games, you can also use the manager for very basic control of any other server you wish. That's
very useful in case you want to run other servers like Minecraft besides your primary Arma/DayZ servers. For setup,
see [Additional servers](#additional-servers).

## Installation
There are two ways to run the project.

### Docker setup (recommended)

#### Prerequisites
- [Docker](https://docs.docker.com/engine/install/) installed

#### Installation

To run the manager, you can use the following files:

`docker-compose.yml`
```yml
version: '3.3'

services:
  db:
    image: mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: "${MYSQL_ROOT_PASSWORD}"
      MYSQL_DATABASE: "${MYSQL_DB_NAME}"
      MYSQL_USER: "${MYSQL_USER}"
      MYSQL_PASSWORD: "${MYSQL_PASSWORD}"
    volumes:
      - armaservermanager-db:/var/lib/mysql

  adminer:
    image: adminer
    restart: always
    ports:
      - "8090:8080"

  armaservermanager:
    image: "fugasjunior/armaservermanager:${VERSION}"
    restart: always
    # 'host' network mode is not available on Windows. If you need to run this image on Windows, you need to set up port mappings manually.
    network_mode: host
    # uncommend when running on Windows and add additional ports according to your needs 
    # ports:
    #   - "8080:8080"
    #   - "2302:2303/udp"
    #   - "2303:2303/udp"
    depends_on:
      - db
    volumes:
      # Change the path where the servers will be stored on your server (first part before semicolon)
      - /home/armaservermanager/storage:/home/steam/armaservermanager/
    environment:
      AUTH_USERNAME: "${AUTH_USERNAME}"
      AUTH_PASSWORD: "${AUTH_PASSWORD}"
      SPRING_DATASOURCE_URL: "${MYSQL_DB_URL}"
      SPRING_DATASOURCE_USERNAME: "${MYSQL_USER}"
      SPRING_DATASOURCE_PASSWORD: "${MYSQL_PASSWORD}"
      STEAM_API_KEY: "${STEAM_API_KEY}"
      JWT_SECRET: "${JWT_SECRET}"
      DATABASE_ENCRYPTION_SECRET: "${DATABASE_ENCRYPTION_SECRET}"

volumes:
  armaservermanager-db:
```

`.env`
```properties
# App version
VERSION=latest

# Username and password for accessing the web interface
AUTH_USERNAME=test
AUTH_PASSWORD=password

# Database settings. Make sure to change the password and root password
MYSQL_DB_NAME=armaservermanager_db
MYSQL_DB_URL=jdbc:mysql://db:3306/armaservermanager_db
MYSQL_USER=armaservermanager
MYSQL_PASSWORD=example
MYSQL_ROOT_PASSWORD=change_me

# Steam API key, needed for Steam Workshop interaction.
# Generated with https://steamcommunity.com/dev/apikey.
STEAM_API_KEY=

# JWT secret
# Used for creating JSON web tokens for app authentication.
# Any string can be used as a secret. The secret should be at least 32 characters long.
JWT_SECRET=

# (OPTIONAL) Database encryption secret used for encrypting the Steam account password inside the database.
# Must be a valid AES 256-bit key (https://www.allkeysgenerator.com/Random/Security-Encryption-Key-Generator.aspx).
# This setting is optional. In case the secret is not provided, the password will be stored in plain text form.
# Also make sure there are no '$' symbols in the secret which cause issues when being passed through the .env file
DATABASE_ENCRYPTION_SECRET=
```

You can also find these files for download in the root of the repository.

Place these two files in the same directory and follow the comments which tell you how to set them up. It's especially
important to set up the `.env` file, as it contains passwords, database connection settings and API keys to generate.

After you've set up the values, you can run `docker compose up`, which will automatically bring
the database and the server manager up. It should then be accessible on http://localhost:8080 by default.

### Custom installation without Docker
While the Docker approach is recommended because of the ease of setup, there might be reasons why you'd wish to run
the manager natively. A noteworthy reason for this would be if you have a Windows server and wish to run DayZ stable
server, which is currently unsupported on Linux.

You can find the `.jar` file in releases section of this repository. Follow the next steps to set it up manually.

#### Prerequisites
[JDK 17](https://www.oracle.com/java/technologies/downloads/#java17) + MySQL database

#### Installing SteamCMD

Follow [this guide](https://developer.valvesoftware.com/wiki/SteamCMD#Downloading_SteamCMD) to install SteamCMD on your
server. After the installation is finished, set the full path to SteamCMD executable in `application.properties`. On
Linux,
the default path when installing with package manager is `/usr/games/steamcmd`.

#### Configuring the Admin UI app

There is a `config` directory bundled with the .jar executable file. In it, you will
find `application.properties.EXAMPLE`
file which contains sample configuration. Copy this file and name it `application.properties`.

Open the new file and set all the required properties as described.

#### Setting up MySQL database with Docker

In the project you can find `docker-compose.yml` file for the MySQL database Docker container. Edit the environment
variables to match `application.properties`, **especially the database properties**, comment out the `armaservermanager`
service and then start the container with `docker-compose up -d`.

You can also use your own MySQL database server instead if you prefer do to so.

#### Running the Admin UI app

Launch the application by running: `java -jar arma3-server-gui.jar`. You should be able to access the GUI through
`http://localhost:8080` by default.


## First time setup

### Getting SteamAuth token

If you have Steam Guard 2FA enabled on your Steam account, you're going to need to get a SteamAuth token. To do this,
either manually launch SteamCMD with a command line and log in, or use the semi-automated approach through UI.

**NOTE:** Currently, there is no support for 2FA authentication with Steam mobile application.     

#### Option 1: Obtain Steam Guard token through UI

Upon first startup, head to "App config" tab and fill out your Steam username and password. Then, head to "Dashboard" and
try to download any available server.

After a short while, the installation will fail because of invalid credentials. However, at this time, you should have
received an email with Steam Guard token, which you can now add in the "App Config" tab. Proceed to the next step to
store this token in the app. 

#### Option 2: Manually logging into SteamCMD
If you have access to the SteamCMD (when running the manager natively or by execing into the manager container with
`docker exec -it <container_name> /bin/bash`), you can also get the token directly.

Launch `steamcmd` command. Next, in the shell, type `login <your_steam_name>` and input password when prompted. 
Then you'll be prompted to input Steam token which will be sent into your email. Enter it to successfully login and 
then type `quit` to exit the SteamCMD interface.

### Setting up SteamAuth

In the GUI, navigate to "App config" tab. There, you need to enter your Steam account username, password and
the token which you previously received through e-mail. This is needed to interact with SteamCMD to
install/update the server and any workshop mods.

Note: The token has a limited lifetime. Sooner or later, some actions will start to fail because of invalid token. At the
same time, you should get a fresh token to your email. After that, you just need to update the token in the manager.

### Installing server files

After setting up the SteamAuth, navigate to "Dashboard" tab and just click the "Install/Update" button on the servers you want
to install. The server will download its necessary files. This might take a few minutes or even a couple of hours depending on your network bandwidth, but eventually, the installation
should finish.

### Configuring your first server
Head to "Servers" tab and click the "Create new server" button. From the dropdown, select which server you want to create.
After that, finish the configuration and press "Submit" to create the server.

Now, you should already see the server appear in the servers list. Now you're pretty much done - try running the server!

### Additional servers

If you need to manage other servers than the ones natively supported, you can use the Additional servers feature.
This allows you to start and stop any gaming servers you have previously installed on the machine.

Setup of an additional server requires you to access the database directly and import the settings manually.


1. Install the server you wish to control with the app
2. Create a shell script used to start the server executable with necessary parameters
3. Execute the following statement in the database (e.g. using PgAdmin which comes with the basic docker-compose file)

```sql
INSERT INTO `additional_server` (`id`, `name`, `command`, `server_dir`, `image_url`)
VALUES (0,
        'Minecraft',
        '/path/to/minecraft.sh',
        '/path/to/minecraft/server/directory',
        'https://optionalIconUrl.com/minecraft.png');
```

After the setup, you should be able to see the server listed in Additional servers tab, and start it with a click
of a button.

## Discord
Have some feedback, need help or just want to chat? Join the [Discord server](https://discord.gg/Yn93vCADPg).

## Support
Enjoy my work?

[Donate with PayPal](https://paypal.me/fugasjunior)

or [Buy me a coffee](https://www.buymeacoffee.com/fugasjunior)

[!["Buy Me A Coffee"](https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png)](https://www.buymeacoffee.com/fugasjunior)


## Credits
This app was inspired by Dahlgren's [Arma Server Admin](https://github.com/Dahlgren/arma-server-web-admin) project. 
Feel free to check out his project too!
