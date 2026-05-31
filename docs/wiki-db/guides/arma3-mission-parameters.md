---
game: arma3
slug: arma3-mission-parameters
file: description.ext
source: https://community.bistudio.com/wiki/Mission_Parameters
scraped: 2026-05-31T11:45:20Z
---

- [1 Param Types](#Param_Types)
  - [1.1 Primary Params](#Primary_Params)
  - [1.2 Secondary Params](#Secondary_Params)
- [2 Mission Implementation](#Mission_Implementation)
  - [2.1 Available Templates](#Available_Templates)
- [3 Extraction](#Extraction)
- [4 See Also](#See_Also)

Mission parameters are integer values that are passed to the mission at the beginning and which are used by the mission designer to customise user experience accordingly.
The parameters are set in [description.ext](/wiki/Description.ext "Description.ext").
They can also be either manually altered by a dedicated server admin or host during ROLE ASSIGNMENT in MP lobby from available parameters menu or by including override values in dedicated server config [server config](/wiki/Arma_3:_Server_Config_File "Arma 3: Server Config File").
In short:

- Mission maker can set up a list of parameters for the game and set default values for each
- Server admin or host can change default values by selecting different options from provided parameters menu
- Server owner can additionally override the default values in server config for each mission separately

In any case, a person selecting parameters from parameters menu at the beginning of the mission has the final say what those options will be.

- [!["Parameters" GUI](/wikidata/images/thumb/f/ff/params.png/500px-params.png)](/wiki/File:params.png "\"Parameters\" GUI")

  "Parameters" GUI
- [![Available Options](/wikidata/images/thumb/1/14/params_options.png/500px-params_options.png)](/wiki/File:params_options.png "Available Options")

  Available Options
- [![Editing Options](/wikidata/images/thumb/2/22/params_edit.png/500px-params_edit.png)](/wiki/File:params_edit.png "Editing Options")

  Editing Options

Since [![Arma 3 logo black.png](/wikidata/images/thumb/8/80/Arma_3_logo_black.png/30px-Arma_3_logo_black.png)](/wiki/Category:Introduced_with_Arma_3_version_2.18 "Category:Introduced with Arma 3 version 2.18") [2.18](/wiki/Category:Introduced_with_Arma_3_version_2.18 "Category:Introduced with Arma 3 version 2.18") it is possible to deactivate automatic parameter initialization in **singleplayer** by setting BIS\_fnc\_initParams\_skip to [true](/wiki/true "true") in [preInit](/wiki/Arma_3:_Functions_Library "Arma 3: Functions Library").

## Param Types[[edit source](/wiki?title=Mission_Parameters&action=edit&section=1 "Edit section: Param Types")]

There are 2 types of parameters, primary and secondary. While primary parameters are inherited from earlier versions of Arma, they are still valid and recognised by the engine.

### Primary Params[[edit source](/wiki?title=Mission_Parameters&action=edit&section=2 "Edit section: Primary Params")]

Primary parameters were introduced with **Arma: Cold War Assault / Operation Flashpoint**. They are also known as **param1** and **param2**. They are defined in the following way in [description.ext](/wiki/Description.ext#param1/param2 "Description.ext"), for example:

```
titleParam1 = "Time limit:";
textsParam1[] = { "Unlimited", "5 min", "10 min", "15 min" };
valuesParam1[] = { 0, 300, 600, 900 };
defValueParam1 = 900;

titleParam2 = "Score to win:";
textsParam2[] = { "Don't keep score", "50", "100", "150" };
valuesParam2[] = { 0, 50, 100, 150 };
defValueParam2 = 50;
```

Here is some info about each entry and what it means:

- *titleParam* - This is the title that will be displayed in parameters menu available to server admin or host at ROLE ASSIGNMENT
- *textsParam* - These are options presented to the server admin or host when they double click on the title in the parameters menu
- *valuesParam* - These are the actual param values for each of the text options, that will be passed to the mission accordingly to chosen option
- *defValueParam* - This is default option which would be passed to the mission if no selection was made. It **must** match one of the *valuesParam* values

The chosen option value will be stored in **param1** or **param2** variable (respectfully) and then broadcast to everyone as [publicVariable](/wiki/publicVariable "publicVariable"). As mentioned before, *defValueParam* value can be overridden from the server config. Here is an example of such override for both params in [server config](/wiki/Arma_3:_Server_Config_File "Arma 3: Server Config File"):

```
class Missions 
{
	class Mission1
	{
		template = "Mission1.Altis";
		difficulty = "Veteran";
		param1 = 600;
		param2 = 100;
	};
};
```

As with *defValueParam* provided override values **must** match one of the *valuesParam* values. The logged admin during ROLE ASSIGNMENT phase can still override even server override by selecting an option from parameters UI.

⚠

Try to **avoid using Primary Params** in **Arma 2 v1.03** and above and use **[Secondary Params](#Secondary_Params)** instead!

[![Logo A2.png](/wikidata/images/thumb/9/97/Logo_A2.png/30px-Logo_A2.png)](/wiki/Category:Introduced_with_Arma_2_version_1.03 "Category:Introduced with Arma 2 version 1.03") [1.03](/wiki/Category:Introduced_with_Arma_2_version_1.03 "Category:Introduced with Arma 2 version 1.03")

### Secondary Params

Secondary params were introduced with **Arma 2 v1.03**. You can think of the secondary params as primary params extended and wrapped in a class called *Params*:

```
class Params
{
	class name1
	{
		title = "Item 1";
		texts[] = { "One", "Two", "Three" };
		values[] = { 1, 2, 3 };
		default = 1;
	};

	class name2
	{
		title = "Item 2";
		texts[] = { "Ten", "Twenty", "Thirty" };
		values[] = { 10, 20, 30 };
		default = 20;
	};

	class name3
	{
		title = "Item 3";
		texts[] = { "One Hundred", "Two Hundred", "Three Hundred" };
		values[] = { 100, 200, 300 };
		default = 300;
	};
};
```

As you can see the structure is the same, but the names of the entries are slightly different, but consistently different (apart from *default*):

- *title* - (same as *titleParam*) - This is the title that will be displayed in parameters menu available to server admin or host at ROLE ASSIGNMENT
- *texts* - (same as *textsParam*) - These are options presented to the server admin or host when they double click on the title in the parameters menu
- *values* - (same as *valuesParam*) - These are the actual (numerical) param values for each of the text options, that will be passed to the mission accordingly to chosen option

  [![Arma 3](/wikidata/images/thumb/8/80/Arma_3_logo_black.png/48px-Arma_3_logo_black.png)](/wiki/Category:Arma_3 "Arma 3")

  Since Arma 3, *values* can only be integers (0, 1, 42, -33). Before it, floats were accepted (1.25, 17.2, etc).
- *default* - (same as *defValueParam*) - This is default option which would be passed to the mission if no selection was made. It **must** match one of the *values* values

The chosen options values will be stored in **paramsArray** variable, in the order of appearance in the *Params* class and then broadcast to everyone as [publicVariable](/wiki/publicVariable "publicVariable").
As with primary params, the *default* value can be overridden from the server config using class names of the defined params. Here is an example of such override in [server config](/wiki/Arma_3:_Server_Config_File "Arma 3: Server Config File"):

```
class Missions 
{
	class Mission1
	{
		template = "Mission1.Altis";
		difficulty = "Veteran";
		class Params
		{
			name1 = 2;
			name2 = 30;
			name3 = 100;
		};
	};
};
```

As override values provided **must** match one of the *values* values. The logged admin during ROLE ASSIGNMENT phase can still override the server override by selecting options from parameters UI manually.

⚠

You should be careful when using both primary and secondary params in the same mission as **paramsArray** in this case will contain both types of parameters.

For example, if just secondary params used from the example above, **paramsArray** would look something like this: [1,20,300].

However if both types are used at the same time, **paramsArray** will look something like this: [900,50,1,20,30]. The primary params are added in front of the secondary params in **paramsArray**.

## Mission Implementation[[edit source](/wiki?title=Mission_Parameters&action=edit&section=3 "Edit section: Mission Implementation")]

As pointed out, params are just values that are passed over to the mission. In order to retrieve passed param value in mission use [BIS\_fnc\_getParamValue](/wiki/BIS_fnc_getParamValue "BIS fnc getParamValue") function. For example to retrieve mission param *name2*:

\_param [=](/wiki/a_=_b) ["name2", -1] [call](/wiki/call) [BIS\_fnc\_getParamValue](/wiki/BIS_fnc_getParamValue);

This should return 20 (in default example above) or -1 if no param with this name is found. This method is good to be used in other scripts, but there is even better way of setting mission params at the start of the mission.
You can instruct the game to run a function or a script of your choice automatically by including it in the secondary *Params* config. Note that it is not possible to do with primary params.
For example:

```
class Params
{
	class Daytime
	{
		title = "Time";
		texts[] = { "Morning", "Day", "Evening", "Night" };
		values[] = { 6, 12, 18, 0 };
		default = 12;
		function = "BIS_fnc_paramDaytime";
	};
};
```

The [BIS\_fnc\_paramDaytime](/wiki/BIS_fnc_paramDaytime "BIS fnc paramDaytime") function will be executed on mission start on the server. It will also receive current value of the parameter *Daytime* as an argument in [\_this](/wiki/Magic_Variables#this) [select](/wiki/select) 0.
You can also instruct the game to execute a script file instead and not just on server but on every client including the JIP clients:

```
class Params
{
	class ViewDistance
	{
		title = "View distance (in metres)";
		values[] = { 500, 1000, 2000, 5000 };
		default = 1000;
		file = "setViewDistance.sqf";
		isGlobal = 1;
	};
};
```

The *setViewDistance.sqf* script will be [compiled](/wiki/compile "compile") and [called](/wiki/call "call") globally (`isGlobal = 1;`) on **every client including server** and param value is passed to it in [\_this](/wiki/Magic_Variables#this) [select](/wiki/select) 0.
If for some weird reason you have both *function* and *file* entries, the priority is given to *function*.

### Available Templates[[edit source](/wiki?title=Mission_Parameters&action=edit&section=4 "Edit section: Available Templates")]

**Arma 3** introduces a framework for defining commonly used params (e.g., time of the day or mission duration), which can be shared across multiple missions.
Once included in description.ext, they will initialize automatically. Some of them can be further customized using specific [macros](/wiki/PreProcessor_Commands "PreProcessor Commands").

⚠

Make sure that include path to a3 mod has leading backslash (\) `#include "\a3\functions_f\Params\paramCountdown.hpp"`.

⚠

Param templates **do not** work with PBO missions manually copied to MPMissions folder. Unpacked missions, Steam missions and [missions which are part of an addon](/wiki/Mission_Export#Addon_Format "Mission Export") works correctly.

| File | Description | Optional variables |
| --- | --- | --- |
| ``` "\a3\functions_f\Params\paramCountdown.hpp" ```  ``` "\a3\functions_f\Params\paramCountdownNoDisabled.hpp" ``` | Set mission countdown (in seconds) The "NoDisabled" version has no "disabled" option, and will pick the middle option by default. | ``` #define COUNTDOWN_MIN		600 #define COUNTDOWN_MAX		3600 #define COUNTDOWN_DEFAULT	-1 ``` |
| ``` "\a3\functions_f\Params\paramDaytimeHour.hpp" ``` | Set starting hour, options are represented by whole hours | ``` // can be any integer between 0 and 23 #define DAYTIMEHOUR_DEFAULT	19 ``` |
| ``` "\a3\functions_f\Params\paramDaytimePeriod.hpp" ``` | Set starting hour, options are described by words | ``` // can be 0, 6, 12 or 18 #define DAYTIMEPERIOD_DEFAULT	12 ``` |
| ``` "\a3\functions_f\Params\paramDebugConsole.hpp" ``` | Allow [debug console](/wiki/Arma_3:_Debug_Console "Arma 3: Debug Console") for server host or logged in admin | ``` // 0 (disabled) or 1 (enabled) #define DEBUGCONSOLE_DEFAULT	1 ``` |
| ``` "\a3\functions_f\Params\paramGuerFriendly.hpp" ``` | Set to whom will [independent](/wiki/independent "independent") side be friendly | ``` // can be any -1 (Nobody}, 0 (OPFOR), 1 (BLUFOR) or 2 (Everybody) #define GUERFRIENDLY_DEFAULT	-1 ``` |
| ``` "\a3\functions_f\Params\paramRespawnTickets.hpp" ```  ``` "\a3\functions_f\Params\paramRespawnTicketsNoDisabled.hpp" ``` | Set respawn tickets for all sides The "NoDisabled" version has no "disabled" option, and will pick the middle option by default. | ``` #define TICKETS_MIN			100 #define TICKETS_MAX			1100 #define TICKETS_DEFAULT		-1 ``` |
| ``` "\a3\functions_f\Params\paramWeather.hpp" ``` | Set default weather | ``` // can be 0 (sunny), 25, 50, 75 or 100 (storm)) #define WEATHER_DEFAULT		75 ``` |
| ``` "\a3\Functions_F_MP_Mark\Params\paramTimeAcceleration.hpp" ``` | Sets a time multiplier for in-game time. See [setTimeMultiplier](/wiki/setTimeMultiplier "setTimeMultiplier") | ``` // can be x1, x2, x5, x10 or x20 #define TIMEACCELERATION_DEFAULT	10 ``` |
| ``` "\a3\Functions_F_Heli\Params\paramViewDistance.hpp" ``` | Set rendering distance, in meters. See [setViewDistance](/wiki/setViewDistance "setViewDistance") | ``` #define VIEW_DISTANCE_MIN		1500 #define VIEW_DISTANCE_MAX		4000 #define VIEW_DISTANCE_DEFAULT	2000 ``` |
| ``` "\a3\Functions_F\Params\paramRevive.hpp" ``` | Set various revive related options [Arma 3: Revive](/wiki/Arma_3:_Revive "Arma 3: Revive") | N/A |

Example Config

```
class Params
{
	#define COUNTDOWN_MIN 600
	#define COUNTDOWN_MAX 3600
	#define COUNTDOWN_DEFAULT -1
	#include "\a3\functions_f\Params\paramCountdown.hpp"

	#define DAYTIMEHOUR_DEFAULT 19
	#include "\a3\functions_f\Params\paramDaytimeHour.hpp"

	// #define DAYTIMEPERIOD_DEFAULT 12
	// #include "\a3\functions_f\Params\paramDaytimePeriod.hpp"

	#define DEBUGCONSOLE_DEFAULT 1
	#include "\a3\functions_f\Params\paramDebugConsole.hpp"

	#define GUERFRIENDLY_DEFAULT -1
	#include "\a3\functions_f\Params\paramGuerFriendly.hpp"

	#define TICKETS_MIN 100
	#define TICKETS_MAX	1100
	#define TICKETS_DEFAULT	-1
	#include "\a3\functions_f\Params\paramRespawnTickets.hpp"

	#define WEATHER_DEFAULT	40
	#include "\a3\functions_f\Params\paramWeather.hpp"

	#define TIMEACCELERATION_DEFAULT 10
	#include "\a3\Functions_F_MP_Mark\Params\paramTimeAcceleration.hpp"

	#define VIEW_DISTANCE_MIN 1500
	#define VIEW_DISTANCE_MAX 4000
	#define VIEW_DISTANCE_DEFAULT 2000
	#include "\a3\Functions_F_Heli\Params\paramViewDistance.hpp"

    #include "\a3\Functions_F\Params\paramRevive.hpp"
};
```

[↑ Back to spoiler's top](#bikisp6a1b15a2c5110)

## Extraction[[edit source](/wiki?title=Mission_Parameters&action=edit&section=5 "Edit section: Extraction")]

The link below offers a script allowing to extract mission parameters in [Description.ext](/wiki/Description.ext "Description.ext") format.

ⓘ

Extraction script can be found on the [Biki Export Scripts](/wiki/Biki_Export_Scripts#Mission_Parameters "Biki Export Scripts") page.

## See Also[[edit source](/wiki?title=Mission_Parameters&action=edit&section=6 "Edit section: See Also")]

- [Description.ext - Mission Parameters](/wiki/Description.ext#Mission_Parameters "Description.ext")
- [BIS\_fnc\_getParamValue](/wiki/BIS_fnc_getParamValue "BIS fnc getParamValue")
- [BIS\_fnc\_storeParamsValues](/wiki/BIS_fnc_storeParamsValues "BIS fnc storeParamsValues")
- [getMissionConfigValue](/wiki/getMissionConfigValue "getMissionConfigValue")
- [getMissionConfig](/wiki/getMissionConfig "getMissionConfig")