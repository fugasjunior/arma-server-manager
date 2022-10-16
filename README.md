# Arma 3 Server GUI
A simple administration web app for managing your Arma 3 server

[Screenshots](https://imgur.com/a/jEkDCkc) 

## Features

- Start, stop, update your server while keeping track of its current state
- Configure the server without the need for modifying your server.cfg
- Download and manage Steam Workshop mods
- Upload custom scenarios from your local machine

## Installation

### Prerequisites
- [JDK 17](https://www.oracle.com/java/technologies/downloads/#java17)
- [Docker](https://docs.docker.com/engine/install/) or own MySQL database 

### Installing SteamCMD
Follow [this guide](https://developer.valvesoftware.com/wiki/SteamCMD#Downloading_SteamCMD) to install SteamCMD on your 
server. After the installation is finished, set the full path to SteamCMD executable in `application.properties`. On Linux,
the default path when installing with package manager is `/usr/games/steamcmd`.

### Getting SteamAuth token
If you have Steam Guard 2FA enabled on your Steam account, you're going to need to get a SteamAuth token. To do this,
manually launch SteamCMD with a command line. 

Type `login <your_steam_name>` and input password when prompted. Then you'll be prompted to input Steam token 
which will be sent into your email. Enter it to successfully login and then type `quit` to exit the SteamCMD interface.
**Keep the token for later use**.  

### Configuring the Admin UI app
There is a `config` directory bundled with the .jar executable file. In it, you will find `application.properties.EXAMPLE`
file which contains sample configuration. Copy this file and name it `application.properties`.

Open the new file and set all the required properties as described.

### Setting up MySQL database with Docker (preferred)
In the project you can find `docker-compose.yml` file for the MySQL database Docker container. Edit the environment
variables to match `application.properties` and then start the container with `docker-compose up -d`.

You can also use your own MySQL database server insted if you prefer do to so.

### Running the Admin UI app
Launch the application by running: `java -jar arma3-server-gui.jar`. You should be able to access the GUI through 
`http://localhost:8080` by default.

### Setting up SteamAuth
In the GUI, navigate to "App config" tab. Here you need to enter your Steam account username, password and
the token which you previously received through e-mail. This is needed to interact with SteamCMD to
install/update the server and any workshop mods.

### Installing Arma 3 server
#### Automatically (preferred)
After setting up the SteamAuth, navigate to "Dashboard" tab and just click the "Update" button. The server should
automatically start installing into the directory specified in the config. Once the server is installed, you
can continue to use the GUI.

#### Manually
You can follow [this guide](https://community.bistudio.com/wiki/Arma_3_Dedicated_Server). Install the server to the
directory specified with `serverDir` property in `application.properties`

## Planned features
- Docker image for the Admin UI
- Better error messages
- Multiple server instances
- Arma Reforger support (Arma 4 hopefully soon?)
- In-built headless client support
- Workshop scenario installation
- Access to logs

## Credits
This app is heavily based on Dahlgren's [Arma Server Admin](https://github.com/Dahlgren/arma-server-web-admin) project
and I took a lot of inspiration from it on how to make things work, especially when working with the Steam Workshop.
