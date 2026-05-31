---
game: arma3
slug: arma3-dedicated-server
file: general
source: https://community.bistudio.com/wiki/Arma_3:_Dedicated_Server
scraped: 2026-05-31T11:45:19Z
---

- [1 Simple Setup](#Simple_Setup)
- [2 Installation](#Installation)
  - [2.1 Requirements](#Requirements)
  - [2.2 Instructions (Windows o/s)](#Instructions_(Windows_o/s))
  - [2.3 Instructions (Linux o/s)](#Instructions_(Linux_o/s))
    - [2.3.1 Installation](#Installation_2)
    - [2.3.2 Updating](#Updating)
    - [2.3.3 Configuration](#Configuration)
    - [2.3.4 Launching](#Launching)
    - [2.3.5 Case sensitivity & Mods](#Case_sensitivity_&_Mods)
- [3 Additional Info](#Additional_Info)
  - [3.1 Useful Links](#Useful_Links)
    - [3.1.1 Further Reading](#Further_Reading)
  - [3.2 File Locations](#File_Locations)
  - [3.3 Port Forwarding](#Port_Forwarding)
    - [3.3.1 NAT traversal](#NAT_traversal)
  - [3.4 Configuring for stable or Dev branch](#Configuring_for_stable_or_Dev_branch)
  - [3.5 Advanced Configuration](#Advanced_Configuration)
  - [3.6 Bandwidth Optimisation](#Bandwidth_Optimisation)
  - [3.7 Multiple Server configuration](#Multiple_Server_configuration)
    - [3.7.1 Method 1](#Method_1)
    - [3.7.2 Method 2](#Method_2)
    - [3.7.3 Method 3](#Method_3)
    - [3.7.4 Method 4](#Method_4)
    - [3.7.5 Method 5](#Method_5)
  - [3.8 Dedicated Server & Client configuration](#Dedicated_Server_&_Client_configuration)
  - [3.9 Headless Client](#Headless_Client)
    - [3.9.1 Setup and Execution](#Setup_and_Execution)
    - [3.9.2 Headless Client on a Dedicated Server Notes](#Headless_Client_on_a_Dedicated_Server_Notes)
  - [3.10 Example Files](#Example_Files)
    - [3.10.1 Arma3server\_steamcmd\_example.cmd](#Arma3server_steamcmd_example.cmd)
    - [3.10.2 CONFIG\_server.cfg](#CONFIG_server.cfg)
    - [3.10.3 server.Arma3Profile](#server.Arma3Profile)
    - [3.10.4 Arma3.cfg](#Arma3.cfg)
- [4 Support & Troubleshooting](#Support_&_Troubleshooting)
  - [4.1 Known Issues](#Known_Issues)
  - [4.2 Live Help](#Live_Help)
  - [4.3 F.A.Q](#F.A.Q)
- [5 See Also](#See_Also)

|  |  |
| --- | --- |
| [Arma 2](/wiki/Category:Arma_2 "Arma 2")  For Arma 2/Arma 2:OA, see [Arma 2: Dedicated Server](/wiki/Arma_2:_Dedicated_Server "Arma 2: Dedicated Server"). | ⓘ  For other titles, see [Dedicated Server](/wiki/Dedicated_Server "Dedicated Server"). |

[![Arma 3 logo black.png](/wikidata/images/thumb/8/80/Arma_3_logo_black.png/200px-Arma_3_logo_black.png)](/wiki/File:Arma_3_logo_black.png)

This page contains useful information on how to install and configure an Arma 3 server and includes step by step guides of the install process. Also information on how to configure and run a server.
The majority of directories and paths are customisable, however those defined are all consistent with the tutorial instructions.

Much of the tutorial here was created using [this forum guide](https://forums.bohemia.net/forums/topic/139003-tutorial-installation-configuration-of-arma3-dedicated-server/).

ⓘ

About steamCMD utility: if you are using it on a dedicated host to install and update Arma 3 dedicated server, it is a good practice to create a separate steam account for that.
Arma 3 Dedicated server package is available for free (does not require regular Arma 3 to be purchased).

[![Arma 3 logo black.png](/wikidata/images/thumb/8/80/Arma_3_logo_black.png/30px-Arma_3_logo_black.png)](/wiki/Category:Introduced_with_Arma_3_version_1.24 "Category:Introduced with Arma 3 version 1.24") [1.24](/wiki/Category:Introduced_with_Arma_3_version_1.24 "Category:Introduced with Arma 3 version 1.24")

## Simple Setup

The introduction of the [Arma 3 Launcher](/wiki/Arma_3:_Launcher "Arma 3: Launcher") with Arma 3 v1.24 made it easier to use the game's exe as a server with a simplified server mods configuration.

- Start the [Arma 3 Launcher](/wiki/Arma_3:_Launcher "Arma 3: Launcher") from [Steam](/wiki/Steam "Steam")
- Go to the **PARAMETERS > All Parameters > Host** section and tick **Server**
- In the **MODS** tab, select the server's wanted mods
- Press "Play"

This starts a dedicated server with the selected mods. This is adapted for a small casual dedicated server.

## Installation[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=1 "Edit section: Installation")]

### Requirements[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=2 "Edit section: Requirements")]

1. Steam Account
2. Supported Operating System:
   - Windows Server 2008 or later
   - A modern Linux distribution
3. Minimum Hardware:

:   :   [source](https://www.servermania.com/kb/articles/what-are-the-requirements-for-an-arma-3-server/)

        | Part | Minimum | Recommended |
        | --- | --- | --- |
        | CPU | 2.4 GHz Dual-Core | 3.5 GHz Quad-Core |
        | RAM | 2GB | 4GB |
        | Storage | 32 GB HDD | 32 GB SSD |

⚠

Arma 3 server does **not** support [IPv6](https://en.wikipedia.org/wiki/IPv6) or [DSlite IPv4 via IPv6 tunnel](https://en.wikipedia.org/wiki/IPv6_transition_mechanism#Dual-Stack_Lite_.28DS-Lite.29).
You **must** have a **real [IPv4](https://en.wikipedia.org/wiki/IPv4) connection**.  
If you are in doubt, contact your Internet Service Provider.

### Instructions (Windows o/s)[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=3 "Edit section: Instructions (Windows o/s)")]

The following instructions will guide you through setting up one Arma 3 server on 1 box, however they will also set the foundations for installing multiple servers on that same box:

1. Install the latest version of [DirectX](http://support.microsoft.com/kb/179113)
2. Create the following empty directories
   - D:\Apps\Steam
   - D:\Games\Arma3\A3Master
   - D:\Games\Arma3\A3Files
3. Download [steamcmd.exe](https://developer.valvesoftware.com/wiki/SteamCMD) and save it to your targetted Steam install directory (E.g D:\Apps\Steam)
4. Run the steamcmd.exe. (This will download and install the required steam files to your custom steam directory)
5. Create an [Arma3server\_steamcmd\_example.cmd](#Arma3server_steamcmd_example.cmd) file and save it to D:\Games\Arma3\A3Files
6. Run the [Arma3server\_steamcmd\_example.cmd](#Arma3server_steamcmd_example.cmd) file
7. Just after logging into Steam, the console window will hang and ask for a validation key
   - Steam will have automatically sent you an email with this validation code, which you then need to input at the command prompt
   - The Update console window should then continue to run and install Arma 3 ((DEV or STABLE) version to the target directory as defined in the .cmd file) eg (D:\Games\Arma3\A3Master)
8. Create a shortcut for the Arma3Server.exe on the server desktop
9. Add the following parameters to the Target Line in the shortcut tab of the newly created desktop shortcut
   - -port=2302 (Required if running multiple server instances including any previous Arma 2 instances)
   - "-profiles=D:\Games\Arma3\A3Master"
   - -config=CONFIG\_server.cfg
   - -world=empty
     - so it looks something similar to the following
     - "D:\Games\Arma3\A3Master\arma3server.exe" **"-profiles=D:\Games\Arma3\A3Master" -port=2302 -config=CONFIG\_server.cfg -world=empty**
10. Setup the [Port Forwarding](#Port_Forwarding) in your firewall and/or router accordingly, also doesn't seems like implemented in some routers Port Trigger feature works with Arma 3 Server (tested with ASUS RT-N66U)
11. Make sure you have:
    - Network Discovery enabled for your network type (the setting can be found in [Control Panel\Network and Internet\Network and Sharing Center\Advanced sharing settings]. If you have problem with its state reset on the interface reopen, check if all of the required for this feature services are running: DNS Client, Function Discovery Resource Publication, SSDP Discovery, UPnP Device Host; this makes the server accessible from internet
    - Windows Media Player Network Sharing Service enabled (makes server joinable) ;

If you cannot connect to server through server browser try to use "direct connect":
this is a client side issue (doesn't mean other clients will have it too), reason for the issue for now unknown,
I can say it is not related to: Windows services, Windows network settings, network routers, firewalls...

Device Host:
(note: Changes to the above 2 settings require Arma relaunch to be applied, as for server same for client machines;)
Create a simple Notepad document called "[CONFIG\_server.cfg](#CONFIG_server.cfg)" and save it to the root folder of your Arma 3 install on the server D:\Games\Arma3\A3Master

1. Start up your shortcut, check the server runs. (You will see a console pop up in your desktop after a few seconds)
2. Close the console window down, then you will need to edit the following files which will have been automatically created
   - D:\Games\Arma3\A3Master\Users\Adminstrator\Administrator.Arma3Profile
   - D:\Games\Arma3\A3Master\Users\Administrator\Arma3.cfg
3. Restart the server
4. Start up your client Arma3.exe (Running the same branch as the server, (eg Stable or DEV) and you should then be able to see your server in the server browser (Filters are available to reduce the server list)
5. Login to your server using the password you defined in [CONFIG\_server.cfg](#CONFIG_server.cfg) by pressing `/` to open the chat window and then type #login *ADMINPASSWORD* followed by `Return ↵`.

   ⚠

   Make sure to never forget the '#' in front of any server command!
6. Once logged in you will be presented with a mission list, select one of the missions to start the game
7. Prove the stability of your server by running BI missions initially before you start adding user made content

### Instructions (Linux o/s)[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=4 "Edit section: Instructions (Linux o/s)")]

#### Installation[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=5 "Edit section: Installation")]

As a security best practice, create a user to run steam instead of running as root or an administrator.
This way, if your Arma 3 server is compromised the attacker will find it more difficult to access the rest of the operating system.

```
useradd -m -s /bin/bash steam
```

Switch to the newly created steam user.

```
sudo -i -u steam
```

Create a new directory for SteamCMD to avoid cluttering the home directory.

```
mkdir ~/steamcmd && cd ~/steamcmd
```

Download the [SteamCMD for Linux](https://developer.valvesoftware.com/wiki/SteamCMD#Linux) tarball from the link at the Valve Developer Community page.
Use a command line download tool such as wget or curl.

Extract the tarball.

```
tar xf steamcmd_linux.tar.gz
```

Execute steamcmd.sh. It will install the rest of the Steam client and start the Steam client shell.
If this step fails on a 64-bit OS, you likely need to [install 32-bit libraries](https://developer.valvesoftware.com/wiki/SteamCMD#32-bit_libraries_on_64-bit_Linux_systems).

```
./steamcmd.sh
```

Change the directory SteamCMD will install the server in.

```
force_install_dir ./arma3/
```

At the Steam client shell, login with a valid Steam username and password.
The Valve Developer Community Wiki [recommends that you create a new Steam account for this](https://developer.valvesoftware.com/wiki/SteamCMD#SteamCMD_Login).
SteamCMD will cache the login credentials and anyone who gains access to your server will be able to log into the account used here.
In addition, you cannot log into a single Steam account from two places at once. You do not need to have Arma 3 purchased on the Steam account used here to download the server.
Therefore, you should create a new Steam account with no purchases only for use on this server.

```
login myusername
```

Install the Arma 3 Linux dedicated server. The validate option will check for corruption.

```
app_update 233780 validate
```

Exit SteamCMD.

```
exit
```

Create the directories used to store the profile files and Arma3.cfg file.

```
mkdir -p ~/".local/share/Arma 3" && mkdir -p ~/".local/share/Arma 3 - Other Profiles"
```

#### Updating[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=6 "Edit section: Updating")]

You will have to update the server whenever a patch is released on Steam.

If the server is running, stop it by pressing Ctrl+C in the terminal (or screen/tmux instance) that the server is attached to. Otherwise, switch to the steam user.

```
sudo -u steam -i
```

Launch steamcmd.

```
cd /home/steam/steamcmd
./steamcmd.sh
```

Set the Arma 3 installation directory to the same directory used above.

```
force_install_dir ./arma3/
```

Login to the Steam account used in the installation section above.

```
login myusername
```

Update the Arma 3 Linux dedicated server. The validate option will check for corruption.

```
app_update 233780 validate
```

Exit SteamCMD.

```
exit
```

#### Configuration[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=7 "Edit section: Configuration")]

You will most likely want to customise your server's name, password, security settings, and so on with a server.cfg file. Change to the arma3 directory and create and edit your server.cfg file.

```
cd ~/steamcmd/arma3
vim server.cfg
```

If you don't know how to use vim, use nano instead.

```
nano server.cfg
```

See [server config](/wiki/Arma_3:_Server_Config_File "Arma 3: Server Config File") for documentation and examples.

The first time you run the server it will auto-create a profile file at ~/.local/share/Arma 3 - Other Profiles/server/server.Arma3Profile.
Edit this file to customise difficulty settings.

⚠

The -profiles= parameter is broken on Linux - you **must** place your profiles in this directory.

#### Launching[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=8 "Edit section: Launching")]

A Linux executable is provided to launch the server. Make sure that you are running it under the steam user and not root or another administrator!

```
cd /home/steam/steamcmd/arma3
./arma3server_x64 -name=server -config=server.cfg
```

NB: If you are using the -mod= parameter, you must specify relative paths *within or below* the Arma 3 directory. Symlinks will work.

The arma3server\_x64 process will attach to the current terminal.
If you are connected to the server over SSH, the server will stop when you disconnect.
You can keep the server running using a terminal multiplexeer like [GNU Screen](https://www.gnu.org/software/screen/) or [tmux](http://tmux.sourceforge.net/).
There are many tutorials online on how to use these programs to detach and reattach processes from and to a terminal.

You can safely stop the server by pressing Ctrl+C in the terminal (or screen/tmux instance) that the server is attached to.

For older 32-bit executable use ./arma3server instead of ./arma3server\_x64.

#### Case sensitivity & Mods[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=9 "Edit section: Case sensitivity & Mods")]

Some mods such as CUP Terrains and @ALiVE will not function if there are capital letters in any of their file names. If you do not update your mods on a regular basis, you can just use the command

```
find . -depth -exec rename 's/(.*)\/([^\/]*)/$1\/\L$2/' {} \;
```

in the directory where your mods are located. This will recursively search the directory tree and make all the filenames lowercase.

If you regularly update your mods using, e.g. Arma3Sync, you will find that this will redownload any files/folders that have changed case every time you run it.
The solution to this is to use a package called "ciopfs" - Case Insensitive On Purpose Filesystem. You should first run the "find . -depth..." command mentioned above on your mod folder.
Then, make an empty directory outside of the mods directory, e.g. mods\_caseinsensitive. You then mount the directory with:

```
ciopfs mods mods_caseinsensitive
```

and tell Arma3Sync to synchronise in the mods\_caseinsensitive directory. Once it has finished, you can unmount the directory with:

```
fusermount -u mods_caseinsensitive
```

You should find that all of the files in the original mods directory are lowercase.

*This article is a [Stub](/wiki/Category:Stubs "Category:Stubs"). You can help BI Community Wiki by [expanding it](https://community.bistudio.com/wiki?title=Arma_3:_Dedicated_Server&action=edit).*

## Additional Info[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=10 "Edit section: Additional Info")]

### Useful Links[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=11 "Edit section: Useful Links")]

#### Further Reading[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=12 "Edit section: Further Reading")]

- [Steam Cmd Tutorial](https://developer.valvesoftware.com/wiki/SteamCMD)
- [Dedicated Server Status (Linux info)](https://forums.bohemia.net/forums/topic/139605-dedicated-server-status/)
- [Kelly's Heroes Dedicated server guide](http://www.kellys-heroes.eu/files/tutorials/dedicated/arma3dedicated.php#one)
- [In Game server commands](/wiki/In_Game_Server_Commands "In Game Server Commands")
- [In Game admin commands](/wiki/Multiplayer_Server_Commands "Multiplayer Server Commands")
- [Command line params](/wiki/Arma_3:_Startup_Parameters "Arma 3: Startup Parameters")
- [Basic.cfg](/wiki/basic.cfg "basic.cfg")
- [Steam Difficulty settings](/wiki/server.armaprofile "server.armaprofile")
- [Server Configuration](/wiki/ArmA:_Armed_Assault:_Server_configuration "ArmA: Armed Assault: Server configuration")
- [Sim-link GUI](https://code.google.com/p/symlinker/)

### File Locations[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=13 "Edit section: File Locations")]

Some files are automatically created when you use certain commandline parameters
Some files have to be created manually.
If you have followed the instructions accurately, you will now have the following files and directories in addition to the clean install

- D:\Games\Arma3\A3Master\Users\Administrator\**Administrator.Arma3Profile** *(Difficulty settings)*
- D:\Games\Arma3\A3Master\Users\Administrator\**Administrator.vars.Arma3Profile** *(Some binarised content which you cannot edit)*
- D:\Games\Arma3\A3Master\Users\Administrator\**Arma3.cfg** *(Bandwidth settings)*
- D:\Games\Arma3\A3Master\**MPMissions\** *(This is where custom made mission.pbo's need to be placed)*
- D:\Games\Arma3\A3Master\**arma3.rpt** *(Debug Log, automatically created every time the arma3server.exe is started)*
- D:\Games\Arma3\A3Master\**CONFIG\_server.cfg** *(Manually created)*
- D:\Games\Arma3\A3Files\**Arma3server\_steamcmd\_example.cmd** *(Manually created)*
- D:\Apps\Steam\

### Port Forwarding[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=14 "Edit section: Port Forwarding")]

Arma 3 uses the same default ports as Arma 2 with the addition of several ports. So if you intend running Arma 2 and Arma 3 servers on the same machine, you need to edit the ports used.
**Default ports are all UDP** and as follows:

Incoming

- 2302 (default Arma 3 Game port) + (VON is now part of main gameport due to NAT issues)
- 2303 (STEAM query, +1)
- 2304 (Steam port, +2)
- 2305 (VON port, +3 - not used atm. but allocated)
- 2306 (BattlEye traffic, +4)

Outgoing

| DST Port | Protocol | Destination | Comment |
| --- | --- | --- | --- |
| 2344 | TCP + UDP | 81.0.236.111 | BattlEye - arma31.battleye.com |
| 2345 | TCP | 81.0.236.111 | BattlEye - arma31.battleye.com |
| 2302-2306 | UDP | any | Arma Server to Client Traffic |
| 2303 | UDP | any | Arma Server STEAM query port |
| 2304 | UDP | any | Arma Server to STEAM master traffic |

To define the Arma 3 Game port used,
state -port= \*\*\*\* in your command line arguments (Where \*\*\*\* is the new initial Arma 3 Game, e.g 2302)

and to define your Steam ports
add the following lines to your CONFIG\_server.cfg (editing the actual port numbers as required)

ⓘ

Steam ports are now linked to game-port as +1 for query and +2 to-master.

If you are running multiple servers, i would suggest the following format

SERVER 1

2302 UDP (gameport + VON)
  
2303 UDP (STEAM query port)
  
2304 UDP (STEAM master port)
  
2305 UDP (VON reserved port but not used atm.)
  
2306 UDP (BattlEye traffic port)
  
so open ports 2302-2306
  
... and leave at least **100** ports between the next 2nd server set

SERVER 2

2402 UDP (gameport + VON)
  
2403 UDP (STEAM query port)
  
2404 UDP (STEAM master port)
  
2405 UDP (VON reserved port but not used atm.)
  
2406 UDP (BattlEye traffic port)
  
so open ports 2402-2406
  
and leave at least **100** ports between the next 3rd server set, etc.

#### NAT traversal[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=15 "Edit section: NAT traversal")]

UPnP allows you to automatically create port forwarding on your router to bypass NAT (UPnP enabled routers are required).
The functionality can be easily enabled in server config as described in the [server.cfg](/wiki/Arma_3:_Server_Config_File#Server_Options "Arma 3: Server Config File") page.

### Configuring for stable or Dev branch[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=16 "Edit section: Configuring for stable or Dev branch")]

Edit the Arma3server\_steamcmd\_example.cmd as defined below then run it

- To select Development version

```
SET A3BRANCH=107410 -beta development
```

- To select stable build (Dedicated server package)

```
SET A3BRANCH=233780 -beta
```

- To select stable build (Full client\server package)

```
SET A3BRANCH=107410
```

Important Feature

Since **12th March 2013** the development branch and the stable branch versions are no longer compatible. This means:

- Only DEV clients can connect to a DEV server
- Only Stable clients can connect to stable servers

### Advanced Configuration[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=17 "Edit section: Advanced Configuration")]

*This article is a [Stub](/wiki/Category:Stubs "Category:Stubs"). You can help BI Community Wiki by [expanding it](https://community.bistudio.com/wiki?title=Arma_3:_Dedicated_Server&action=edit).*

### Bandwidth Optimisation[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=18 "Edit section: Bandwidth Optimisation")]

*This article is a [Stub](/wiki/Category:Stubs "Category:Stubs"). You can help BI Community Wiki by [expanding it](https://community.bistudio.com/wiki?title=Arma_3:_Dedicated_Server&action=edit).*

### Multiple Server configuration[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=19 "Edit section: Multiple Server configuration")]

There are various methods available to run multiple Dedicated Arma 3 servers on the same box.
Each server instance requires:

- Its own unique set of ports
- Its own profile
- Its own config

[This post](https://forums.bohemia.net/forums/topic/139003-tutorial-installation-configuration-of-arma3-dedicated-server/?page=10&tab=comments#comment-2344472) explains it more in depth.

Each method has some pro's and cons
To select the preferred methodology that suits your requirements you need to have considered the following

- The useage of the "Keys" folder
- The useage of the "MpMissions" folder
- Available Hard drive space
- Update management

#### Method 1[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=20 "Edit section: Method 1")]

see [THIS POST](https://forums.bohemia.net/forums/topic/139003-tutorial-installation-configuration-of-arma3-dedicated-server/?page=9&tab=comments#comment-2343865) for more detailed information
This is copy of the master install into a different directory
As may times as you have space for

**Pros**

- Most robust
- Unique MpMissions folder
- Unique "keys" folder
- Allows ability to run separate instances of different branches of the game, (E.g Dev or Stable)

**Cons**

- Uses more drive space
- Requires more effort to create and automate the updating process

#### Method 2[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=21 "Edit section: Method 2")]

This uses 1 master install folder and has renamed arma3server.exe's in sub folders of the master
see THIS POST for more detailed information

**Pros** (Compared to [Method 1](#Method_1))

- Uses less space than Method 1
- Updating will be easier

**Cons** (Compared to [Method 1](#Method_1))

- Not very robust
- Shares MPMissions folders with all the other exe's (Will create a very cluttered MpMissions folder and allows admins to select addon required missions for a mod this server instance isn't running
- Shares "keys" folder with all other exe's (This can cause an issue when running various differing -mod servers)
- Will not allow you to run separate instances of different branches of the game

#### Method 3[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=22 "Edit section: Method 3")]

This is a hybrid of method 1, in that it is essentially a "Virtual" copy of the master install in a different directory
the difference being that many folders are [symlinked](https://en.wikipedia.org/wiki/Symbolic_link) to the initial Master directory
symlink GUI:

**Pros** (Compared to [Method 1](#Method_1))

- Uses less drive space (As you wont have multiple copies of the addons folder)
- Allows for unique MpMissions folder if desired
- Allows for unique "keys" folder if desired
- Updating will be easier

**Cons** (Compared to [Method 1](#Method_1))

- Requires more effort to initially create the symlinks and directories
- Will not allow you to run separate instances of different branches of the game at the same time, (E.g Dev or Stable)

For example, on Linux, if you initially have the following folder layout:

```
 ~/game_servers/
	arma3server/
	...
	mods # folder where you keep your mods
	arma3server
	...
```

Instead of copying the folders as directed in (Compared to [Method 1](#Method_1)), run the following commands:

```
cd ~/game_servers/
mkdir arma3server_2/
ln -sr arma3server/* arma3server_2
cd arma3server_2
rm keys mpmissions mods
mkdir keys
mkdir mpmissions
mkdir mods
```

Then proceed with the rest of the instructions in METHOD 1.

#### Method 4[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=23 "Edit section: Method 4")]

You can of course mix and match all 3 previous methods to suit your specific requirements.

[![Arma 3 logo black.png](/wikidata/images/thumb/8/80/Arma_3_logo_black.png/30px-Arma_3_logo_black.png)](/wiki/Category:Introduced_with_Arma_3_version_2.22 "Category:Introduced with Arma 3 version 2.22") [2.22](/wiki/Category:Introduced_with_Arma_3_version_2.22 "Category:Introduced with Arma 3 version 2.22")

#### Method 5

You can combine the [-port](/wiki/Arma_3:_Startup_Parameters#port "Arma 3: Startup Parameters"), [-profiles](/wiki/Arma_3:_Startup_Parameters#profiles "Arma 3: Startup Parameters"), [-mpmissions](/wiki/Arma_3:_Startup_Parameters#mpmissions "Arma 3: Startup Parameters") and [-keysFolder](/wiki/Arma_3:_Startup_Parameters#keysFolder "Arma 3: Startup Parameters") startup parameters to run multiple servers from the same Arma 3 installation directory while using different ports and separate directories for profiles, missions, and keys.

This approach allows you to host multiple servers without using symbolic links or duplicating the entire server installation.

Example batch script for running multiple servers:

```
@echo off

rem Starting TvT server
start arma3server_x64.exe -port=2302^
 -name=TvT^
 -profiles=Profiles\TvT^
 -mpmissions=Profiles\TvT\MPMissions^
 -keysFolder=Profiles\TvT\Keys^
 -cfg=Profiles\TvT\Configs\Basic.cfg^
 -config=Profiles\TvT\Configs\Server.cfg^
 -mod="@CBA_A3;@ace"

rem Starting PvE server
start arma3server_x64.exe -port=2402^
 -name=PvE^
 -profiles=Profiles\PvE^
 -mpmissions=Profiles\PvE\MPMissions^
 -config=Profiles\PvE\Configs\Server.cfg
```

Before running the script, create the following directory structure:

```
Arma 3 installation/
├── Profiles/
│   ├── TvT/
│   │   ├── MPMissions/
│   │   ├── Keys/
│   │   └── Configs/
│   │       ├── Basic.cfg
│   │       └── Server.cfg
│   │
│   └── PvE/
│       ├── MPMissions/
│       ├── Keys/
│       └── Configs/
│           └── Server.cfg
│
└── arma3server_x64.exe
```

### Dedicated Server & Client configuration[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=24 "Edit section: Dedicated Server & Client configuration")]

ⓘ

In order for the Arma 3 server to be able to connect to the Steam servers, the Arma 3 server has to be ping-able (ICMP 8 - Echo Reply) as well as opened TCP/UDP ports.

The only known issue, is to make sure you start the server up before you start the steam client
Failing to do this causes steam port issues and your client will not be able to connect to the server

### Headless Client[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=25 "Edit section: Headless Client")]

ⓘ

See [Arma 3: Headless Client](/wiki/Arma_3:_Headless_Client "Arma 3: Headless Client") for changes in headless clients.

*This article is a [Stub](/wiki/Category:Stubs "Category:Stubs"). You can help BI Community Wiki by [expanding it](https://community.bistudio.com/wiki?title=Arma_3:_Dedicated_Server&action=edit).*

A Headless Client can be used by missions to offload AI processing to a dedicated client, freeing up the dedicated server process from most AI processing.
Thus more AI units can be spawned and the server process will be able to dedicate most of it is processing towards communication with the clients.
For a Headless Client/Dedicated Server to function together efficiently, they both need to be connected to each other via extremely low latency and high bandwidth.
Effectively, they need to be on the same LAN at least, but preferably running on the same computer.

#### Setup and Execution[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=26 "Edit section: Setup and Execution")]

- Headless Client for Arma 3 requires a **valid active Steam account logged in** to function (see [Dwarden's post](https://forums.bohemia.net/forums/topic/140621-arma-3-headless-client/?page=5&tab=comments#comment-2481581))
- A Headless Client is simply Arma3.exe run from the command line with parameters, for example:

```
arma3.exe -connect=localhost -port=2444 -client -nosound -password=some_server_password -name=HC -profile=HC -mod=@CBA_A3;@blah;@blah;@blah
```

In the example above, the headless client is running on the same host as the dedicated server.
It uses a profile called **HC** and connects to the server with a password as required by the server (the password is for password protected servers, optional).
The server in this example is running on port 2444, alter this to the port of your server.

- The Headless Client profile needs to be created like any other profile in Arma 3 (Note: It is best if you name this profile **HC**)

No configuration of the profile is necessary, but after creation, edit **HC.Arma3Profile** located in **C:\Users\yourusername\Documents\Arma 3 - Other Profiles\HC** and add this line:

```
battleyeLicense=1;
```

Alternatively, you can use the full Arma 3 client to connect to any multiplayer server with BattlEye enabled and accept the BattlEye License Agreement,
which will do the same thing as manually editing **HC.Arma3Profile**.

Once the profile is ready, copy the profile directory to **C:\Users\yourusername\Documents\Arma 3 - Other Profiles** on the server (if the profile was not created on the server).
If the profile was created on the server or is not going to run on the server and is already on the computer it is going to run on, then it is ready to go.

- If the Headless Client is on the server, the server's cfg must have the local loopback address as seen below (e.g. A3Server.cfg):

```
localClient[]={ "127.0.0.1" };
headlessClients[]={ "127.0.0.1" };
battleyeLicense=1;
```

Dwarden suggests that **battleyeLicense=1;** be in the server's config, but it is unclear whether this is actually necessary.
([Dwarden's post](https://forums.bohemia.net/forums/topic/150815-headless-client-battleye/?tab=comments#comment-2413319)).

- If the Headless Client is running in the same network or the same computer, the server's config has to have the address in **localClient[]=x.x.x.x**.

This will allow the server to communicate with the Headless Client using unrestricted bandwidth. It is also required to add the adress to **headlessClients[]={ "x.x.x.x" };**.
Multiple headless clients at different addresses can be added:

```
localClient[]={ "127.0.0.1", "192.168.1.10", "192.168.1.210" };
headlessClients[]={ "127.0.0.1", "192.168.1.10", "192.168.1.210" };
battleyeLicense=1;
```

#### Headless Client on a Dedicated Server Notes[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=27 "Edit section: Headless Client on a Dedicated Server Notes")]

If the [Arma 3: Headless Client](/wiki/Arma_3:_Headless_Client "Arma 3: Headless Client") and a Dedicated Server are running on the same Windows computer (VM or physical),
it *may* be beneficial to set processor affinity to prevent execution intensive threads from being scheduled across the same (v)CPUs.

⚠

Do **not** use this if you do not know what you are doing, as performance may be strongly impacted.

The operating system will generally schedule threads efficiently, but manual CPU allocation is possible.
This can be achieved by right-clicking on the process (e.g. Arma3Server.exe(\*32)) in the **processes tab** of the Windows Task Manager and selecting **Set Affinity...**.
Be aware that on a physical intel CPU, the odd numbered CPUs are Hyper-thread cores.

ⓘ

You can use automated solution for affinity assign via batch file with commandline CMD /AFFINITY HEXvalue e.g. CMD /C START /AFFINITY 0xF3 arma3server.exe.

It has been observed that although the Arma server and client processes will kick off multiple threads, the bulk of processing is used up by only one or two threads.
For example, spawning 50 AI units does not generate 50 threads.
There is one thread in the process that handles all of the AI units, irrespective of how many have been spawned.
In this way, the ARMA server and client processes do not make maximal use of the processing capability found in modern processors and so AI counts do not scale easily.
As such, faster CPU core speed is king and offloading the AI to multiple headless clients on the same computer will probably produce the best possible results for complex missions involving many AI units (albeit an expensive way to get the results).

ⓘ

Read the complete and detailed HOW-TO guide on Headless Client: [Arma 3: Headless Client](/wiki/Arma_3:_Headless_Client "Arma 3: Headless Client").

-

### Example Files[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=28 "Edit section: Example Files")]

#### Arma3server\_steamcmd\_example.cmd[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=29 "Edit section: Arma3server steamcmd example.cmd")]

- Standalone Server Package (no need for game ownership on STEAM account)
- Note: you might be asked to authorise your STEAM account due to STEAMguard (check email)
- Note: it needs to be ran two or three times (1st authorise, 2nd STEAMcmd update itself, (when done, quit), 3rd update the Arma 3 Server data package)
- Note: STEAMcmd needs to be in separate folder from STEAMclient (so always use unique folder like "STEAMcmd") to avoid file conflicts
- Note: Arma 3 server data package needs to be in separate folder from Arma 3 client (so always use unique folder like "A3server" or "Arma3server") to avoid file conflicts
- Code:

  ```
  @echo off
  rem http://media.steampowered.com/installer/steamcmd.zip
  SETLOCAL ENABLEDELAYEDEXPANSION

  	:: DEFINE the following variables where applicable to your install

  	SET STEAMLOGIN=mylogin mypassword
  	SET A3serverBRANCH=233780 -beta public
  		:: For stable use 233780 -beta public
  		:: For Dev use 233780 -beta development
  				:: Note: quotation marks need to be wrapped around the entire "+app_data......"
  				:: There is no DEV branch data yet for Arma 3 Dedicated Server package!

  	SET A3serverPath=D:\Games\Arma3\A3Master
  		SET STEAMPATH=D:\Apps\Steam

  :: 

  echo.
  echo You are about to update Arma 3 server
  echo 	Dir: %A3serverPath%
  echo 	Branch: %A3serverBRANCH%
  echo.
  echo Key "ENTER" to proceed
  pause
  %STEAMPATH%\steamcmd.exe +force_install_dir %A3serverPath% +login %STEAMLOGIN% +"app_update %A3serverBRANCH%" validate +quit
  echo.
  echo Your Arma 3 server is now up to date
  echo Press "ENTER" to exit
  pause
  ```

#### CONFIG\_server.cfg[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=30 "Edit section: CONFIG server.cfg")]

```
//
// server.cfg
//

// NOTE: More parameters and details are available at http://community.bistudio.com/wiki/server.cfg

// STEAM PORTS (not needed anymore, it is +1 +2 to gameport)
// steamPort		= 8766;		// default 8766, needs to be unique if multiple servers are on the same box
// steamQueryPort	= 27016;	// default 27016, needs to be unique if multiple servers are on the same box

// GENERAL SETTINGS
hostname		= "My Arma 3 Server";	// Name of the server displayed in the public server list
//password		= "ServerPassword";		// Password required to join the server (remove // at start of line to enable)
passwordAdmin	= "AdminPassword";		// Password to login as admin. Open the chat and type: #login password
maxPlayers		= 40;	// Maximum amount of players, including headless clients. Anybody who joins the server is considered a player, regardless of their role or team.
persistent		= 1;	// If set to 1, missions will continue to run after all players have disconnected; required if you want to use the -autoInit startup parameter

// VOICE CHAT
disableVoN		= 0;	// If set to 1, voice chat will be disabled
vonCodecQuality	= 10;	// Supports range 1-30, the higher the better sound quality, the more bandwidth consumption:
						// 1-10 is 8kHz (narrowband)
						// 11-20 is 16kHz (wideband)
						// 21-30 is 32kHz (ultrawideband)

// VOTING
voteMissionPlayers	= 1;		// Minimum number of players required before displaying the mission selection screen, if you have not already selected a mission in this config
voteThreshold		= 0.33;		// Percentage (0.00 to 1.00) of players needed to vote something into effect, for example an admin or a new mission. Set to 9999 to disable voting.
allowedVoteCmds[] =				// Voting commands allowed to players
{
	// { command, preinit, postinit, threshold } - specifying a threshold value will override "voteThreshold" for that command
	{ "admin", false, false},		// vote admin
	{ "kick", false, true, 0.51},	// vote kick
	{ "missions", false, false},		// mission change
	{ "mission", false, false},		// mission selection
	{ "restart", false, false},		// mission restart
	{ "reassign", false, false}		// mission restart with roles unassigned
};

// WELCOME MESSAGE ("message of the day")
// It can be several lines, separated by comma
// Empty messages "" will not be displayed, but can be used to increase the delay before other messages
motd[] =
{
	"Welcome to My Arma 3 Server",
	"Discord: discord.somewhere.com",
	"TeamSpeak: ts.somewhere.com",
	"Website: www.example.com"
};
motdInterval = 5;	// Number of seconds between each message

// MISSIONS CYCLE
class Missions
{
	class Mission1
	{
		template	= "MyMission.Altis";	// Filename of pbo in MPMissions folder
		difficulty	= "Regular";			// "Recruit", "Regular", "Veteran", "Custom"
	};
};

// LOGGING
timeStampFormat	= "short";				// Timestamp format used in the server RPT logs. Possible values are "none" (default), "short", "full"
logFile			= "server_console.log";	// Server console output filename

// SECURITY
BattlEye				= 1;	// If set to 1, BattlEye Anti-Cheat will be enabled on the server (default: 1, recommended: 1)
verifySignatures		= 2;	// If set to 2, players with unknown or unsigned mods won't be allowed join (default: 0, recommended: 2)
kickDuplicate			= 1;	// If set to 1, players with an ID that is identical to another player will be kicked (recommended: 1)
allowedFilePatching		= 1;	// Prevents clients with filePatching enabled from joining the server
								// (0 = block filePatching, 1 = allow headless clients, 2 = allow all) (default: 0, recommended: 1)

// FILE EXTENSIONS

// only allow files with those extensions to be loaded via loadFile command (since Arma 3 v1.20)
allowedLoadFileExtensions[] =		{ "hpp", "sqs", "sqf", "fsm", "cpp", "paa", "txt", "xml", "inc", "ext", "sqm", "ods", "fxy", "lip", "csv", "kb", "bik", "bikb", "html", "htm", "biedi" };

// only allow files with those extensions to be loaded via preprocessFile / preprocessFileLineNumbers commands (since Arma 3 v1.20)
allowedPreprocessFileExtensions[] =	{ "hpp", "sqs", "sqf", "fsm", "cpp", "paa", "txt", "xml", "inc", "ext", "sqm", "ods", "fxy", "lip", "csv", "kb", "bik", "bikb", "html", "htm", "biedi" };

// only allow files and URLs with those extensions to be loaded via htmlLoad command (since Arma 3 v1.28)
allowedHTMLLoadExtensions[] =		{ "htm", "html", "php", "xml", "txt" };

// EVENT SCRIPTS - see http://community.bistudio.com/wiki/Arma_3:_Server_Side_Scripting
onUserConnected		= "";	// command to run when a player connects
onUserDisconnected	= "";	// command to run when a player disconnects
doubleIdDetected	= "";	// command to run if a player has the same ID as another player in the server
onUnsignedData		= "kick (_this select 0)";	// command to run if a player has unsigned files
onHackedData		= "kick (_this select 0)";	// command to run if a player has tampered files

// HEADLESS CLIENT
headlessClients[]	= { "127.0.0.1" };	// list of IP addresses allowed to connect using headless clients; example: { "127.0.0.1", "192.168.1.100" };
localClient[]		= { "127.0.0.1" };	// list of IP addresses to which are granted unlimited bandwidth; example: { "127.0.0.1", "192.168.1.100" };
```

#### server.Arma3Profile[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=31 "Edit section: server.Arma3Profile")]

See [server.armaprofile](/wiki/server.armaprofile "server.armaprofile").

#### Arma3.cfg[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=32 "Edit section: Arma3.cfg")]

ⓘ

See [basic.cfg](/wiki/basic.cfg "basic.cfg") for more information.

```
language="English";
adapter=-1;
MinBandwidth=800000;
MaxBandwidth=25000000;
MaxMsgSend=384;
MaxSizeGuaranteed=512;
MaxSizeNonguaranteed=256;
MinErrorToSend=0.003;
MaxCustomFileSize=100000;
Windowed=0;
serverLongitude=0;
serverLatitude=52;
serverLongitudeAuto=0;
serverLatitudeAuto=52;
```

## Support & Troubleshooting[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=33 "Edit section: Support & Troubleshooting")]

### Known Issues[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=34 "Edit section: Known Issues")]

1. My server has 2500ms ping in server browser (This is caused by running the steam client as Non Administrator) see <http://feedback.arma3.com/view.php?id=9374>
2. MaxMsgSend default is 128, higher values can create Desync problems if the server's available processing and/or bandwidth can not support the higher value
3. if you run Arma 3 Dedicated server data package on system where is also installed client there is possibility the server will load the full client data instead server data package, this is caused by precedence check in registry.  
   The solution/workaround to this issue is to use arma3server.exe -mod= (where empty modline ensure it loads data from where server is placed)
4. upnp=1; // in server.cfg may result into server not starting for 600 seconds on server with active firewalls / anti-ddos measures ,   
   it's not recommended setting for dedicated-servers where server process related ports are open/forwarded by default
5. using multiple server instances with too narrow ranges in between each (e.g. first 2302, second 2314) may cause problems with query where second server reports at first range, use of minimum +100 ports for next instance next to 2302 thus 2402 then 2502 etc. for utmost safety use +200 or +1000
6. engine has legacy automated port increment system for occupied sockets (used for client-listen servers, upnp) where first is tried 2302, then +12 up to 15 times (so 2314, 2326 ... 2482)

### Live Help[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=35 "Edit section: Live Help")]

For live assistance, join the official **[Arma Discord server](https://discord.gg/arma)!**
Especially these channels:

- #server\_admins
- #server\_linux
- #server\_windows
- #server\_tools

### F.A.Q[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=36 "Edit section: F.A.Q")]

*This article is a [Stub](/wiki/Category:Stubs "Category:Stubs"). You can help BI Community Wiki by [expanding it](https://community.bistudio.com/wiki?title=Arma_3:_Dedicated_Server&action=edit).*

## See Also[[edit source](/wiki?title=Arma_3:_Dedicated_Server&action=edit&section=37 "Edit section: See Also")]

- [Server Options](/wiki/Arma_3:_Startup_Parameters#Server_Options "Arma 3: Startup Parameters")
- [Battleye's RCON](/wiki/BattlEye#RCon "BattlEye")
- [Arma 3: Headless Client](/wiki/Arma_3:_Headless_Client "Arma 3: Headless Client")
- [Multiplayer Server Commands](/wiki/Multiplayer_Server_Commands "Multiplayer Server Commands")
- [Armed Assault Server Configuration](/wiki/ArmA:_Armed_Assault:_Server_configuration "ArmA: Armed Assault: Server configuration")