# [-FE-] Arma Server Manager

**An administration web app for managing your Arma and DayZ servers**

<details><summary>SHOW SCREENSHOT</summary>

![Main dashboard screenshot](https://imgur.com/LcvCtlk.jpg "Main dashboard screenshot")
</details>

[More screenshots](https://imgur.com/a/74pWsoO)

## Features

- Support for **Arma 3**, **Arma Reforger**, **DayZ** and **DayZ Experimental** servers
- Automatically install and update server installations
- Download and manage **Steam Workshop mods** for Arma 3 and DayZ
- Configure, run and monitor multiple servers at once
- Arma 3 scenarios management
- Support for simplified management of additional game servers

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

### Setting up MySQL database with Docker

In the project you can find `docker-compose.yml` file for the MySQL database Docker container. Edit the environment
variables to match `application.properties`, **especially the database properties**,
and then start the container with `docker-compose up -d`.

You can also use your own MySQL database server insted if you prefer do to so.

### Running the Admin UI app

Launch the application by running: `java -jar arma3-server-gui.jar`. You should be able to access the GUI through
`http://localhost:8080` by default.

### Setting up SteamAuth

In the GUI, navigate to "App config" tab. Here you need to enter your Steam account username, password and
the token which you previously received through e-mail. This is needed to interact with SteamCMD to
install/update the server and any workshop mods.

### Installing servers

After setting up the SteamAuth, navigate to "Dashboard" tab and just click the "Update" button on the servers you want
to install.

_Note: DayZ (stable) server is currently only supported on Windows. You can use DayZ Experimental on Linux in the
meantime._

### Next steps

The main part of the installation is now done. You can now go:

- set up your first server from the Servers tab
- install workshop mods for Arma 3 and DayZ in the Mods tab
- upload scenarios in the Scenarios tab

## Additional servers

If you need to manage other servers than the ones natively supported, you can use the Additional servers feature.
This allows you to start and stop any gaming servers you have previously installed on the machine.

Setup of an additional server requires you to access the database directly and import the settings manually.

<details><summary>Recommended steps</summary>

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

</details>

After the setup, you should be able to see the server listed in Additional servers tab, and start it with a click
of a button.

## Planned features

- Docker image for the Admin UI
- In-built headless client support
- Access to logs

## Credits

This app is heavily based on Dahlgren's [Arma Server Admin](https://github.com/Dahlgren/arma-server-web-admin) project
and I took a lot of inspiration from it on how to make things work. Check out his project too!

## Support

[!["Buy Me A Coffee"](https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png)](https://www.buymeacoffee.com/fugasjunior)

Enjoy my work? You can [_Buy me a coffee_](https://www.buymeacoffee.com/fugasjunior)