---
game: reforger-exp
slug: reforger-exp-startup-parameters
file: startup
source: https://community.bistudio.com/wiki/Arma_Reforger:Startup_Parameters
scraped: 2026-05-31T11:45:20Z
---

- [1 General](#General)
  - [1.1 addons](#addons)
  - [1.2 addonsDir](#addonsDir)
  - [1.3 addonDownloadDir](#addonDownloadDir)
  - [1.4 addonTempDir](#addonTempDir)
  - [1.5 backendDisableStorage](#backendDisableStorage)
  - [1.6 backendFreshSession](#backendFreshSession)
  - [1.7 backendLocalStorage](#backendLocalStorage)
  - [1.8 freezeCheck](#freezeCheck)
  - [1.9 freezeCheckMode](#freezeCheckMode)
  - [1.10 cfg](#cfg)
  - [1.11 GPUAdapter](#GPUAdapter)
  - [1.12 language](#language)
  - [1.13 noBackend](#noBackend)
  - [1.14 noSound](#noSound)
  - [1.15 noSplash](#noSplash)
  - [1.16 noThrow](#noThrow)
  - [1.17 profile](#profile)
  - [1.18 singleThreadedUpdate](#singleThreadedUpdate)
  - [1.19 world](#world)
- [2 Window](#Window)
  - [2.1 window](#window_2)
  - [2.2 posX](#posX)
  - [2.3 posY](#posY)
  - [2.4 screenWidth](#screenWidth)
  - [2.5 screenHeight](#screenHeight)
  - [2.6 forceUpdate](#forceUpdate)
  - [2.7 noFocus](#noFocus)
- [3 Workbench](#Workbench)
  - [3.1 forceSettings](#forceSettings)
  - [3.2 gproj](#gproj)
  - [3.3 gprojConfig](#gprojConfig)
  - [3.4 wbModule](#wbModule)
  - [3.5 plugin](#plugin)
  - [3.6 run](#run)
  - [3.7 load](#load)
  - [3.8 packAddon](#packAddon)
  - [3.9 packAddonDir](#packAddonDir)
  - [3.10 publishAddon](#publishAddon)
  - [3.11 publishAddonDir](#publishAddonDir)
  - [3.12 publishAddonVersion](#publishAddonVersion)
  - [3.13 publishAddonChangeNote](#publishAddonChangeNote)
  - [3.14 publishAddonChangeNoteFile](#publishAddonChangeNoteFile)
  - [3.15 publishAddonPreviewImage](#publishAddonPreviewImage)
  - [3.16 publishAddonScreenshots](#publishAddonScreenshots)
  - [3.17 rebuild-database-only](#rebuild-database-only)
  - [3.18 scrDefine](#scrDefine)
  - [3.19 scrNoInline](#scrNoInline)
  - [3.20 wbBackendLogin](#wbBackendLogin)
  - [3.21 noGameScriptsOnInit](#noGameScriptsOnInit)
  - [3.22 enableWARP](#enableWARP)
  - [3.23 clearSettings](#clearSettings)
  - [3.24 exitAfterInit](#exitAfterInit)
  - [3.25 diagMenu](#diagMenu)
- [4 Workbench - AnimationEditor Module](#Workbench_-_AnimationEditor_Module)
  - [4.1 animDebugger](#animDebugger)
- [5 Workbench - LocalizationEditor Module](#Workbench_-_LocalizationEditor_Module)
  - [5.1 build](#build)
  - [5.2 saveDir](#saveDir)
- [6 Workbench - NavmeshGeneratorMain Module](#Workbench_-_NavmeshGeneratorMain_Module)
  - [6.1 autogenerate](#autogenerate)
  - [6.2 saveDir](#saveDir_2)
  - [6.3 logNavmeshGen](#logNavmeshGen)
- [7 Workbench - ResourceManager Module](#Workbench_-_ResourceManager_Module)
  - [7.1 buildData](#buildData)
  - [7.2 metaFiles](#metaFiles)
  - [7.3 loadBuiltData](#loadBuiltData)
  - [7.4 includeTypes](#includeTypes)
  - [7.5 excludeTypes](#excludeTypes)
  - [7.6 filterPath](#filterPath)
  - [7.7 filterTags](#filterTags)
  - [7.8 wbSilent](#wbSilent)
- [8 Workbench - ScriptEditor Module](#Workbench_-_ScriptEditor_Module)
  - [8.1 validate](#validate)
- [9 Workbench - WorldEditor Module](#Workbench_-_WorldEditor_Module)
  - [9.1 forceSaveAll](#forceSaveAll)
- [10 Hosting](#Hosting)
  - [10.1 a2sIpAddress](#a2sIpAddress)
  - [10.2 a2sPort](#a2sPort)
  - [10.3 autoreload](#autoreload)
  - [10.4 autoshutdown](#autoshutdown)
  - [10.5 bindIP](#bindIP)
  - [10.6 bindPort](#bindPort)
  - [10.7 config](#config)
  - [10.8 enableNightGrain](#enableNightGrain)
  - [10.9 listScenarios](#listScenarios)
  - [10.10 loadSessionSave](#loadSessionSave)
  - [10.11 keepSessionSave](#keepSessionSave)
  - [10.12 logStats](#logStats)
  - [10.13 logVoting](#logVoting)
  - [10.14 maxFPS](#maxFPS)
  - [10.15 playerLimits](#playerLimits)
  - [10.16 server](#server)
  - [10.17 client](#client)
  - [10.18 worldSystemsConfig](#worldSystemsConfig)
  - [10.19 addonsVerify](#addonsVerify)
  - [10.20 addonsRepair](#addonsRepair)
- [11 Network Tuning](#Network_Tuning)
  - [11.1 nds](#nds)
  - [11.2 nwkResolution](#nwkResolution)
  - [11.3 rpl-timeout-ms](#rpl-timeout-ms)
  - [11.4 rpl-timeout-disable](#rpl-timeout-disable)
  - [11.5 rpl-reconnect](#rpl-reconnect)
  - [11.6 rpl-vcons](#rpl-vcons)
  - [11.7 rpl-validation-rdb-disable](#rpl-validation-rdb-disable)
  - [11.8 rpl-validation-scr-disable](#rpl-validation-scr-disable)
  - [11.9 rpl-validation-version-disable](#rpl-validation-version-disable)
  - [11.10 rpl-validation-devbin-disable](#rpl-validation-devbin-disable)
  - [11.11 staggeringBudget](#staggeringBudget)
  - [11.12 streamingBudget](#streamingBudget)
  - [11.13 streamsDelta](#streamsDelta)
- [12 Debug](#Debug)
  - [12.1 AILimit](#AILimit)
  - [12.2 AIPartialSim](#AIPartialSim)
  - [12.3 autodeployFaction](#autodeployFaction)
  - [12.4 autodeployLoadout](#autodeployLoadout)
  - [12.5 createDB](#createDB)
  - [12.6 debugger](#debugger)
  - [12.7 debuggerPort](#debuggerPort)
  - [12.8 disableAI](#disableAI)
  - [12.9 disableCrashReporter](#disableCrashReporter)
  - [12.10 disableNavmeshStreaming](#disableNavmeshStreaming)
  - [12.11 disableShadersBuild](#disableShadersBuild)
  - [12.12 generateShaders](#generateShaders)
  - [12.13 rplEncodeAsLongJobs](#rplEncodeAsLongJobs)
  - [12.14 jobsysShortWorkerCount](#jobsysShortWorkerCount)
  - [12.15 jobsysLongWorkerCount](#jobsysLongWorkerCount)
  - [12.16 keepNumOfLogs](#keepNumOfLogs)
  - [12.17 log-rdb-checksum](#log-rdb-checksum)
  - [12.18 log-scr-checksum](#log-scr-checksum)
  - [12.19 logAppend](#logAppend)
  - [12.20 logFS](#logFS)
  - [12.21 logLevel](#logLevel)
  - [12.22 logTime](#logTime)
  - [12.23 logsDir](#logsDir)
  - [12.24 keepCrashFiles](#keepCrashFiles)
  - [12.25 minidump](#minidump)
  - [12.26 scriptAuthorizeAll](#scriptAuthorizeAll)
  - [12.27 silentCrashReport](#silentCrashReport)
  - [12.28 VMErrorMode](#VMErrorMode)
- [13 Profiling](#Profiling)
  - [13.1 checkInstance](#checkInstance)

Startup parameters are command line interface(CLI) arguments that go after the executable's name in order to set options; e.g ArmaReforgerSteam.exe -window to start the game in [windowed](#window) mode.

ⓘ

- Startup parameters' *names* are case-insensitive - e.g -logstats and -LogSTATS are identical.
- Spaces in parameter values must be wrapped in quotations - e.g. -addonDownloadDir "E:\Arma Addons"

1. [General](#General)
2. [Window](#Window)
3. [Workbench](#Workbench)
4. [Workbench - AnimationEditor Module](#Workbench_-_AnimationEditor_Module)
5. [Workbench - LocalizationEditor Module](#Workbench_-_LocalizationEditor_Module)
6. [Workbench - NavmeshGeneratorMain Module](#Workbench_-_NavmeshGeneratorMain_Module)
7. [Workbench - ResourceManager Module](#Workbench_-_ResourceManager_Module)
8. [Workbench - ScriptEditor Module](#Workbench_-_ScriptEditor_Module)
9. [Workbench - WorldEditor Module](#Workbench_-_WorldEditor_Module)
10. [Hosting](#Hosting)
11. [Network Tuning](#Network_Tuning)
12. [Debug](#Debug)
13. [Profiling](#Profiling)

## General[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=1 "Edit section: General") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=1 "Edit section: General")]

### addons[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=2 "Edit section: addons") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=2 "Edit section: addons")]

-addons takes a list of mod IDs (specified in their gproj files, without brackets) to be loaded on game start, separated by a comma ,.  
Mods are searched in profile/addons, in <executableDir>/addons and in directories specified by -addonsDir (see [below](#addonsDir)).

ⓘ

Mod IDs can be:

- GUIDs (**preferred** - found in .gproj)
- Project ID (found in .gproj)
- Sub-directory name (fallback solution)

Example

```
ArmaReforgerSteam.exe -addons 88037E46AD234C72,88037E46AD234C73
```

### addonsDir[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=3 "Edit section: addonsDir") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=3 "Edit section: addonsDir")]

-addonsDir specifies additional directories in which the game must look for mods to load, separated by a comma ,.

ⓘ

By default, the game looks into:

- Documents/My Games/ArmaReforger/profile/addons
- <executableDir>/addons

⚠

It is recommended to use *absolute* paths to Mod directories even though the provided path *can* be relative to the executable location.

Example

```
ArmaReforgerSteam.exe -addonsDir D:\DownloadedMods
```

### addonDownloadDir[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=4 "Edit section: addonDownloadDir") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=4 "Edit section: addonDownloadDir")]

-addonDownloadDir specifies where the game and the Workshop should download addons. Downloaded addons will be stored in addons subfolder inside of target directory.

⚠

It is recommended to use *absolute* paths to Mod directories even though the provided path *can* be relative to the executable location.

ⓘ

It is not necessary to combine this command with **addonsDir** parameter.

Example

```
ArmaReforgerSteam.exe -addonDownloadDir D:\DownloadedMods
```

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.1.0 "Category:Arma Reforger/Version 1.1.0") [1.1.0](/wiki?title=Category:Arma_Reforger/Version_1.1.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.1.0 (page does not exist)")

### addonTempDir

-addonTempDir specifies the directory for temporary content. The default temporary directory is:

- before [![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.4.0 "Category:Arma Reforger/Version 1.4.0") [1.4.0](/wiki?title=Category:Arma_Reforger/Version_1.4.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.4.0 (page does not exist)"): %localappdata%/Temp/<addon>/<temp data> on Windows or /tmp/<addon>/<temp data> on Linux
- since [![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.4.0 "Category:Arma Reforger/Version 1.4.0") [1.4.0](/wiki?title=Category:Arma_Reforger/Version_1.4.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.4.0 (page does not exist)"): <addonsDir>/<addon>/temp/<temp data>

Example

```
ArmaReforgerSteam.exe -addonTempDir "C:\Temp"
```

### backendDisableStorage[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=5 "Edit section: backendDisableStorage") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=5 "Edit section: backendDisableStorage")]

-backendDisableStorage disables use of storage, no loads or saves can be performed both online and **local**.

Example

```
ArmaReforgerSteam.exe -backendDisableStorage
```

### backendFreshSession[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=6 "Edit section: backendFreshSession") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=6 "Edit section: backendFreshSession")]

-backendFreshSession skips the initial load request from dedicated server's configuration file or from script - the DS Session basically starts as a brand new one, the rest of functionalities is not affected (saves, runtime loads, etc).

Example

```
ArmaReforgerSteam.exe -backendFreshSession
```

### backendLocalStorage[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=7 "Edit section: backendLocalStorage") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=7 "Edit section: backendLocalStorage")]

-backendLocalStorage forces Save & Load of player and general data normally sent to/received from the backend to work with local JSON files in profile folder instead.

Example

```
ArmaReforgerSteam.exe -backendLocalStorage
```

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.2.0 "Category:Arma Reforger/Version 1.2.0") [1.2.0](/wiki?title=Category:Arma_Reforger/Version_1.2.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.2.0 (page does not exist)")

### freezeCheck

number value, range 0..600, default 300

-freezeCheck overrides time in seconds to forcefully crash on application freeze or completely disable detection.

ⓘ

Workbench has default time set to 1 minute and will only generate minidump without crashing.

Example

```
ArmaReforgerSteam.exe -freezeCheck 30
```

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.2.0 "Category:Arma Reforger/Version 1.2.0") [1.2.0](/wiki?title=Category:Arma_Reforger/Version_1.2.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.2.0 (page does not exist)")

### freezeCheckMode

string value, default minidump (workbench) or crash

-freezeCheckMode overrides behavior which should happen when freeze is detected.

- crash - force crashes the application
- minidump - silently generates minidump and keeps application running until it unfreezes or is terminated by user
- [![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/22px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.3.0 "Category:Arma Reforger/Version 1.3.0") [1.3.0](/wiki?title=Category:Arma_Reforger/Version_1.3.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.3.0 (page does not exist)") kill - will force kill the application - is more reliable than {hl|crash} but does not provide any data about the freeze

⚠

Option minidump is supported only on Windows.

Example

```
ArmaReforgerSteam.exe -freezeCheckMode crash
ArmaReforgerSteam.exe -freezeCheckMode minidump
```

### cfg[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=8 "Edit section: cfg") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=8 "Edit section: cfg")]

-cfg forces to load specific user engine settings config (things like graphics settings, window position and size etc).

### GPUAdapter[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=9 "Edit section: GPUAdapter") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=9 "Edit section: GPUAdapter")]

-GPUAdapter forces to use the graphic card at the provided index. GPU index can be found using [DxDiag](https://support.microsoft.com/en-us/windows/open-and-run-dxdiag-exe-dad7792c-2ad5-f6cd-5a37-bf92228dfd85).

Example

```
ArmaReforgerSteam.exe -GPUAdapter 0
```

### language[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=10 "Edit section: language") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=10 "Edit section: language")]

-language sets the game language by language code - supported language codes depends on StringTable which is set in gproj. Arma Reforger supported languages are:

- [![flag uk.gif](/wikidata/images/b/be/flag_uk.gif)](/wiki/File:flag_uk.gif) en\_us - English
- [![flag france.gif](/wikidata/images/e/eb/flag_france.gif)](/wiki/File:flag_france.gif) fr\_fr - French
- [![flag italy.gif](/wikidata/images/0/07/flag_italy.gif)](/wiki/File:flag_italy.gif) it\_it - Italian
- [![flag germany.gif](/wikidata/images/3/3d/flag_germany.gif)](/wiki/File:flag_germany.gif) de\_de - German
- [![flag spain.gif](/wikidata/images/e/e2/flag_spain.gif)](/wiki/File:flag_spain.gif) es\_es - Spanish
- [![flag czechrep.gif](/wikidata/images/7/79/flag_czechrep.gif)](/wiki/File:flag_czechrep.gif) cs\_cz - Czech
- [![flag poland.gif](/wikidata/images/7/75/flag_poland.gif)](/wiki/File:flag_poland.gif) pl\_pl - Polish
- [![flag russia.gif](/wikidata/images/c/c2/flag_russia.gif)](/wiki/File:flag_russia.gif) ru\_ru - Russian
- [![flag japan.gif](/wikidata/images/d/db/flag_japan.gif)](/wiki/File:flag_japan.gif) ja\_jp - Japanese
- [![flag korea.gif](/wikidata/images/9/9f/flag_korea.gif)](/wiki/File:flag_korea.gif) ko\_kr - Korean
- [![flag portugal.gif](/wikidata/images/7/7a/flag_portugal.gif)](/wiki/File:flag_portugal.gif) pt\_br - Portuguese
- [![flag china.gif](/wikidata/images/c/c5/flag_china.gif)](/wiki/File:flag_china.gif) zh\_cn - Mandarin
- [![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/22px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.2.0 "Category:Arma Reforger/Version 1.2.0") [1.2.0](/wiki?title=Category:Arma_Reforger/Version_1.2.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.2.0 (page does not exist)") [![flag ukraine.gif](/wikidata/images/9/93/flag_ukraine.gif)](/wiki/File:flag_ukraine.gif) uk\_ua - Ukrainian

Example

```
ArmaReforgerSteam.exe -language cs_cz
```

### noBackend[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=11 "Edit section: noBackend") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=11 "Edit section: noBackend")]

-noBackend disables backend-related http communication.

Example

```
ArmaReforgerSteam.exe -noBackend
```

### noSound[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=12 "Edit section: noSound") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=12 "Edit section: noSound")]

-noSound disables the sound system processing.

Example

```
ArmaReforgerSteam.exe -noSound
```

### noSplash[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=13 "Edit section: noSplash") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=13 "Edit section: noSplash")]

-noSplash skips splash screens on game load.

Example

```
ArmaReforgerSteam.exe -noSplash
```

### noThrow[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=14 "Edit section: noThrow") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=14 "Edit section: noThrow")]

-noThrow disables all kinds of error message dialogs. (VMEs, asserts, crashes, ...).

Example

```
ArmaReforgerSteam.exe -noThrow
```

### profile[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=15 "Edit section: profile") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=15 "Edit section: profile")]

-profile defines the path to the profile directory to be used, either by parent directory name or by path.

Example

```
ArmaReforgerSteam.exe -profile "David Armstrong"								// targets %userprofile%\My Documents\My Games\David Armstrong\profile
ArmaReforgerSteam.exe -profile "C:\Users\MyUserName\Documents\ArmaReforgerDir"	// targets "C:\Users\MyUserName\Documents\ArmaReforgerDir\profile"
```

### singleThreadedUpdate[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=16 "Edit section: singleThreadedUpdate") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=16 "Edit section: singleThreadedUpdate")]

-singleThreadedUpdate disables multithreaded update.

Example

```
ArmaReforgerSteam.exe -singleThreadedUpdate
```

### world[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=17 "Edit section: world") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=17 "Edit section: world")]

-world defines the world file to be loaded on startup, both absolute and relative paths are supported although it is recommended to use path relative to created filesystems (eg. worlds/myworld.ent). Workbench ignores this parameter - for that, use [-load](#load) parameter instead.

Example

```
ArmaReforgerSteam.exe -world worlds/myworld.ent
```

## Window[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=18 "Edit section: Window") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=18 "Edit section: Window")]

### window[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=19 "Edit section: window") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=19 "Edit section: window")]

-window starts the game windowed (fullscreen by default).

Example

```
ArmaReforgerSteam.exe -window
```

### posX[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=20 "Edit section: posX") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=20 "Edit section: posX")]

### posY[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=21 "Edit section: posY") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=21 "Edit section: posY")]

-posX and -posY define the initial window position. They must be used with -window.  
posX 0 is left of the screen,  
posY 0 is top of the screen.

Example

```
ArmaReforgerSteam.exe -window -posX 0 -posY 64
```

### screenWidth[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=22 "Edit section: screenWidth") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=22 "Edit section: screenWidth")]

### screenHeight[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=23 "Edit section: screenHeight") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=23 "Edit section: screenHeight")]

-screenWidth and -screenHeight define the window's size (and not its render *resolution*). They must be used with -window.

Example

```
ArmaReforgerSteam.exe -window -screenWidth 1024 -screenHeight 578
```

### forceUpdate[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=24 "Edit section: forceUpdate") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=24 "Edit section: forceUpdate")]

-forceUpdate forces the application to render and update even when the window is out of focus.

ⓘ

Previously known as [-noPause](/wiki/Arma_3:_Startup_Parameters#noPause "Arma 3: Startup Parameters") in earlier titles.

Example

```
ArmaReforgerSteam.exe -forceUpdate
```

### noFocus[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=25 "Edit section: noFocus") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=25 "Edit section: noFocus")]

-noFocus prevents window focus stealing on game initialization.

Example

```
ArmaReforgerSteam.exe -noFocus
```

## Workbench[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=26 "Edit section: Workbench") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=26 "Edit section: Workbench")]

⚠

These parameters apply to the Workbench executable.

### forceSettings[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=27 "Edit section: forceSettings") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=27 "Edit section: forceSettings")]

-forcesettings forces to use the specified Workbench settings from a file instead of the usual settings stored in Windows registry which will be entirely ignored.
The settings in a file will be used for reading and also writing.
This functionality is compatible with wbSettingsDump.ini files being attached to crash reports.

ⓘ

It is possible to export Workbench settings to an .ini file from [Workbench options](/wiki/Arma_Reforger:Resource_Manager:_Options#Export_settings "Arma Reforger:Resource Manager: Options").

### gproj[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=28 "Edit section: gproj") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=28 "Edit section: gproj")]

-gproj defines the addon project to be loaded - it can be used to skip the Workbench's addon selection screen.

Example

```
ArmaReforgerWorkbenchSteamDiag.exe -gproj "C:\Program Files (x86)\Steam\steamapps\common\Arma Reforger\addons\data\ArmaReforger.gproj"
```

### gprojConfig[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=29 "Edit section: gprojConfig") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=29 "Edit section: gprojConfig")]

-gprojConfig forces loading the provided configuration e.g PC, HEADLESS etc.

Example

```
ArmaReforgerWorkbenchSteamDiag.exe -gprojConfig PC
```

### wbModule[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=30 "Edit section: wbModule") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=30 "Edit section: wbModule")]

-wbModule specifies which [Workbench](/wiki/Category:Arma_Reforger/Modding/Official_Tools "Category:Arma Reforger/Modding/Official Tools") module must be launched (with [-run](#run)):

| Editor | Entry (case-insensitive) | Additional Information |
| --- | --- | --- |
| [Resource Manager](/wiki/Arma_Reforger:Resource_Manager "Arma Reforger:Resource Manager") | resourceManager |
| [World Editor](/wiki/Arma_Reforger:World_Editor "Arma Reforger:World Editor") | worldEditor | See also [Workbench - WorldEditor Module](#Workbench_-_WorldEditor_Module) below |
| [Particle Editor](/wiki/Arma_Reforger:Particle_Editor "Arma Reforger:Particle Editor") | particleEditor |
| [Animation Editor](/wiki/Arma_Reforger:Animation_Editor "Arma Reforger:Animation Editor") | animEditor |
| [Script Editor](/wiki/Arma_Reforger:Script_Editor "Arma Reforger:Script Editor") | scriptEditor | See also [Workbench - ScriptEditor Module](#Workbench_-_ScriptEditor_Module) below |
| [Audio Editor](/wiki/Arma_Reforger:Audio_Editor "Arma Reforger:Audio Editor") | audioEditor |
| [Behavior Editor](/wiki/Arma_Reforger:Behavior_Editor "Arma Reforger:Behavior Editor") | behaviorEditor |
| [Procedural Animation Editor](/wiki/Arma_Reforger:Procedural_Animation_Editor "Arma Reforger:Procedural Animation Editor") | procAnimEditor |
| [String Editor](/wiki/Arma_Reforger:String_Editor "Arma Reforger:String Editor") | localizationEditor | See also [Workbench - LocalizationEditor Module](#Workbench_-_LocalizationEditor_Module) below |
| Navmesh Generator | navmeshGeneratorMain | See also [Workbench - NavmeshGeneratorMain Module](#Workbench_-_NavmeshGeneratorMain_Module) below |

⚠

- -wbmodule requires an equal sign = to work.
- Parameters specified after the module name are considered to be parameters to the module specifically.
- The [-run](#run) parameter must be added after each instance of -wbModule for them to launch.

Example

```
ArmaReforgerWorkbenchSteamDiag.exe -wbModule=WorldEditor -run
```

### plugin[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=31 "Edit section: plugin") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=31 "Edit section: plugin")]

-plugin specifies which [Workbench](/wiki/Category:Arma_Reforger/Modding/Official_Tools "Category:Arma Reforger/Modding/Official Tools") plugin must be launched (WorldTestPlugin, TextureImportTool, MaterialImportTool, WorldDataExport etc).

ⓘ

- Parameters specified after the plugin are considered to be parameters to the plugin specifically.
- Plugins are loaded for specific modules. Before loading a plugin specify the appropriate [wbModule](#wbModule).

Example

```
ArmaReforgerWorkbenchSteamDiag.exe -wbModule=ResourceManager -plugin=ResavePlugin pluginArguments
```

### run[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=32 "Edit section: run") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=32 "Edit section: run")]

-run tells the executable to open the designated Workbench module (specified by [-wbModule](#wbModule) usage).

Example

```
ArmaReforgerWorkbenchSteamDiag.exe -wbModule=WorldEditor -run
```

### load[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=33 "Edit section: load") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=33 "Edit section: load")]

-load tells the executable to open selected file in the designated Workbench module (specified by [-wbModule](#wbModule) usage).
The file path can be specified either by absolute path, relative path, exact path or by resource name.

Example

```
ArmaReforgerWorkbenchSteamDiag.exe -wbModule=WorldEditor -run -load "world/myworld.ent"
```

This will open up the [World Editor](/wiki/Arma_Reforger:World_Editor "Arma Reforger:World Editor") and load the myworld.ent file.

### packAddon[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=34 "Edit section: packAddon") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=34 "Edit section: packAddon")]

-packAddon is the main parameter used for addon packing.

When no directory is specified (by using [packAddonDir](#packAddonDir)), the directory from the last session is used.

When packing an addon for the first time, a new directory is automatically created in the same dir as profile.

Example

```
ArmaReforgerWorkbenchSteamDiag.exe -wbModule=ResourceManager -packAddon
```

### packAddonDir[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=35 "Edit section: packAddonDir") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=35 "Edit section: packAddonDir")]

-packAddonDirspecifies the output directory where resulting PAKs will be saved.

Example

```
ArmaReforgerWorkbenchSteamDiag.exe -wbModule=ResourceManager -packAddon -packAddonDir "D:\build\Green"
```

### publishAddon[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=36 "Edit section: publishAddon") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=36 "Edit section: publishAddon")]

-publishAddon is main parameter used for addon publishing.

It should be used only for publishing addon updates, and not for the initial publish because data from last session are used to fill in the blanks.

It can be used on its own, in combination with **packAddon** or in combination with parameters starting with "publishAddon".

When no directory is specified (by using **publishAddonDir**) a directory from last session is used.

Example

```
ArmaReforgerWorkbenchSteamDiag.exe -wbModule=ResourceManager -publishAddon
```

### publishAddonDir[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=37 "Edit section: publishAddonDir") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=37 "Edit section: publishAddonDir")]

-publishAddonDir specifies the pre-packed data directory (obtained by the -packAddon parameter above).
When not specified, directory from the last session is used.

Example

```
ArmaReforgerWorkbenchSteamDiag.exe -wbModule=ResourceManager -publishAddon -publishAddonDir "D:\build\Green"
```

### publishAddonVersion[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=38 "Edit section: publishAddonVersion") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=38 "Edit section: publishAddonVersion")]

-publishAddonVersion specifies the mod's version number.
When not specified, newest version found on back-end is used and it's last digit is automatically incremented.

Example

```
ArmaReforgerWorkbenchSteamDiag.exe -wbModule=ResourceManager -publishAddon -publishAddonVersion "2.3.5"
```

### publishAddonChangeNote[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=39 "Edit section: publishAddonChangeNote") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=39 "Edit section: publishAddonChangeNote")]

-publishAddonChangeNote is an optional parameter that specifies a note to the provided update.

Example

```
ArmaReforgerWorkbenchSteamDiag.exe -publishAddonChangeNote "Fix shading"
```

### publishAddonChangeNoteFile[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=40 "Edit section: publishAddonChangeNoteFile") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=40 "Edit section: publishAddonChangeNoteFile")]

-publishAddonChangeNoteFile is an optional parameter which is used to specify this update's change note.

Example

```
ArmaReforgerWorkbenchSteamDiag.exe -publishAddonChangeNoteFile "C:\Addon\changelog.txt"
```

### publishAddonPreviewImage[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=41 "Edit section: publishAddonPreviewImage") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=41 "Edit section: publishAddonPreviewImage")]

-publishAddonPreviewImage is an optional parameter which you can use to specify the preview image file's path.

Example

```
ArmaReforgerWorkbenchSteamDiag.exe -publishAddonPreviewImage "z:/mymod/my_preview_image.jpg"
```

### publishAddonScreenshots[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=42 "Edit section: publishAddonScreenshots") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=42 "Edit section: publishAddonScreenshots")]

-publishAddonScreenshots is an optional parameter that specifies the directory from where the screenshots should be taken. It is then automatically scanned for all valid image file types (jpg, png, bmp).

Example

```
ArmaReforgerWorkbenchSteamDiag.exe -publishAddonScreenshots "Z:/mymod/screenshots"
```

### rebuild-database-only[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=43 "Edit section: rebuild-database-only") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=43 "Edit section: rebuild-database-only")]

-rebuild-database-only makes Workbench automatically exit when the database file is refreshed. This is useful for creating game packages which should be distributed with the latest database file.

Example

```
ArmaReforgerWorkbenchSteamDiag.exe -rebuild-database-only
```

### scrDefine[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=44 "Edit section: scrDefine") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=44 "Edit section: scrDefine")]

-scrDefine allows to determine a script preprocessor definition. Multiple ones can be chained using multiple -scrDefine.

ⓘ

This parameter also works for Client and Server executables.

#ifdef TAG\_DEBUG
Print("The TAG\_DEBUG flag is defined.");
#else
Print("The TAG\_DEBUG flag is not defined.");
#endif

Example

```
ArmaReforgerWorkbenchSteamDiag.exe -scrDefine TAG_DEBUG -scrDefine OTHER_FLAG
ArmaReforgerSteam.exe -scrDefine TAG_DEBUG -scrDefine OTHER_FLAG
ArmaReforgerServer.exe -scrDefine TAG_DEBUG -scrDefine OTHER_FLAG
```

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.1.0 "Category:Arma Reforger/Version 1.1.0") [1.1.0](/wiki?title=Category:Arma_Reforger/Version_1.1.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.1.0 (page does not exist)")

### scrNoInline

⚠

This parameter only works on **diag exe**!

-scrNoInline disables inline optimisation for debug purposes.

Example

```
ArmaReforgerSteamDiag.exe -scrNoInline
```

### wbBackendLogin[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=45 "Edit section: wbBackendLogin") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=45 "Edit section: wbBackendLogin")]

-wbBackendLogin enforces a specific account to log in. Password is hidden console log.

Example

```
ArmaReforgerWorkbenchSteamDiag.exe -wbBackendLogin myEmail@bistudio.com myPassword
```

### noGameScriptsOnInit[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=46 "Edit section: noGameScriptsOnInit") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=46 "Edit section: noGameScriptsOnInit")]

-noGameScriptsOnInit skips game scripts compilation on Workbench initialisation.
This results in a faster startup but with the need to recompile scripts before using any script-dependent elements (prefabs, configs, etc).

ⓘ

Game scripts can be compiled in Script Editor using `F7`.

Example

```
ArmaReforgerWorkbenchSteamDiag.exe -noGameScriptsOnInit
```

### enableWARP[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=47 "Edit section: enableWARP") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=47 "Edit section: enableWARP")]

-enableWARP allows [DirectX WARP](https://docs.microsoft.com/en-us/windows/win32/direct3darticles/directx-warp) (Windows Advanced Rasterization Platform) to create a virtual device for computers without a DirectX 12 device.

ⓘ

This parameter does not force WARP, only allows it.

Example

```
ArmaReforgerWorkbenchSteamDiag.exe -enableWARP
```

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_0.9.7 "Category:Arma Reforger/Version 0.9.7") [0.9.7](/wiki?title=Category:Arma_Reforger/Version_0.9.7&action=edit&redlink=1 "Category:Arma Reforger/Version 0.9.7 (page does not exist)")

### clearSettings

-clearSettings, as its name suggests, clears all Workbench user preference - window positions, preview settings, last opened files etc.

Example

```
ArmaReforgerWorkbenchSteamDiag.exe -clearSettings
```

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.1.0 "Category:Arma Reforger/Version 1.1.0") [1.1.0](/wiki?title=Category:Arma_Reforger/Version_1.1.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.1.0 (page does not exist)")

### exitAfterInit

-exitAfterInit makes Workbench automatically exit once it is completely initialised and all startup parameters are executed.

Example

```
ArmaReforgerWorkbenchSteamDiag.exe -exitAfterInit
```

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.1.0 "Category:Arma Reforger/Version 1.1.0") [1.1.0](/wiki?title=Category:Arma_Reforger/Version_1.1.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.1.0 (page does not exist)")

### diagMenu

-diagMenu overrides where diagnostic menu saved settings are stored (see hints section of [Arma Reforger:Diag Menu](/wiki/Arma_Reforger:Diag_Menu "Arma Reforger:Diag Menu") ).

Example

```
ArmaReforgerWorkbenchSteamDiag.exe -diagMenu "file.txt"
```

## Workbench - AnimationEditor Module[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=48 "Edit section: Workbench - AnimationEditor Module") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=48 "Edit section: Workbench - AnimationEditor Module")]

### animDebugger[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=49 "Edit section: animDebugger") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=49 "Edit section: animDebugger")]

⚠

This parameter only works on **diag exe**!

-animDebugger enables animation debugging.

Example

```
ArmaReforgerWorkbenchSteamDiag.exe -animDebugger
```

## Workbench - LocalizationEditor Module[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=50 "Edit section: Workbench - LocalizationEditor Module") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=50 "Edit section: Workbench - LocalizationEditor Module")]

### build[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=51 "Edit section: build") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=51 "Edit section: build")]

-build creates runtime string tables from the opened string table.

Example
:   -wbModule=LocalizationEditor -run -load {C014582791ECBF24}Language/localization.st -build -saveDir C:\tmp\

For building also hidden items from the localisation database, use -scrDefine LOCALIZATION\_BUILD\_HIDDEN:

```
ArmaReforgerWorkbenchSteamDiag.exe -scrDefine LOCALIZATION_BUILD_HIDDEN -wbModule=LocalizationEditor -run -load {C014582791ECBF24}Language/localization.st -build -saveDir C:\tmp\
```

### saveDir[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=52 "Edit section: saveDir") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=52 "Edit section: saveDir")]

-saveDir specifies the (absolute) save directory for the module's build command.

Example

```
see above
```

## Workbench - NavmeshGeneratorMain Module[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=53 "Edit section: Workbench - NavmeshGeneratorMain Module") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=53 "Edit section: Workbench - NavmeshGeneratorMain Module")]

### autogenerate[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=54 "Edit section: autogenerate") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=54 "Edit section: autogenerate")]

-autogenerate generates the provided world's navmesh.

Example

```
// This opens up the World Editor with myworld.ent loaded, connects Navmesh Editor to it and starts navmesh generation of the specified navmesh projects.
// Navmesh Editor generates the navmesh and saves it to the path originally held by NavmeshWorldEntity in the world.
ArmaReforgerWorkbenchSteamDiag.exe -wbModule=NavmeshGeneratorMain -run -autogenerate "world/myworld.ent"
```

navmesh projects are optional and can be ALL to generate all projects.

### saveDir[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=55 "Edit section: saveDir") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=55 "Edit section: saveDir")]

-saveDir is an optional parameter that defines in which directory the data will be exported.

Example

```
ArmaReforgerWorkbenchSteamDiag.exe -wbModule=NavmeshGeneratorMain -run -autogenerate "world/myworld.ent" -saveDir "C:/temp/output/"
```

### logNavmeshGen[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=56 "Edit section: logNavmeshGen") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=56 "Edit section: logNavmeshGen")]

-logNavmeshGen logs output from changing states of the navmesh generator state machine.

ⓘ

This parameter does nothing at the moment as it is always enabled.

## Workbench - ResourceManager Module[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=57 "Edit section: Workbench - ResourceManager Module") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=57 "Edit section: Workbench - ResourceManager Module")]

### buildData[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=58 "Edit section: buildData") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=58 "Edit section: buildData")]

-buildData builds all data of one addon in a specified directory. Without addon specification, it builds data of the current addon (usually a game).
There are two mandatory parameters: PlatformName and TargetFolder. The third parameter, AddonName, is not mandatory.

Example 1

```
This builds data of a current addon
ArmaReforgerWorkbenchSteamDiag.exe -wbModule=ResourceManager -builddata PC "C:\Data\PCData"
```

Example 2

```
This builds data of a specific addon called "ArmaReforger"
ArmaReforgerWorkbenchSteamDiag.exe -wbModule=ResourceManager -builddata PC "C:\Data\PCData" ArmaReforger
```

This will build PC data to the provided directory. Possible options for the platform name are PC, PC\_WB, XBOX\_ONE, XBOX\_SERIES, PS4, HEADLESS.

Resource database cache file resourceDatabase.rdb is being saved after build too. It contains only the files which are part of the build.

State of the incremental build progress is being continuously saved every 60 seconds into LastBuildInfo.binfo file. After an unexpected crash, the next build will continue from this last saved state.

Note that entire workbench application will be exited when build is completed.

### metaFiles[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=59 "Edit section: metaFiles") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=59 "Edit section: metaFiles")]

-metaFiles is an optional parameter which may be used together with [-buildData](#buildData) - it copies .meta files to the built data.

Example

```
ArmaReforgerWorkbenchSteamDiag.exe -wbModule=ResourceManager -buildData PC "C:\Data\PCData" -metaFiles
```

### loadBuiltData[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=60 "Edit section: loadBuiltData") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=60 "Edit section: loadBuiltData")]

-loadBuiltData is an optional parameter which may be used together with [-buildData](#buildData) - built resource is loaded into memory and possible errors are printed into the log. Useful for identifying broken resources.

Example

```
ArmaReforgerWorkbenchSteamDiag.exe -wbmodule=ResourceManager -buildData PC "C:\Data\PCData" -metaFiles -loadBuiltData
```

### includeTypes[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=61 "Edit section: includeTypes") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=61 "Edit section: includeTypes")]

-includeTypes is an optional parameter which may be used together with [-buildData](#buildData) - it specifies a list of file types (separated by a comma) that will be included in data build. Can be combined together with [-excludeTypes](#excludeTypes)

Example

```
ArmaReforgerWorkbenchSteamDiag.exe -wbmodule=ResourceManager -builddata PC "c:\Data\PCData" -metaFiles -includeTypes "edds,xob,emat"
```

### excludeTypes[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=62 "Edit section: excludeTypes") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=62 "Edit section: excludeTypes")]

-excludeTypes is an optional parameter which may be used together with [-buildData](#buildData) - it specifies a list of file types (separated by a comma) that will not be included in data build. Can be combined together with [-includeTypes](#excludeTypes)

Example

```
ArmaReforgerWorkbenchSteamDiag.exe -wbmodule=ResourceManager -builddata PC "C:\Data\PCData" -metaFiles -excludeTypes "nmn,ent,terr"
```

### filterPath[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=63 "Edit section: filterPath") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=63 "Edit section: filterPath")]

-filterPath is an optional parameter which may be used together with [-buildData](#buildData) - it allows to limit data build to only a single folder or file.

Example 1

```
ArmaReforgerWorkbenchSteamDiag.exe -wbmodule=ResourceManager -buildData -filterPath "c:\Data\ArmaReforger\worlds\Arland"
```

This limits that only data from this specific folder will go to data build

Example 2

```
ArmaReforgerWorkbenchSteamDiag.exe -wbmodule=ResourceManager -buildData -filterPath "c:\Data\ArmaReforger\worlds\Arland\Arland.ent"
```

This limits that only data that belong to Arland.ent will go to data build (ent, smap, smd, ...)

### filterTags[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=64 "Edit section: filterTags") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=64 "Edit section: filterTags")]

-filterTags is an optional parameter which may be used together with [-buildData](#buildData) - it can be used to exclude/include particular files and folders from/to data build.
It is based on tagging files and folders through the "Build Tags" property which is part of file/folder import settings. This parameter has a form of a C++ expression where can be used only string tags and these three operators:

- **&** - logical AND
- **|** - logical OR
- **!** - logical NOT

Examples

```
ArmaReforgerWorkbenchSteamDiag.exe -filterTags "!wbOnly & !testOnly & !consolesOnly"
```

This includes to build everything except files and folders tagged as wbOnly or testOnly or consolesOnly

```
ArmaReforgerWorkbenchSteamDiag.exe -filterTags "wbOnly"
```

This includes to build only files and folders tagged as wbOnly

```
ArmaReforgerWorkbenchSteamDiag.exe -filterTags "wbOnly & !testOnly"
```

This includes to build only files and folders tagged as wbOnly but without those of them that are tagged as testOnly

```
ArmaReforgerWorkbenchSteamDiag.exe -filterTags "wbOnly | testOnly"
```

This includes to build only files and folders tagged as wbOnly or testOnly

```
ArmaReforgerWorkbenchSteamDiag.exe -filterTags "(!wbOnly & !testOnly) | alwaysIncluded"
```

This includes to build everything except files and folders tagged as wbOnly or testOnly. If some file or folder is tagged with alwaysIncluded it will be included even though it's located inside a wbOnly folder for example

### wbSilent[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=65 "Edit section: wbSilent") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=65 "Edit section: wbSilent")]

-wbsilent initialises the engine, workbench modules and exit without opening any windows; it can be used to validate engine/game initialisation and script compilation.

## Workbench - ScriptEditor Module[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=66 "Edit section: Workbench - ScriptEditor Module") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=66 "Edit section: Workbench - ScriptEditor Module")]

### validate[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=67 "Edit section: validate") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=67 "Edit section: validate")]

string value, default ALL

-validate checks if the game scripts are compilable and returns Workbench application return code of "-1" when compilation failed and "0" when compilation was successful.
Value is optional and script configuration name is expected (Configurations can be found in project settings, it is usually PC, XBOX\_SERIES, etc).

## Workbench - WorldEditor Module[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=68 "Edit section: Workbench - WorldEditor Module") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=68 "Edit section: Workbench - WorldEditor Module")]

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.1.0 "Category:Arma Reforger/Version 1.1.0") [1.1.0](/wiki?title=Category:Arma_Reforger/Version_1.1.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.1.0 (page does not exist)")

### forceSaveAll

-forceSaveAll

- **Normal edit mode:** re-saves all entity layers of opened map (.ent file + its .layer files) and also all prefabs and configs that are used in the opened map.
- **Prefab edit mode:** re-saves only opened prefab and also all other prefabs and configs that are used in the opened prefab.

Example 1 (normal edit mode)

```
ArmaReforgerWorkbenchSteamDiag.exe -wbModule=WorldEditor -run -load "world/myworld.ent" -forceSaveAll
```

Example 2 (prefab edit mode)

```
ArmaReforgerWorkbenchSteamDiag.exe -wbModule=WorldEditor -run -load "Prefabs/Structures/Houses/Villa/Villa_E_2I01/Villa_E_2I01.et" -forceSaveAll
```

## Hosting[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=69 "Edit section: Hosting") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=69 "Edit section: Hosting")]

⚠

These parameters apply to the Server executable.

### a2sIpAddress[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=70 "Edit section: a2sIpAddress") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=70 "Edit section: a2sIpAddress")]

-a2sIpAddress sets Steam Query Protocol's Bind IP address.

Example

```
ArmaReforgerServer.exe -a2sIpAddress 192.168.1.10
```

### a2sPort[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=71 "Edit section: a2sPort") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=71 "Edit section: a2sPort")]

-a2sPort sets Steam Query Protocol's Bind port.

Example

```
ArmaReforgerServer.exe -a2sPort 7777
```

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_0.9.6 "Category:Arma Reforger/Version 0.9.6") [0.9.6](/wiki?title=Category:Arma_Reforger/Version_0.9.6&action=edit&redlink=1 "Category:Arma Reforger/Version 0.9.6 (page does not exist)")

### autoreload

-autoreload reloads the scenario when the session ends after the provided delay, without shutting down the server. Value is in seconds.

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.3.0 "Category:Arma Reforger/Version 1.3.0") [1.3.0](/wiki?title=Category:Arma_Reforger/Version_1.3.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.3.0 (page does not exist)") the disabled value makes the *server* restart and relaunch as the same instance.

Example

```
ArmaReforgerServer.exe -autoreload 10
```

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.3.0 "Category:Arma Reforger/Version 1.3.0") [1.3.0](/wiki?title=Category:Arma_Reforger/Version_1.3.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.3.0 (page does not exist)")

### autoshutdown

-autoshutdown ensures the correct server shutdown process.

To give 30 seconds post game and then shutdown:

Example

```
ArmaReforgerServer.exe -autoreload 30 -autoshutdown
```

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.3.0 "Category:Arma Reforger/Version 1.3.0") [1.3.0](/wiki?title=Category:Arma_Reforger/Version_1.3.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.3.0 (page does not exist)")

To circumvent 1.3.0's server restart issue, make sure to use this command and configure the server to automatically restart on failure or shutdown.

### bindIP[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=72 "Edit section: bindIP") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=72 "Edit section: bindIP")]

-bindIP can be used to override both [gameHostBindAddress](/wiki/Arma_Reforger:Server_Hosting#gameHostBindAddress "Arma Reforger:Server Hosting") and [gameHostRegisterBindAddress](/wiki/Arma_Reforger:Server_Hosting#gameHostRegisterBindAddress "Arma Reforger:Server Hosting") values present in [server config](/wiki/Arma_Reforger:Server_Hosting#Configuration_File "Arma Reforger:Server Hosting").

Example

```
ArmaReforgerServer.exe -bindIP 192.168.1.42
```

### bindPort[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=73 "Edit section: bindPort") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=73 "Edit section: bindPort")]

-bindPort can be used to override both [gameHostBindPort](/wiki/Arma_Reforger:Server_Hosting#gameHostBindPort "Arma Reforger:Server Hosting") and [gameHostRegisterBindPort](/wiki/Arma_Reforger:Server_Hosting#gameHostRegisterBindPort "Arma Reforger:Server Hosting") values present in [server config](/wiki/Arma_Reforger:Server_Hosting#Configuration_File "Arma Reforger:Server Hosting").

Example

```
ArmaReforgerServer.exe -bindPort 2302
```

### config[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=74 "Edit section: config") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=74 "Edit section: config")]

-config is used by servers to point to a JSON server configuration.

ⓘ

See  [Server Hosting](/wiki/Arma_Reforger:Server_Hosting "Arma Reforger:Server Hosting") for more information.

Example

```
ArmaReforgerServer.exe -config myConfigFile.json
```

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.6.0 "Category:Arma Reforger/Version 1.6.0") [1.6.0](/wiki?title=Category:Arma_Reforger/Version_1.6.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.6.0 (page does not exist)")

### enableNightGrain

*was forceDisableNightGrain in 1.6.0.54, changed in 1.6.0.76*

-enableNightGrain enables night grain in multiplayer (dedicated or hosted).

ⓘ

Single player always has night grain disabled.

Example

```
ArmaReforgerServer.exe -enableNightGrain
```

### listScenarios[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=75 "Edit section: listScenarios") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=75 "Edit section: listScenarios")]

-listScenarios prints to game logs the scenario .conf file paths.

ⓘ

- Workshop scenarios will only be listed if no scenario is being loaded (through e.g a [config](#config)).
- A list of official scenarios can be found on [Server Config - scenarioId](/wiki/Arma_Reforger:Server_Config#scenarioId "Arma Reforger:Server Config").

Example

```
ArmaReforgerServer.exe -listScenarios
```

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.0.0 "Category:Arma Reforger/Version 1.0.0") [1.0.0](/wiki?title=Category:Arma_Reforger/Version_1.0.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.0.0 (page does not exist)")[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.6.0 "Category:Arma Reforger/Version 1.6.0") [1.6.0](/wiki?title=Category:Arma_Reforger/Version_1.6.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.6.0 (page does not exist)")

### loadSessionSave

-loadSessionSave allows the game to load a previous game session.

It can be used alone to load the latest save, or with a specific save file name.
When left empty, it will attempt to locate the latest save game data for the current scenario on launch.
Optionally, as a parameter, the UUID of a specific save can be passed, which is found inside each save point's meta-info.json file.

Examples

```
ArmaReforgerSteam.exe -loadSessionSave
ArmaReforgerSteam.exe -loadSessionSave 07e8fa26-d5b6-47f6-a19b-641c72ab05bc
ArmaReforgerServer.exe -loadSessionSave
ArmaReforgerServer.exe -loadSessionSave 0d51b792-2a66-47d2-b71a-09086cffba3a
```

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.6.0 "Category:Arma Reforger/Version 1.6.0") [1.6.0](/wiki?title=Category:Arma_Reforger/Version_1.6.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.6.0 (page does not exist)")

### keepSessionSave

-keepSessionSave keeps any save data for completed playthroughs on the game's end screen.

Example

```
ArmaReforgerSteam.exe -keepSessionSave
```

### logStats[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=76 "Edit section: logStats") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=76 "Edit section: logStats")]

-logStats allows to log performance statistics every (optionally) defined interval (in milliseconds).

Example
:   ArmaReforgerServer.exe -logStats - logs every seconds
:   ArmaReforgerServer.exe -logStats 10000 - only logs every 10s

Result
:   `FPS: 60.0, frame time (avg: 16.7 ms, min: 9.3 ms, max: 23.7 ms), Mem: 3291106 kB, Player: 2, AI: 104, Veh: 0 (17), Proj (S: 12, M: 0, G: 0 | 12), RplItemsS: 410, RplItemsC0: 17068`

- FPS: <float> = Current server FPS value
- frame time (avg: <float> ms, min: <float> ms, max: <float> ms) = The average, minimum, & maximum server frame times
- Mem: <int> = The current memory usage in kilobytes as reported internally by the server
- Player: <int> = Number of current players
- AI: <int> = Number of current AI spawned on the server
- Veh <int> (<int>) = The value inside the parentheses is the current number of vehicles spawned on the server
- Proj:
  - S: <int> = Number of active **s**hells
  - M: <int> = Number of active **m**issiles
  - G: <int> = Number of active **g**renades
  - | <int> = Total projectiles
- RplItemsS: <int> = The number of dynamic (spawned during game) streams on the server
- RplItemsC0: <int> = The number of streams open to the client

⚠

The memory usage reported by -logStats is a server approximation and is therefore not 100% accurate when compared with the operating system values.

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.3.0 "Category:Arma Reforger/Version 1.3.0") [1.3.0](/wiki?title=Category:Arma_Reforger/Version_1.3.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.3.0 (page does not exist)")

### logVoting

-logVoting adds logging info to the voting system with information about who created, voted, and against whom the vote was created.

Example

```
ArmaReforgerServer.exe -logVoting
```

### maxFPS[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=77 "Edit section: maxFPS") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=77 "Edit section: maxFPS")]

-maxFPS sets max FPS limit - useful for a server, or to force a client's max FPS.

ⓘ

Also works on clients, even though the Video Settings FPS limitation option is preferred.

Example

```
ArmaReforgerServer.exe -maxFPS 30
```

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.7.0 "Category:Arma Reforger/Version 1.7.0") [1.7.0](/wiki?title=Category:Arma_Reforger/Version_1.7.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.7.0 (page does not exist)")

### playerLimits

-playerLimits sets the maximum players count per faction. The format is FactionKey:Number, multiple entries are separated by a comma.

Example

```
ArmaReforgerServer.exe -playerLimits "FIA:3"
ArmaReforgerServer.exe -playerLimits "US:1,USSR:1,FIA:62"
```

### server[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=78 "Edit section: server") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=78 "Edit section: server")]

This parameter instructs the executable to launch **local** server and **load selected world**. When this parameter is used, config is ignored. Server parameter can be combined with [addons](#addons) & [addonsDir](#addonsDir) parameters to start a server with local mods, which can be useful when testing addon before uploading it Workshop.

```
ArmaReforgerServer.exe -server "worlds/MP/MPTest.ent" -addonsDir "C:\MyModsDir" -addons MyCustomMod
```

⚠

These parameters apply to the Game executable.

### client[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=79 "Edit section: client") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=79 "Edit section: client")]

-client starts up an RplSession in local client mode. The session tries to connect to the provided IP.

Example #1

```
ArmaReforgerSteam.exe -client 127.0.0.1
```

Example #2

This will connect the game to a [locally hosted dedicated server](#server) and load the local MyCustomMod addon:

```
ArmaReforgerSteam.exe -client -addonsDir "C:\MyModsDir" -addons MyCustomMod
```

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.6.0 "Category:Arma Reforger/Version 1.6.0") [1.6.0](/wiki?title=Category:Arma_Reforger/Version_1.6.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.6.0 (page does not exist)")

### worldSystemsConfig

⚠

This parameter only works on **diag exe**!

-worldSystemsConfig allows setting a world systems config file (of type SystemSettings) **when starting a mission directly** (*not* via MissionHeader.conf).

Example

```
ArmaReforgerSteamDiag.exe -worldSystemsConfig "{C50579A2EC48E8EF}Configs/Systems/MissionSystems.conf"
```

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.3.0 "Category:Arma Reforger/Version 1.3.0") [1.3.0](/wiki?title=Category:Arma_Reforger/Version_1.3.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.3.0 (page does not exist)")

### addonsVerify

⚠

This parameter only works on **dedicated server**!

Verifies the integrity of all installed addons. If any corrupted addons are found, the server will shut down and log the corrupted files. The verification may take some time depending on the size of the addons.

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.3.0 "Category:Arma Reforger/Version 1.3.0") [1.3.0](/wiki?title=Category:Arma_Reforger/Version_1.3.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.3.0 (page does not exist)")

### addonsRepair

⚠

This parameter only works on **dedicated server**!

Verifies the integrity of all installed addons. If any corrupted addons are found, they will be repaired automatically. If the repair fails, the server will shut down.
The repair duration depends on the addons size and the amount of data which needs to be repaired.

## Network Tuning[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=80 "Edit section: Network Tuning") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=80 "Edit section: Network Tuning")]

⚠

These parameters apply to the Server executable.

### nds[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=81 "Edit section: nds") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=81 "Edit section: nds")]

number value, default 2

-nds Network Dynamic Simulation (nds) is a server feature that only streams in relevant replicated entities for each client.
The provided value stands for diameter, or the number of cells which are being replicated.
To turn the feature off use -nds 0. A higher diameter will result in a bigger networked view range, lower server performance.

Example

```
ArmaReforgerServer.exe -nds 1
```

### nwkResolution[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=82 "Edit section: nwkResolution") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=82 "Edit section: nwkResolution")]

-nwkResolution defines what resolution Spatial Map cells should be set at in a 100..1000m range. Smaller resolution will result in less "pop-in" but lower networked view range.
For high view range use high resolution, but small diameter.

Example

```
ArmaReforgerServer.exe -nwkResolution 500
```

### rpl-timeout-ms[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=83 "Edit section: rpl-timeout-ms") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=83 "Edit section: rpl-timeout-ms")]

-rpl-timeout-ms sets the client/server timeout's value, in milliseconds.

Example

```
ArmaReforgerServer.exe -rpl-timeout-ms 10000
```

### rpl-timeout-disable[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=84 "Edit section: rpl-timeout-disable") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=84 "Edit section: rpl-timeout-disable")]

⚠

This parameter only works on **diag exe**!

-rpl-timeout-disable disables the client/server timeout.

⚠

If a connection stops responding, the server's performance will deteriorate until it eventually runs out of memory and crash. This parameter is strictly for debugging purposes.

Example

```
ArmaReforgerSteamDiag.exe -rpl-timeout-disable
```

### rpl-reconnect[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=85 "Edit section: rpl-reconnect") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=85 "Edit section: rpl-reconnect")]

⚠

This parameter only works on **diag exe**, on **client**!

The replication tries to reconnect instead of immediately raising connection.

Example

```
ArmaReforgerSteamDiag.exe -rpl-reconnect
```

### rpl-vcons[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=86 "Edit section: rpl-vcons") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=86 "Edit section: rpl-vcons")]

number value, range 0..200, default 0

⚠

This parameter only works on **diag exe**!

-rpl-vcons sets the number of virtual connections.

Example

```
ArmaReforgerSteamDiag.exe -rpl-vcons 5
```

### rpl-validation-rdb-disable[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=87 "Edit section: rpl-validation-rdb-disable") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=87 "Edit section: rpl-validation-rdb-disable")]

⚠

This parameter only works on **diag exe**!

-rpl-validation-rdb-disable disables the validation of clients resource database.

⚠

Make sure you know what you are doing first before disabling the validation as data mismatch may bring undefined behaviour!

Example

```
ArmaReforgerSteamDiag.exe -rpl-validation-rdb-disable
```

### rpl-validation-scr-disable[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=88 "Edit section: rpl-validation-scr-disable") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=88 "Edit section: rpl-validation-scr-disable")]

⚠

This parameter only works on **diag exe**!

-rpl-validation-scr-disable disables the validation of clients scripts checksum.

⚠

Make sure you know what you are doing first before disabling the validation as script mismatch may bring undefined behaviour!

Example

```
ArmaReforgerSteamDiag.exe -rpl-validation-scr-disable
```

### rpl-validation-version-disable[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=89 "Edit section: rpl-validation-version-disable") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=89 "Edit section: rpl-validation-version-disable")]

⚠

This parameter only works on **diag exe**!

-rpl-validation-version-disable disables the validation of clients executable version.

⚠

Make sure you know what you are doing first before disabling the validation as version mismatch may bring undefined behaviour!

Example

```
ArmaReforgerSteamDiag.exe -rpl-validation-version-disable
```

### rpl-validation-devbin-disable[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=90 "Edit section: rpl-validation-devbin-disable") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=90 "Edit section: rpl-validation-devbin-disable")]

⚠

This parameter only works on **diag exe**!

-rpl-validation-devbin-disable disables the validation of developer binary flag, allowing mixing developer and non-developer binaries in the same multiplayer session.

### staggeringBudget[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=91 "Edit section: staggeringBudget") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=91 "Edit section: staggeringBudget")]

-staggeringBudget defines how many stationary spatial map cells are allowed to be processed in one tick in 1..10201 range. If not set it uses "[-nds](#nds)" diameter.
A lower number will limit how many cells the server has to process per tick, but increase the time it takes for a client to have all relevant entities streamed in.
If the server experiences significant performance drops on spawning/teleporting then the number is set too high, if the client experiences "pop-in" of replicated items then the number is set too low.

Example

```
ArmaReforgerServer.exe -staggeringBudget 5000
```

### streamingBudget[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=92 "Edit section: streamingBudget") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=92 "Edit section: streamingBudget")]

-streamingBudget is the global streaming budget that is equally distributed between all connections.
To decrement the budget, it uses the replicated hierarchy size of each entity that needs to be streamed in. It cannot go under 100 to prevent the system stalling.
A lower number will limit how many entities the server has to process per tick, but increase the time it takes for a client to have that entity streamed in, if the server experiences significant performance drops on spawning/teleporting then the number is set too high, if the client experiences "pop-in" of replicated items then the number is set too low.

Example

```
ArmaReforgerServer.exe -streamingBudget 500
```

### streamsDelta[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=93 "Edit section: streamsDelta") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=93 "Edit section: streamsDelta")]

number value, range 1..1000, default 100

-streamsDelta is a tool to limit the amount of streams being opened for a client.
If the difference between 'the number of streams the server has open' and 'the number of streams the client has open' is larger than the NUMBER then the server will not open any more streams this tick.
To be adjusted based on average client networking speed.

Example

```
ArmaReforgerServer.exe -streamsDelta 200
```

## Debug[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=94 "Edit section: Debug") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=94 "Edit section: Debug")]

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_0.9.8 "Category:Arma Reforger/Version 0.9.8") [0.9.8](/wiki?title=Category:Arma_Reforger/Version_0.9.8&action=edit&redlink=1 "Category:Arma Reforger/Version 0.9.8 (page does not exist)")

### AILimit

-aiLimit sets the top limit of AIs. No systems will be able to spawn any AIs when this ceiling is reached (through aiWorld.CanAICharacterBeAdded() - see also [SCR\_AIGroup](enfusion://ScriptEditor/scripts/Game/Entities/SCR_AIGroup.c;74) and [SCR\_ChimeraAIAgent](enfusion://ScriptEditor/scripts/Game/AI/SCR_ChimeraAIAgent.c;5)).

ⓘ

A value less than or equal to zero simply disables any possible AI.

⚠

This parameter applies to both hosted and singleplayer scenarios; while it can save performance, it can also break the experience!

Example

```
ArmaReforgerServer.exe -AILimit 32
ArmaReforgerServer.exe -AILimit 0
ArmaReforgerSteam.exe -AILimit 100
ArmaReforgerSteam.exe -AILimit 0
```

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.0.0 "Category:Arma Reforger/Version 1.0.0") [1.0.0](/wiki?title=Category:Arma_Reforger/Version_1.0.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.0.0 (page does not exist)")

### AIPartialSim

number value, default: 4

-aiPartialSim sets in how many batches all simulable AIs will divided and processed.  
e.g: 150 AIs, 100 simulable AIs (100 in LOD0, 50 in LOD 10), these 100 AIs divided by 4 will make four batches of 25 AIs each.

Example

```
ArmaReforgerSteam.exe -aiPartialSim 5
```

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.0.0 "Category:Arma Reforger/Version 1.0.0") [1.0.0](/wiki?title=Category:Arma_Reforger/Version_1.0.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.0.0 (page does not exist)")

### autodeployFaction

*alias: tdmf*

-autodeployFaction overrides respawn faction (e.g US, USSR, FIA etc).

⚠

This parameter only works on **diag exe**!

Example

```
ArmaReforgerSteamDiag.exe -autodeployFaction US
```

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.0.0 "Category:Arma Reforger/Version 1.0.0") [1.0.0](/wiki?title=Category:Arma_Reforger/Version_1.0.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.0.0 (page does not exist)")

### autodeployLoadout

*alias: tdml*

⚠

This parameter only works on **diag exe**!

-autodeployLoadout overrides respawn role (using role name from scenario's LoadoutManager).

Example

```
ArmaReforgerSteamDiag.exe -autodeployLoadout #AR-Role_Driver
```

### createDB[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=95 "Edit section: createDB") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=95 "Edit section: createDB")]

-createDB forces database file's regeneration. Useful after file directories changes, when some resources were moved elsewhere.
Takes a few seconds upon start.

Example

```
ArmaReforgerServer.exe -createDB
```

### debugger[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=96 "Edit section: debugger") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=96 "Edit section: debugger")]

string value, default localhost

-debugger sets the script debugger to a specific address.

Example

```
ArmaReforgerServer.exe -debugger 192.168.0.5
```

### debuggerPort[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=97 "Edit section: debuggerPort") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=97 "Edit section: debuggerPort")]

number value, default 1000

-debuggerPort sets the script debugger to a specific port.

Example

```
ArmaReforgerServer.exe -debuggerPort 1040
```

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.1.0 "Category:Arma Reforger/Version 1.1.0") [1.1.0](/wiki?title=Category:Arma_Reforger/Version_1.1.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.1.0 (page does not exist)")

### disableAI

-disableAI disables AIWorld initialisation and ticking.

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_0.9.7 "Category:Arma Reforger/Version 0.9.7") [0.9.7](/wiki?title=Category:Arma_Reforger/Version_0.9.7&action=edit&redlink=1 "Category:Arma Reforger/Version 0.9.7 (page does not exist)")

### disableCrashReporter

-disableCrashReporter disables the [Crash Reporter](/wiki/Arma_Reforger:Crash_Report#Crash_Reporter "Arma Reforger:Crash Report") from appearing/sending reports automatically.

Example

```
ArmaReforgerSteam.exe -disableCrashReporter
```

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.1.0 "Category:Arma Reforger/Version 1.1.0") [1.1.0](/wiki?title=Category:Arma_Reforger/Version_1.1.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.1.0 (page does not exist)")

### disableNavmeshStreaming

-disableNavmeshStreaming disables navmesh streaming on all navmesh worlds.

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/22px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.2.0 "Category:Arma Reforger/Version 1.2.0") [1.2.0](/wiki?title=Category:Arma_Reforger/Version_1.2.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.2.0 (page does not exist)") added navmesh projects list support ("BTRlike" for vehicles, "Soldiers" for characters), separated by commas.

Example

```
ArmaReforgerSteam.exe -disableNavmeshStreaming
ArmaReforgerSteam.exe -disableNavmeshStreaming NAVMESH_PROJECT1,NAVMESHPROJECT2,etc // >= 1.2.0
```

### disableShadersBuild[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=98 "Edit section: disableShadersBuild") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=98 "Edit section: disableShadersBuild")]

-disableShadersBuild disables shaders generation.

Example

```
ArmaReforgerServer.exe -disableShadersBuild
```

### generateShaders[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=99 "Edit section: generateShaders") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=99 "Edit section: generateShaders")]

-generateShaders forces shaders generation.

Example

```
ArmaReforgerServer.exe -generateShaders
```

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.1.0 "Category:Arma Reforger/Version 1.1.0") [1.1.0](/wiki?title=Category:Arma_Reforger/Version_1.1.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.1.0 (page does not exist)")

### rplEncodeAsLongJobs

-rplEncodeAsLongJobs makes replication use long encoding jobs instead of short ones.

Example

```
ArmaReforgerServer.exe -rplEncodeAsLongJobs
```

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.1.0 "Category:Arma Reforger/Version 1.1.0") [1.1.0](/wiki?title=Category:Arma_Reforger/Version_1.1.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.1.0 (page does not exist)")

### jobsysShortWorkerCount

-jobsysShortWorkerCount sets the number of threads working on short jobs (jobs that must finish in one update loop).
Value is limited to the number of CPUs or 16, whichever is the lowest.

Example

```
ArmaReforgerServer.exe -jobsysShortWorkerCount 4
```

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.1.0 "Category:Arma Reforger/Version 1.1.0") [1.1.0](/wiki?title=Category:Arma_Reforger/Version_1.1.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.1.0 (page does not exist)")

### jobsysLongWorkerCount

number value, range 1..CPUCount, default *[jobsysShortWorkerCount](#jobsysShortWorkerCount) / 2*

-jobsysLongWorkerCount sets the number of threads working on long jobs (jobs that can span multiple iterations of update loop).

Example

```
ArmaReforgerServer.exe -jobsysLongWorkerCount 4
```

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_0.9.8 "Category:Arma Reforger/Version 0.9.8") [0.9.8](/wiki?title=Category:Arma_Reforger/Version_0.9.8&action=edit&redlink=1 "Category:Arma Reforger/Version 0.9.8 (page does not exist)")

### keepNumOfLogs

number value, default: 10

-keepNumOfLogs sets the maximum amount of logs to keep.

Example

```
ArmaReforgerServer.exe -keepNumOfLogs 3
```

### log-rdb-checksum[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=100 "Edit section: log-rdb-checksum") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=100 "Edit section: log-rdb-checksum")]

-log-rdb-checksum enables logging detailed information about RDB checksum computation (GUID and path in order in which they are added to checksum) for RDB checksum issues debugging.

Example

```
ArmaReforgerSteam.exe -log-rdb-checksum
```

### log-scr-checksum[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=101 "Edit section: log-scr-checksum") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=101 "Edit section: log-scr-checksum")]

-log-scr-checksum enables the logging of all script files used in compilation and their checksums.

Example

```
ArmaReforgerSteam.exe -log-scr-checksum
```

### logAppend[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=102 "Edit section: logAppend") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=102 "Edit section: logAppend")]

-logAppend makes logs to not be emptied on game start, keeping the history.

Example

```
ArmaReforgerSteam.exe -logAppend
```

### logFS[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=103 "Edit section: logFS") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=103 "Edit section: logFS")]

-logFS logs every *F'*ile **S**ystem's read/write operation in filesystem.log. This option is very demanding and can produce a big file in a short time, so it is to be used with caution for debug purpose only.

Example

```
ArmaReforgerSteam.exe -logFS
```

### logLevel[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=104 "Edit section: logLevel") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=104 "Edit section: logLevel")]

-logLevel allows for different log levels. Each level includes the ones below it (e.g error includes error and fatal).
Possible values range from normal (where everything is logged) to fatal (where only extreme issues are logged):

- normal
- warning
- error
- fatal

Example

```
ArmaReforgerSteam.exe -logLevel warning
```

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.3.0 "Category:Arma Reforger/Version 1.3.0") [1.3.0](/wiki?title=Category:Arma_Reforger/Version_1.3.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.3.0 (page does not exist)")

### logTime

string value, default time

-logTime sets log line time's format; can be one of:

- none
- time (default)
- datetime

Example

```
ArmaReforgerServer.exe -logTime datetime
```

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_0.9.8 "Category:Arma Reforger/Version 0.9.8") [0.9.8](/wiki?title=Category:Arma_Reforger/Version_0.9.8&action=edit&redlink=1 "Category:Arma Reforger/Version 0.9.8 (page does not exist)")

### logsDir

-logsDir defines the directory by name (located in the [profile](#profile) directory) or [![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/22px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_0.9.8 "Category:Arma Reforger/Version 0.9.8") [0.9.8](/wiki?title=Category:Arma_Reforger/Version_0.9.8&action=edit&redlink=1 "Category:Arma Reforger/Version 0.9.8 (page does not exist)") by path, in which game instance specific data are stored like logs, memory dumps, temporary files etc.

Examples

```
ArmaReforgerSteam.exe -logsDir "DebugSession1337"								// targets <profileDir>\logs\DebugSession1337
ArmaReforgerSteam.exe -logsDir "C:\Users\MyUserName\Documents\ArmaReforgerDir"	// targets "C:\Users\MyUserName\Documents\ArmaReforgerDir" directly
```

### keepCrashFiles[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=105 "Edit section: keepCrashFiles") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=105 "Edit section: keepCrashFiles")]

-keepCrashFiles prevents Crash Reporter from cleaning crash files on successful crash sending.

Example

```
ArmaReforgerSteam.exe -keepCrashFiles
```

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.1.0 "Category:Arma Reforger/Version 1.1.0") [1.1.0](/wiki?title=Category:Arma_Reforger/Version_1.1.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.1.0 (page does not exist)")

### minidump

-minidump allows minidump generation to include additional data; possible values:

| Value | Microsoft Equivalent (see [minidump API documentation](https://learn.microsoft.com/en-us/windows/win32/api/minidumpapiset/ne-minidumpapiset-minidump_type#constants)) |
| --- | --- |
| normal | MiniDumpNormal |
| dataSegs | MiniDumpWithDataSegs |
| fullMemory | MiniDumpWithFullMemory |

Example

```
ArmaReforgerSteam.exe -minidump fullMemory
```

### scriptAuthorizeAll[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=106 "Edit section: scriptAuthorizeAll") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=106 "Edit section: scriptAuthorizeAll")]

-scriptAuthorizeAll disables the security popup about RunCmd, RunProcess, KillProcess and (outside of profile directory) FileIO operations.

Example

```
ArmaReforgerSteam.exe -scriptAuthorizeAll
```

[![armareforger-symbol black.png](/wikidata/images/thumb/6/69/armareforger-symbol_black.png/30px-armareforger-symbol_black.png)](/wiki/Category:Arma_Reforger/Version_1.0.0 "Category:Arma Reforger/Version 1.0.0") [1.0.0](/wiki?title=Category:Arma_Reforger/Version_1.0.0&action=edit&redlink=1 "Category:Arma Reforger/Version 1.0.0 (page does not exist)")

### silentCrashReport

-silentCrashReport suppresses the Crash Reporter's dialog and automatically sends the report without any user input or message.

Example

```
ArmaReforgerWorkbenchSteamDiag.exe -silentCrashReport
```

*was **noCrashDialog** before 1.0.0*

### VMErrorMode[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=107 "Edit section: VMErrorMode") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=107 "Edit section: VMErrorMode")]

-VMErrorMode sets script VM's error reporting mode; possible values are:

- silent - silently continues
- log\_only - logs the error then continues
- full - shows the VME dialog, logs the error, then clicking "ignore" continues execution
- fatal - logs the error then exits the game

## Profiling[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=108 "Edit section: Profiling") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=108 "Edit section: Profiling")]

### checkInstance[[edit](/wiki?title=Arma_Reforger:Startup_Parameters&veaction=edit&section=109 "Edit section: checkInstance") | [edit source](/wiki?title=Arma_Reforger:Startup_Parameters&action=edit&section=109 "Edit section: checkInstance")]

-checkInstance turns on script VM's memory allocations logging - see [Script Profiling](/wiki/Arma_Reforger:Script_Profiling "Arma Reforger:Script Profiling").

Example

```
ArmaReforgerSteam.exe -checkInstance
```