<#-- @ftlvariable name="" type="cz.forgottenempire.arma3servergui.serverinstance.entities.ReforgerServer" -->
{
"dedicatedServerId": "${dedicatedServerId!}",
"region": "EU",
"gameHostBindAddress": "",
"gameHostBindPort": ${port?string.computer},
"gameHostRegisterBindAddress": "",
"gameHostRegisterPort": ${port?string.computer},
"adminPassword": "${adminPassword!}",
"game": {
"name": "${name!}",
"password": "${password!}",
"scenarioId": "${scenarioId!}",
"playerCountLimit": ${maxPlayers?string.computer},
"autoJoinable": false,
"visible": true,
"supportedGameClientTypes": [
"PLATFORM_PC",
"PLATFORM_XBL"
],
"gameProperties": {
"serverMaxViewDistance": 2500,
"serverMinGrassDistance": 50,
"networkViewDistance": 1000,
"disableThirdPerson": ${thirdPersonViewEnabled?then('false', 'true')},
"fastValidation": true,
"battlEye": ${battlEye?then('true', 'false')},
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
<#list activeMods as mod>
  {
  "modID": "${mod.id}",
  "name": "${mod.name}"
  }<#sep>,</#sep>
</#list>
]
},
"a2sQueryEnabled": true,
"steamQueryPort": ${queryPort?string.computer}
}