<#-- @ftlvariable name="" type="cz.forgottenempire.servermanager.serverinstance.entities.DayZServer" -->

// GLOBAL SETTINGS
hostname = "${name}";
password = "${password!}";
passwordAdmin = "${adminPassword!}";
steamQueryPort = ${queryPort};
guaranteedUpdates = 1;
storageAutoFix = 1;

// JOINING RULES
maxPlayers = ${maxPlayers};
allowFilePatching = <#if clientFilePatching>1<#else>0</#if>;
verifySignatures = 2;
forceSameBuild = ${forceSameBuild?then('1', '0')};

// INGAME SETTINGS
disableVoN = ${vonEnabled?then('0', '1')};
vonCodecQuality = 30;
serverTimePersistent = ${persistent?then('1', '0')};
serverTimeAcceleration = ${timeAcceleration};
serverNightTimeAcceleration = ${nightTimeAcceleration};
disable3rdPerson = ${thirdPersonViewEnabled?then('0', '1')};
disableCrosshair = ${crosshairEnabled?then('0', '1')};
respawnTime = ${respawnTime};

instanceId = ${id};

${additionalOptions!}