---
game: arma3
slug: arma3-basic-config
file: basic.cfg
source: https://community.bistudio.com/wiki/Arma_3:_Basic_Server_Config_File
scraped: 2026-05-31T11:45:19Z
---

- [1 Performance Tuning Options](#Performance_Tuning_Options)
- [2 Example Configuration File](#Example_Configuration_File)
- [3 Notes and Comments](#Notes_and_Comments)
  - [3.1 Related comments by Suma](#Related_comments_by_Suma)

| Arma 3 Server Configuration Overview | | | | |
| --- | --- | --- | --- | --- |
| Setup | [Arma 3: Dedicated Server](/wiki/Arma_3:_Dedicated_Server "Arma 3: Dedicated Server") |
| Files | [Arma 3: Server Config File](/wiki/Arma_3:_Server_Config_File "Arma 3: Server Config File") ● Arma 3: Basic Server Config File ● [Arma 3: Server Profile](/wiki/Arma_3:_Server_Profile "Arma 3: Server Profile") |
| Other | [Multiplayer Server Commands](/wiki/Multiplayer_Server_Commands "Multiplayer Server Commands") ● [Arma 3: Mission voting](/wiki/Arma_3:_Mission_voting "Arma 3: Mission voting") ● [Arma 3: Headless Client](/wiki/Arma_3:_Headless_Client "Arma 3: Headless Client") ● [BattlEye](/wiki/BattlEye "BattlEye") |

[![Arma 2: Operation Arrowhead](/wikidata/images/thumb/5/5b/A2_OA_Logo.png/48px-A2_OA_Logo.png)](/wiki/Category:Arma_2:_Operation_Arrowhead "Arma 2: Operation Arrowhead")

This page is about **Arma 3** basic server configuration. For previous titles, see [Arma 2: Basic Server Config File](/wiki/Arma_2:_Basic_Server_Config_File "Arma 2: Basic Server Config File").

ⓘ

Some config entries or script parameters support receiving numbers as a string. These string formats are Case-Sensitive! See [String Number Formats](/wiki/Number#String_Number_Formats "Number")

This article deals with the **basic.cfg**, the name means nothing, and can be called anything. The real name is determined by the [-cfg](/wiki/Arma_3:_Startup_Parameters "Arma 3: Startup Parameters") command line option when launching the dedicated server or the game executable - in other words **it also works for clients**. When you do not provide a name, the default Arma3.cfg file is loaded located in the user profile folder.

In this configuration file you should configure your server's connectivity, mainly for performance tuning.

## Performance Tuning Options[[edit source](/wiki?title=Arma_3:_Basic_Server_Config_File&action=edit&section=1 "Edit section: Performance Tuning Options")]

There are also some parameters that can be used to fine-tune network performance. You can add following entries to arma.cfg (the main Armed Assault configuration file)

| Parameter | Default | Description |
| --- | --- | --- |
| MaxMsgSend = 128 | 128 | Maximum number of packets (aggregate messages) that can be sent in one simulation cycle ("frame"). Increasing this value can decrease lag on high upload bandwidth servers. |
| MaxSizeGuaranteed = 512 | 512 | Maximum size (payload) of guaranteed packet in bytes (without headers). Small messages are packed to larger packets (aggregate messages). Guaranteed packets (aggregate messages) are used for non-repetitive events like shooting. |
| MaxSizeNonguaranteed = 256 | 256 | Maximum size (payload) of non-guaranteed packet in bytes (without headers). Small messages are packed to larger packets (aggregate messages). Non-guaranteed packets (aggregate messages) are used for repetitive updates like soldier or vehicle position. Increasing this value may improve bandwidth requirement, but it may increase lag. |
| MinBandwidth = 131072 | 131072 | Bandwidth the server is guaranteed to have (in bps). This value helps server to estimate bandwidth available. Increasing it to too optimistic values can increase lag and CPU load, as too many messages will be sent but discarded. |
| MaxBandwidth = 200000 | UNKNOWN | Bandwidth the server is guaranteed to never have (in bps). This value helps the server to estimate bandwidth available. |
| MinErrorToSend = 0.001 | 0.001 | Minimal error to send updates across network. Using a smaller value can make units observed by binoculars or sniper rifle to move smoother at the trade off of increased network traffic.  - Example: A unit 1km (distance d = 1km) from the player with MinErrorToSend = 0.001 (METS = 0.001) would send an update when that unit moves 50m (error value E = 50). - Formula: d = sqrt[(20E)/METS] ; 1000 = sqrt[(20E)/0.001] -> E = 50 - In reality, other factors about the object are taken into effect as well according to a weighted scale. - When the error value (E) between the master copy of an object (whoever "owns" the object) and a client's perceived/simulated copy of the same object >= MinErrorToSend (METS), a network update message will be sent for it. |
| MinErrorToSendNear = 0.01 | 0.01 | Minimal error to send updates across network for near units. Using larger value can reduce traffic sent for near units. Used to control client to server traffic as well.  - When using MinErrorToSend alone, for small values of distance (d) the frequency of high errors (E) would cause excessive network messages that are not necessary but could negatively impact FPS. - Too large of a value for METSN can prevent timely desirable network update messages which can result in units appearing to "warp"   [Detailed explanation of METS and METSN by Suma](http://forums.bistudio.com/showthread.php?125396-Arma-2-OA-beta-build-84984&p=2103305&highlight=minerrortosend#post2103305) |
| Networking Tuning Options | | |
| ``` class sockets { 	maxPacketSize = 1400; }; ``` | 1400 | Maximal size of packet sent over network. This **can be set for both** client-to-server and server-to-client(s) independently! ⚠  Only use in the case your router or ISP enforces lower packet size and you have connectivity issues with the game. Desync might happen if used **MaxSizeGuaranteed/MaxSizeNonguaranteed values over the maxPacketSize**. |
| MaxCustomFileSize = 1024 | UNKNOWN | Users with custom face or custom sound larger than this size are kicked when trying to connect. |

## Example Configuration File[[edit source](/wiki?title=Arma_3:_Basic_Server_Config_File&action=edit&section=2 "Edit section: Example Configuration File")]

```
MinBandwidth = 131072;			// Bandwidth the server is guaranteed to have (in bps). This value helps server to estimate bandwidth available. Increasing it to too optimistic values can increase lag and CPU load, as too many messages will be sent but discarded. Default: 131072
MaxBandwidth = 10000000000;		// Bandwidth the server is guaranteed to never have. This value helps the server to estimate bandwidth available.

MaxMsgSend = 128;				// Maximum number of messages that can be sent in one simulation cycle. Increasing this value can decrease lag on high upload bandwidth servers. Default: 128
MaxSizeGuaranteed = 512;		// Maximum size of guaranteed packet in bytes (without headers). Small messages are packed to larger frames. Guaranteed messages are used for non-repetitive events like shooting. Default: 512
MaxSizeNonguaranteed = 256;		// Maximum size of non-guaranteed packet in bytes (without headers). Non-guaranteed messages are used for repetitive updates like soldier or vehicle position. Increasing this value may improve bandwidth requirement, but it may increase lag. Default: 256

MinErrorToSend = 0.001;			// Minimal error to send updates across network. Using a smaller value can make units observed by binoculars or sniper rifle to move smoother. Default: 0.001
MinErrorToSendNear = 0.01;		// Minimal error to send updates across network for near units. Using larger value can reduce traffic sent for near units. Used to control client to server traffic as well. Default: 0.01

MaxCustomFileSize = 0;			// (bytes) Users with custom face or custom sound larger than this size are kicked when trying to connect.
```

## Notes and Comments[[edit source](/wiki?title=Arma_3:_Basic_Server_Config_File&action=edit&section=3 "Edit section: Notes and Comments")]

The greatest level of optimization can be achieved by setting the MaxMsgSend and MinBandwidth parameters. For a server with 1024 kbps we recommend the following values:

```
MaxMsgSend = 256;
MinBandwidth = 768000;
```

While connected to the dedicated server, you can use the [admin command](/wiki/Multiplayer_Server_Commands "Multiplayer Server Commands") #monitor to monitor server resource usage. (You have to be logged in as or voted as game admin to do this.) The server never runs at more than 50 fps. When running slower, it always uses all available CPU processing power to maintain the smoothest possible gameplay. When running at less than 20 fps, you can consider the server overloaded – the mission currently played is probably too complex for given server. If you see the server is not using bandwidth that it could use, you can try increasing values *MaxMsgSend* and *MinBandwidth*.

---

Official Response to the meaning of a Simulation Cycle

«

« "Simulation cycle" is what makes "frame" in a normal game.

However, as there is no rendering on server, we cannot talk
about frames, and we talk about simulation cycles instead.
One cycle break down approximately to:

```
simulate all units AI
simulate all units movement including collisions and physics
detect visibility between units
simulate scripts and FSMs
receive network updates about remote entities
```

send network updates about what has changed to the server (if on client) or to other clients (if on server) » – BI Forums ([source](http://forums.bistudio.com/showthread.php?p=1363400))

### Related comments by Suma[[edit source](/wiki?title=Arma_3:_Basic_Server_Config_File&action=edit&section=4 "Edit section: Related comments by Suma")]

Does a 10× times lower MinErrorToSend mean also 10× times the traffic from the server?
:   In extreme case (provided you have enough bandwidth available) it might mean 10× more traffic. I think it should be easy to measure the effect, using #monitor command or external tools.

Do lower MinErrorToSend take more CPU processing on the server?
:   Yes, some more processing, as there are more messages to handle, but I do not expect anything major.

Do basic.cfg settings on clients also have an effect? Like for the player entity itself or the AI under his command (AI local to the player)?
:   Yes, it affects what client sends to a server as well, but in a different way (the error computation is different, it is not considering any camera position).

Is that correct that every message contains motion update (or other update) ONLY FOR ONE object? So when we have 1000 AIs near player, and we wish to have update 50× per second for all AIs, we must set MaxMsgSend to higher than 1000?
:   The aggregate message contains multiple logical messages, which are related to multiple objects.

**Related comment by Suma**

- MinErrorToSendNear is used differently from MinErrorToSend.
- When computing the error for MinErrorToSend, the error is divided by (distance\_in\_m/20m)^2.

Because of this, when MinErrorToSend is small enough so that distant units move smooth,the near units move "supersmooth" (the updates are sent even when the movement is so smooth, it makes no gameplay difference).

- MinErrorToSendNear gives a way to give another absolute (not distance dependent) limit ("never send update smaller than 1 cm, even if MinErrorToSend would allow it to be sent").