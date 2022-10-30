<#-- @ftlvariable name="" type="cz.forgottenempire.arma3servergui.serverinstance.entities.Arma3Server" -->

// GLOBAL SETTINGS
hostname = "${name}";
password = "${password!}";
passwordAdmin = "${adminPassword!}";

// JOINING RULES
maxPlayers = ${maxPlayers};
kickDuplicate = 1;
verifySignatures = <#if verifySignatures>2<#else>0</#if>;
allowedFilePatching = <#if clientFilePatching>2<#else>0</#if>;

// INGAME SETTINGS
disableVoN = ${vonEnabled?then('0', '1')};
vonCodec = 1;
vonCodecQuality = 30;
persistent = ${persistent?then('1', '0')};
timeStampFormat = "short";
BattlEye = ${battlEye?then('1', '0')};

// SIGNATURE VERIFICATION
onUnsignedData = "kick (_this select 0)";
onHackedData = "kick (_this select 0)";
onDifferentData = "";

${additionalOptions!}