---
game: reforger
slug: reforger-server-config
file: serverconfig.json
source: https://community.bistudio.com/wiki/Arma_Reforger:Server_Config
scraped: 2026-05-31T11:45:20Z
---

- [1 Summary](#Summary)
- [2 Root](#Root)
  - [2.1 bindAddress](#bindAddress)
  - [2.2 bindPort](#bindPort)
  - [2.3 publicAddress](#publicAddress)
  - [2.4 publicPort](#publicPort)
  - [2.5 a2s](#a2s)
  - [2.6 rcon](#rcon)
  - [2.7 game](#game)
  - [2.8 operating](#operating)
- [3 a2s](#a2s_2)
  - [3.1 address](#address)
  - [3.2 port](#port)
- [4 rcon](#rcon_2)
  - [4.1 address](#address_2)
  - [4.2 port](#port_2)
  - [4.3 password](#password)
  - [4.4 maxClients](#maxClients)
  - [4.5 permission](#permission)
  - [4.6 blacklist](#blacklist)
  - [4.7 whitelist](#whitelist)
- [5 game](#game_2)
  - [5.1 name](#name)
  - [5.2 password](#password_2)
  - [5.3 passwordAdmin](#passwordAdmin)
  - [5.4 admins](#admins)
  - [5.5 scenarioId](#scenarioId)
  - [5.6 maxPlayers](#maxPlayers)
  - [5.7 visible](#visible)
  - [5.8 crossPlatform](#crossPlatform)
  - [5.9 supportedPlatforms](#supportedPlatforms)
  - [5.10 gameProperties](#gameProperties)
  - [5.11 modsRequiredByDefault](#modsRequiredByDefault)
  - [5.12 mods](#mods)
    - [5.12.1 modID](#modID)
    - [5.12.2 name](#name_2)
    - [5.12.3 version](#version)
    - [5.12.4 required](#required)
- [6 gameProperties](#gameProperties_2)
  - [6.1 serverMaxViewDistance](#serverMaxViewDistance)
  - [6.2 serverMinGrassDistance](#serverMinGrassDistance)
  - [6.3 fastValidation](#fastValidation)
  - [6.4 networkViewDistance](#networkViewDistance)
  - [6.5 battlEye](#battlEye)
  - [6.6 disableThirdPerson](#disableThirdPerson)
  - [6.7 VONDisableUI](#VONDisableUI)
  - [6.8 VONDisableDirectSpeechUI](#VONDisableDirectSpeechUI)
  - [6.9 VONCanTransmitCrossFaction](#VONCanTransmitCrossFaction)
  - [6.10 missionHeader](#missionHeader)
  - [6.11 persistence](#persistence)
    - [6.11.1 autoSaveInterval](#autoSaveInterval)
    - [6.11.2 saveRetention](#saveRetention)
    - [6.11.3 loadSessionSave](#loadSessionSave)
    - [6.11.4 keepSessionSave](#keepSessionSave)
    - [6.11.5 hiveId](#hiveId)
    - [6.11.6 databases](#databases)
    - [6.11.7 storages](#storages)
- [7 operating](#operating_2)
  - [7.1 lobbyPlayerSynchronise](#lobbyPlayerSynchronise)
  - [7.2 disableCrashReporter](#disableCrashReporter)
  - [7.3 disableNavmeshStreaming](#disableNavmeshStreaming)
  - [7.4 disableServerShutdown](#disableServerShutdown)
  - [7.5 disableAI](#disableAI)
  - [7.6 playerSaveTime](#playerSaveTime)
  - [7.7 aiLimit](#aiLimit)
  - [7.8 slotReservationTimeout](#slotReservationTimeout)
  - [7.9 joinQueue](#joinQueue)
    - [7.9.1 maxSize](#maxSize)
- [8 Template](#Template)
- [9 Example](#Example)

Arma Reforger servers uses [JSON](https://en.wikipedia.org/wiki/JSON) configuration format. [Template](#Template) and [Example](#Example) are available at the bottom of this page.

⚠

Two **very important** aspects of dedicated server public hosting:

- Keep [fastValidation](#fastValidation) to true
- Limit max FPS with the [-maxFPS](/wiki/Arma_Reforger:Startup_Parameters#maxFPS "Arma Reforger:Startup Parameters") startup parameter in order to save performance

ⓘ

- Parameters inside the JSON file are case-sensitive!
- Values are strings, unless mentioned otherwise.
- For Community config generators, see the following:
  - <https://arscfg.johandejong.dev/>
  - <https://armareforger.se/server-generator/>

## Summary[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=1 "Edit section: Summary") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=1 "Edit section: Summary")]

Default ports to be opened for hosting:

:   | Port | Protocol | Optional | Reason |
    | --- | --- | --- | --- |
    | 2001 | UDP | Unchecked | [Public port](#publicPort) |
    | 17777 | Checked | [A2S port](#port) |
    | 19999 | Checked | [RCON port](#port_2) |

## Root[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=2 "Edit section: Root") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=2 "Edit section: Root")]

### bindAddress[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=3 "Edit section: bindAddress") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=3 "Edit section: bindAddress")]

IP address to which the server socket will be bound. In most cases, this should be left empty.
It can be used to restrict connections to a particular network interface. When left out or empty, 0.0.0.0 is used, which allows connections through any IP address.

⚠

- This entryshould generally be left out of server config to use default value and should only be set by experienced users with specific network needs.
- IP**v6** is **not** supported by Arma Reforger.

*was **gameHostBindAddress** before 0.9.8.73*

### bindPort[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=4 "Edit section: bindPort") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=4 "Edit section: bindPort")]

number value, range 1..65535, default: 2001

UDP port to which the server socket will be bound.

⚠

This entry should generally be left out of server config so that it automatically uses same value as [publicPort](#publicPort).
It should only be used by users with specific port forwarding needs.

*was **gameHostBindPort** before 0.9.8.73*

### publicAddress[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=5 "Edit section: publicAddress") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=5 "Edit section: publicAddress")]

IP address registered in backend.
This should be set to the public IP address to which clients can connect in order to reach the server (either IP of the server itself or IP of the machine that will forward data to the server).
If the entry is missing or empty, then the public IP address will be automatically detected and used by the backend.

⚠

- This entry should generally be left out of server config to let the server automatically detect and use its public address used to reach it from internet.
- It is possible to use the "local" keyword to automatically detect the local address of the network card.
- IP**v6** is **not** supported by Arma Reforger.

*was **gameHostRegisterBindAddress** before 0.9.8.73*

### publicPort[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=6 "Edit section: publicPort") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=6 "Edit section: publicPort")]

number value, range 1..65535, default: 2001

UDP port registered in backend. If the server itself has a public IP address, this should be the same value as in [bindPort](#bindPort). Otherwise, this is the UDP port that is forwarded to the server.

*was **gameHostRegisterPort** before 0.9.8.73*

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_0.9.9 "Category:Arma Reforger/Version 0.9.9") [0.9.9](/wiki?title=Category:Arma_Reforger/Version_0.9.9&action=edit&redlink=1 "Category:Arma Reforger/Version 0.9.9 (page does not exist)")

### a2s

*0.9.9.31*

Steam Query protocol definition - see the [a2s](#a2s_2) section below.

ⓘ

Before [![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/22px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_0.9.9 "Category:Arma Reforger/Version 0.9.9") [0.9.9](/wiki?title=Category:Arma_Reforger/Version_0.9.9&action=edit&redlink=1 "Category:Arma Reforger/Version 0.9.9 (page does not exist)"), a2s settings were:

- a2sQueryEnabled - bool value, default: false
- steamQueryPort - number value, range 1..65535, default: 17777

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.0.0 "Category:Arma Reforger/Version 1.0.0") [1.0.0](/wiki?title=Category:Arma_Reforger/Version_1.0.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.0.0 (page does not exist)")

### rcon

The RCON Protocol is a UDP-based communication protocol that allows console commands to be issued to the server via a **R**emote **CON**sole, or RCON - see the [rcon](#rcon_2) section below.

### game[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=7 "Edit section: game") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=7 "Edit section: game")]

Define the server's settings - see the [game](#game_2) section below.

⚠

Only **one** scenario can be defined - Arma Reforger does **not** allow for mission rotation as of v0.9.8.

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_0.9.7 "Category:Arma Reforger/Version 0.9.7") [0.9.7](/wiki?title=Category:Arma_Reforger/Version_0.9.7&action=edit&redlink=1 "Category:Arma Reforger/Version 0.9.7 (page does not exist)")

### operating

Define various server settings - see the [operating](#operating_2) section below.

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_0.9.9 "Category:Arma Reforger/Version 0.9.9") [0.9.9](/wiki?title=Category:Arma_Reforger/Version_0.9.9&action=edit&redlink=1 "Category:Arma Reforger/Version 0.9.9 (page does not exist)")

## a2s

*0.9.9.31*

See [A2S server queries](https://developer.valvesoftware.com/wiki/Server_queries).

### address[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=8 "Edit section: address") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=8 "Edit section: address")]

required

IP address to which A2S socket will be bound. It can be used to restrict A2S queries to a particular network interface.

### port[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=9 "Edit section: port") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=9 "Edit section: port")]

number value, range 1..65535, default: 17777

Change Steam Query UDP port on which game listens to A2S requests

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.0.0 "Category:Arma Reforger/Version 1.0.0") [1.0.0](/wiki?title=Category:Arma_Reforger/Version_1.0.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.0.0 (page does not exist)")

## rcon

(Optional)

See [RCON protocol](https://www.battleye.com/downloads/BERConProtocol.txt).

### address[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=10 "Edit section: address") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=10 "Edit section: address")]

required

IP address to which the RCON socket will be bound. It can be used to restrict connection to a particular network interface.

### port[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=11 "Edit section: port") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=11 "Edit section: port")]

number value, range 1..65535, default 19999

RCON protocol port on which the game listens

### password[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=12 "Edit section: password") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=12 "Edit section: password")]

string

⚠

The password:

- is required for RCON to start
- does **not** support spaces
- must be at least **3** characters long

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.1.0 "Category:Arma Reforger/Version 1.1.0") [1.1.0](/wiki?title=Category:Arma_Reforger/Version_1.1.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.1.0 (page does not exist)")

### maxClients

number value, range 1..16, default 16

The maximum number of clients that can connect to RCON at the same time.

ⓘ

RCON lacks client disconnect support, so when a user exits the RCON client, the server maintains the connection for an additional 30 seconds before a timeout removes the user from the list.

### permission[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=13 "Edit section: permission") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=13 "Edit section: permission")]

string

Permission for all RCON clients.
Possible values:

| Key | Value |
| --- | --- |
| admin | The admin can perform any command. |
| monitor | The monitor can only perform commands which do not change the server's state. |

### blacklist[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=14 "Edit section: blacklist") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=14 "Edit section: blacklist")]

array value, default []

A list of commands excluded from execution.

### whitelist[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=15 "Edit section: whitelist") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=15 "Edit section: whitelist")]

array value, default []

If defined, it specifies the list of commands that can be executed, and no other command is allowed.

## game[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=16 "Edit section: game") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=16 "Edit section: game")]

### name[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=17 "Edit section: name") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=17 "Edit section: name")]

length 0..100 characters

### password[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=18 "Edit section: password") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=18 "Edit section: password")]

length 0..x characters

Password required to join the server.

### passwordAdmin[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=19 "Edit section: passwordAdmin") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=19 "Edit section: passwordAdmin")]

length: 0..x characters

Defines the server's admin password, allows a server administrator to login and control the server, to access this either open the chat input box by pressing `C` in the lobby or `Return ↵` in-game followed by: #login [the admin password]

⚠

This password does not support spaces in it.

*was root/passwordAdmin before 0.9.8.73*

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_0.9.9 "Category:Arma Reforger/Version 0.9.9") [0.9.9](/wiki?title=Category:Arma_Reforger/Version_0.9.9&action=edit&redlink=1 "Category:Arma Reforger/Version 0.9.9 (page does not exist)")

### admins

*0.9.9.31*

array value of **IdentityIds** and/or steamIds

List players as server admins - they can be checked in script using [BackendApi](enfusion://ScriptEditor/scripts/GameLib/generated/online/BackendApi.c;13).IsListedServerAdmin([int](enfusion://ScriptEditor/scripts/Core/generated/Types/int.c;12) playerId) regardless of their logged-in state.

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/22px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.0.0 "Category:Arma Reforger/Version 1.0.0") [1.0.0](/wiki?title=Category:Arma_Reforger/Version_1.0.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.0.0 (page does not exist)") Listed users can #login without a password.

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/22px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.3.0 "Category:Arma Reforger/Version 1.3.0") [1.3.0](/wiki?title=Category:Arma_Reforger/Version_1.3.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.3.0 (page does not exist)")

:   - list is now limited to only 20 unique IDs
    - listed admins can use priority queue when joining server
      - they will have their own queue which have priority before normal players when joining the server
      - priority queue works only with admins which are specified by **IdentityIds**
      - **admins specified by their SteamId are not currently supported for priority queue**

### scenarioId[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=20 "Edit section: scenarioId") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=20 "Edit section: scenarioId")]

The scenario's .conf file path is defined here.
See the [listScenarios](/wiki/Arma_Reforger:Startup_Parameters#listScenarios "Arma Reforger:Startup Parameters") startup parameter to list available scenarios and obtain their .conf file path.

:   [![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/22px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.6.0 "Category:Arma Reforger/Version 1.6.0") [1.6.0](/wiki?title=Category:Arma_Reforger/Version_1.6.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.6.0 (page does not exist)") Show Official Scenarios (1.6.0)

    ```
    --------------------------------------------------
    Official scenarios (31 entries)
    --------------------------------------------------
    {ECC61978EDCC2B5A}Missions/23_Campaign.conf (Conflict - Everon)
    {002AF7323E0129AF}Missions/Tutorial.conf (Training)
    {59AD59368755F41A}Missions/21_GM_Eden.conf (Game Master - Everon)
    {2BBBE828037C6F4B}Missions/22_GM_Arland.conf (Game Master - Arland)
    {F45C6C15D31252E6}Missions/27_GM_Cain.conf (Game Master - Kolguyev)
    {C700DB41F0C546E1}Missions/23_Campaign_NorthCentral.conf (Conflict - Northern Everon)
    {28802845ADA64D52}Missions/23_Campaign_SWCoast.conf (Conflict - Southern Everon)
    {94992A3D7CE4FF8A}Missions/23_Campaign_Western.conf (Conflict - Western Everon)
    {FDE33AFE2ED7875B}Missions/23_Campaign_Montignac.conf (Conflict - Montignac)
    {DAA03C6E6099D50F}Missions/24_CombatOps.conf (Combat Ops - Arland)
    {C41618FD18E9D714}Missions/23_Campaign_Arland.conf (Conflict - Arland)
    {DFAC5FABD11F2390}Missions/26_CombatOpsEveron.conf (Combat Ops - Everon)
    {3F2E005F43DBD2F8}Missions/CAH_Briars_Coast.conf (Capture & Hold - Briars)
    {F1A1BEA67132113E}Missions/CAH_Castle.conf (Capture & Hold - Montfort Castle)
    {589945FB9FA7B97D}Missions/CAH_Concrete_Plant.conf (Capture & Hold - Concrete Plant)
    {9405201CBD22A30C}Missions/CAH_Factory.conf (Capture & Hold - Almara Factory)
    {1CD06B409C6FAE56}Missions/CAH_Forest.conf (Capture & Hold - Simon's Wood)
    {7C491B1FCC0FF0E1}Missions/CAH_LeMoule.conf (Capture & Hold - Le Moule)
    {6EA2E454519E5869}Missions/CAH_Military_Base.conf (Capture & Hold - Camp Blake)
    {2B4183DF23E88249}Missions/CAH_Morton.conf (Capture & Hold - Morton)
    {C47A1A6245A13B26}Missions/SP01_ReginaV2.conf (Elimination)
    {0648CDB32D6B02B3}Missions/SP02_AirSupport.conf (Air Support)
    {0220741028718E7F}Missions/23_Campaign_HQC_Everon.conf (Conflict: HQ Commander - Everon)
    {68D1240A11492545}Missions/23_Campaign_HQC_Arland.conf (Conflict: HQ Commander - Arland)
    {BB5345C22DD2B655}Missions/23_Campaign_HQC_Cain.conf (Conflict: HQ Commander - Kolguyev)
    {10B8582BAD9F7040}Missions/Scenario01_Intro.conf (Operation Omega 01: Over The Hills And Far Away)
    {1D76AF6DC4DF0577}Missions/Scenario02_Steal.conf (Operation Omega 02: Radio Check)
    {D1647575BCEA5A05}Missions/Scenario03_Villa.conf (Operation Omega 03: Light In The Dark)
    {6D224A109B973DD8}Missions/Scenario04_Sabotage.conf (Operation Omega 04: Red Silence)
    {FA2AB0181129CB16}Missions/Scenario05_Hill.conf (Operation Omega 05: Cliffhanger)
    {CB347F2F10065C9C}Missions/CombatOpsCain.conf (Combat Ops - Kolguyev)
    ```

    [↑ Back to spoiler's top](#bikisp6a1bff9e29f36)
:   [![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/22px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.3.0 "Category:Arma Reforger/Version 1.3.0") [1.3.0](/wiki?title=Category:Arma_Reforger/Version_1.3.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.3.0 (page does not exist)") Show Official Scenarios (1.3.0)

    ```
    --------------------------------------------------
    Official scenarios (20 entries)
    --------------------------------------------------
    {ECC61978EDCC2B5A}Missions/23_Campaign.conf (Conflict - Everon)
    {59AD59368755F41A}Missions/21_GM_Eden.conf (Game Master - Everon)
    {002AF7323E0129AF}Missions/Tutorial.conf (Training)
    {2BBBE828037C6F4B}Missions/22_GM_Arland.conf (Game Master - Arland)
    {C700DB41F0C546E1}Missions/23_Campaign_NorthCentral.conf (Conflict - Northern Everon)
    {28802845ADA64D52}Missions/23_Campaign_SWCoast.conf (Conflict - Southern Everon)
    {94992A3D7CE4FF8A}Missions/23_Campaign_Western.conf (Conflict - Western Everon)
    {FDE33AFE2ED7875B}Missions/23_Campaign_Montignac.conf (Conflict - Montignac)
    {DAA03C6E6099D50F}Missions/24_CombatOps.conf (Combat Ops - Arland)
    {C41618FD18E9D714}Missions/23_Campaign_Arland.conf (Conflict - Arland)
    {DFAC5FABD11F2390}Missions/26_CombatOpsEveron.conf (Combat Ops - Everon)
    {3F2E005F43DBD2F8}Missions/CAH_Briars_Coast.conf (Capture & Hold - Briars)
    {F1A1BEA67132113E}Missions/CAH_Castle.conf (Capture & Hold - Montfort Castle)
    {589945FB9FA7B97D}Missions/CAH_Concrete_Plant.conf (Capture & Hold - Concrete Plant)
    {9405201CBD22A30C}Missions/CAH_Factory.conf (Capture & Hold - Almara Factory)
    {1CD06B409C6FAE56}Missions/CAH_Forest.conf (Capture & Hold - Simon's Wood)
    {7C491B1FCC0FF0E1}Missions/CAH_LeMoule.conf (Capture & Hold - Le Moule)
    {6EA2E454519E5869}Missions/CAH_Military_Base.conf (Capture & Hold - Camp Blake)
    {2B4183DF23E88249}Missions/CAH_Morton.conf (Capture & Hold - Morton)
    {C47A1A6245A13B26}Missions/SP01_ReginaV2.conf (Elimination)
    ```
:   [![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/22px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.2.1 "Category:Arma Reforger/Version 1.2.1") [1.2.1](/wiki?title=Category:Arma_Reforger/Version_1.2.1&action=edit&redlink=1 "Category:Arma Reforger/Version 1.2.1 (page does not exist)") Show Official Scenarios (1.2.1)

    ```
    : --------------------------------------------------
    : Official scenarios (17 entries)
    : --------------------------------------------------
    : {ECC61978EDCC2B5A}Missions/23_Campaign.conf (Conflict - Everon)
    : {59AD59368755F41A}Missions/21_GM_Eden.conf (Game Master - Everon)
    : {002AF7323E0129AF}Missions/Tutorial.conf (Training)
    : {2BBBE828037C6F4B}Missions/22_GM_Arland.conf (Game Master - Arland)
    : {C700DB41F0C546E1}Missions/23_Campaign_NorthCentral.conf (Conflict - Northern Everon)
    : {28802845ADA64D52}Missions/23_Campaign_SWCoast.conf (Conflict - Southern Everon)
    : {DAA03C6E6099D50F}Missions/24_CombatOps.conf (Combat Ops - Arland)
    : {C41618FD18E9D714}Missions/23_Campaign_Arland.conf (Conflict - Arland)
    : {DFAC5FABD11F2390}Missions/26_CombatOpsEveron.conf (Combat Ops - Everon)
    : {3F2E005F43DBD2F8}Missions/CAH_Briars_Coast.conf (Capture & Hold: Briars )
    : {F1A1BEA67132113E}Missions/CAH_Castle.conf (Capture & Hold: Montfort Castle)
    : {589945FB9FA7B97D}Missions/CAH_Concrete_Plant.conf (Capture & Hold: Concrete Plant)
    : {9405201CBD22A30C}Missions/CAH_Factory.conf (Capture & Hold: Almara Factory)
    : {1CD06B409C6FAE56}Missions/CAH_Forest.conf (Capture & Hold: Simon's Wood)
    : {7C491B1FCC0FF0E1}Missions/CAH_LeMoule.conf (Capture & Hold: Le Moule)
    : {6EA2E454519E5869}Missions/CAH_Military_Base.conf (Capture & Hold: Camp Blake)
    : {2B4183DF23E88249}Missions/CAH_Morton.conf (Capture & Hold: Morton)
    ```
:   [![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/22px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.1.0 "Category:Arma Reforger/Version 1.1.0") [1.1.0](/wiki?title=Category:Arma_Reforger/Version_1.1.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.1.0 (page does not exist)") Show Official Scenarios (1.1.0)

    ```
    : --------------------------------------------------
    : Official scenarios (9 entries)
    : --------------------------------------------------
    : {ECC61978EDCC2B5A}Missions/23_Campaign.conf (Conflict - Everon)
    : {59AD59368755F41A}Missions/21_GM_Eden.conf (Game Master - Everon)
    : {94FDA7451242150B}Missions/103_Arland_Tutorial.conf (Training)
    : {2BBBE828037C6F4B}Missions/22_GM_Arland.conf (Game Master - Arland)
    : {C700DB41F0C546E1}Missions/23_Campaign_NorthCentral.conf (Conflict - Northern Everon)
    : {28802845ADA64D52}Missions/23_Campaign_SWCoast.conf (Conflict - Southern Everon)
    : {DAA03C6E6099D50F}Missions/24_CombatOps.conf (Combat Ops - Arland)
    : {C41618FD18E9D714}Missions/23_Campaign_Arland.conf (Conflict - Arland)
    : {DFAC5FABD11F2390}Missions/26_CombatOpsEveron.conf (Combat Ops - Everon)
    ```

    ```
    : --------------------------------------------------
    : Workshop scenarios (8 entries)
    : --------------------------------------------------
    : {6EA2E454519E5869}Missions/CAH_Military_Base.conf
    : {7C491B1FCC0FF0E1}Missions/CAH_LeMoule.conf
    : {F1A1BEA67132113E}Missions/CAH_Castle.conf
    : {589945FB9FA7B97D}Missions/CAH_Concrete_Plant.conf
    : {2B4183DF23E88249}Missions/CAH_Morton.conf
    : {3F2E005F43DBD2F8}Missions/CAH_Briars_Coast.conf
    : {9405201CBD22A30C}Missions/CAH_Factory.conf
    : {1CD06B409C6FAE56}Missions/CAH_Forest.conf
    ```
:   [![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/22px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.0.0 "Category:Arma Reforger/Version 1.0.0") [1.0.0](/wiki?title=Category:Arma_Reforger/Version_1.0.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.0.0 (page does not exist)") Show Official Scenarios (1.0.0)

    ```
    : {ECC61978EDCC2B5A}Missions/23_Campaign.conf (Conflict - Everon)
    : {59AD59368755F41A}Missions/21_GM_Eden.conf (Game Master - Everon)
    : {94FDA7451242150B}Missions/103_Arland_Tutorial.conf (Training)
    : {2BBBE828037C6F4B}Missions/22_GM_Arland.conf (Game Master - Arland)
    : {C700DB41F0C546E1}Missions/23_Campaign_NorthCentral.conf (Conflict - Northern Everon)
    : {28802845ADA64D52}Missions/23_Campaign_SWCoast.conf (Conflict - Southern Everon)
    : {DAA03C6E6099D50F}Missions/24_CombatOps.conf (Combat Ops - Arland)
    : {C41618FD18E9D714}Missions/23_Campaign_Arland.conf (Conflict - Arland)
    ```
:   [![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/22px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_0.9.9 "Category:Arma Reforger/Version 0.9.9") [0.9.9](/wiki?title=Category:Arma_Reforger/Version_0.9.9&action=edit&redlink=1 "Category:Arma Reforger/Version 0.9.9 (page does not exist)") Show Official Scenarios (0.9.9)

    ```
    : {ECC61978EDCC2B5A}Missions/23_Campaign.conf (Conflict - Everon)
    : {59AD59368755F41A}Missions/21_GM_Eden.conf (Game Master - Everon)
    : {90F086877C27B6F6}Missions/99_Tutorial.conf (Tutorial)
    : {2BBBE828037C6F4B}Missions/22_GM_Arland.conf (Game Master - Arland)
    : {C700DB41F0C546E1}Missions/23_Campaign_NorthCentral.conf (Conflict - Northern Everon)
    : {28802845ADA64D52}Missions/23_Campaign_SWCoast.conf (Conflict - Southern Everon)
    : {DAA03C6E6099D50F}Missions/24_CombatOps.conf (Combat Ops - Arland)
    : {C41618FD18E9D714}Missions/23_Campaign_Arland.conf (Conflict - Arland)
    ```

### maxPlayers[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=21 "Edit section: maxPlayers") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=21 "Edit section: maxPlayers")]

number value, range 1..128, default: 64

Set the maximum amount of players on the server.

*was **playerCountLimit** before 0.9.8.73*

### visible[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=22 "Edit section: visible") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=22 "Edit section: visible")]

bool value, default: true (since 0.9.8.73)

Set the visibility of the server in the Server Browser.

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_0.9.9 "Category:Arma Reforger/Version 0.9.9") [0.9.9](/wiki?title=Category:Arma_Reforger/Version_0.9.9&action=edit&redlink=1 "Category:Arma Reforger/Version 0.9.9 (page does not exist)")

### crossPlatform

bool value, default: false

Accept all platforms if set to true, use [supportedPlatforms](#supportedPlatforms) if set to false.

ⓘ

It is recommended to use **crossPlatform** only, leaving [supportedPlatforms](#supportedPlatforms) undefined.

### supportedPlatforms[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=23 "Edit section: supportedPlatforms") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=23 "Edit section: supportedPlatforms")]

array value, default: ["PLATFORM\_PC"]

Define the platforms which the server accepts, allowing crossplay.
Possible values:

| Key | Value |
| --- | --- |
| PLATFORM\_PC | PC |
| PLATFORM\_XBL | Xbox console |
| PLATFORM\_PSN | PlayStation console (since Arma Reforger 1.2.1.169) |

⚠

It is recommended to use **[crossPlatform](#crossPlatform)** instead and leave this property undefined:

|  |  |  |
| --- | --- | --- |
| ``` crossPlatform = true; ``` | is equivalent to | ``` supportedPlatforms = ["PLATFORM_PC", "PLATFORM_XBL", "PLATFORM_PSN"]; ``` |
| ``` crossPlatform = false; ``` | ``` supportedPlatforms = ["PLATFORM_PC"]; ``` |

These two being the only valid combinations.

*was **supportedGameClientTypes** before 0.9.8.73*

### gameProperties[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=24 "Edit section: gameProperties") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=24 "Edit section: gameProperties")]

Define the scenario's settings - see the [gameProperties](#gameProperties_2) section below.

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.2.1 "Category:Arma Reforger/Version 1.2.1") [1.2.1](/wiki?title=Category:Arma_Reforger/Version_1.2.1&action=edit&redlink=1 "Category:Arma Reforger/Version 1.2.1 (page does not exist)")

### modsRequiredByDefault

bool value, default: true

Overrides default value for [required](#required) for all mods.

### mods[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=25 "Edit section: mods") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=25 "Edit section: mods")]

The list of mods required by the client that will automatically be downloaded and activated on join.
It can be created by activating wanted mods in the [Workshop](/wiki/Arma_Reforger:Workshop "Arma Reforger:Workshop") and then, via the **Mod manager**, result can be converted to ready to be used string after navigating to **JSON** tab and then clicking on the **Copy to clipboard** button.

[![armareforger-server-hosting-mod-list.png](/wikidata/images/thumb/8/86/armareforger-server-hosting-mod-list.png/906px-armareforger-server-hosting-mod-list.png)](/wiki/File:armareforger-server-hosting-mod-list.png)

#### modID[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=26 "Edit section: modID") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=26 "Edit section: modID")]

GUID of the mod. Single GUID can be obtained from Workshop webpage

[![armareforger-server-hosting-workshop-guid.png](/wikidata/images/c/c9/armareforger-server-hosting-workshop-guid.png)](/wiki/File:armareforger-server-hosting-workshop-guid.png)

Alternatively you can grab it from Workbench options when mod is running or directly from gproj file

[![armareforger-server-hosting-guid.png](/wikidata/images/8/8c/armareforger-server-hosting-guid.png)](/wiki/File:armareforger-server-hosting-guid.png)

#### name[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=27 "Edit section: name") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=27 "Edit section: name")]

This parameter does not do anything and is only used as sort of comment, with human readable name of the mod.

#### version[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=28 "Edit section: version") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=28 "Edit section: version")]

ⓘ

The version mod parameter is optional. If it missing, the latest mod version will be used.

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.2.1 "Category:Arma Reforger/Version 1.2.1") [1.2.1](/wiki?title=Category:Arma_Reforger/Version_1.2.1&action=edit&redlink=1 "Category:Arma Reforger/Version 1.2.1 (page does not exist)")

#### required

bool value, default: true or value of [modsRequiredByDefault](#modsRequiredByDefault)

Is optional parameter to specify if this addon is required for server to start.
If set to false then addon will be automatically removed from list with warning in logs if it cannot be for some reason downloaded from the Workshop.

## gameProperties[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=29 "Edit section: gameProperties") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=29 "Edit section: gameProperties")]

### serverMaxViewDistance[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=30 "Edit section: serverMaxViewDistance") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=30 "Edit section: serverMaxViewDistance")]

number value, range 500..10000, default: 1600

### serverMinGrassDistance[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=31 "Edit section: serverMinGrassDistance") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=31 "Edit section: serverMinGrassDistance")]

number value, range 0 / 50..150, default: 0

Minimum grass distance in meters. If set to 0 no distance is forced upon clients.

### fastValidation[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=32 "Edit section: fastValidation") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=32 "Edit section: fastValidation")]

bool value, default: true (since 0.9.6)

Validation of map entities and components loaded on client when it joins, ensuring things match with initial server state.

- true (enabled) - minimum information required to make sure data matches is exchanged between client. When a mismatch occurs, no additional information will be available for determining where client and server states start to differ. All servers that expect clients to connect over internet should have fast validation enabled.
- false (disabled) - extra data for every replicated entity and component in the map will be transferred when new client connects to the server. When a mismatch occurs, it is possible to point at particular entity or component where things start to differ. When developing locally (ie. both server and client run on the same machine), it is fine to disable fast validation to more easily pin point source of the problem.

⚠

**Always** set this value to true for a public server!

### networkViewDistance[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=33 "Edit section: networkViewDistance") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=33 "Edit section: networkViewDistance")]

number value, range 500..5000, default: 1500

Maximum network streaming range of replicated entities.

### battlEye[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=34 "Edit section: battlEye") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=34 "Edit section: battlEye")]

bool value, default: true (since 0.9.8.73)

true to enable BattlEye, false to disable it.

### disableThirdPerson[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=35 "Edit section: disableThirdPerson") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=35 "Edit section: disableThirdPerson")]

bool value, default: false

Force clients to use the first-person view.

### VONDisableUI[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=36 "Edit section: VONDisableUI") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=36 "Edit section: VONDisableUI")]

bool value, default: false

Force clients to not have VON (Voice Over Network) UI.

### VONDisableDirectSpeechUI[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=37 "Edit section: VONDisableDirectSpeechUI") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=37 "Edit section: VONDisableDirectSpeechUI")]

bool value, default: false

Force clients to not have VON (Voice Over Network) Direct Speech UI.

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.0.0 "Category:Arma Reforger/Version 1.0.0") [1.0.0](/wiki?title=Category:Arma_Reforger/Version_1.0.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.0.0 (page does not exist)")

### VONCanTransmitCrossFaction

bool value, default: false

Option to allow players to transmit on other factions radios. true is allow to communicate, false is listen-only

### missionHeader[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=38 "Edit section: missionHeader") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=38 "Edit section: missionHeader")]

This property overwrites the scenario's header, allowing to e.g change the scenario's displayed name:

```
"missionHeader": {
	"m_sName": "My Very Own Hosted Conflict",
	"m_sDetails": "If you teamkill or commit war crimes, you WILL be banned!",
	"m_iStartingHours": 7,
	"m_iStartingMinutes": 30,
	"m_bRandomStartingWeather": true
}
```

Generic scenario properties (from [SCR\_MissionHeader](enfusion://ScriptEditor/scripts/Game/Mission/SCR_MissionHeader.c;1)):
Show text

|  |  |
| --- | --- |
| ``` "missionHeader": { 	"m_sName": "My Very Own Hosted Conflict", 	"m_sAuthor": "Bohemia and I", 	"m_sDescription": "Play Conflict here!", 	"m_sDetails": "Here are the rules: have fun!", 	"m_iPlayerCount": 64, 	"m_bIsSavingEnabled": true, 	"m_sSaveFileName": "MyConflict_Save", 	"m_sBriefingConfig": "{0123456789ABCDEF}Configs/MyBriefingConfig.conf", 	"m_bOverrideScenarioTimeAndWeather": true, 	"m_iStartingHours": 7, 	"m_iStartingMinutes": 30, 	"m_bRandomStartingDaytime": false, 	"m_fDayTimeAcceleration": 6, 	"m_fNightTimeAcceleration": 12, 	"m_bRandomStartingWeather": true, 	"m_bRandomWeatherChanges": false, 	"m_fXpMultiplier": 10, 	"m_bMapMarkerEnableDeleteByAnyone": false, 	"m_iMapMarkerLimitPerPlayer": 99 } ``` | - **m\_sName**: scenario name - **m\_sAuthor**: scenario author - **m\_sDescription**: brief description of this scenario's purpose - **m\_sDetails**: detailed description of this scenario (i.e. rules) - **m\_iPlayerCount**: The count of players for this scenario - **m\_bIsSavingEnabled**: enable saving scenario state - **m\_sSaveFileName**: name of save file for this scenario; when undefined, the name of associated world file will be used - **m\_sBriefingConfig**: configuration file for briefing screen - **m\_bOverrideScenarioTimeAndWeather**: if the scenario allows it, its daytime and weather will use values from this header - **m\_iStartingHours**: starting time of day (hours) - **m\_iStartingMinutes**: starting time of day (minutes) - **m\_bRandomStartingDaytime**: randomise the start time (overrides above parameters) - **m\_fDayTimeAcceleration**: time acceleration during the day (1 = 100%, 2 = 200% etc) - **m\_fNightTimeAcceleration**: time acceleration during the night (1 = 100%, 2 = 200% etc) - **m\_bRandomStartingWeather**: the weather is randomised on start - **m\_bRandomWeatherChanges**: weather can change during gameplay - **m\_fXpMultiplier**: player XP multiplier (when enabled in gamemode; 1 for default) - **m\_bMapMarkerEnableDeleteByAnyone**: determines whether map markers can be deleted only by player who placed them or by anyone within faction - **m\_iMapMarkerLimitPerPlayer**: how many map markers per player can exist at a time |

[↑ Back to spoiler's top](#bikisp6a1bff9f5cbdb)

Conflict-specific properties (from [SCR\_MissionHeaderCampaign](enfusion://ScriptEditor/scripts/Game/Mission/SCR_MissionHeaderCampaign.c;1)):
Show text

|  |  |
| --- | --- |
| ``` "missionHeader": { 	"m_iControlPointsCap": 42, 	"m_fVictoryTimeout": -1, 	"m_iStartingHQSupplies": 9001, 	"m_iMinimumBaseSupplies": 500, 	"m_iMaximumBaseSupplies": 10000, 	"m_bCustomBaseWhitelist": true, 	"m_bIgnoreMinimumVehicleRank": true, 	"m_fSupplyOffloadAssistanceReward": 0.25, 	"m_aCampaignCustomBaseList": { 		{ 			"m_sBaseName": "MajorBaseMorton", 			"m_bIsControlPoint": false, 			"m_bCanBeHQ": true, 			"m_bDisableWhenUnusedAsHQ": true, 			"m_fRadioRange": 500 		} 	} } ``` | - **m\_iControlPointsCap**: how many control points are required to win (-1 = default) - **m\_fVictoryTimeout**: how long a faction needs to hold a control point, in seconds (-1 = default) - **m\_iStartingHQSupplies**: how much supplies should the main HQ start with (-1 = default) - **m\_iMinimumBaseSupplies**: minimum starting amount of supplies in small bases (-1 = default) - **m\_iMaximumBaseSupplies**: maximum starting amount of supplies in small bases (-1 = default) - **m\_bCustomBaseWhitelist**: use custom base list whitelist instead of blacklist - **m\_bIgnoreMinimumVehicleRank**: disable rank requirements for vehicle spawning - **m\_fSupplyOffloadAssistanceReward**: fraction of XP awarded to players unloading supplies which they have not loaded themselves (-1 = default) - **m\_aCampaignCustomBaseList**: array of custom bases (from [SCR\_CampaignCustomBase](enfusion://ScriptEditor/scripts/Game/Components/Locations/SCR_CampaignMilitaryBaseComponent.c;2714)):   - **m\_sBaseName**: base entity name as set up in World Editor   - **m\_bIsControlPoint**: whether or not the base is a control point   - **m\_bCanBeHQ**: whether or not the base can be a headquarter   - **m\_bDisableWhenUnusedAsHQ**: whether or not the base is disabled if not used as HQ   - **m\_fRadioRange**: base's radio range in metres (-1 = default) |

[↑ Back to spoiler's top](#bikisp6a1bff9f8abbb)

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.6.0 "Category:Arma Reforger/Version 1.6.0") [1.6.0](/wiki?title=Category:Arma_Reforger/Version_1.6.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.6.0 (page does not exist)")

### persistence

Configuration values for the [Persistence System](/wiki/Arma_Reforger:Persistence_System "Arma Reforger:Persistence System") that can be optionally defined. The system is set up to automatically work for most use cases by default.
Show possible options

```
"persistence": {
    "autoSaveInterval": 10,
    "saveRetention": 10,
    "loadSessionSave": true,
    "keepSessionSave": false,
    "hiveId": 0,
    "databases": {
        "MyCustomDb": {
            "preset": "{F128A4430B15EE4F}Configs/Systems/Persistence/Database/JsonSaveGame.conf"
        }
    },
    "storages": {
        "session": {
            "database": "MyCustomDb"
        }
    }
}
```

To disable persistence entirely, set the enabled save types in game->gameProperties->missionHeader to 0 as shown below:

```
"missionHeader": {
    "m_eSaveTypes": 0
}
```

#### autoSaveInterval[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=39 "Edit section: autoSaveInterval") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=39 "Edit section: autoSaveInterval")]

number value, range 0..60, default: 10

Interval (in minutes) at which automatic saves are created, if possible for the current mission. Disabled if set to 0.

#### saveRetention[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=40 "Edit section: saveRetention") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=40 "Edit section: saveRetention")]

number value, range 1..128, default: 10

Number of save points to keep for the current mission.

#### loadSessionSave[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=41 "Edit section: loadSessionSave") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=41 "Edit section: loadSessionSave")]

boolean value, true/false, default: true

Automatically load the latest available save point on first startup.

#### keepSessionSave[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=42 "Edit section: keepSessionSave") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=42 "Edit section: keepSessionSave")]

boolean value, true/false, default: false

Keep the playthrough save points after the mission is finished.

#### hiveId[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=43 "Edit section: hiveId") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=43 "Edit section: hiveId")]

number value, range 0..16383, default: 0

Number identifying each hive (server) when they share the same persistence database. Used for separating UUIDs for save game data.

#### databases[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=44 "Edit section: databases") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=44 "Edit section: databases")]

(Case-insensitive) named objects representing an override of the system configuration provided databases or the addition of a new one. Optional child properties:

- "preset" - refers to database config presets that are defined in game/mod data. Full resource name.
- "options" - a complex object which corresponds to the individual database type of the preset. Usually, a key-value collection

#### storages[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=45 "Edit section: storages") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=45 "Edit section: storages")]

(Case-insensitive) named objects representing an override of the system configuration-provided storages. Optional child properties:

- "database" - changes the storage to use a different database than the default configured. Must match the name given in the databases object or "main".

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_0.9.7 "Category:Arma Reforger/Version 0.9.7") [0.9.7](/wiki?title=Category:Arma_Reforger/Version_0.9.7&action=edit&redlink=1 "Category:Arma Reforger/Version 0.9.7 (page does not exist)")

## operating

### lobbyPlayerSynchronise[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=46 "Edit section: lobbyPlayerSynchronise") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=46 "Edit section: lobbyPlayerSynchronise")]

bool value, default: true (since 0.9.8.73)

If enabled, the list of player identities present on server is sent to the GameAPI along with the server's heartbeat.

ⓘ

This setting fixes the discrepancy between the real and reported number of players on the server.

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_0.9.8 "Category:Arma Reforger/Version 0.9.8") [0.9.8](/wiki?title=Category:Arma_Reforger/Version_0.9.8&action=edit&redlink=1 "Category:Arma Reforger/Version 0.9.8 (page does not exist)")

### disableCrashReporter

bool value, default: false

If enabled, the automatic server-side [Crash Report](/wiki/Arma_Reforger:Crash_Report "Arma Reforger:Crash Report") is disabled. Has the same effect as [-disableCrashReporter](/wiki/Arma_Reforger:Startup_Parameters#disableCrashReporter "Arma Reforger:Startup Parameters").

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.0.0 "Category:Arma Reforger/Version 1.0.0") [1.0.0](/wiki?title=Category:Arma_Reforger/Version_1.0.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.0.0 (page does not exist)")

### disableNavmeshStreaming

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/22px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.0.0 "Category:Arma Reforger/Version 1.0.0") [1.0.0](/wiki?title=Category:Arma_Reforger/Version_1.0.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.0.0 (page does not exist)") bool value, default: false  
[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/22px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.2.0 "Category:Arma Reforger/Version 1.2.0") [1.2.0](/wiki?title=Category:Arma_Reforger/Version_1.2.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.2.0 (page does not exist)") array value, default undefined

A disabled navmesh streaming loads the entire navmesh in memory - this provides slightly better server performance and reaction times of moving AIs at the cost of a bigger memory footprint (up to hundreds of MB depending on the terrain) - see [2024-07-07 Modding Update](https://reforger.armaplatform.com/news/modding-update-june-7-2024)

- [![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/22px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.0.0 "Category:Arma Reforger/Version 1.0.0") [1.0.0](/wiki?title=Category:Arma_Reforger/Version_1.0.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.0.0 (page does not exist)") If set to true, navmesh streaming is disabled on all navmesh components
- [![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/22px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.2.0 "Category:Arma Reforger/Version 1.2.0") [1.2.0](/wiki?title=Category:Arma_Reforger/Version_1.2.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.2.0 (page does not exist)") If not provided, no navmesh streaming is disabled; if provided empty, streaming of all navmeshes is disabled; if filled, only disables provided navmeshes's streaming

*was **bool** before 1.2.0*

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_0.9.8 "Category:Arma Reforger/Version 0.9.8") [0.9.8](/wiki?title=Category:Arma_Reforger/Version_0.9.8&action=edit&redlink=1 "Category:Arma Reforger/Version 0.9.8 (page does not exist)")

### disableServerShutdown

bool value, default: false

If enabled, the server will not automatically shutdown if connection to backend is lost.
Related to room requests errors - other causes like corrupted config will still shutdown the server.

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.1.0 "Category:Arma Reforger/Version 1.1.0") [1.1.0](/wiki?title=Category:Arma_Reforger/Version_1.1.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.1.0 (page does not exist)")

### disableAI

bool value, default: false

If enabled, the server will prevent initialization and ticking of AIWorld and its components.
Will completely disable AI functionality on the server.

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_0.9.8 "Category:Arma Reforger/Version 0.9.8") [0.9.8](/wiki?title=Category:Arma_Reforger/Version_0.9.8&action=edit&redlink=1 "Category:Arma Reforger/Version 0.9.8 (page does not exist)")

### playerSaveTime

number value, default: 120

Default period in seconds for saving players for both Online and Local storage (player save can still be requested on demand).

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_0.9.8 "Category:Arma Reforger/Version 0.9.8") [0.9.8](/wiki?title=Category:Arma_Reforger/Version_0.9.8&action=edit&redlink=1 "Category:Arma Reforger/Version 0.9.8 (page does not exist)")

### aiLimit

*0.9.8.64*

number value, default: -1

Sets the top limit of AIs. No system will be able to spawn any AIs when this ceiling is reached (through aiWorld.CanAICharacterBeAdded())

A negative number is not considered as valid value and is then ignored - limit is not applied.

This param is overridden by the [-aiLimit](/wiki/Arma_Reforger:Startup_Parameters#AILimit "Arma Reforger:Startup Parameters") startup parameter.

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_0.9.9 "Category:Arma Reforger/Version 0.9.9") [0.9.9](/wiki?title=Category:Arma_Reforger/Version_0.9.9&action=edit&redlink=1 "Category:Arma Reforger/Version 0.9.9 (page does not exist)")

### slotReservationTimeout

*0.9.9.31*

number value, range 5..300, default 60

Sets the duration (in seconds) for how long will the backend and server reserve a slot for kicked player.
It is considered disabled when set to the minimal value, the value being the same as for a normal disconnect.

It can be used in scripts *via* Game Mode events:

OnPlayerAuditTimeouted([int](enfusion://ScriptEditor/scripts/Core/generated/Types/int.c;12) iPlayerID) // invoked when player did not reconnect in time
OnPlayerAuditRevived([int](enfusion://ScriptEditor/scripts/Core/generated/Types/int.c;12) iPlayerID) // invoked when player successfully reconnected in time

ⓘ

Reservation works only for **replication** kicks.

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.2.1 "Category:Arma Reforger/Version 1.2.1") [1.2.1](/wiki?title=Category:Arma_Reforger/Version_1.2.1&action=edit&redlink=1 "Category:Arma Reforger/Version 1.2.1 (page does not exist)")

### joinQueue

*1.2.1.66*

Configuration for player join queue to the server

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.2.1 "Category:Arma Reforger/Version 1.2.1") [1.2.1](/wiki?title=Category:Arma_Reforger/Version_1.2.1&action=edit&redlink=1 "Category:Arma Reforger/Version 1.2.1 (page does not exist)")

#### maxSize

number value, range 0..50, default 0 (disabled)

Sets maximum size of how many people can be at one time in join queue to the server.

## Template[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=47 "Edit section: Template") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=47 "Edit section: Template")]

```
{
	"bindAddress": "",
	"bindPort": 0,
	"publicAddress": "",
	"publicPort": 0,
	"a2s": {
		"address": "",
		"port": 0
	},
	"rcon": {
		"address": "",
		"port": 0,
		"password": "",
		"permission": "monitor",
		"blacklist": [],
		"whitelist": []
	},
	"game": {
		"name": "",
		"password": "",
		"passwordAdmin": "",
		"admins" : [],
		"scenarioId": "",
		"maxPlayers": 0,
		"visible": true,
		"gameProperties": {
			"serverMaxViewDistance": 1600,
			"serverMinGrassDistance": 0,
			"networkViewDistance": 1500,
			"disableThirdPerson": false,
			"fastValidation": true,
			"battlEye": true,
			"VONDisableUI": false,
			"VONDisableDirectSpeechUI": false,
			"VONCanTransmitCrossFaction": false
		},
		"mods": []
	}
}
```

## Example[[edit](/wiki?title=Arma_Reforger:Server_Config&veaction=edit&section=48 "Edit section: Example") | [edit source](/wiki?title=Arma_Reforger:Server_Config&action=edit&section=48 "Edit section: Example")]

Show Example Configuration

```
{
	"bindAddress": "0.0.0.0",
	"bindPort": 2001,
	"publicAddress": "192.168.9.10",
	"publicPort": 2001,
	"a2s": {
		"address": "192.168.9.10",
		"port": 17777
	},
	"rcon": {
		"address": "192.168.9.10",
		"port": 19999,
		"password": "changeme_withoutspaces",
		"permission": "monitor",
		"blacklist": [],
		"whitelist": []
	},
	"game": {
		"name": "Server Name - Mission Name",
		"password": "",
		"passwordAdmin": "changeme",
		"admins" : [
			"76561198200329058"
		],
		"scenarioId": "{ECC61978EDCC2B5A}Missions/23_Campaign.conf",
		"maxPlayers": 32,
		"visible": true,
		"crossPlatform": true,
		"supportedPlatforms": [
			"PLATFORM_PC",
			"PLATFORM_XBL"
		],
		"gameProperties": {
			"serverMaxViewDistance": 2500,
			"serverMinGrassDistance": 50,
			"networkViewDistance": 1000,
			"disableThirdPerson": true,
			"fastValidation": true,
			"battlEye": true,
			"VONDisableUI": true,
			"VONDisableDirectSpeechUI": true,
			"missionHeader": {
				"m_iPlayerCount": 40,
				"m_eEditableGameFlags": 6,
				"m_eDefaultGameFlags": 6,
				"other": "values"
			}
		},
		"mods": [
			{
				"modId": "59727DAE364DEADB",
				"name": "WeaponSwitching",
				"version": "1.0.1"
			},
			{
				"modId": "59727DAE32981C7D",
				"name": "Explosive Goats beta",
				"version": "0.5.42"
			}
		]
	},
	"operating": {
		"lobbyPlayerSynchronise": true,
		"joinQueue" : {
			"maxSize" : 12
		},
		"disableNavmeshStreaming": [
			"Soldiers",
			"BTRlike"
		]
	}
}
```

[↑ Back to spoiler's top](#bikisp6a1bffa06c71d)