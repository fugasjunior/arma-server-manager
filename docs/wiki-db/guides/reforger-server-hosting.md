---
game: reforger
slug: reforger-server-hosting
file: general
source: https://community.bistudio.com/wiki/Arma_Reforger:Server_Hosting
scraped: 2026-05-31T11:45:20Z
---

- [1 Dedicated Server](#Dedicated_Server)
  - [1.1 BattlEye](#BattlEye)
  - [1.2 Startup Parameters](#Startup_Parameters)
    - [1.2.1 config](#config)
    - [1.2.2 maxFPS](#maxFPS)
    - [1.2.3 server](#server)
    - [1.2.4 Others](#Others)
  - [1.3 Configuration File](#Configuration_File)
- [2 Player-Hosted Server](#Player-Hosted_Server)
  - [2.1 Settings](#Settings)
    - [2.1.1 Scenario Selection](#Scenario_Selection)
    - [2.1.2 Crossplay](#Crossplay)
  - [2.2 Mods](#Mods)
- [3 Linux Server](#Linux_Server)
  - [3.1 SteamCMD Setup](#SteamCMD_Setup)
    - [3.1.1 Example Script](#Example_Script)
    - [3.1.2 With Bash Scripts](#With_Bash_Scripts)
  - [3.2 LinuxGSM](#LinuxGSM)
  - [3.3 Docker Setup](#Docker_Setup)

Server Hosting is the fact of hosting a game instance accessible over the network to other players.
There are two possible modes in Arma Reforger: **player-hosted** and **dedicated**.

⚠

[![](/wikidata/images/thumb/8/82/armar-port_fowarding_connection_failed.png/300px-armar-port_fowarding_connection_failed.png)](/wiki/File:armar-port_fowarding_connection_failed.png)

Connection fails from the outside if the door is closed.

For a hosted server to be visible from outside the local network (e.g from internet), [Port Forwarding](https://en.wikipedia.org/wiki/Port_forwarding) must be used.

Please refer to your router's user guide for assistance.

Main router brands's documentation links:

- [Cisco router](https://www.cisco.com/c/en/us/support/docs/smb/routers/cisco-rv-series-small-business-routers/smb5818-configure-port-forwarding-port-triggering-nat-on-rv34x-serie.html)
- [D-Link router](https://www.dlink.com/uk/en/support/faq/routers/mydlink-routers/dir-810l/how-do-i-configure-port-forwarding-on-my-router)
- [Netgear router](https://kb.netgear.com/24290/How-do-I-add-a-custom-port-forwarding-service-on-my-NETGEAR-router)
- [TP-Link router](https://www.tp-link.com/us/support/faq/1379/)

## Dedicated Server[[edit](/wiki?title=Arma_Reforger:Server_Hosting&veaction=edit&section=1 "Edit section: Dedicated Server") | [edit source](/wiki?title=Arma_Reforger:Server_Hosting&action=edit&section=1 "Edit section: Dedicated Server")]

A **Dedicated Server** is a server without any game instance launched; it purely processes game information and network synchronisation.

ⓘ

stable is app id: **1874900** - <https://steamdb.info/app/1874900/>experimental: **1890870** - <https://steamdb.info/app/1890870/>

### BattlEye[[edit](/wiki?title=Arma_Reforger:Server_Hosting&veaction=edit&section=2 "Edit section: BattlEye") | [edit source](/wiki?title=Arma_Reforger:Server_Hosting&action=edit&section=2 "Edit section: BattlEye")]

ⓘ

See [BattlEye](http://www.battleye.com/)'s [documentation](https://www.battleye.com/support/documentation/) and [FAQ](https://www.battleye.com/support/faq/) - there is also a [BattlEye](/wiki/BattlEye "BattlEye") wiki article.

It is possible to modify BattlEye's RCon port and password by **adding** the following settings to Arma Reforger\BattlEye\BEServer\_x64.cfg:

```
RConPort 5678
RConPassword myNewBEPassword
```

⚠

When editing Arma Reforger's BattlEye config, make sure to **append** new settings to it and not *erase* or even *edit* existing information.  

Missing information will have the kick message "Missing GameID/MasterPort server config settings" welcome (and eject) players, whereas modified values will prevent BattlEye from working.  
In the event of the file being already incorrectly edited, verify the game's files on [Steam](https://steampowered.com/) (see [Steam's tutorial](https://help.steampowered.com/en/faqs/view/0C48-FCBD-DA71-93EB)):

- delete BEServer\_x64.cfg
- in Steam, right-click on Arma Reforger
- select "Properties"
- click on "Local Files"
- press "Verify integrity of game files"

Steam will now verify and repair modified files. This will not erase any personal files, game progress, controls and other configurations will remain unchanged.

### Startup Parameters[[edit](/wiki?title=Arma_Reforger:Server_Hosting&veaction=edit&section=3 "Edit section: Startup Parameters") | [edit source](/wiki?title=Arma_Reforger:Server_Hosting&action=edit&section=3 "Edit section: Startup Parameters")]

See [Startup Parameters - Hosting](/wiki/Arma_Reforger:Startup_Parameters#Hosting "Arma Reforger:Startup Parameters") and the [Server Config](/wiki/Arma_Reforger:Server_Config "Arma Reforger:Server Config") page for more information.

#### config[[edit](/wiki?title=Arma_Reforger:Server_Hosting&veaction=edit&section=4 "Edit section: config") | [edit source](/wiki?title=Arma_Reforger:Server_Hosting&action=edit&section=4 "Edit section: config")]

The Server exe uses the -config [startup parameter](/wiki/Arma_Reforger:Startup_Parameters "Arma Reforger:Startup Parameters") to target the configuration file.

```
ArmaReforgerServer.exe -config ".\Configs\Campaign_SWCoast.json"
```

In above example, Campaign\_SWCoast.json is expected to be locted in Configs folder next to the exe.

#### maxFPS[[edit](/wiki?title=Arma_Reforger:Server_Hosting&veaction=edit&section=5 "Edit section: maxFPS") | [edit source](/wiki?title=Arma_Reforger:Server_Hosting&action=edit&section=5 "Edit section: maxFPS")]

⚠

As of **0.9.8** it is **heavily recommended** to use [this](/wiki/Arma_Reforger:Startup_Parameters#maxFPS "Arma Reforger:Startup Parameters") [startup parameter](/wiki/Arma_Reforger:Startup_Parameters "Arma Reforger:Startup Parameters"), set to a value in the 60..120 range; otherwise, the server can try to use all the available resources!

```
ArmaReforgerServer.exe -maxFPS 60
```

#### server[[edit](/wiki?title=Arma_Reforger:Server_Hosting&veaction=edit&section=6 "Edit section: server") | [edit source](/wiki?title=Arma_Reforger:Server_Hosting&action=edit&section=6 "Edit section: server")]

This parameter instructs the executable to launch **local** server and **load selected world**. When this parameter is used, config is ignored. Server parameter can be combined with [addons](/wiki/Arma_Reforger:Startup_Parameters#addons "Arma Reforger:Startup Parameters") and [addonsDir](/wiki/Arma_Reforger:Startup_Parameters#addonsDir "Arma Reforger:Startup Parameters") parameters to start a server with local mods, which can be useful when testing addon before uploading it Workshop.

```
ArmaReforgerServer.exe -server "worlds/MP/MPTest.ent" -addonsDir "C:\MyModsDir" -addons MyCustomMod
```

#### Others[[edit](/wiki?title=Arma_Reforger:Server_Hosting&veaction=edit&section=7 "Edit section: Others") | [edit source](/wiki?title=Arma_Reforger:Server_Hosting&action=edit&section=7 "Edit section: Others")]

The below [Startup Parameters](/wiki/Arma_Reforger:Startup_Parameters "Arma Reforger:Startup Parameters") are optional but may prove useful upon some cases:

- [logStats](/wiki/Arma_Reforger:Startup_Parameters#logStats "Arma Reforger:Startup Parameters") - allows to log server's FPS every x milliseconds
- [logLevel](/wiki/Arma_Reforger:Startup_Parameters#logLevel "Arma Reforger:Startup Parameters") - sets the log detail level
- [listScenarios](/wiki/Arma_Reforger:Startup_Parameters#listScenarios "Arma Reforger:Startup Parameters") - logs available scenario .conf file paths on startup

### Configuration File[[edit](/wiki?title=Arma_Reforger:Server_Hosting&veaction=edit&section=8 "Edit section: Configuration File") | [edit source](/wiki?title=Arma_Reforger:Server_Hosting&action=edit&section=8 "Edit section: Configuration File")]

See [Server Config](/wiki/Arma_Reforger:Server_Config "Arma Reforger:Server Config").

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_0.9.7 "Category:Arma Reforger/Version 0.9.7") [0.9.7](/wiki?title=Category:Arma_Reforger/Version_0.9.7&action=edit&redlink=1 "Category:Arma Reforger/Version 0.9.7 (page does not exist)")

## Player-Hosted Server

Also known as **Listen Server**, a **Player-Hosted Server** is a server also hosting a local player.
Such server is started from within the game, in Multiplayer > Host tab > Host new server.

␿

Player-hosting is only available on PC.

### Settings[[edit](/wiki?title=Arma_Reforger:Server_Hosting&veaction=edit&section=9 "Edit section: Settings") | [edit source](/wiki?title=Arma_Reforger:Server_Hosting&action=edit&section=9 "Edit section: Settings")]

The settings are all self-explanatory and [Dedicated Server](#Dedicated_Server) can be used.

#### Scenario Selection[[edit](/wiki?title=Arma_Reforger:Server_Hosting&veaction=edit&section=10 "Edit section: Scenario Selection") | [edit source](/wiki?title=Arma_Reforger:Server_Hosting&action=edit&section=10 "Edit section: Scenario Selection")]

**Scenario** and **Source** are two related fields:

- **Scenario** is the list of all available scenarios
- **Source** is a read-only field telling from which mod (or Arma Reforger) the selected Scenario is.

#### Crossplay[[edit](/wiki?title=Arma_Reforger:Server_Hosting&veaction=edit&section=11 "Edit section: Crossplay") | [edit source](/wiki?title=Arma_Reforger:Server_Hosting&action=edit&section=11 "Edit section: Crossplay")]

This option allows console players to join - see [supportedGameClientTypes](#supportedGameClientTypes).

### Mods[[edit](/wiki?title=Arma_Reforger:Server_Hosting&veaction=edit&section=12 "Edit section: Mods") | [edit source](/wiki?title=Arma_Reforger:Server_Hosting&action=edit&section=12 "Edit section: Mods")]

This tab allows to enable or disable local mods to make them available to the hosted game (or not). The [Workshop](/wiki/Arma_Reforger:Workshop "Arma Reforger:Workshop") is accessible from here.

## Linux Server[[edit](/wiki?title=Arma_Reforger:Server_Hosting&veaction=edit&section=13 "Edit section: Linux Server") | [edit source](/wiki?title=Arma_Reforger:Server_Hosting&action=edit&section=13 "Edit section: Linux Server")]

The game server will by default use Docker container's IP for server browser registration and client connection which will cause failure during client connection attempt.  
To avoid it use:

- Run the "ipconfig" command in cmd to list the local IPs
- "IP Connect" option in the server browser and insert one of the server's local IPs
- Custom server config (.json file) with "gameHostRegisterBindAddress" and "gameHostRegisterPort" parameters set to one of the local IP:Port combinations

Example:

```
-config "./My_Config.json"
ClientConnectAddress 192.168.39.98
```

### SteamCMD Setup[[edit](/wiki?title=Arma_Reforger:Server_Hosting&veaction=edit&section=14 "Edit section: SteamCMD Setup") | [edit source](/wiki?title=Arma_Reforger:Server_Hosting&action=edit&section=14 "Edit section: SteamCMD Setup")]

⚠

This tutorial has been tested on **Ubuntu**.

1. Install SteamCMD - for the latest documentation, see <https://developer.valvesoftware.com/wiki/SteamCMD>
   1. Download and install it (link on the [SteamCMD](https://developer.valvesoftware.com/wiki/SteamCMD) page) - it will auto-update to the latest version
   2. Set the install path with the force\_install\_dir command (otherwise the default location will be used, home/<username>/.steam/steam/steamcmd)
      1. You can name the folder *arma-reforger, armaR, armarserver, armarexpserver* and etc
   3. Login as anonymous - type in login anonymous
2. Download and install the server app\_update 1874900
   1. *1890870* for experimental version
3. Quit SteamCMD quit

ⓘ

To run Arma Reforger's server, run ./ArmaReforgerServer in the installation directory.

#### Example Script[[edit](/wiki?title=Arma_Reforger:Server_Hosting&veaction=edit&section=15 "Edit section: Example Script") | [edit source](/wiki?title=Arma_Reforger:Server_Hosting&action=edit&section=15 "Edit section: Example Script")]

update\_armar\_ds.txt

Based on the [SteamCMD](https://developer.valvesoftware.com/wiki/SteamCMD) page's example

```
// update_armar_ds.txt
//
@ShutdownOnFailedCommand 1
@NoPromptForPassword 1
force_install_dir ../armar_ds
login anonymous
app_update 1874900 validate
quit
```

Execution

```
steamcmd +runscript update_armar_ds.txt
```

#### With Bash Scripts[[edit](/wiki?title=Arma_Reforger:Server_Hosting&veaction=edit&section=16 "Edit section: With Bash Scripts") | [edit source](/wiki?title=Arma_Reforger:Server_Hosting&action=edit&section=16 "Edit section: With Bash Scripts")]

1. Install SteamCMD

   ```
   sudo apt install steamcmd
   ```
2. create a directory lets say `armaR` somewhere, lets say `/home/<username>/armaR`
3. Create in it a steam shell script that will install reforger server `steamShell.txt` with something like

   ```
   @ShutdownOnFailedCommand 1
   login anonymous
   // Stable ID: 1874900 EXP: 1890870
   app_update 1874900 validate
   quit
   ```
4. then create bash script that will install/update game `steamInstall.sh` with:

   ```
   /usr/games/steamcmd +force_install_dir /home/<username>/armaR +runscript /home/<username>/armaR/steamShell.txt
   ```
5. after that, server startup script `start.sh` with:

   ```
   // Install or Update game
   ./steamInstall.sh
   // Start server
   ./ArmaReforgerServer -config /home/<username>/.config/ArmaReforgerServer/config.json -profile /home/<username>/.config/ArmaReforgerServer -maxFPS 60
   ```
6. So the actual file tree will look something like this:

   ```
   /home/<username>/armaR
   				steamShell.txt
   				steamInstall.sh
   				start.sh
   ```
7. Then create actual `config.json` at `/home/<username>/.config/ArmaReforgerServer/config.json`, create folders if they don't exist
8. Run `start.sh` to install the server and start it.

Console logs etc can be found in above mentioned profile folder, addons will also install there by default, you can again change that.

You can also change profile/config directories as desired.

ⓘ

You can then create new service for systemctl to run start.sh and the thing will run in the background and auto restart after crashes etc

### LinuxGSM[[edit](/wiki?title=Arma_Reforger:Server_Hosting&veaction=edit&section=17 "Edit section: LinuxGSM") | [edit source](/wiki?title=Arma_Reforger:Server_Hosting&action=edit&section=17 "Edit section: LinuxGSM")]

The [Arma LinuxGSM Tool](https://linuxgsm.com/servers/armarserver/) can also be used.

1. It can monitor the game server by checking that the proccess is running and querying it. Should the server go offline LinuxGSM can restart the server and send you an alert. You can use cronjobs to setup monitoring.
2. Update checks for any server updates and applies them. The server will update and restart only if required. **This also needs to be set up on a schedule.**

ⓘ

For server's experimental version, add `appid="1890870"` to your `./lgsm/config-lgsm/armarserver/armarserver.cfg` and validate files by `./armarserver v`.

### Docker Setup[[edit](/wiki?title=Arma_Reforger:Server_Hosting&veaction=edit&section=18 "Edit section: Docker Setup") | [edit source](/wiki?title=Arma_Reforger:Server_Hosting&action=edit&section=18 "Edit section: Docker Setup")]

⚠

This Docker configuration is adapted to **Ubuntu**. Other distributions such as Fedora or Arch Linux may store their certificates at the following location: /etc/pki/ca-trust/ - be sure to edit the configuration accordingly.

ⓘ

A community Docker image is available on GitHub: <https://github.com/acemod/docker-reforger>

1. Install the latest Docker:
   1. [Download](https://docs.docker.com/desktop/windows/install/) and install Docker
   2. [Enable Hyper-V in Windows](https://docs.microsoft.com/en-us/virtualization/hyper-v-on-windows/quick-start/enable-hyper-v) if it is not already
   3. Assign HW resources in Docker/Settings/Resources/Advanced:
      1. CPU: 4 cores
      2. Memory: 6 GB
2. Download Ubuntu 18.04 image *via* batch or powershell cmd: docker pull ubuntu:18.04
3. Run Ubuntu image:
   1. mount volume with server data
   2. expose client connection UDP port
   3. Example: docker container run -t -d -p 2001:2001/udp -v D:\server\_data\linux\_packed:/home/packed --name ubuntu\_test ubuntu:18.04
4. Connect to bash console: docker exec -it ubuntu\_test /bin/bash
5. Install necessary software:
   1. libcurl4 - required by server app
   2. net-tools - for debug purposes (enables ifconfig etc)
   3. Installation:
      1. apt-get update
      2. apt-get install libcurl4
      3. apt-get install net-tools
      4. apt-get install libssl1.1
6. Create logs directory, e.g: mkdir /home/profile
7. Run server:
   1. server executable needs proper execution/access rights:
      1. cd server\_root\_folder
      2. chmod +x ArmaReforgerServer
   2. Example: ./ArmaReforgerServer -gproj ./addons/data/ArmaReforger.gproj -config Configs/ServerConfig/Campaign.json -backendlog -nothrow -profile /home/profile

ⓘ

If you are getting curl errors `Curl error=Failed writing received data to disk/application` and `error curl_easy_perform, code: 23` when downloading large mods in a docker container, you might need to set -addonTempDir ./tmp or some other location where the container has write permissions.