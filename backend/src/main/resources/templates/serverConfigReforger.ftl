<#-- @ftlvariable name="" type="cz.forgottenempire.servermanager.serverinstance.entities.ReforgerServer" -->
{
"bindAddress": "",
"bindPort": ${port},
"publicAddress": "",
"publicPort": ${port},
"game": {
"name": "${name!}",
"password": "${password!}",
"passwordAdmin": "${adminPassword!}",
"scenarioId": "${scenarioId!}",
"maxPlayers": ${maxPlayers},
"visible": true,
<#--TODO make supported client types configurable-->
"supportedPlatforms": [
"PLATFORM_PC"
],
"gameProperties": {
"serverMaxViewDistance": 2500,
"serverMinGrassDistance": 50,
"networkViewDistance": 1000,
"disableThirdPerson": ${thirdPersonViewEnabled?then('false', 'true')},
"fastValidation": true,
"battlEye": ${battlEye?then('true', 'false')},
"VONDisableUI": true,
"VONDisableDirectSpeechUI": true
},
"mods": [
<#list activeMods as mod>
  {
  "modId": "${mod.id}",
  "name": "${mod.name}"
  }<#sep>,</#sep>
</#list>
]
},
"a2s": {
"address": "0.0.0.0",
"port": ${queryPort}
}
}