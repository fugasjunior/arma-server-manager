---
game: arma3
slug: arma3-server-profile
file: server.armaprofile
source: https://community.bistudio.com/wiki/Arma_3:_Server_Profile
scraped: 2026-05-31T11:45:19Z
---

- [1 Location](#Location)
  - [1.1 Windows](#Windows)
  - [1.2 Linux](#Linux)
- [2 Example Configuration File](#Example_Configuration_File)
  - [2.1 Server Difficulty Example](#Server_Difficulty_Example)

| Arma 3 Server Configuration Overview | | | | |
| --- | --- | --- | --- | --- |
| Setup | [Arma 3: Dedicated Server](/wiki/Arma_3:_Dedicated_Server "Arma 3: Dedicated Server") |
| Files | [Arma 3: Server Config File](/wiki/Arma_3:_Server_Config_File "Arma 3: Server Config File") ● [Arma 3: Basic Server Config File](/wiki/Arma_3:_Basic_Server_Config_File "Arma 3: Basic Server Config File") ● Arma 3: Server Profile |
| Other | [Multiplayer Server Commands](/wiki/Multiplayer_Server_Commands "Multiplayer Server Commands") ● [Arma 3: Mission voting](/wiki/Arma_3:_Mission_voting "Arma 3: Mission voting") ● [Arma 3: Headless Client](/wiki/Arma_3:_Headless_Client "Arma 3: Headless Client") ● [BattlEye](/wiki/BattlEye "BattlEye") |

This article is about the **server.Arma3Profile**.

## Location[[edit source](/wiki?title=Arma_3:_Server_Profile&action=edit&section=1 "Edit section: Location")]

### Windows[[edit source](/wiki?title=Arma_3:_Server_Profile&action=edit&section=2 "Edit section: Windows")]

The default profile named after the system user can be found at: C:\Users\%UserName%\Documents\Arma 3

User created profiles with a different naming are found at: C:\Users\%UserName%\Documents\Arma 3 - Other Profiles

**Profile name:** You can define the profile to be loaded with the [-name](/wiki/Arma_3:_Startup_Parameters "Arma 3: Startup Parameters") parameter.

**Custom Location:** You can specify the location by using the [-profiles](/wiki/Arma_3:_Startup_Parameters "Arma 3: Startup Parameters") parameter.

### Linux[[edit source](/wiki?title=Arma_3:_Server_Profile&action=edit&section=3 "Edit section: Linux")]

The name depends upon the [-name](/wiki/Arma_3:_Startup_Parameters#Profile_Options "Arma 3: Startup Parameters") parameter when starting the Arma 3 server.
Having started the server with "./server -name=server" (-profiles seems to be useless on Linux) you'll find it as a subfolder of your arma-server directory, for example "/usr/home/arma-server/server/server.armaprofile".

If you're not using the -name parameter, the default name "player" will be used and you'll find the Arma 3 profile in player/player.armaprofile.

# Example Configuration File[[edit source](/wiki?title=Arma_3:_Server_Profile&action=edit&section=4 "Edit section: Example Configuration File")]

With [![Arma 3 logo black.png](/wikidata/images/thumb/8/80/Arma_3_logo_black.png/30px-Arma_3_logo_black.png)](/wiki/Category:Introduced_with_Arma_3_version_1.58 "Category:Introduced with Arma 3 version 1.58") [1.58](/wiki/Category:Introduced_with_Arma_3_version_1.58 "Category:Introduced with Arma 3 version 1.58") the difficulty levels were transformed to true presets (Recruit, Regular, Veteran). Each preset has exactly defined value for each difficulty option. If any of the options doesn't match the preset, the preset is changed to Custom. The particular flags stored in profile are only for the Custom preset, since Recruit, Regular and Veteran are fixed ones and predefined in the CfgDifficultyPresets config class.

- Documentation of presets and data config as well as a list of the most significant changes in 1.58: [Arma 3: Difficulty Settings](/wiki/Arma_3:_Difficulty_Settings "Arma 3: Difficulty Settings")
- Related forum thread: [Difficulty Overhaul](https://forums.bistudio.com/topic/188661-difficulty-overhaul/)

## Server Difficulty Example[[edit source](/wiki?title=Arma_3:_Server_Profile&action=edit&section=5 "Edit section: Server Difficulty Example")]

```
class DifficultyPresets
{
	class CustomDifficulty
	{
		class Options
		{
			/* Simulation */

			reducedDamage = 0;		// Reduced damage

			/* Situational awareness */

			groupIndicators = 0;	// Group indicators (0 = never, 1 = limited distance, 2 = always)
			friendlyTags = 0;		// Friendly name tags (0 = never, 1 = limited distance, 2 = always)
			enemyTags = 0;			// Enemy name tags (0 = never, 1 = limited distance, 2 = always)
			detectedMines = 0;		// Detected mines (0 = never, 1 = limited distance, 2 = always)
			commands = 1;			// Commands (0 = never, 1 = fade out, 2 = always)
			waypoints = 1;			// Waypoints (0 = never, 1 = fade out, 2 = always)
			tacticalPing = 0;		// Tactical ping (0 = disable, 1 = enable)

			/* Personal awareness */

			weaponInfo = 2;			// Weapon info (0 = never, 1 = fade out, 2 = always)
			stanceIndicator = 2;	// Stance indicator (0 = never, 1 = fade out, 2 = always)
			staminaBar = 0;			// Stamina bar
			weaponCrosshair = 0;	// Weapon crosshair
			visionAid = 0;			// Vision aid

			/* View */

			thirdPersonView = 0;	// 3rd person view (0 = disabled, 1 = enabled, 2 = enabled for vehicles only (Since  Arma 3 v1.99))
			cameraShake = 1;		// Camera shake

			/* Multiplayer */

			scoreTable = 1;			// Score table
			deathMessages = 1;		// Killed by
			vonID = 1;				// VoN ID

			/* Misc */

			mapContent = 0;			// Extended map content
			autoReport = 0;			// (former autoSpot) Automatic reporting of spotted enemied by players only. This doesn't have any effect on AIs.
			multipleSaves = 0;		// Multiple saves
		};
		
		// aiLevelPreset defines AI skill level and is counted from 0 and can have following values: 0 (Low), 1 (Normal), 2 (High), 3 (Custom).
		// when 3 (Custom) is chosen, values of skill and precision are taken from the class CustomAILevel.
		aiLevelPreset = 3;
	};

	class CustomAILevel
	{
		skillAI = 0.5;
		precisionAI = 0.5;
	};
};
```