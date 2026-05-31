---
game: arma3
slug: arma3-server-config
file: server.cfg
source: https://community.bistudio.com/wiki/Arma_3:_Server_Config_File
scraped: 2026-05-31T11:45:19Z
---

- [1 Server Options](#Server_Options)
- [2 Server Administration](#Server_Administration)
  - [2.1 Voted In Admin](#Voted_In_Admin)
  - [2.2 Logged In Admin](#Logged_In_Admin)
- [3 Server Security](#Server_Security)
  - [3.1 Notes](#Notes)
  - [3.2 Safe Folder Structure](#Safe_Folder_Structure)
- [4 Example Configuration File](#Example_Configuration_File)
- [5 Mission Rotation](#Mission_Rotation)
  - [5.1 Parameters](#Parameters)
    - [5.1.1 Template](#Template)
    - [5.1.2 Difficulty](#Difficulty)
    - [5.1.3 class Params](#class_Params)
  - [5.2 Arma 3 Apex - Mission Collection](#Arma_3_Apex_-_Mission_Collection)
  - [5.3 Important Settings](#Important_Settings)
- [6 Additional Details](#Additional_Details)
- [7 Mission Parameters Override](#Mission_Parameters_Override)

| Arma 3 Server Configuration Overview | | | | |
| --- | --- | --- | --- | --- |
| Setup | [Arma 3: Dedicated Server](/wiki/Arma_3:_Dedicated_Server "Arma 3: Dedicated Server") |
| Files | Arma 3: Server Config File ● [Arma 3: Basic Server Config File](/wiki/Arma_3:_Basic_Server_Config_File "Arma 3: Basic Server Config File") ● [Arma 3: Server Profile](/wiki/Arma_3:_Server_Profile "Arma 3: Server Profile") |
| Other | [Multiplayer Server Commands](/wiki/Multiplayer_Server_Commands "Multiplayer Server Commands") ● [Arma 3: Mission voting](/wiki/Arma_3:_Mission_voting "Arma 3: Mission voting") ● [Arma 3: Headless Client](/wiki/Arma_3:_Headless_Client "Arma 3: Headless Client") ● [BattlEye](/wiki/BattlEye "BattlEye") |

[![Arma 2: Operation Arrowhead](/wikidata/images/thumb/5/5b/A2_OA_Logo.png/48px-A2_OA_Logo.png)](/wiki/Category:Arma_2:_Operation_Arrowhead "Arma 2: Operation Arrowhead")

This page is about **Arma 3** server configuration. For previous titles, see [Arma 2: Server Config File](/wiki/Arma_2:_Server_Config_File "Arma 2: Server Config File").

This article deals with the **server.cfg**, a configuration file which one can use to configure various game server settings such as the difficulty level, how many votes are needed, and welcome messages.
The name *server.cfg* means nothing, and this file can be called anything. The real name is determined by the [-config](/wiki/Arma_3:_Startup_Parameters "Arma 3: Startup Parameters") command line option when launching the dedicated server. There is no default name: when no filename is specified, no server configuration file is loaded.

## Server Options[[edit source](/wiki?title=Arma_3:_Server_Config_File&action=edit&section=1 "Edit section: Server Options")]

| Parameter | Default | Description | Since |
| --- | --- | --- | --- |
| `passwordAdmin = "xyzxyz";` | `""` | Password to protect admin access. | N/A |
| `password = "xyz";` | `""` | Password required to connect to server. | N/A |
| `serverCommandPassword = "xyzxyz";` | `""` | Password required by alternate syntax of [serverCommand](/wiki/serverCommand "serverCommand") server-side scripting (Case-Sensitive). | N/A |
| `hostname = "My Server";` | local machine name | Servername visible in the game browser. | N/A |
| `maxPlayers = 10;` | `64` (DS) | The maximum number of players that can connect to server. The final number will be lesser between number given here and number of mission slots. | N/A |
| `motd[] = { "Welcome to my server.", "Hosted in the net." };` | `{}` | Two lines welcome message. Comma is the 'new line' separator. | N/A |
| `motdInterval = 5;` | `5` | Time interval (in seconds) between each MOTD line. | N/A |
| `admins[] = { "<UID>" };` | `{}` | Whitelisted clients can use #login without password. See [Logged In Admin](#Logged_In_Admin). | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_1.70 "Category:Introduced with Arma 3 version 1.70") [1.70](/wiki/Category:Introduced_with_Arma_3_version_1.70 "Category:Introduced with Arma 3 version 1.70") |
| `headlessClients[] = { "<IP>" };` | `{}` | Headless Client IPs. Multiple connections and addresses are allowed in case of multiple Headless Clients. See [Arma 3: Headless Client](/wiki/Arma_3:_Headless_Client "Arma 3: Headless Client"). | N/A |
| `localClient[] = { "<IP>" };` | `{}` | Indicates clients with *unlimited* bandwidth and *nearly no latency*. See [Arma 3: Headless Client](/wiki/Arma_3:_Headless_Client "Arma 3: Headless Client"). | N/A |
| `filePatchingExceptions[] = { "<UID>" };` | `{}` | Whitelisted clients ignore the rules defined by allowedFilePatching and verifySignatures, allowing them to join the server with any mods of their choice. Signature errors are still logged by the server upon connection. ⚠  Signature errors may still kick the user if the error happens before the server knows the clients SteamID, for example when trying to join with completely unsigned pbo's. | *This entry has existed since 2016, but only became fully operational in* [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_2.10 "Category:Introduced with Arma 3 version 2.10") [2.10](/wiki/Category:Introduced_with_Arma_3_version_2.10 "Category:Introduced with Arma 3 version 2.10") |
| Server Behaviour | | | |
| `voteThreshold = 0.33;` | `0.5` | Percentage of votes needed to confirm a vote. 33% in this example. | N/A |
| `voteMissionPlayers = 3;` | `1` | Start mission-voting when *X* numberOfPlayers connect. | N/A |
| `allowedVoteCmds[] = { { "kick", false, false, 0.75 } };` | `{}` | See [Arma 3: Mission voting](/wiki/Arma_3:_Mission_voting "Arma 3: Mission voting"). | N/A |
| `allowedVotedAdminCmds[] = { { "mission", true, true } };` | `{}` | See [Arma 3: Mission voting](/wiki/Arma_3:_Mission_voting "Arma 3: Mission voting"). | N/A |
| `kickduplicate = 1;` | `0` | Do not allow duplicate *game IDs*. Second player with an existing ID will be kicked automatically. 1 means active, 0 disabled. | N/A |
| `loopback = 1;` | `false` | Adding this option will force server into LAN mode. This will allow multiple local instances of the game to connect to the server for testing purposes. At the same time it will prevent all non-local instances from connecting. | N/A |
| `upnp = 1;` | `false` | Automatically creates port mapping on UPNP/IGD enabled router. This option allows to create a server behind NAT (the router must have public IP and support UPNP/IGD protocol). Read more [Internet Gateway Device (IGD) Standardized Device Control Protocol](https://en.wikipedia.org/wiki/Internet_Gateway_Device_Protocol). ⚠  When enabled then this setting may delay server start-up by 600s (standard UDP timeout of 10 minutes) if blocked on firewall or bad routing etc. Thus in such case is recommended to disable it. | N/A |
| `allowedFilePatching = 0;` | `0` | Prevent or allow file patching for the clients (including the HC)  - 0 is no clients - 1 is Headless Clients only - 2 is all clients | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_1.50 "Category:Introduced with Arma 3 version 1.50") [1.50](/wiki/Category:Introduced_with_Arma_3_version_1.50 "Category:Introduced with Arma 3 version 1.50") |
| `allowedLoadFileExtensions[] = { "sqf", "txt" };` | undefined | Only allow files with listed extensions to be loaded via [loadFile](/wiki/loadFile "loadFile") command. Not listing any extension means everything is allowed. Defining the setting as empty arrays means nothing is allowed. | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_1.20 "Category:Introduced with Arma 3 version 1.20") [1.20](/wiki/Category:Introduced_with_Arma_3_version_1.20 "Category:Introduced with Arma 3 version 1.20") |
| `allowedPreprocessFileExtensions[] = { "sqf", "sqs" };` | undefined | Only allow files with listed extensions to be loaded via [preprocessFile](/wiki/preprocessFile "preprocessFile") / [preprocessFileLineNumbers](/wiki/preprocessFileLineNumbers "preprocessFileLineNumbers") commands. Not listing any extension means everything is allowed. Defining the setting as empty arrays means nothing is allowed. | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_1.20 "Category:Introduced with Arma 3 version 1.20") [1.20](/wiki/Category:Introduced_with_Arma_3_version_1.20 "Category:Introduced with Arma 3 version 1.20") |
| `allowedHTMLLoadExtensions[] = { "htm", "html" };` | undefined | Only allow files and URLs with listed extensions to be loaded via [htmlLoad](/wiki/htmlLoad "htmlLoad") command. Not listing any extension means everything is allowed. Defining the setting as empty arrays means nothing is allowed. | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_1.28 "Category:Introduced with Arma 3 version 1.28") [1.28](/wiki/Category:Introduced_with_Arma_3_version_1.28 "Category:Introduced with Arma 3 version 1.28") |
| `allowedHTMLLoadURIs[] = { "http://arma3.com" };` | undefined | Only allow files from listed URIs and URLs to be loaded via [htmlLoad](/wiki/htmlLoad "htmlLoad") command. Comment out if not used. Can use += to add to the existing list. Not listing any extension means everything is allowed. Defining the setting as empty arrays means nothing is allowed. | N/A |
| `MaxPing = 200;` | `-1` | Max ping value until server kick the user | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_1.56 "Category:Introduced with Arma 3 version 1.56") [1.56](/wiki/Category:Introduced_with_Arma_3_version_1.56 "Category:Introduced with Arma 3 version 1.56") |
| `MaxPacketLoss = 50;` | `-1` | Max packetloss value until server kick the user | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_1.56 "Category:Introduced with Arma 3 version 1.56") [1.56](/wiki/Category:Introduced_with_Arma_3_version_1.56 "Category:Introduced with Arma 3 version 1.56") |
| `MaxDesync = 150;` | `-1` | Max desync value until server kick the user | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_1.56 "Category:Introduced with Arma 3 version 1.56") [1.56](/wiki/Category:Introduced_with_Arma_3_version_1.56 "Category:Introduced with Arma 3 version 1.56") |
| `DisconnectTimeout = 5;` | `15` | Server wait time before disconnecting client after loss of active traffic connection, range 1 to 90 seconds. | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_1.56 "Category:Introduced with Arma 3 version 1.56") [1.56](/wiki/Category:Introduced_with_Arma_3_version_1.56 "Category:Introduced with Arma 3 version 1.56") |
| `kickClientsOnSlowNetwork[] = { 0, 0, 0, 0 };` | `{ 1, 1, 1, 1 };` | Defines if {<MaxPing>, <MaxPacketLoss>, <MaxDesync>, <DisconnectTimeout>} will be logged (0) or kicked (1) | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_1.56 "Category:Introduced with Arma 3 version 1.56") [1.56](/wiki/Category:Introduced_with_Arma_3_version_1.56 "Category:Introduced with Arma 3 version 1.56") |
| `enablePlayerDiag = 1;` | `0` | Logs players' bandwidth and desync info every 60 seconds, as well as "network message is pending" owner identity. | N/A |
| `callExtReportLimit = 1000;` | `1000` | If server initiated [callExtension](/wiki/callExtension "callExtension") takes longer than specified limit in milliseconds, the warning will be logged into server .rpt file as well as reflected in the extension return result. | N/A |
| ``` kickTimeout[] = { 	{ 0, -1 }, 	{ 1, 180 }, 	{ 2, 180 }, 	{ 3, 180 } }; ``` | ``` { 	{ 0, 60 }, 	{ 1, 60 }, 	{ 2, 60 }, 	{ 3, 60 } }; ``` | `kickTimeout[] = { { kickID, timeout }, ... };`  kickID (type to determine from where the kick originated e.g. admin or votekick etc.)   - 0 - manual kick (vote kick, admin kick, bruteforce detection etc.) - 1 - connectivity kick (ping, timeout, packetloss, desync) - 2 - BattlEye kick - 3 - harmless kick (wrong addons, steam timeout or checks, signatures, content etc.)   timeout = in seconds how long until kicked player can return   - >0 seconds - -1 until missionEnd - -2 until serverRestart | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_1.90 "Category:Introduced with Arma 3 version 1.90") [1.90](/wiki/Category:Introduced_with_Arma_3_version_1.90 "Category:Introduced with Arma 3 version 1.90") |
| `votingTimeOut = 60;` | `60` | Voting timeout | N/A |
| `votingTimeOut[] = { 60, 90 };` | `{ 60, 90 }` | Voteing timeout { ready, notReady } | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_1.90 "Category:Introduced with Arma 3 version 1.90") [1.90](/wiki/Category:Introduced_with_Arma_3_version_1.90 "Category:Introduced with Arma 3 version 1.90") |
| `roleTimeOut = 90;` | `90` | Role selection timout | N/A |
| `roleTimeOut[] = { 90, 120 };` | `{ 90, 120 }` | Role selection timeout { ready, notReady } | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_1.90 "Category:Introduced with Arma 3 version 1.90") [1.90](/wiki/Category:Introduced_with_Arma_3_version_1.90 "Category:Introduced with Arma 3 version 1.90") |
| `briefingTimeOut = 60;` | `60` | Briefing timeout | N/A |
| `briefingTimeOut[] = { 60, 90 };` | `{ 60, 90 }` | Briefing timeout { ready, notReady } | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_1.90 "Category:Introduced with Arma 3 version 1.90") [1.90](/wiki/Category:Introduced_with_Arma_3_version_1.90 "Category:Introduced with Arma 3 version 1.90") |
| `debriefingTimeOut = 45;` | `45` | Debriefing timeout | N/A |
| `debriefingTimeOut[] = { 45, 60 };` | `{ 45, 60 }` | Debriefing timeout { ready, notReady } | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_1.90 "Category:Introduced with Arma 3 version 1.90") [1.90](/wiki/Category:Introduced_with_Arma_3_version_1.90 "Category:Introduced with Arma 3 version 1.90") |
| `lobbyIdleTimeout = 300;` | `0` | Lobby idle timeout Independent of set *lobbyIdleTimeout* in the config file, it will be at least  MAX(votingTimeout, lobbyTimeout, briefingTimeout, debriefingTimeout) + 5 seconds by default, this makes it 300 seconds of time for people in lobby and 95 in roleTimeout or briefing screen | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_1.90 "Category:Introduced with Arma 3 version 1.90") [1.90](/wiki/Category:Introduced_with_Arma_3_version_1.90 "Category:Introduced with Arma 3 version 1.90") |
| `missionsToServerRestart = 8;` | `0` | Number of times missionEnd happens before server initiate process restart (uses actual session startup command-line parameters, not possible to combine with `missionsToShutdown` ) | N/A |
| `missionsToShutdown = 8;` | `0` | Number of times missionEnd happens before server initiate process shutdown (has same behavior as setting named `missionsToHardRestart`) | N/A |
| `autoSelectMission = true;` | `false` | When enabled, the server auto-starts next mission in mission cycle and waits for players in the role selection. This allows full mission information in server browser and then results in proper filtering of the servers. This is lesser-variant (trimmed) of server startup command-line parameter -autoInit. Might collide with campaign linked missions / need mission cycle etc. | N/A |
| `randomMissionOrder = true;` | `false` | When enabled, the server random start / next selection with one of missions from mission rotation list. ( setting goes outside(before) Mission class {}; ) | N/A |
| ``` disableChannels[] = { 	{ 		0,		// channel ID 		false,		// text chat 		true,		// voice chat 		false,		// map markers 		true		// drawing on map 	}, 	{ 3, true, true } }; ``` | `{}` | `disableChannels[] = { { channelID, text, voice, mapMarkers, drawOnMap }, ... };`   - *channelID*: [Number](/wiki/Number "Number") - Available channel IDs:   - 0 = Global   - 1 = Side   - 2 = Command   - 3 = Group   - 4 = Vehicle   - 5 = Direct   - 16 = System - *text*: [Boolean](/wiki/Boolean "Boolean") - use [true](/wiki/true "true") to disable usage of text-chat for defined channelID. Default: **[false](/wiki/false "false")** - *voice*: [Boolean](/wiki/Boolean "Boolean") - use [true](/wiki/true "true") to disable usage of voice-chat (VON) for defined channelID. Default: **[false](/wiki/false "false")** - [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_2.20 "Category:Introduced with Arma 3 version 2.20") [2.20](/wiki/Category:Introduced_with_Arma_3_version_2.20 "Category:Introduced with Arma 3 version 2.20") *mapMarkers*: [Boolean](/wiki/Boolean "Boolean") - use [true](/wiki/true "true") to disable the possibility to manually place a map marker. Default: **[false](/wiki/false "false")** - [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_2.20 "Category:Introduced with Arma 3 version 2.20") [2.20](/wiki/Category:Introduced_with_Arma_3_version_2.20 "Category:Introduced with Arma 3 version 2.20") *drawOnMap*: [Boolean](/wiki/Boolean "Boolean") - use [true](/wiki/true "true") to disable the possibility to draw on the map with `Ctrl` + Left Mouse Button. Default: **[false](/wiki/false "false")**   ⓘ  Missions using [Description.ext#disableChannels](/wiki/Description.ext#disableChannels "Description.ext") will override any setting of *disableChannels[]* in the **server.cfg**. | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_1.60 "Category:Introduced with Arma 3 version 1.60") [1.60](/wiki/Category:Introduced_with_Arma_3_version_1.60 "Category:Introduced with Arma 3 version 1.60") [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_2.20 "Category:Introduced with Arma 3 version 2.20") [2.20](/wiki/Category:Introduced_with_Arma_3_version_2.20 "Category:Introduced with Arma 3 version 2.20") |
| `idleFPSLimit = 30;` | `30` | Servers with no players will limit their FPS to the specified value. Supported range is 5-60 FPS | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_2.22 "Category:Introduced with Arma 3 version 2.22") [2.22](/wiki/Category:Introduced_with_Arma_3_version_2.22 "Category:Introduced with Arma 3 version 2.22") |
| Other Options | | | |
| `verifySignatures = 2;` | `2` | Enables or disables the [signature verification](/wiki/ArmA:_Addon_Signatures "ArmA: Addon Signatures") for addons.  - Verification disabled = 0. - 1 Will default back to 2 |
| `equalModRequired = 1;` | `0` | Outdated - If set to 1, players have to use exactly the same -mod= startup parameter as the server. (0 - disabled, 1 - enabled). | - |
| `drawingInMap = false;` | `true` | Enables or disables the ability to place markers and draw lines in map. | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_1.64 "Category:Introduced with Arma 3 version 1.64") [1.64](/wiki/Category:Introduced_with_Arma_3_version_1.64 "Category:Introduced with Arma 3 version 1.64") |
| `disableVoN = 1;` | `0` | Enables or disables the ***V**oice **o**ver **N**et* (0 is VoN enabled (default), 1 is disabled). | N/A |
| `vonCodecQuality = 10;` | `3` | Defines *VoN* codec quality. Value range is from 1 to 20.  - Since 1.62 supports range 1-20 - Since 1.64 will support range 1-30 - 8kHz is 0-10, 16kHz is 11-20, for 21-30 it is 32kHz for SPEEX codec and 48kHz for OPUS codec. | - |
| `vonCodec = 1;` | `1` | Defines *VoN* codec type. Value range is from 0 to 1.  - Since Arma 3 update 1.58 supports value 1 - Value 0 uses older [SPEEX codec](https://en.wikipedia.org/wiki/Speex), while 1 switches to new IETF standard [OPUS codec](https://en.wikipedia.org/wiki/Opus_(audio_format)). | - |
| `skipLobby = false;` | `false` | If true, joining player will skip role selection. This is only used if no Mission, Campaign or Config setting skipLobby is defined (See [Description.ext#skipLobby](/wiki/Description.ext#skipLobby "Description.ext")). | N/A |
| `allowProfileGlasses = false;` | `true` | If false, glasses set in player profile will be ignored. This is only used if no Mission, Campaign or Config setting allowProfileGlasses is defined (See [Description.ext - allowProfileGlasses](/wiki/Description.ext#allowProfileGlasses "Description.ext")) | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_2.06 "Category:Introduced with Arma 3 version 2.06") [2.06](/wiki/Category:Introduced_with_Arma_3_version_2.06 "Category:Introduced with Arma 3 version 2.06") |
| `zeusCompositionScriptLevel = 0;` | `1` | - 0: all scripts are forbidden - 1: only attributes are allowed (including custom attributes added by mods) - 2: all scripts are allowed including init scripts   This is only used if no Mission, Campaign or Config setting zeusCompositionScriptLevel is defined (See [Description.ext - zeusCompositionScriptLevel](/wiki/Description.ext#zeusCompositionScriptLevel "Description.ext")). | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_2.06 "Category:Introduced with Arma 3 version 2.06") [2.06](/wiki/Category:Introduced_with_Arma_3_version_2.06 "Category:Introduced with Arma 3 version 2.06") |
| `logFile = "server_console.log";` | `""` | Enables output of dedicated server console into *textfile*. Default location of log is same as *crash dumps* and other logs. (Local settings) Note that this does not change the location of the "*net.log*" file, which is enabled with the -netlog command line option. | N/A |
| `doubleIdDetected = "command";` | `""` | See [Server Side Scripting](/wiki/Arma_3:_Server_Side_Scripting "Arma 3: Server Side Scripting") |
| `onUserConnected = "command";` | `""` | N/A |
| `onUserDisconnected = "command";` | `""` | N/A |
| `onHackedData = "command";` | `""` | N/A |
| `onDifferentData = "command";` | `""` | N/A |
| `onUnsignedData = "command";` | `""` | N/A |
| `onUserKicked = "command";` | `""` | N/A |
| `regularCheck = "command";` | `""` | N/A |
| `BattlEye = 1;` | `1` | Enables or disables the [BattlEye](/wiki/BattlEye "BattlEye") anti-cheat engine. Requires installed [BattlEye](/wiki/BattlEye "BattlEye") on server and clients joining the server | N/A |
| `timeStampFormat = "none";` | `"short"` | Set the timestamp format used on each report line in server-side [RPT file](/wiki/Crash_Files "Crash Files"). Possible values are "none", "short", "full". | N/A |
| `timeStampFormatConsole = "none";` | `"short"` | Set the timestamp format used on each report line in server console. Possible values are "none", "short", "full". | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_2.22 "Category:Introduced with Arma 3 version 2.22") [2.22](/wiki/Category:Introduced_with_Arma_3_version_2.22 "Category:Introduced with Arma 3 version 2.22") |
| `forceRotorLibSimulation = 0;` | `0` | Enforces the Advanced Flight Model on the server. 0 (up to the player). 1 - forced AFM, 2 - forced SFM. | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_1.34 "Category:Introduced with Arma 3 version 1.34") [1.34](/wiki/Category:Introduced_with_Arma_3_version_1.34 "Category:Introduced with Arma 3 version 1.34") |
| `persistent = 1;` | `0` | Mission keeps running when all clients disconnect. Enabling the persistence option will make missions that have either *base* or *instant* respawn keep on running after all players have disconnected. The other respawn types will not make a mission persistent. The kind of respawn a certain mission uses is set in its [Description.ext](/wiki/Description.ext "Description.ext"). | N/A |
| `requiredBuild = xxxxx;` | `0` | Minimum required client version. Clients with version lower than requiredBuild will not be able to connect. If requiredBuild is set to a large number, like `requiredBuild = 999999999;` for example, it will automatically be lowered to the current server version. | N/A |
| `statisticsEnabled = 1;` | `1` | Allows to opt-out of [Arma 3 analytics](/wiki/Arma_3:_Analytics "Arma 3: Analytics") for the server by using 0 | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_1.56 "Category:Introduced with Arma 3 version 1.56") [1.56](/wiki/Category:Introduced_with_Arma_3_version_1.56 "Category:Introduced with Arma 3 version 1.56") |
| `forcedDifficulty = "regular";` | `""` | Enforces the selected difficulty on the server. **forcedDifficulty** = "<difficultyClass>";  - If Recruit, Regular or Veteran is passed as the parameter, the particular difficulty options will be taken from data config, from the class CfgDifficultyPresets. - If Custom will be passed as the parameter, the particular flags will be taken from CustomDifficulty class from server's profile (only Custom is saved to the profile). - If mission cycle is defined in the **server.cfg**, the difficulty set in the mission cycle overrides the difficulty set by **forcedDifficulty** parameter. | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_1.56 "Category:Introduced with Arma 3 version 1.56") [1.56](/wiki/Category:Introduced_with_Arma_3_version_1.56 "Category:Introduced with Arma 3 version 1.56") |
| `missionWhitelist[] = { "intro.altis" };` | `{}` | Limit the available missions for the admin for the mission change. See [Arma 3: MP Mission Names](/wiki/Arma_3:_MP_Mission_Names "Arma 3: MP Mission Names") for a full list. | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_1.56 "Category:Introduced with Arma 3 version 1.56") [1.56](/wiki/Category:Introduced_with_Arma_3_version_1.56 "Category:Introduced with Arma 3 version 1.56") |
| `steamProtocolMaxDataSize = 1024;` | `1024` | Limit for maximum Steam Query packet length. Increasing this value is dangerous as it can cause Arma 3 server to send UDP packets of a size larger than the MTU. This will cause UDP packets to be fragmented which is not supported by some older routers. But increasing this will fix the modlist length limit in [Arma 3: Launcher](/wiki/Arma_3:_Launcher "Arma 3: Launcher"). | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_2.00 "Category:Introduced with Arma 3 version 2.00") [2.00](/wiki/Category:Introduced_with_Arma_3_version_2.00 "Category:Introduced with Arma 3 version 2.00") |
| ``` class AdvancedOptions { 	logObjectNotFound = 1;			// logging enabled 	skipDescriptionParsing = 0;		// parse description.ext 	ignoreMissionLoadErrors = 0;	// do not ingore errors  	// if a specific players message queue 	// is larger than 1MB and #monitor is running, 	// dump his messages to a logfile for analysis 	queueSizeLogG = 1000000; // 1MB }; ``` | ``` logObjectNotFound = 1; skipDescriptionParsing = 0; ignoreMissionLoadErrors = 0; queueSizeLogG = 0; ``` | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_2.02 "Category:Introduced with Arma 3 version 2.02") [2.02](/wiki/Category:Introduced_with_Arma_3_version_2.02 "Category:Introduced with Arma 3 version 2.02")   - LogObjectNotFound - [false](/wiki/false "false") to skip logging "Server: Object not found" messages - SkipDescriptionParsing - [true](/wiki/true "true") to skip parsing of description.ext/mission.sqm. Will show pbo filename instead of configured missionName. OverviewText and such won't work, but loading the mission list is a lot faster when there are many missions   [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_2.04 "Category:Introduced with Arma 3 version 2.04") [2.04](/wiki/Category:Introduced_with_Arma_3_version_2.04 "Category:Introduced with Arma 3 version 2.04")   - ignoreMissionLoadErrors - when set to [true](/wiki/true "true"), the mission will load no matter the amount of loading errors. If set to [false](/wiki/false "false"), the server will abort mission's loading and return to mission selection   [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_2.08 "Category:Introduced with Arma 3 version 2.08") [2.08](/wiki/Category:Introduced_with_Arma_3_version_2.08 "Category:Introduced with Arma 3 version 2.08")   - queueSizeLogG - if #monitor is running and a player's Guaranteed Message Queue size (Listed as G: in #monitor) goes above the threshold, all that player's message types and sizes are dumped to a logfile in the RPT directory (see [Crash Files](/wiki/Crash_Files "Crash Files")); the dumping itself is logged server-side. 0 disables the feature. | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_2.02 "Category:Introduced with Arma 3 version 2.02") [2.02](/wiki/Category:Introduced_with_Arma_3_version_2.02 "Category:Introduced with Arma 3 version 2.02") [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_2.04 "Category:Introduced with Arma 3 version 2.04") [2.04](/wiki/Category:Introduced_with_Arma_3_version_2.04 "Category:Introduced with Arma 3 version 2.04") [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_2.08 "Category:Introduced with Arma 3 version 2.08") [2.08](/wiki/Category:Introduced_with_Arma_3_version_2.08 "Category:Introduced with Arma 3 version 2.08") |
| `armaUnitsTimeout = 30;` | `30` | Defines how long the player will be stuck connecting and wait for [armaUnits data](https://units.arma3.com/). Player will be notified if timeout elapsed and no units data was received | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_2.06 "Category:Introduced with Arma 3 version 2.06") [2.06](/wiki/Category:Introduced_with_Arma_3_version_2.06 "Category:Introduced with Arma 3 version 2.06") |
| `overrideHazeQuality = 1;` | `-1` | 0/1/2 - VeryLow/Low/Standard - Forces haze quality in MP on all clients. Standard (2) - mod config driven. Default (-1) - do not force. Mission config's [overrideHazeQuality](/wiki/Description.ext#overrideHazeQuality "Description.ext") has priority over the server option. | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_2.16 "Category:Introduced with Arma 3 version 2.16") [2.16](/wiki/Category:Introduced_with_Arma_3_version_2.16 "Category:Introduced with Arma 3 version 2.16") |
| ``` class AntiFlood { 	cycleTime = 0.5; 	cycleLimit = 400; 	cycleHardLimit = 4000; 	enableKick = 0; }; ``` | ``` cycleTime = 0.5; cycleLimit = 400; cycleHardLimit = 4000; enableKick = 0; ``` | `cycle` is time in seconds. Every cycle, if player sends more than "limit" number of messages, they get flagged.  If the last 4 out of 8 cycles were flagged or if within one cycle the hard limit is exceeded, the player is kicked (if `enableKick` is set to true) or logged to RPT that they exceeded the limit. | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_2.18 "Category:Introduced with Arma 3 version 2.18") [2.18](/wiki/Category:Introduced_with_Arma_3_version_2.18 "Category:Introduced with Arma 3 version 2.18") |
| `missionHTTPDownloadBaseURL = "https://example.com/missionfiles/";` | `""` | See [Mission HTTP Download](/wiki/Arma_3:_Mission_HTTP_Download "Arma 3: Mission HTTP Download") | [Arma 3 logo black.png](/wiki/Category:Introduced_with_Arma_3_version_2.20 "Category:Introduced with Arma 3 version 2.20") [2.20](/wiki/Category:Introduced_with_Arma_3_version_2.20 "Category:Introduced with Arma 3 version 2.20") |

## Server Administration[[edit source](/wiki?title=Arma_3:_Server_Config_File&action=edit&section=2 "Edit section: Server Administration")]

If BattlEye RCon is not in use, there can only be 1 server admin at any given time. There are two ways of becoming admin, through a vote or through login with authenticated credentials.

### Voted In Admin[[edit source](/wiki?title=Arma_3:_Server_Config_File&action=edit&section=3 "Edit section: Voted In Admin")]

It is possible to become a server administrator through player voting process. Such admin has less abilities than logged in admin.  
For example, voted in admin would be able to kick a player, but only logged in admin would be able to ban a player.  
For more information on voting and voting configuration see page [**Server Voting**](/wiki/Arma_3:_Mission_voting "Arma 3: Mission voting").

### Logged In Admin[[edit source](/wiki?title=Arma_3:_Server_Config_File&action=edit&section=4 "Edit section: Logged In Admin")]

To log in as admin, one is required to type in#login command followed by server password, which matches password defined in **passwordAdmin** param.  
Since Arma 2:OA it is possible to add one or several user ids [UIDs](/wiki/getPlayerUID "getPlayerUID") into **admins[]** server config parameter, which would allow listed users to log in as admin by simply typing #login without a password.  
This presents several advantages for managing the servers. The server owner can have multiple admins selected from the community and doesn't have to provide each one with server admin password.  
The adding and removing of UIDs is done on the server side which makes it easy to add and to remove admins if necessary. On the server, admins are handled on first come first served basis.  
Here are the rules:

- If there is already a logged in admin on the server, the new admin will not be able to log in until previously logged in admin logs out
- If there is a voted in admin on the server, the logged in admin will override voted in admin and take admin role
- There is no difference between logged in admin that used admin password or logged in admin that was whitelisted with **admins[]** param

The logged in and voted in admins have different set of **[Multiplayer Server Commands](/wiki/Multiplayer_Server_Commands "Multiplayer Server Commands")** available to them. See **[serverCommandAvailable](/wiki/serverCommandAvailable "serverCommandAvailable")** for more information.

## Server Security[[edit source](/wiki?title=Arma_3:_Server_Config_File&action=edit&section=5 "Edit section: Server Security")]

Several of these settings directly contribute to the security of the server and have been highlighted as important, particularly for running public - no password - servers.

The most updated ones that give a good protection (and are, *de facto*, the standard for public servers) are

```
battlEye = 1;
verifySignatures = 2;
allowedFilePatching = 0;
allowedLoadFileExtensions[] = { "hpp", "sqs", "sqf", "fsm", "cpp", "paa", "txt", "xml", "inc", "ext", "sqm", "ods", "fxy", "lip", "csv", "kb", "bik", "bikb", "html", "htm", "biedi" };
allowedPreprocessFileExtensions[] = { "hpp", "sqs", "sqf", "fsm", "cpp", "paa", "txt", "xml", "inc", "ext", "sqm", "ods", "fxy", "lip", "csv", "kb", "bik", "bikb", "html", "htm", "biedi" }; // ,"sqfc" // add .sqfc if used
allowedHTMLLoadExtensions[] = { "htm", "html", "xml", "txt" };
// allowedHTMLLoadURIs[] = {};
passwordAdmin = "xyzxyz123";
serverCommandPassword = "xyzxyz456";
```

### Notes[[edit source](/wiki?title=Arma_3:_Server_Config_File&action=edit&section=6 "Edit section: Notes")]

The properties allowedLoad\*/allowedPreprocess\*/allowedHTML\* are server.cfg settings with array list of extensions for server-side use only. The above listed examples are for basic game multiplayer modes. Server admins may attempt to make it stricter for their servers. However, if too strict then server's log file will contain warning entries about unable read.

:   With the exception of allowedHTMLLoadURIs those arrays covers both files inside and outside PBOs so don't change the above defaults w/o testing first as there is a chance it will break the game.

- To read [loadFile](/wiki/loadFile "loadFile") , [preprocessFile](/wiki/preprocessFile "preprocessFile") , [preprocessFileLineNumbers](/wiki/preprocessFileLineNumbers "preprocessFileLineNumbers") and to remember, those works on files only-within Arma 3 server directory and its sub-directories!
- Refer to [ArmA: Addon Signatures](/wiki/ArmA:_Addon_Signatures "ArmA: Addon Signatures") for current best practices in server mod signing and the use of key signature files.
- To further increase servers security, remember that [BattlEye](/wiki/BattlEye "BattlEye") has the ability to use server-side (including preventing remote execution) and client-side script check filters.
  - These BattlEye filters needs to be written specifically for each mission and mod as the scripting differs in each of them.

### Safe Folder Structure[[edit source](/wiki?title=Arma_3:_Server_Config_File&action=edit&section=7 "Edit section: Safe Folder Structure")]

Engine supports absolute outside Arma 3 server folder for command-line parameters -servermod=, -mod= and same for profile directories and config locations.
This puts those out of reach by various load script command features which are limited only within Arma 3 folder and it is sub-directories for security reason.
thus e.g. safe folder-structure looks like:

- \arma3server\
- \arma3server\@publicmods\
- \arma3server\_servermods\_secrethash\
- \arma3server\_profiles\_and\_configs\_secrethash\

Note that [callExtensions](/wiki/callExtension "callExtension") are loaded only from Arma 3 server root / sub-folders

## Example Configuration File[[edit source](/wiki?title=Arma_3:_Server_Config_File&action=edit&section=8 "Edit section: Example Configuration File")]

Show text

```
// server.cfg

// GLOBAL SETTINGS
hostname = "Fun and Test Server";		// The name of the server that shall be displayed in the public server list
password = "";							// Password for joining, eg connecting to the server
passwordAdmin = "xyz";					// Password to become server admin. When in Arma MP and connected to the server, type '#login xyz'
serverCommandPassword = "xyzxyz";		// Password required by alternate syntax of [[serverCommand]] server-side scripting.

logFile = "server_console.log";			// Where the logfile should go and what it should be called

// WELCOME MESSAGE ("Message Of The Day")
// It can be several lines, separated by comma
// Empty messages "" will not be displayed and are only here to add delay
motd[] =
{
	"", "",
	"Two empty lines above to increase the time interval",
	"Welcome to our server",
	"", "",
	"We are looking for fun - Join us Now!",
	"http://www.example.com",
	"One more empty line below to increase interval",
	""
};
motdInterval = 2.5;					// Time interval (in seconds) between each message

// JOINING RULES
maxPlayers = 64;					// Maximum amount of players. Civilians and watchers, beholder, bystanders and so on also count as player.
kickDuplicate = 1;					// Each ArmA version has its own ID. If kickDuplicate is set to 1, a player will be kicked when he joins a server where another player with the same ID is playing.
verifySignatures = 2;				// Verifies .pbos against .bisign files. Valid values 0 (disabled), 1 (prefer v2 sigs but accept v1 too) and 2 (only v2 sigs are allowed).
equalModRequired = 0;				// Outdated. If set to 1, player has to use exactly the same -mod= startup parameter as the server.
allowedFilePatching = 0;			// Allow or prevent client using -filePatching to join the server. 0, is disallow, 1 is allow HC, 2 is allow all clients (since Arma 3 v1.50)
filePatchingExceptions[] = { "123456789", "987654321" }; // Whitelisted Steam IDs allowed to join with -filePatching enabled
// requiredBuild = 12345;			// Require clients joining to have at least build 12345 of game, preventing obsolete clients to connect

// VOTING
voteMissionPlayers = 1;				// Tells the server how many people must connect so that it displays the mission selection screen.
voteThreshold = 0.33;				// 33% or more players need to vote for something, for example an admin or a new map, to become effective

// INGAME SETTINGS
disableVoN = 0;					// If set to 1, Voice over Net will not be available
vonCodec = 1; 					// If set to 1 then it uses IETF standard OPUS codec, if to 0 then it uses SPEEX codec (since Arma 3 v1.58)
vonCodecQuality = 30;			// 0..10 = 8kHz, 11..20 = 16kHz, 21..30 = 32kHz (48kHz)
persistent = 1;					// If 1, missions still run on even after the last player disconnected.
timeStampFormat = "short";		// Set the timestamp format used on each report line in server-side RPT file. Possible values are "none" (default), "short", "full".
BattlEye = 1;					// Server to use BattlEye system
allowedLoadFileExtensions[] = { "hpp", "sqs", "sqf", "fsm", "cpp", "paa", "txt", "xml", "inc", "ext", "sqm", "ods", "fxy", "lip", "csv", "kb", "bik", "bikb", "html", "htm", "biedi" }; // only allow files with those extensions to be loaded via loadFile command (since Arma 3 build 1.19.124216)
allowedPreprocessFileExtensions[] = { "hpp", "sqs", "sqf", "fsm", "cpp", "paa", "txt", "xml", "inc", "ext", "sqm", "ods", "fxy", "lip", "csv", "kb", "bik", "bikb", "html", "htm", "biedi" }; // only allow files with those extensions to be loaded via preprocessFile/preprocessFileLineNumber commands (since Arma 3 build 1.19.124323)
allowedHTMLLoadExtensions[] = { "htm", "html", "xml", "txt" }; // only allow files with those extensions to be loaded via HTMLLoad command (since Arma 3 build 1.27.126715)
// allowedHTMLLoadURIs[] = {}; // Leave commented to let missions/campaigns/addons decide what URIs are supported. Uncomment to define server-level restrictions for URIs

// TIMEOUTS
disconnectTimeout = 5;			// Time to wait before disconnecting a user which temporarly lost connection. Range is 5 to 90 seconds.
maxDesync = 150;				// Max desync value until server kick the user
maxPing= 200;					// Max ping value until server kick the user
maxPacketLoss = 50;				// Max packetloss value until server kick the user
kickClientsOnSlowNetwork[] = { 0, 0, 0, 0 }; // Defines if {<MaxPing>, <MaxPacketLoss>, <MaxDesync>, <DisconnectTimeout>} will be logged (0) or kicked (1)
kickTimeout[] = { { 0, -1 }, { 1, 180 }, { 2, 180 }, { 3, 180 } };
votingTimeOut[] = { 60, 90 };		// Kicks users from server if they spend too much time in mission voting
roleTimeOut[] = { 90, 120 };		// Kicks users from server if they spend too much time in role selection
briefingTimeOut[] = { 60, 90 };		// Kicks users from server if they spend too much time in briefing (map) screen
debriefingTimeOut[] = { 45, 60 };	// Kicks users from server if they spend too much time in debriefing screen
lobbyIdleTimeout = 300;				// The amount of time the server will wait before force-starting a mission without a logged-in Admin.

// SCRIPTING ISSUES
onUserConnected = "";
onUserDisconnected = "";
doubleIdDetected = "";

// SIGNATURE VERIFICATION
onUnsignedData = "kick (_this select 0)";	// unsigned data detected
onHackedData = "kick (_this select 0)";		// tampering of the signature detected
onDifferentData = "";				// data with a valid signature, but different version than the one present on server detected

// MISSIONS CYCLE (see below)
randomMissionOrder = true;	// Randomly iterate through Missions list
autoSelectMission = true;	// Server auto selects next mission in cycle

class Missions {};			// An empty Missions class means there will be no mission rotation

missionWhitelist[] = {};	// An empty whitelist means there is no restriction on what missions' available
```

[↑ Back to spoiler's top](#bikisp6a1ae135cba13)

## Mission Rotation[[edit source](/wiki?title=Arma_3:_Server_Config_File&action=edit&section=9 "Edit section: Mission Rotation")]

One can set an automatic mission rotation. Without an admin, the server will automatically select a mission when at least one player is connected.  
Once the mission is done and if there are still players on the server, it will automatically switch to the next mission in the cycle.

General definition:

```
class Missions
{
	class CUSTOMNAME
	{
		template = "MISSION.TERRAIN";
		difficulty = "DIFFICULTYLEVEL";
		class Params
		{
			MISSIONPARAMETER1 = VALUE;
			MISSIONPARAMETER2 = VALUE;
			MISSIONPARAMETER3 = VALUE;
		};
	};
};
```

⚠

The keyword class for class definitions **must** be lowercase, otherwise a parsing error will occur.

### Parameters[[edit source](/wiki?title=Arma_3:_Server_Config_File&action=edit&section=10 "Edit section: Parameters")]

The template and difficulty must be defined. Parameter definition is optional.

#### Template[[edit source](/wiki?title=Arma_3:_Server_Config_File&action=edit&section=11 "Edit section: Template")]

The entry naming is always **missionName.terrainName**.

The name can come from the different options:

1. **Mission PBO** in MPMissions folder - like **MyMission.MyTerrain.pbo**
2. **Mission folder** in MPMissions folder - like **MyMission.MyTerrain\mission.sqm**
3. **Addon Mission** loaded via some mod (folder) - in such case it must be defined via a config.cpp in [class CfgMissions/class MPMissions](/wiki/CfgMissions "CfgMissions"). You determine the naming with the [config viewer](/wiki/Arma_3:_Splendid_Config_Viewer "Arma 3: Splendid Config Viewer").

⚠

The crucial part in the case of **Addon Mission** is that its **className.terrainName** - NOT the mission folder name! Good practice is to name the class the same as the folder, but it is not always the case.
So watch out and use only the class name + terrain name.

Sample definition from CfgMissions/MPMissions relevant for **Addon Mission':**

```
class CfgMissions
{
	class MPMissions
	{
		class MP_Marksmen_01
		{
			briefingName = "End Game 16 Kavala";
			directory = "A3\Missions_F_MP_Mark\MPScenarios\MP_Marksmen_01.Altis";
		};

		class EscapeFromMalden//class name not the same as mission folder name!
		{
			briefingName = "Escape 10 Malden";
			directory = "A3\Missions_F_Patrol\MPScenarios\MP_EscapeFromMalden.Malden";
		};

		class MP_CombatPatrol_01
		{
			briefingName = "COOP 12 Combat Patrol";
			directory = "A3\Missions_F_Patrol\mpscenarios\MP_CombatPatrol_01.Altis";
		};

		class Apex
		{
			briefingName = "Apex Protocol";
			class EXP_m01
			{
				briefingName = "01 Keystone";
				directory = "a3\missions_f_exp\campaign\missions\exp_m01.tanoa";
			};
		};
};
```

#### Difficulty[[edit source](/wiki?title=Arma_3:_Server_Config_File&action=edit&section=12 "Edit section: Difficulty")]

Set the desired enforced difficulty level.

The standard options are:

- Recruit
- Regular
- Veteran
- Custom

For more see: [Difficulty Settings](/wiki/Arma_3:_Difficulty_Settings#Configuration "Arma 3: Difficulty Settings")

ⓘ

- Some mods or CDLCs define additional difficulty levels. You determine the naming with the [config viewer](/wiki/Arma_3:_Splendid_Config_Viewer "Arma 3: Splendid Config Viewer") in **CfgDifficultyPresets**.
- When difficulty is set to `difficulty = "Custom";` the server will look into USERNAME.Arma3Profile file for the definition of custom difficulty, which should look like this: see [server.armaprofile - Server Difficulty Example](/wiki/server.armaprofile#Server_Difficulty_Example "server.armaprofile").

#### class Params[[edit source](/wiki?title=Arma_3:_Server_Config_File&action=edit&section=13 "Edit section: class Params")]

A mission may offer [mission parameters](/wiki/Mission_Parameters "Mission Parameters"). You can apply a different default for the given mission in your mission rotation by setting the key-value pair in here.  
To determine if/what mission parameters are available, you need to exact the mission pbo to get to the [Description.ext](/wiki/Description.ext "Description.ext").  
The actual definition may also be found in another files if `#include "PATH\FILE.extension"` is used.

```
class Missions
{
	class TestMission01 // name for the first entry - can be anything (characters/numbers/underscore, but no spaces!)
	{
		template = "MP_Marksmen_01.Altis";
		difficulty = "recruit";
		class Params
		{
			RespawnDelay = 15;			// default 30
			EndGameRespawnDelay = 30;	// default 45
		};
	};

	class TestMission02
	{
		template = "EscapeFromMalden.Malden";
		difficulty = "regular";
		class Params
		{
			Loadouts = 0;				// default 1
			EnemyEquipment = 0;			// default 1
		};
	};

	class TestMission03
	{
		template = "MP_CombatPatrol_01.Altis";
		difficulty = "veteran";
		class Params
		{
			BIS_CP_reinforcements = 2;	// default 0
			BIS_CP_tickets = 5;			// default 20
		};
	};

	class TestMission04
	{
		template = "EXP_m01.Tanoa";		// Apex Protocol - COOP campaign
		difficulty = "custom";
		class Params
		{
		};
	};
};
```

ⓘ

Execute the [Mission Parameters export script](/wiki/Biki_Export_Scripts#Mission_Parameters "Biki Export Scripts") from the [Debug Console](/wiki/Arma_3:_Debug_Console "Arma 3: Debug Console") to export the current mission's parameters set in order to use it in the mission rotation class.

### Arma 3 Apex - Mission Collection[[edit source](/wiki?title=Arma_3:_Server_Config_File&action=edit&section=14 "Edit section: Arma 3 Apex - Mission Collection")]

Mainly intended for MP campaigns. If the progress is defined in the MPMissions class, server administrator can simply add the whole campaign by adding *campaign class* into the *server.cfg*. See the example below.

**config.cpp:**

```
class MPMissions
{
	class Apex
	{
		briefingName = "$STR_A3_CoopCampaignName";

		class EXP_m01
		{
			briefingName = "$STR_A3_exp_m01_missionname";
			directory = "a3\missions_f_exp\campaign\missions\exp_m01.tanoa";
		};

		class EXP_m02
		{
			briefingName = "$STR_A3_exp_m02_missionname";
			directory = "a3\missions_f_exp\campaign\missions\exp_m02.tanoa";
		};

		class EXP_m03
		{
			briefingName = "$STR_A3_exp_m03_missionname";
			directory = "a3\missions_f_exp\campaign\missions\exp_m03.tanoa";
		};
	};
};
```

**server.cfg:**

```
// MISSIONS CYCLE
class Missions
{
	class Apex{};
};
```

Also the difficulty of the each mission can be overridden

```
class Missions
{
	class Apex
	{
		class EXP_m01
		{
			difficulty = "veteran";
		};
	};
};
```

### Important Settings[[edit source](/wiki?title=Arma_3:_Server_Config_File&action=edit&section=15 "Edit section: Important Settings")]

In the **Server Config File**

- autoSelectMission
- randomMissionOrder
- persistent
- missionsToServerRestart and missionsToShutdown
- votingTimeOut, roleTimeOut, briefingTimeOut and debriefingTimeOut

And from [Startup Parameters](/wiki/Arma_3:_Startup_Parameters "Arma 3: Startup Parameters"):

- loadMissionToMemory
- autoInit

## Additional Details[[edit source](/wiki?title=Arma_3:_Server_Config_File&action=edit&section=16 "Edit section: Additional Details")]

For hosting server behind NAT or firewall, please ensure gameport and STEAMports are forwarded and open!  
Set the ICMP "echo reply" as allowed so the server is able return ping delay properly.  
It's recommended to enable the NAT traversal (so called "Edge traversal" in Windows Firewall) for **arma3server.exe**, **arma3.exe** to better support clients/servers behind NAT.

## Mission Parameters Override[[edit source](/wiki?title=Arma_3:_Server_Config_File&action=edit&section=17 "Edit section: Mission Parameters Override")]

On dedicated server, only an admin can set mission options provided by mission maker via class Params. However as a server owner it is possible to override default setting with your own. See [Mission Parameters](/wiki/Mission_Parameters "Mission Parameters") for more info.