---
game: dayz
slug: dayz-server-config
file: serverDZ.cfg
source: https://community.bistudio.com/wiki/DayZ:Server_Configuration
scraped: 2026-05-31T11:45:20Z
---

- [1 Configuration](#Configuration)
  - [1.1 Main Parameters](#Main_Parameters)
  - [1.2 Additional Parameters](#Additional_Parameters)
- [2 XML Configuration](#XML_Configuration)
  - [2.1 dayzsettings.xml](#dayzsettings.xml)
- [3 BattlEye Configuration](#BattlEye_Configuration)
- [4 Launch Parameters](#Launch_Parameters)
- [5 Priority Queuing](#Priority_Queuing)

This article deals with the configuration file which is used to configure various game server settings.

The name *server.cfg* means nothing, and this file can be called anything.
The real name is determined by the -config command line option when launching the dedicated server.
There is no default name; when non is specified, the server won't launch due to the mandatory settings.

⚠

Is is highly recommended to use the graceful shutdown method to avoid negative side effects on player characters and the server storage saving. More information under [DayZ: Server Messages](https://community.bistudio.com/wiki/DayZ:Server_Messages#Shutdown)

## Configuration[[edit source](/wiki?title=DayZ:Server_Configuration&action=edit&section=1 "Edit section: Configuration")]

### Main Parameters[[edit source](/wiki?title=DayZ:Server_Configuration&action=edit&section=2 "Edit section: Main Parameters")]

```
hostname = "EXAMPLE NAME";			// Server name
description = "Some description";   // Description of the server. Gets displayed to users in the client server browser, max length 255 characters
password = "";						// Password to connect to the server
passwordAdmin = "";					// Password to become a server admin

enableWhitelist = 0;				// Enable/disable whitelist (value 0-1)
disableBanlist = false;				// Disables the usage of ban.txt (default: false)
disablePrioritylist = false;		// Disables usage of priority.txt (default: false)

maxPlayers = 60;					// Maximum amount of players

verifySignatures = 2;				// Verifies .pbos against .bisign files. (only 2 is supported)

forceSameBuild = 1;					// When enabled, the server will allow the connection only to clients with same the .exe revision as the server (value 0-1)

disableVoN = 0;						// Enable/disable voice over network (value 0-1)
vonCodecQuality = 20;				// Voice over network codec quality, the higher the better (values 0-20)

disable3rdPerson = 0;				// Toggles the 3rd person view for players (value 0-1)
disableCrosshair = 0;				// Toggles the cross-hair (value 0-1)

serverTime = "SystemTime";			// Initial in-game time of the server. "SystemTime" means the local time of the machine.
									// Another possibility is to set the time to some value in "YYYY/MM/DD/HH/MM" format, e.g "2015/4/8/17/23".

serverTimeAcceleration = 1;			// Accelerated Time - The numerical value being a multiplier (0.1-64).
									// Thus, in case it is set to 24, time would move 24 times faster than normal. An entire day would pass in one hour.

serverNightTimeAcceleration = 1;	// Accelerated Nigh Time - The numerical value being a multiplier (0.1-64) and also multiplied by serverTimeAcceleration value.
									// Thus, in case it is set to 4 and serverTimeAcceleration is set to 2, night time would move 8 times faster than normal.
									// An entire night would pass in 3 hours.

serverTimePersistent = 0;			// Persistent Time (value 0-1) - The actual server time is saved to storage, so when active, the next server start will use the saved time value.

guaranteedUpdates = 1;				// Communication protocol used with game server (use only number 1)

loginQueueConcurrentPlayers = 5;	// The number of players concurrently processed during the login process.
									// Should prevent massive performance drop during connection when a lot of people are connecting at the same time.

loginQueueMaxPlayers = 500;			// The maximum number of players that can wait in login queue

instanceId = 1;						// DayZ server instance id, to identify the number of instances per box and their storage folders with persistence files

storageAutoFix = 1;					// Checks if the persistence files are corrupted and replaces corrupted ones with empty ones (value 0-1)

class Missions
{
	class DayZ
	{
		template = "dayzOffline.chernarusplus";	// Mission to load on server startup. <MissionName>.<TerrainName>
	};
};
```

### Additional Parameters[[edit source](/wiki?title=DayZ:Server_Configuration&action=edit&section=3 "Edit section: Additional Parameters")]

```
respawnTime = 5;				// Sets the respawn delay (in seconds) before the player is able to get a new character on the server, when the previous one is dead

motd[] = { "line1","line2" };	// Message of the day displayed in the in-game chat
motdInterval = 1;				// Time interval (in seconds) between each message

timeStampFormat = "Short";		// Format for timestamps in the .rpt file (value Full/Short)
logAverageFps = 1;				// Logs the average server FPS (value in seconds), needs to have ''-doLogs'' launch parameter active
logMemory = 1;					// Logs the server memory usage (value in seconds), needs to have the ''-doLogs'' launch parameter active
logPlayers = 1;					// Logs the count of currently connected players (value in seconds), needs to have the ''-doLogs'' launch parameter active
logFile = "server_console.log";	// Saves the server console log to a file in the folder with the other server logs

adminLogPlayerHitsOnly = 0;		// 1 - log player hits only / 0 - log all hits ( animals/infected )
adminLogPlacement = 0;			// 1 - log placement action ( traps, tents )
adminLogBuildActions = 0;		// 1 - log basebuilding actions ( build, dismantle, destroy )
adminLogPlayerList = 0;			// 1 - log periodic player list with position every 5 minutes

disableMultiAccountMitigation = false;	// disables multi account mitigation on consoles when true (default: false)

enableDebugMonitor = 1;			// shows info about the character using a debug window in a corner of the screen (value 0-1)

steamQueryPort = 2305;			// defines Steam query port, should fix the issue with server not being visible in client server browser

allowFilePatching = 1;			// if set to 1 it will enable connection of clients with "-filePatching" launch parameter enabled

simulatedPlayersBatch = 20;		// Set limit of how much players can be simulated per frame (for server performance gain)

multithreadedReplication = 1;	// enables multi-threaded processing of server's replication system
								// number of worker threads is derived by settings of jobsystem in dayzSettings.xml by "maxcores" and "reservedcores" parameters (value 0-1)
speedhackDetection = 1;			// enable speedhack detection, values 1-10 (1 strict, 10 benevolent, can be float)

networkRangeClose = 20;			// network bubble distance for spawn of close objects with items in them (f.i. backpacks), set in meters, default value if not set is 20
networkRangeNear = 150;			// network bubble distance for spawn (despawn +10%) of near inventory items objects, set in meters, default value if not set is 150
networkRangeFar = 1000;			// network bubble distance for spawn (despawn +10%) of far objects (other than inventory items), set in meters, default value if not set is 1000
networkRangeDistantEffect = 4000;		// network bubble distance for spawn of effects (currently only sound effects), set in meters, default value if not set is 4000
networkObjectBatchLogSlow = 5;	//Maximum time a bubble can take to iterate in seconds before it is logged to the console
networkObjectBatchEnforceBandwidthLimits = 1;	//Enables a limiter for object creation based on bandwidth statistics
networkObjectBatchUseEstimatedBandwidth = 0;	//Switch between the method behind finding the bandwidth usage of a connection. If set to 0, it will use the total of the actual data sent since the last server frame, and if set to 1, it will use a crude estimation
networkObjectBatchUseDynamicMaximumBandwidth = 1;	//Determines if the bandwidth limit should be a factor of the maximum bandwidth that can be sent or a hard limit. The maximum bandwidth that can be sent fluctuates depending on demand in the system.
networkObjectBatchBandwidthLimit = 0.8;		//The actual limit, could be a [0,1] value or a [1,inf] value depending on networkObjectBatchUseDynamicMaximumBandwidth. See above
networkObjectBatchCompute = 1000;	//Number of objects in the create/destroy lists that are checked in a single server frame
networkObjectBatchSendCreate = 10;	//Maximum number of objects that can be sent for creation
networkObjectBatchSendDelete = 10;	//Maximum number of objects that can be sent for deletion

defaultVisibility=1375;			// highest terrain render distance on server (if higher than "viewDistance=" in DayZ client profile, clientside parameter applies)
defaultObjectViewDistance=1375;	// highest object render distance on server (if higher than "preferredObjectViewDistance=" in DayZ client profile, clientside parameter applies)

lightingConfig = 0;				// 0 for brighter night, 1 for darker night, 2 for Sakhal-specific lighting - if enableCfgGameplayFile is enabled, this option will be overriden by the WorldsData::lightingConfig value
disablePersonalLight = 1;		// disables personal light for all clients connected to server

disableBaseDamage = 0;			// set to 1 to disable damage/destruction of fence and watchtower
disableContainerDamage = 0;		// set to 1 to disable damage/destruction of tents, barrels, wooden crate and seachest
disableRespawnDialog = 0;		// set to 1 to disable the respawn dialog (new characters will be spawning as random)

pingWarning = 200;				// set to define the ping value from which the initial yellow ping warning is triggered (value in milliseconds)
pingCritical = 250;				// set to define the ping value from which the red ping warning is triggered (value in milliseconds)
MaxPing = 300;					// set to define the ping value from which a player is kicked from the server (value in milliseconds)
serverFpsWarning = 15;			// set to define the server fps value under which the initial server fps warning is triggered (minimum value is 11)

shotValidation = 1;				// 1 enables the validation, 0 disables
clientPort = 2304;              // int value, forces the port the clients connect with
```

## XML Configuration[[edit source](/wiki?title=DayZ:Server_Configuration&action=edit&section=4 "Edit section: XML Configuration")]

### dayzsettings.xml[[edit source](/wiki?title=DayZ:Server_Configuration&action=edit&section=5 "Edit section: dayzsettings.xml")]

```
<jobsystem globalqueue="4096" threadqueue="1024">
	<pc maxcores="4" reservedcores="1" />
	<!--
		maxcores - maximum number of CPU cores which will be used for jobsystem
		reservedcores - number of CPU cores which will be used for other threads

		number of worker threads is then "maxcores - reservedcores", but still at least one worker thread is allocated
	-->
</jobsystem>
```

## BattlEye Configuration[[edit source](/wiki?title=DayZ:Server_Configuration&action=edit&section=6 "Edit section: BattlEye Configuration")]

The config file **BEServer\_x64.cfg** needs to be in the same folder as BEServer\_x64.dll.
The location of this folder can be customized via the startup parameters *-bePath* and *-profiles*.

Supported parameters:

- **RConPassword MyPassword** - Sets the password for the connection of the RCon tool (remote connection admin tool like BEC/Dart)
- **RestrictRCon 1** - Enables/Disables RCon functions (kick/ban/connection restrictions)

## Launch Parameters[[edit source](/wiki?title=DayZ:Server_Configuration&action=edit&section=7 "Edit section: Launch Parameters")]

Run the DayZServer\_x64.exe via the batch file, shortcut with parameters or other options.

Supported parameters:

- **-config=serverDZ.cfg** - Selects the Server Config File
- **-port=2302** - Port to have dedicated server listen on
- **-profiles=%userProfile%\Documents\DayZServer** – Path to the folder containing server profile. By default, server logs are written to the server profile's directory. Logs/dumps/etc will be created there, along with BattlEye/BEC/Rcon related files
- **-mission=** - Defines the mission used by the server
- **-doLogs** - Enables all log messages in the server RPT file
- **-adminLog** - Enables the admin log
- **-netLog** - Enables the network traffic logging
- **-freezeCheck** - Stops the server when frozen for more than 5 min and create a dump file
- **-filePatching** - Ensures that only PBOs are loaded and NO unpacked data.
- **-BEpath=** - Sets a custom path to the [BattlEye](/wiki/BattlEye "BattlEye") files
- **-cpuCount=** - Sets the number of logical CPU cores to use for parallel tasks processing. It should be less or equal than the numbers of available cores.
- **-limitFPS=** - Limits server FPS to specified value (current max is 200) to lower CPU usage of low population servers.
- **-mod=<string>** - Loads the specified sub-folders for different mods. Separated by semi-colons. Absolute path and multiple stacked folders are possible.
- **-serverMod=<string>** - Loads the specified sub-folders for different server-side (not broadcasted to clients) mods. Separated by semi-colons. Absolute path and multiple stacked folders are possible.
- **-storage=** - Defines custom root folder for storage location.

ⓘ

It is possible to define a batch variable in the .bat file and use it in the server exe's startup configuration arguments, e.g:

```
set missionLocation=%userProfile%\Documents\DayZ\Stable\mpmissions\dayzOffline.enoch
...
DayZServer_x64.exe -mission=%missionLocation%
```

## Priority Queuing[[edit source](/wiki?title=DayZ:Server_Configuration&action=edit&section=8 "Edit section: Priority Queuing")]

Specified users can be prioritized in the login queue, they will get on the first position, before the non-prioritized users.

Flagged users simply need to added to a *priority.txt* file, located in the root directory of the server install.

```
SteamId;SteamId;01234567890123456;01234567890123456
```