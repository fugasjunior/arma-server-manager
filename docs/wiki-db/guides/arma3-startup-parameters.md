---
game: arma3
slug: arma3-startup-parameters
file: startup
source: https://community.bistudio.com/wiki/Arma_3:_Startup_Parameters
scraped: 2026-05-31T11:45:19Z
---

- [1 Steam](#Steam)
- [2 Windows](#Windows)
- [3 Path Definition](#Path_Definition)
  - [3.1 Spaces](#Spaces)
  - [3.2 Relative vs Absolute](#Relative_vs_Absolute)
- [4 Display Options](#Display_Options)
  - [4.1 window](#window)
  - [4.2 noWindowBorder](#noWindowBorder)
  - [4.3 posX](#posX)
  - [4.4 posY](#posY)
  - [4.5 adapter](#adapter)
- [5 Game Loading Speedup](#Game_Loading_Speedup)
  - [5.1 noSplash](#noSplash)
  - [5.2 skipIntro](#skipIntro)
  - [5.3 world](#world)
  - [5.4 worldCfg](#worldCfg)
- [6 Profile Options](#Profile_Options)
  - [6.1 name](#name)
  - [6.2 profiles](#profiles)
  - [6.3 unit](#unit)
- [7 Misc.](#Misc.)
  - [7.1 buldozer](#buldozer)
  - [7.2 noLand](#noLand)
  - [7.3 noSound](#noSound)
  - [7.4 doNothing](#doNothing)
  - [7.5 mod](#mod)
  - [7.6 mpmissions](#mpmissions)
- [8 Client Network Options](#Client_Network_Options)
  - [8.1 connect](#connect)
  - [8.2 port](#port)
  - [8.3 password](#password)
  - [8.4 host](#host)
- [9 Server Options](#Server_Options)
  - [9.1 setUpHost](#setUpHost)
  - [9.2 server](#server)
  - [9.3 port](#port_2)
  - [9.4 pid](#pid)
  - [9.5 ranking](#ranking)
  - [9.6 netlog](#netlog)
  - [9.7 cfg](#cfg)
  - [9.8 config](#config)
  - [9.9 profiles](#profiles_2)
  - [9.10 bePath](#bePath)
  - [9.11 ip](#ip)
  - [9.12 par](#par)
  - [9.13 client](#client)
  - [9.14 loadMissionToMemory](#loadMissionToMemory)
  - [9.15 autoInit](#autoInit)
  - [9.16 serverMod](#serverMod)
  - [9.17 keysFolder](#keysFolder)
  - [9.18 disableServerThread](#disableServerThread)
  - [9.19 bandwidthAlg](#bandwidthAlg)
- [10 Performance](#Performance)
  - [10.1 limitFPS](#limitFPS)
  - [10.2 maxMem](#maxMem)
  - [10.3 maxVRAM](#maxVRAM)
  - [10.4 maxFileCacheSize](#maxFileCacheSize)
  - [10.5 noCB](#noCB)
  - [10.6 cpuCount](#cpuCount)
  - [10.7 cpuAffinity](#cpuAffinity)
  - [10.8 cpuMainThreadAffinity](#cpuMainThreadAffinity)
  - [10.9 enableHT](#enableHT)
  - [10.10 exThreads](#exThreads)
  - [10.11 malloc](#malloc)
  - [10.12 hugePages](#hugePages)
  - [10.13 setThreadCharacteristics](#setThreadCharacteristics)
- [11 Developer Options](#Developer_Options)
  - [11.1 noPause](#noPause)
  - [11.2 noPauseAudio](#noPauseAudio)
  - [11.3 showScriptErrors](#showScriptErrors)
  - [11.4 debug](#debug)
  - [11.5 noFreezeCheck](#noFreezeCheck)
  - [11.6 noLogs](#noLogs)
  - [11.7 noFilePatching](#noFilePatching)
  - [11.8 filePatching](#filePatching)
  - [11.9 init](#init)
  - [11.10 <path>\mission.sqm](#<path>\mission.sqm)
  - [11.11 autotest](#autotest)
  - [11.12 beta](#beta)
  - [11.13 cfgDependenciesDebugPrint](#cfgDependenciesDebugPrint)
  - [11.14 checkSignatures](#checkSignatures)
  - [11.15 checkSignaturesFull](#checkSignaturesFull)
  - [11.16 d3dNoLock](#d3dNoLock)
  - [11.17 d3dNoMultiCB](#d3dNoMultiCB)
  - [11.18 debugCallExtension](#debugCallExtension)
  - [11.19 command](#command)
  - [11.20 language](#language)
  - [11.21 preprocDefine](#preprocDefine)
  - [11.22 dumpAddonDependencyGraph](#dumpAddonDependencyGraph)
  - [11.23 networkDiagInterval](#networkDiagInterval)

Startup parameters are command line interface(CLI) arguments that go after the executable's name in order to set options; e.g arma3\_x64.exe -window to start the game in [windowed](#window) mode.

ⓘ

- Startup parameters' *names* are case-insensitive - e.g -WINDOW and -window are identical.
- Spaces in parameter values must be wrapped in quotations. e.g. "-profiles=E:\Arma 3\Profiles" or -profiles="E:\Arma 3\Profiles"
- Most of the following parameters can be set with the [Arma 3: Launcher](/wiki/Arma_3:_Launcher "Arma 3: Launcher").

- [Steam](#Steam)
- [Windows](#Windows)
- [Path Definition](#Path_Definition)
- [Display Options](#Display_Options)
- [Game Loading Speedup](#Game_Loading_Speedup)
- [Profile Options](#Profile_Options)
- [Misc.](#Misc.)
- [Client Network Options](#Client_Network_Options)
- [Server Options](#Server_Options)
- [Performance](#Performance)
- [Developer Options](#Developer_Options)

## Steam[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=1 "Edit section: Steam")]

1. Choose the game in Steam\Library
2. Right click on the game
3. Select properties
4. Hit "Set launch options"

Example
:   -nosplash "-mod=test;x\test;c:\arma 3\test2"

## Windows[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=2 "Edit section: Windows")]

When working with shortcuts, cmd launch options: there is max characters limit for the executing line, use **-par** (see: [Startup Parameters Config File](/wiki/Startup_Parameters_Config_File "Startup Parameters Config File")) to avoid it.

| Startup Command Line | parameters file |
| --- | --- |
| ``` arma3_x64.exe "-par=D:\Arma 3\Startup Parameters.txt" ``` | ``` -skipIntro -noSplash -enableHT -hugePages -noLogs ``` |

## Path Definition[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=3 "Edit section: Path Definition")]

There are a few basics to keep in mind when defining path as part of a parameter.

### Spaces[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=4 "Edit section: Spaces")]

The path **and** -profiles need to be enclosed with quotes if it contains spaces, e.g "-profiles=E:\Arma 3\Profiles" or -profiles="E:\Arma 3\Profiles".

### Relative vs Absolute[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=5 "Edit section: Relative vs Absolute")]

You can define most, if not all path in both ways.

Relative
:   -profiles=Profiles

Absolute
:   "-profiles=E:\Arma 3\Profiles"

The relative path is normally based on the game main folder, where the exe resides in. However when you use -profiles, some commands use this path defined there as base.

## Display Options[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=6 "Edit section: Display Options")]

### window[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=7 "Edit section: window")]

Displays Arma 3 windowed instead of full screen. Screen resolution / window size are set in [arma3.cfg](/wiki/arma3.cfg "arma3.cfg").

Example

```
arma3_x64.exe -window
```

[![Arma 3 logo black.png](/wikidata/images/thumb/8/80/Arma_3_logo_black.png/30px-Arma_3_logo_black.png)](/wiki/Category:Introduced_with_Arma_3_version_2.22 "Category:Introduced with Arma 3 version 2.22") [2.22](/wiki/Category:Introduced_with_Arma_3_version_2.22 "Category:Introduced with Arma 3 version 2.22")

### noWindowBorder

Removes the default window border in windowed mode.

Example

```
arma3_x64.exe -window -noWindowBorder
```

### posX[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=8 "Edit section: posX")]

Sets the default X Position of the Window.

Example

```
arma3_x64.exe -posX=50
```

### posY[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=9 "Edit section: posY")]

Sets the default Y Position of the Window.

Example

```
arma3_x64.exe -posY=50
```

### adapter[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=10 "Edit section: adapter")]

Sets the default Video Adapter.

Example

```
arma3_x64.exe -adapter=MISSING_AN_EXAMPLE
```

## Game Loading Speedup[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=11 "Edit section: Game Loading Speedup")]

### noSplash[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=12 "Edit section: noSplash")]

Tells the engine to bypass the splash screens on startup of Arma 3.

ⓘ

Real speed-up gained with this is likely to be negligible in Arma 3, as the loading screens are handled in parallel with the game data being loaded, and the loading itself takes quite long thanks to the amount of data needed.

Example

```
arma3_x64.exe -noSplash
```

### skipIntro[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=13 "Edit section: skipIntro")]

Disables world intros in the main menu permanently.

ⓘ

Real speed-up gained with this is likely to be negligible in Arma 3, as the loading screens are handled in parallel with the game data being loaded, and the loading itself takes quite long thanks to the amount of data needed.

Example

```
arma3_x64.exe -skipIntro
```

### world[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=14 "Edit section: world")]

Select a world loaded by default. For faster game loading (no default world loaded and world intro in the main menu, *only at game start*, disabled): -world=empty.

Example

For faster game loading (no default world loaded and world intro in the main menu, *only at game start*, disabled)

```
arma3_x64.exe -world=empty
```

Load Altis

```
arma3_x64.exe -world=Altis
```

### worldCfg[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=15 "Edit section: worldCfg")]

Inits a landscape by the given world config.

## Profile Options[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=16 "Edit section: Profile Options")]

### name[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=17 "Edit section: name")]

Sets the profile name.

Example

```
arma3_x64.exe -name=PlayerOne
```

### profiles[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=18 "Edit section: profiles")]

Location of user-profile folder.

If a custom path is set, default files and folders (logFiles, AnimDataCache, DataCache, SteamPreviewCache....) located by default in %LocalAppData%/Arma 3 will be created in that new location.

However, old files will stay in the old location and will not be copied.

Example 1

```
arma3_x64.exe -profiles=C:\arma3\Profiles
```

Example 2

```
arma3_x64.exe -profiles=Profiles
```

Example 2 create a folder called *Profiles* in the Arma 3 root directory

⚠

The Windows user account needs write access in the chosen location.

### unit[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=19 "Edit section: unit")]

Parameter passes a unit's ID number to the binary, translates to *https://units.arma3.com/my-units#?unit=<number>*

Example

```
arma3_x64.exe -unit=1337
```

## Misc.[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=20 "Edit section: Misc.")]

### buldozer[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=21 "Edit section: buldozer")]

Starts [Buldozer](/wiki/Buldozer "Buldozer") mode.

Example

```
arma3_x64.exe -buldozer
```

### noLand[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=22 "Edit section: noLand")]

Starts with no world loaded. (Used for [Buldozer](/wiki/Buldozer "Buldozer"))

Example

```
arma3_x64.exe -noLand
```

### noSound[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=23 "Edit section: noSound")]

Disables sound output.

Example

```
arma3_x64.exe -noSound
```

### doNothing[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=24 "Edit section: doNothing")]

Engine closes immediately after detecting this option.

Example

```
arma3_x64.exe -doNothing
```

### mod[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=25 "Edit section: mod")]

Loads the specified [mod folders](/wiki/Arma:_Mod_Folders "Arma: Mod Folders"). Multiple folder need to be separated by a semicolon.

Example 1

```
arma3_x64.exe -mod=test;x\test;c:\arma3\test2
```

- **"test"** is located in the Arma 3 installation folder ("Arma 3\test") (relative path)
- **"x\test"** is in the installation folder ("Arma 3\x\test") (relative path; subfolder)
- **"c:\arma3\test2"** is in "c:\arma3\test2" (absolute path)

Example 2

On Windows

```
arma3_x64.exe "-mod=test;x\test;c:\arma3\test2"
```

Example 3

On Linux. Note the additional backslash in front of the semicolon

```
arma3_x64.exe "-mod=test\;x\test\;c:\arma3\test2"
```

ⓘ

"Relative path" starts from, or is rooted at, the directory from where the Arma 3 executable has been started and is not always the same where the executable is, like in betas.

Usually this is the same as the installation path and the same as what is written in Windows registry, but if you copy or symlink the necessary file and folders, you may have different roots.

It is useful when having multiple dedicated servers.

[![Arma 3 logo black.png](/wikidata/images/thumb/8/80/Arma_3_logo_black.png/30px-Arma_3_logo_black.png)](/wiki/Category:Introduced_with_Arma_3_version_2.22 "Category:Introduced with Arma 3 version 2.22") [2.22](/wiki/Category:Introduced_with_Arma_3_version_2.22 "Category:Introduced with Arma 3 version 2.22")

### mpmissions

Sets alternative MPMissions directory for the server. The directory must be relative to 'Arma3' directory

ⓘ

Use **#mpmissions** command from BattlEye RCon to list available missions on the server. The RCon **missions** command does not work with custom -mpmissions=

Example 1

```
arma3_x64.exe -server -mpmissions=MyMPMissionsGoHere
```

Example 2

```
arma3_x64.exe -server -mpmissions=MPMissions\Experimental
```

Example 3

```
arma3server_x64.exe -mpmissions=MPMissions2
```

## Client Network Options[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=26 "Edit section: Client Network Options")]

### connect[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=27 "Edit section: connect")]

Server IP to connect to.

Example

```
arma3_x64.exe -connect=168.152.15.147
```

### port[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=28 "Edit section: port")]

Server port to connect to.

Example

```
arma3_x64.exe -port=1337
```

### password[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=29 "Edit section: password")]

Server password to connect to.

Example

```
arma3_x64.exe -password=1337abc
```

### host[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=30 "Edit section: host")]

Start a non-dedicated multiplayer host.

Example

```
arma3_x64.exe -host
```

## Server Options[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=31 "Edit section: Server Options")]

### setUpHost[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=32 "Edit section: setUpHost")]

Start the game in the setup server display. Works with the client exe. See [reference image](/wiki/File:a3_startup_parameters_createhost.png "File:a3 startup parameters createhost.png")

Example

```
arma3_x64.exe -setUpHost
```

### server[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=33 "Edit section: server")]

Start a [dedicated server](/wiki/Arma_3:_Dedicated_Server "Arma 3: Dedicated Server"). Not needed for the dedicated server exe.

Example

```
arma3_x64.exe -server
```

### port[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=34 "Edit section: port")]

Port to have dedicated server listen on.

Example

```
arma3server_x64.exe -port=1337
```

### pid[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=35 "Edit section: pid")]

File to write the server's PID (process ID) to.

- The file is removed automatically when the exe is stopped
- Only works for dedicated servers

Example

```
arma3server_x64.exe "-pid=C:\My Server\PID.txt"
```

### ranking[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=36 "Edit section: ranking")]

Allowing you to output ranking information or otherwise know as player stats to a file.

The windows user account would need permissions to write to the folder of choice.

Output sample:

```
class Player1
{
	name = "PlayerName";
	killsInfantry = 71;
	killsSoft = 3;
	killsArmor = 5;
	killsAir = 5;
	killsPlayers = 0;
	customScore = 0;
	killsTotal = 84;
	killed = 3;
};
```

Example

```
arma3server_x64.exe "-ranking=C:\arma3\ranking.log"
```

### netlog[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=37 "Edit section: netlog")]

Enables multiplayer network traffic logging. For more details see [server configuration](/wiki/ArmA:_Server_configuration "ArmA: Server configuration").

Example

```
arma3server_x64.exe -netlog
```

### cfg[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=38 "Edit section: cfg")]

Selects the [Server Basic Config file](/wiki/basic.cfg "basic.cfg"). Config file for server specific settings like network performance tuning.

⚠

Using this parameter overrides the game config (Documents\Arma 3\Arma3.cfg); use at your own risk on clients.

Example

```
arma3server_x64.exe "-config=C:\My Server\basic.cfg"
```

### config[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=39 "Edit section: config")]

Selects the [Server Config File](/wiki/Arma_3:_Server_Config_File "Arma 3: Server Config File"). Config file for server specific settings like admin password and mission selection.

Example

```
arma3server_x64.exe "-config=C:\My Server\config.cfg"
```

### profiles[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=40 "Edit section: profiles")]

Path to the folder containing server profile.

- By default, server logs are written to server profile folder
- If folder doesn't exist, it will be automatically created
- Does not work on Linux, no profile will be created or used - instead, remove this parameter and the profile will be created in ~/.local/share/Arma 3 - Other Profiles

Example

```
arma3server_x64.exe "-profiles=C:\My Server\profiles"
```

### bePath[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=41 "Edit section: bePath")]

By default BattlEye will create *BattlEye* folder inside server profile folder. With -bePath param it is possible to specify a custom folder.

Example

```
arma3_x64.exe -bePath=C:\MyBattlEyeFolder
```

### ip[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=42 "Edit section: ip")]

Command to enable support for Multihome servers. Allows server process to use defined available IP address. (b1.57.76934)

Example

```
arma3_x64.exe -ip=145.412.123.12
```

### par[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=43 "Edit section: par")]

Command to read startup parameters from a file. For more details see [Startup parameters config file](/wiki/Startup_Parameters_Config_File "Startup Parameters Config File").

Example

```
arma3_x64.exe "-par=C:\Users\Player\Games\Arma 3\Arma 3 Parameter Files\par_common.txt"
```

### client[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=44 "Edit section: client")]

Launch as client (console). Useful for [headless clients](/wiki/Arma_3:_Headless_Client "Arma 3: Headless Client").

Example

```
arma3_x64.exe -client
```

### loadMissionToMemory[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=45 "Edit section: loadMissionToMemory")]

Server will load mission into memory on first client downloading it. Then it keeps it pre-processed pre-cached in memory for next clients, saving some server CPU cycles.

Example

```
arma3server_x64.exe -loadMissionToMemory
```

### autoInit[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=46 "Edit section: autoInit")]

Automatically initialize mission just like the first client does.

The server config file (server.cfg) must contain `Persistent = 1;` otherwise the autoInit parameter is skipped.

⚠

This will break the [Arma 3: Mission Parameters](/wiki/Arma_3:_Mission_Parameters "Arma 3: Mission Parameters") function, so do not use it when you work with mission parameters, only default values are returned!

Example

```
arma3server_x64.exe -autoInit
```

### serverMod[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=47 "Edit section: serverMod")]

Loads the specified sub-folders for different server-side (not broadcasted to clients) mods.

- Separated by semi-colons
- Absolute path and multiple stacked folders are possible
- In Linux multiple folders arguments need the following separation syntax: -serverMod=mod1\;mod2\;mod3

Example

```
arma3server_x64.exe "-serverMod=myAwesomeMod;anotherAwesomeMod"
```

[![Arma 3 logo black.png](/wikidata/images/thumb/8/80/Arma_3_logo_black.png/30px-Arma_3_logo_black.png)](/wiki/Category:Introduced_with_Arma_3_version_2.22 "Category:Introduced with Arma 3 version 2.22") [2.22](/wiki/Category:Introduced_with_Arma_3_version_2.22 "Category:Introduced with Arma 3 version 2.22")

### keysFolder

Adds folders out of which .bikey files are loaded.

- Separated by semi-colons
- Absolute path and multiple stacked folders are possible
- In Linux multiple folders arguments need the following separation syntax: -keysFolder=@modfolder\keys\;@othermodfolder\keys

Example

```
arma3server_x64.exe "-keysFolder=@modfolder/keys;@othermodfolder/keys;"
```

Note that by default, the base game "keys" folder is included, but it can be excluded by having !keys in the list

```
-keysFolder=!keys;@modfolder/keys;@othermodfolder/keys;
```

### disableServerThread[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=48 "Edit section: disableServerThread")]

Option to disable the server send messaging thread in case of random crashing (may also decrease performance of server on multicore CPUs)

Example

```
arma3_x64.exe -disableServerThread
```

### bandwidthAlg[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=49 "Edit section: bandwidthAlg")]

Set to 2 to use a new experimental networking algorithm that might be better than the default one.

Example

```
arma3_x64.exe -bandwidthAlg=2
```

## Performance[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=50 "Edit section: Performance")]

[![Arma 3 logo black.png](/wikidata/images/thumb/8/80/Arma_3_logo_black.png/30px-Arma_3_logo_black.png)](/wiki/Category:Introduced_with_Arma_3_version_2.00 "Category:Introduced with Arma 3 version 2.00") [2.00](/wiki/Category:Introduced_with_Arma_3_version_2.00 "Category:Introduced with Arma 3 version 2.00")

### limitFPS

Adjusts the FPS limit for the dedicated server/headless client to the specified value. The current default is 50. The available range goes from 5 to 1000.

ⓘ

The limit does not do anything when it is not reached.  

Higher FPS for a DS/HC network messages sent out more frequently, entities (units/vehicles/projectiles/etc) are simulated more often, as well as AI behavior and vision calculations are done more often.

The practical impact of a higher fps limit for the DS/HC may or may not noticeable.

Since [![Arma 3 logo black.png](/wikidata/images/thumb/8/80/Arma_3_logo_black.png/30px-Arma_3_logo_black.png)](/wiki/Category:Introduced_with_Arma_3_version_2.20 "Category:Introduced with Arma 3 version 2.20") [2.20](/wiki/Category:Introduced_with_Arma_3_version_2.20 "Category:Introduced with Arma 3 version 2.20"):

- servers with no players now limit their FPS to 30 (regardless of the -limitFPS parameter setting)
- this setting also works on clients.

ⓘ

Aside from more frequent simulation of the different engine parts, for a client more frequent rendering may lead to more fluent graphics/a more smooth visual experience and potentially more precise audio.  
The FPS gets automatically limited in the main menu with -world=empty or when windowed mode is unfocused ("tabbed out").

### maxMem[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=51 "Edit section: maxMem")]

Overrides memory allocation limit to a certain amount (in megabytes).

⚠

Until [![Arma 3 logo black.png](/wikidata/images/thumb/8/80/Arma_3_logo_black.png/30px-Arma_3_logo_black.png)](/wiki/Category:Introduced_with_Arma_3_version_2.14 "Category:Introduced with Arma 3 version 2.14") [2.14](/wiki/Category:Introduced_with_Arma_3_version_2.14 "Category:Introduced with Arma 3 version 2.14") there was a bug on the Linux server binary where it parses the maxMem value as a signed integer, therefore making 4096 interpreted as a negative value;
in order to avoid this issue, use values like 2047, 4095, 8191, 16383, 32767 (2048 -1, 4096 -1 etc).

1024 MiB is a hard-coded minimum (anything lower falls back to 1024). The maximum is influenced by your operating system (any value over the maximum will be reverted to this value):

- 32-bit Windows + 32-bit game: 2047
- 64-bit Windows + 32-bit game: 3071
- 64-bit Windows + 64-bit game: (physical memory \* 4) / 5

**Without the -maxMem parameter the engine attempts to set this parameter internaly to a reasonable value often defaulting to max values as described above.** The file cache is always excluded from the virtual address limit, see our developers blog: <https://www.bistudio.com/blog/breaking-the-32-bit-barrier>.

ⓘ

Note that setting maxMem to 2000 does not mean that the game will never allocate more then 2000 MiB. It says that the game will do everything in its power to not cross this limit. In general, it makes sense not using this parameter at all and only resort to it in case you experience issues with memory.

Example

```
arma3_x64.exe -maxMem=4096
```

### maxVRAM[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=52 "Edit section: maxVRAM")]

Defines video memory allocation limit to number (in megabytes).

- Minimum value is 128 MiB (anything lower falls back to 128)
- The value is ignored (under DX11) if engine properly detected VRAM size, minus 20% reserve with ceiling limit 300MB max
- Use to resolve e.g. Windows problem: <http://support.microsoft.com/kb/2026022/en-us?p=1>

Example

```
arma3_x64.exe -maxVRAM=4096
```

### maxFileCacheSize[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=53 "Edit section: maxFileCacheSize")]

[![Arma 3 logo black.png](/wikidata/images/thumb/8/80/Arma_3_logo_black.png/30px-Arma_3_logo_black.png)](/wiki/Category:Introduced_with_Arma_3_version_2.14 "Category:Introduced with Arma 3 version 2.14") [2.14](/wiki/Category:Introduced_with_Arma_3_version_2.14 "Category:Introduced with Arma 3 version 2.14") Sets the default filecache size (when files are loaded from disk, they are cached in RAM. If the cache is full, the oldest file is thrown out).

ⓘ

- Default size was either maxMemory - 448 MB or if you had more than 2 GB of memory available to Arma 3, it would be limited to max 2048 MB.
- Setting lower than 1 GB is not recommended.
- Minimum possible value is 512 MB

Example

```
arma3_x64.exe -maxFileCacheSize=2048
```

### noCB[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=54 "Edit section: noCB")]

Turns off multicore use. It slows down rendering but may resolve visual glitches.

Example

```
arma3_x64.exe -noCB
```

### cpuCount[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=55 "Edit section: cpuCount")]

Change to a number less or equal than numbers of available cores. This will override auto detection (which equate to native cores).

The best way to simulate dual core on quad core is to use -cpuCount=2 when you run the game and then change the affinity to 2 cores to make sure additional cores can never be used when some over-scheduling happens.
It might also be possible to set the affinity in the OS before the process is launched.

[![Arma 3 logo black.png](/wikidata/images/thumb/8/80/Arma_3_logo_black.png/30px-Arma_3_logo_black.png)](/wiki/Category:Introduced_with_Arma_3_version_2.20 "Category:Introduced with Arma 3 version 2.20") [2.20](/wiki/Category:Introduced_with_Arma_3_version_2.20 "Category:Introduced with Arma 3 version 2.20")

The minimum value is 2.

Example

```
arma3_x64.exe -cpuCount=8
```

[![Arma 3 logo black.png](/wikidata/images/thumb/8/80/Arma_3_logo_black.png/30px-Arma_3_logo_black.png)](/wiki/Category:Introduced_with_Arma_3_version_2.22 "Category:Introduced with Arma 3 version 2.22") [2.22](/wiki/Category:Introduced_with_Arma_3_version_2.22 "Category:Introduced with Arma 3 version 2.22")

### cpuAffinity

Set the game's CPU affinity mask ([Microsoft documentation](https://learn.microsoft.com/en-us/windows/win32/api/winbase/nf-winbase-setprocessaffinitymask)).

ⓘ

You can use the [affinity calculator](https://bitsum.com/tools/cpu-affinity-calculator/) to calculate the correct mask value.To see if the value was applied correctly, check the [rpt file](/wiki/Crash_Files "Crash Files").

Example

```
arma3_x64.exe -cpuAffinity=0xFF
```

[![Arma 3 logo black.png](/wikidata/images/thumb/8/80/Arma_3_logo_black.png/30px-Arma_3_logo_black.png)](/wiki/Category:Introduced_with_Arma_3_version_2.22 "Category:Introduced with Arma 3 version 2.22") [2.22](/wiki/Category:Introduced_with_Arma_3_version_2.22 "Category:Introduced with Arma 3 version 2.22")

### cpuMainThreadAffinity

Set the main thread's CPU affinity mask ([Microsoft documentation](https://learn.microsoft.com/en-us/windows/win32/api/winbase/nf-winbase-setprocessaffinitymask)).

ⓘ

If the value could **not** be applied a message will be logged to the [rpt file](/wiki/Crash_Files "Crash Files").

Example

```
arma3_x64.exe -cpuMainThreadAffinity=0x01
```

### enableHT[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=56 "Edit section: enableHT")]

Enables the use of all logical CPU cores for parallel tasks processing. If the CPU does not support Hyper-Threading or similar technology, this parameter is ignored.
When disabled, only physical cores are used.

ⓘ

Note that enabling this parameter may slightly improve or harm the performance depending on a scenario.
This parameter is overridden if -cpuCount or -cpuAffinity are used so if you want to use the maximum number of CPU cores use "-enableHT" without "-cpuCount".

Example

```
arma3_x64.exe -enableHT
```

### exThreads[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=57 "Edit section: exThreads")]

Change to a number 0,1,3,5,7. This will override auto detection (which use 3 for dualcore and 7 for quadcore).

All file operations go through a dedicated thread. This offloads some processing from the main thread, however it adds some overhead at the same time.

The reason why threaded file ops were implemented was to serve as a basement for other threads ops. When multiple threads are running at the same time, OS is scheduling them on different cores.
Geometry and Texture loading (both done by the same thread) are scheduled on different cores outside the main rendering loop at the same time with the main rendering loop.

Ex(tra)threads table

| Parameter | Description | | |
| --- | --- | --- | --- |
| Number | Geometry loading | Texture loading | File operations |
| 0 | Unchecked | Unchecked | Unchecked |
| 1 | Unchecked | Unchecked | Checked |
| 3 | Unchecked | Checked | Checked |
| 5 | Checked | Unchecked | Checked |
| 7 | Checked | Checked | Checked |

Example

```
arma3_x64.exe -exThreads=7
```

### malloc[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=58 "Edit section: malloc")]

Sets the particular memory allocator to be used. Significantly affects both performance and stability of the game. [More details](/wiki/Arma_3:_Custom_Memory_Allocator "Arma 3: Custom Memory Allocator").

Example

```
arma3_x64.exe -malloc=someMalloc
```

### hugePages[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=59 "Edit section: hugePages")]

Enables hugepages with the default memory allocator ([malloc](/wiki/Arma_3:_Custom_Memory_Allocator "Arma 3: Custom Memory Allocator")) for both client and server.

Example

```
arma3_x64.exe -hugePages
```

[![Arma 3 logo black.png](/wikidata/images/thumb/8/80/Arma_3_logo_black.png/30px-Arma_3_logo_black.png)](/wiki/Category:Introduced_with_Arma_3_version_2.10 "Category:Introduced with Arma 3 version 2.10") [2.10](/wiki/Category:Introduced_with_Arma_3_version_2.10 "Category:Introduced with Arma 3 version 2.10")

### setThreadCharacteristics

Registers the game's executable as "Game" in Windows for performance improvements.

⚠

This flag can freeze the whole Operating System if running Windows Server!

Example

```
arma3_x64.exe -setThreadCharacteristics
```

## Developer Options[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=60 "Edit section: Developer Options")]

### noPause[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=61 "Edit section: noPause")]

Allow the game running even when its window does not have focus (i.e. running in the background)

Example

```
arma3_x64.exe -noPause
```

[![Arma 3 logo black.png](/wikidata/images/thumb/8/80/Arma_3_logo_black.png/30px-Arma_3_logo_black.png)](/wiki/Category:Introduced_with_Arma_3_version_2.10 "Category:Introduced with Arma 3 version 2.10") [2.10](/wiki/Category:Introduced_with_Arma_3_version_2.10 "Category:Introduced with Arma 3 version 2.10")

### noPauseAudio

Keeps audio running in background while tabbed out. Should be used together with **-noPause** to work correctly

Example

```
arma3_x64.exe -noPauseAudio
```

### showScriptErrors[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=62 "Edit section: showScriptErrors")]

Introduced to show errors in scripts on-screen. In [Eden Editor](/wiki/Category:Eden_Editor "Category:Eden Editor"), script errors are always shown, even when this parameter is not used.

Example

```
arma3_x64.exe -showScriptErrors
```

### debug[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=63 "Edit section: debug")]

Enables more verbose error logging. See [Arma 3: Debug Mode](/wiki/Arma_3:_Debug_Mode "Arma 3: Debug Mode")

Example

```
arma3_x64.exe -debug
```

### noFreezeCheck[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=64 "Edit section: noFreezeCheck")]

Disables the freeze check. It creates otherwise max 4 dumps per game run in total - 2 per distinct freeze. Similar to [Crash Files](/wiki/Crash_Files "Crash Files").

Example

```
arma3_x64.exe -noFreezeCheck
```

### noLogs[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=65 "Edit section: noLogs")]

Be aware this means none errors saved to RPT file (report log). Yet in case of crash the fault address block info is saved.

More details [here](http://forums.bistudio.com/showthread.php?159155-quot-nologs-quot-may-improve-performance-no-stuttering-read-details).

Example

```
arma3_x64.exe -noLogs
```

### noFilePatching[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=66 "Edit section: noFilePatching")]

Ensures that only PBOs are loaded and NO unpacked data.

ⓘ

Since [![Arma 3 logo black.png](/wikidata/images/thumb/8/80/Arma_3_logo_black.png/30px-Arma_3_logo_black.png)](/wiki/Category:Introduced_with_Arma_3_version_1.50 "Category:Introduced with Arma 3 version 1.50") [1.50](/wiki/Category:Introduced_with_Arma_3_version_1.50 "Category:Introduced with Arma 3 version 1.50") this was replaced in favor of the -filePatching parameter.

Example

```
arma3_x64.exe -noFilePatching
```

[![Arma 3 logo black.png](/wikidata/images/thumb/8/80/Arma_3_logo_black.png/30px-Arma_3_logo_black.png)](/wiki/Category:Introduced_with_Arma_3_version_1.50 "Category:Introduced with Arma 3 version 1.50") [1.50](/wiki/Category:Introduced_with_Arma_3_version_1.50 "Category:Introduced with Arma 3 version 1.50")

### filePatching

Allows the game to load unpacked data. For more info see [CMA:DevelopmentSetup](/wiki/CMA:DevelopmentSetup "CMA:DevelopmentSetup")

Example

```
arma3_x64.exe -filePatching
```

### init[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=67 "Edit section: init")]

Run scripting command once in the main menu. For example to start a certain SP mission of choice automatically. See also [playMission](/wiki/playMission "playMission"). The Mission has to reside in the "arma3\Missions" folder, NOT the user directory.

Example

```
arma3_x64.exe -init=playMission["","Test.VR"]
```

### <path>\mission.sqm[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=68 "Edit section: <path>\mission.sqm")]

Load a mission directly in the editor. Example: "C:\arma3\users\myUser\missions\myMission.intro\mission.sqm"

### autotest[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=69 "Edit section: autotest")]

Loads automatically a series of defined missions and on error writes to a log file.

The parameter can be used to automatically run a series of test missions. For example FPS measurement or scripting validation.

- The game runs in special mode. It runs all missions from the given list.
- If any mission fails (ends with other than END1), it is logged into the [rpt file](/wiki/arma.RPT "arma.RPT") (search: <autotest).
- In case of any fail, the game also returns an errorlevel to DOS. This can be used to issue an notification by a secondary application.

Launch Arma 3 with
:   "-autotest=c:\Arma 3\autotest\autotest.cfg"

The autotest.cfg looks like:

```
class TestMissions
{
	class TestCase01
	{
		campaign = "";
		mission = "autotest\TestCase01.VR"; // relative path to the arma directory
	};
	class TestCase02
	{
		campaign = "";
		mission = "C:\arma3\autotest\TestCase02.VR"; // absolute path
	};
};
```

⚠

If -profiles is used, the relative path is relative to the specified profile path.

Example

```
arma3_x64.exe -autotest=c:\arma3\autotest\autotest.cfg
```

RPT entry

```
<AutoTest result="FAILED">
	EndMode = LOSER
	Mission = autotest\TestCase01.VR
</AutoTest>
```

If possible use simple worlds, like VR, to keep the loading times short. The [loading screen](/wiki/startLoadingScreen "startLoadingScreen") command might be useful as well to speed up task that need no rendering.

### beta[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=70 "Edit section: beta")]

Loads the specified beta sub-folders. This allows use of beta build w/o disabling in-game mod/extension management (in UI menu).

- Separated by semicolons
- Absolute path and multiple stacked folders are possible
- **In Linux** multiple folders arguments need the following separation syntax: arma3\_x64.exe "-mod=betamod1\;betamod2\;betamod3"

[![Arma 3 logo black.png](/wikidata/images/thumb/8/80/Arma_3_logo_black.png/30px-Arma_3_logo_black.png)](/wiki/Category:Introduced_with_Arma_3_version_2.20 "Category:Introduced with Arma 3 version 2.20") [2.20](/wiki/Category:Introduced_with_Arma_3_version_2.20 "Category:Introduced with Arma 3 version 2.20")

### cfgDependenciesDebugPrint

Prints all addons in load order in the [RPT](/wiki/arma.RPT "arma.RPT").

Format is 'addonName' (config file path) - 'dependency1', 'dependency2', ... or 'addonName','addonName2','addonName3' (...) if multiple CfgPatches in same config.

### checkSignatures[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=71 "Edit section: checkSignatures")]

Provide a thorough test of all signatures of all loaded banks (PBOs) at the start of the game. Only the stored sha1 values are verified with signatures/keys. Output is in .rpt file.

Example

```
arma3_x64.exe -checkSignatures
```

### checkSignaturesFull[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=72 "Edit section: checkSignaturesFull")]

Same as above, but checks every byte of the file content, and therefore not only verifies signatures, but also verifies file integrity.

Example

```
arma3_x64.exe -checkSignaturesFull
```

### d3dNoLock[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=73 "Edit section: d3dNoLock")]

Doesn't lock the VRAM.

Example

```
arma3_x64.exe -d3dNoLock
```

### d3dNoMultiCB[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=74 "Edit section: d3dNoMultiCB")]

D3D uses Single Constant Buffers instead of Multiple Constant Buffers.

Example

```
arma3_x64.exe -d3dNoMultiCB
```

### debugCallExtension[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=75 "Edit section: debugCallExtension")]

Logs extension calls in the rpt log like this: [![arma3-startup parameter debugCallExtension output.png](/wikidata/images/1/1d/arma3-startup_parameter_debugCallExtension_output.png)](/wiki/File:arma3-startup_parameter_debugCallExtension_output.png)

Example

```
arma3_x64.exe -debugCallExtension
```

### command[[edit source](/wiki?title=Arma_3:_Startup_Parameters&action=edit&section=76 "Edit section: command")]

Creates [named pipe](https://en.wikipedia.org/wiki/Named_pipe) "\\.\pipe\name" for receiving predefined set of commands. See [Arma 3: Named Pipe](/wiki/Arma_3:_Named_Pipe "Arma 3: Named Pipe").
Note that it works only on clients, but not on [dedicated server](/wiki/Arma_3:_Dedicated_Server "Arma 3: Dedicated Server").

Example

```
arma3_x64.exe -command=MyFancyPipeName
```

[![Arma 3 logo black.png](/wikidata/images/thumb/8/80/Arma_3_logo_black.png/30px-Arma_3_logo_black.png)](/wiki/Category:Introduced_with_Arma_3_version_2.04 "Category:Introduced with Arma 3 version 2.04") [2.04](/wiki/Category:Introduced_with_Arma_3_version_2.04 "Category:Introduced with Arma 3 version 2.04")

### language

Starts client with preferred language. See [Stringtable.xml - Supported Languages](/wiki/Stringtable.xml#Supported_Languages "Stringtable.xml") for a list of supported languages. **Case-sensitive!** (className column)

ⓘ

[![Arma 3 logo black.png](/wikidata/images/thumb/8/80/Arma_3_logo_black.png/30px-Arma_3_logo_black.png)](/wiki/Category:Introduced_with_Arma_3_version_2.18 "Category:Introduced with Arma 3 version 2.18") [2.18](/wiki/Category:Introduced_with_Arma_3_version_2.18 "Category:Introduced with Arma 3 version 2.18") made the parameter work properly.

Example

```
arma3_x64.exe -language=German
```

[![Arma 3 logo black.png](/wikidata/images/thumb/8/80/Arma_3_logo_black.png/30px-Arma_3_logo_black.png)](/wiki/Category:Introduced_with_Arma_3_version_2.06 "Category:Introduced with Arma 3 version 2.06") [2.06](/wiki/Category:Introduced_with_Arma_3_version_2.06 "Category:Introduced with Arma 3 version 2.06")

### preprocDefine

Defines a macro and optionally its value. Multiple macro can be defined by using this parameter multiple times.

Example 1

```
arma3_x64.exe -preprocDefine="CMD__MACRONAME=MACROVALUE"
arma3_x64.exe -preprocDefine=CMD__MACRONAME
arma3_x64.exe -preprocDefine=CMD__MACRO1 -preprocDefine=CMD__MACRO2
```

Example 2

arma3\_x64.exe -preprocDefine=MACRO

The macro name **will** start with CMD\_\_, the engine automatically adds the prefix if it is not present (CMD\_\_MACRO).

[![Arma 3 logo black.png](/wikidata/images/thumb/8/80/Arma_3_logo_black.png/30px-Arma_3_logo_black.png)](/wiki/Category:Introduced_with_Arma_3_version_2.14 "Category:Introduced with Arma 3 version 2.14") [2.14](/wiki/Category:Introduced_with_Arma_3_version_2.14 "Category:Introduced with Arma 3 version 2.14")

### dumpAddonDependencyGraph

dumps [Graphviz](https://en.wikipedia.org/wiki/Graphviz) text file into the RPT directory with a graph of all addon dependencies (requiredAddons)

Example

```
arma3_x64.exe -dumpAddonDependencyGraph
```

[![Arma 3 logo black.png](/wikidata/images/thumb/8/80/Arma_3_logo_black.png/30px-Arma_3_logo_black.png)](/wiki/Category:Introduced_with_Arma_3_version_1.30 "Category:Introduced with Arma 3 version 1.30") [1.30](/wiki?title=Category:Introduced_with_Arma_3_version_1.30&action=edit&redlink=1 "Category:Introduced with Arma 3 version 1.30 (page does not exist)") [![Arma 3 logo black.png](/wikidata/images/thumb/8/80/Arma_3_logo_black.png/30px-Arma_3_logo_black.png)](/wiki/Category:Introduced_with_Arma_3_version_prof "Category:Introduced with Arma 3 version prof") [PROF](/wiki?title=Category:Introduced_with_Arma_3_version_prof&action=edit&redlink=1 "Category:Introduced with Arma 3 version prof (page does not exist)")

### networkDiagInterval

Polls the status of bandwidth, traffic and similar data every X seconds. Since [![Arma 3 logo black.png](/wikidata/images/thumb/8/80/Arma_3_logo_black.png/30px-Arma_3_logo_black.png)](/wiki/Category:Introduced_with_Arma_3_version_2.18 "Category:Introduced with Arma 3 version 2.18") [2.18](/wiki/Category:Introduced_with_Arma_3_version_2.18 "Category:Introduced with Arma 3 version 2.18") it also logs size and count of [public variables](/wiki/publicVariable "publicVariable") when using the *Profiling binary*.

Tells you what type of packages are sent to which clients. Network types are obfuscated, so you can just make out who uses how much bandwidth.

Example

```
arma3_x64.exe -networkDiagInterval=5 // seconds between each diagnostics polling
```

ⓘ

Release statement by BI: Willing server admins may help us gather even more data to base future optimizations on. We'd like admins experiencing desync or other netcode issues to try and enable these diagnostics and send us the results on Feedback Tracker. The parameter is disabled by default because it may reduce performance when used.

File content example for the server: Show example

```
===============================================================================================================================================
Server
-----------------------------------------------------------------------------------------------------------------------------------------------
Raw statistics 0
                                             player |     sent msg |   sent msg/s |       sent B |     sent B/s |      rec msg |    rec msg/s |        rec B |      rec B/s |       DS min |       DS max |       DS avg |       BW min |       BW max |       BW avg |
                                      __SERVER__(2) |            0 |         0.00 |            0 |         0.00 |            0 |       152.79 |            0 |     20819.12 |         0.00 |         0.00 |         0.00 | 2147483648.00 | 2147483648.00 | 2147483648.00 |
                                    test(538428231) |            0 |        21.51 |            0 |      9555.18 |            0 |         0.40 |            0 |        19.72 |         0.00 |         0.00 |         0.00 |    329417.00 |    343000.00 |    336140.00 |
                                              Total |            0 |        21.51 |            0 |      9555.18 |            0 |       153.19 |            0 |     20838.84 |         0.00 |         0.00 |         0.00 |
-----------------------------------------------------------------------------------------------------------------------------------------------
 Server - Sent
Type                                     |          msg |     msg/s |            B |          B/s | avg. B
Type_64                                  |          131 |     41.57 |         6876 |      2182.16 |   52.5
Type_468                                 |           13 |      4.13 |         4730 |      1501.11 |  363.8
Type_109                                 |           39 |     12.38 |         4412 |      1400.19 |  113.1
Type_10                                  |           81 |     25.71 |         4324 |      1372.26 |   53.4
Type_96                                  |           39 |     12.38 |         3947 |      1252.62 |  101.2
Type_126                                 |           31 |      9.84 |         2480 |       787.05 |   80.0
Type_53                                  |           26 |      8.25 |         2314 |       734.37 |   89.0
Type_385                                 |           10 |      3.17 |         2233 |       708.66 |  223.3
Type_114                                 |           40 |     12.69 |         2010 |       637.89 |   50.3
Type_98                                  |           35 |     11.11 |         1715 |       544.27 |   49.0
-----------------------------------------------------------------------------------------------------------------------------------------------
 Server - Received
Type                                     |          msg |     msg/s |            B |          B/s | avg. B
Type_18                                  |            3 |      0.20 |          156 |        10.76 |   52.0
Type_5                                   |            8 |      0.20 |           15 |         0.20 |    1.9
Type_468                                 |            0 |      0.00 |            0 |         0.00 |    0.0
Type_467                                 |            0 |      0.00 |            0 |         0.00 |    0.0
Type_466                                 |            0 |      0.00 |            0 |         0.00 |    0.0
Type_449                                 |            0 |      0.00 |            0 |         0.00 |    0.0
Type_427                                 |            0 |      0.00 |            0 |         0.00 |    0.0
Type_404                                 |            0 |      0.00 |            0 |         0.00 |    0.0
Type_385                                 |            0 |      0.00 |            0 |         0.00 |    0.0
Type_383                                 |            0 |      0.00 |            0 |         0.00 |    0.0
-----------------------------------------------------------------------------------------------------------------------------------------------
===============Vars total
        1804 - bis_fnc_storeparamsvalues_data
         554 - CBAs
         357 - paramsArray
          76 - cba_versioning_versions_serv
          76 - cba_versioning_versions_server
          70 - SPE_IFS_availableCalls
          48 - SPE_IFS_DS_ProviderBlackList
          36 - SPE_IFS_CAS_aimErrorSpread
          22 - SPE_IFS_SafetyDistance_CAS
          22 - SPE_IFS_SafetyDistance_HeavyArty
===============Vars recent
        1804 - bis_fnc_storeparamsvalues_data (1)
         554 - CBAs (9)
         357 - paramsArray (1)
          76 - cba_versioning_versions_serv (1)
          76 - cba_versioning_versions_server (1)
          70 - SPE_IFS_availableCalls (1)
          48 - SPE_IFS_DS_ProviderBlackList (1)
          36 - SPE_IFS_CAS_aimErrorSpread (1)
          22 - SPE_IFS_SafetyDistance_CAS (1)
          22 - SPE_IFS_SafetyDistance_HeavyArty (1)
```

[↑ Back to spoiler's top](#bikisp6a1af414c6dd0)

File content example for a client: Show example

```
===============================================================================================================================================
Client
-----------------------------------------------------------------------------------------------------------------------------------------------
Raw statistics 152.563
                                             player |     sent msg |   sent msg/s |       sent B |     sent B/s |      rec msg |    rec msg/s |        rec B |      rec B/s |       DS min |       DS max |       DS avg |       BW min |       BW max |       BW avg |
                                              Total |            0 |         0.00 |            0 |         0.00 |            0 |         3.78 |            0 |       506.77 |         0.00 |         0.00 |         0.00 |
-----------------------------------------------------------------------------------------------------------------------------------------------
 Client - Sent
Type                                     |          msg |     msg/s |            B |          B/s | avg. B
Type_133                                 |        25529 |    212.95 |      2056925 |     17417.13 |   80.6
Type_120                                 |        11046 |     87.65 |       328091 |      2616.33 |   29.7
Type_119                                 |         4058 |     20.32 |       265924 |      1335.86 |   65.5
Type_126                                 |          750 |     11.55 |        61633 |       958.96 |   82.2
Type_132                                 |          882 |      4.18 |       109926 |       523.11 |  124.6
Type_466                                 |         3671 |     29.08 |        58422 |       464.54 |   15.9
Type_467                                 |         1542 |     12.95 |        35189 |       296.02 |   22.8
Type_98                                  |         1571 |      4.58 |        81492 |       238.05 |   51.9
Type_114                                 |          806 |      2.59 |        45686 |       151.99 |   56.7
Type_166                                 |          190 |      1.59 |        12364 |       116.53 |   65.1
-----------------------------------------------------------------------------------------------------------------------------------------------
 Client - Received
Type                                     |          msg |     msg/s |            B |          B/s | avg. B
Type_381                                 |          247 |      1.00 |        17074 |        54.78 |   69.1
Type_383                                 |          244 |      1.00 |        14420 |        46.81 |   59.1
Type_126                                 |           17 |      0.40 |         1360 |        31.87 |   80.0
Type_133                                 |          104 |      0.20 |         7171 |        12.75 |   69.0
Type_98                                  |           37 |      0.20 |         1879 |        10.16 |   50.8
Type_120                                 |           15 |      0.20 |          435 |         5.78 |   29.0
Type_467                                 |           15 |      0.20 |          330 |         4.38 |   22.0
Type_333                                 |            9 |      0.20 |          153 |         3.39 |   17.0
Type_295                                 |            2 |      0.20 |           16 |         1.59 |    8.0
Type_92                                  |           49 |      0.20 |          363 |         1.39 |    7.4
-----------------------------------------------------------------------------------------------------------------------------------------------
===============Vars total
===============Vars recent
```

[↑ Back to spoiler's top](#bikisp6a1af414c7832)