<#-- @ftlvariable name="" type="cz.forgottenempire.arma3servergui.model.ServerSettings" -->
// GLOBAL SETTINGS
hostname = "${name}";        // The name of the server that shall be displayed in the public server list
password = "${password!}";                    // Password for joining, eg connecting to the server
passwordAdmin = "${adminPassword!}";                // Password to become server admin. When you're in Arma MP and connected to the server, type '#login xyz'

logFile = "server_console.log";            // Tells ArmA-server where the logfile should go and what it should be called

// WELCOME MESSAGE ("message of the day")
// It can be several lines, separated by comma
// Empty messages "" will not be displayed at all but are only for increasing the interval
motd[] = {
<#list messageOfTheDay as msg>
    ${msg}<#sep>, </#sep>
</#list>
};
motdInterval = 5;                // Time interval (in seconds) between each message


// JOINING RULES
maxPlayers = ${maxPlayers};                // Maximum amount of players. Civilians and watchers, beholder, bystanders and so on also count as player.
kickDuplicate = 1;                // Each ArmA version has its own ID. If kickDuplicate is set to 1, a player will be kicked when he joins a server where another player with the same ID is playing.
verifySignatures = <#if verifySignatures>2<#else>0</#if>;    // Verifies .pbos against .bisign files. Valid values 0 (disabled), 1 (prefer v2 sigs but accept v1 too) and 2 (only v2 sigs are allowed).
allowedFilePatching = <#if clientFilePatching>2<#else>0</#if>;                        // Allow or prevent client using -filePatching to join the server. 0, is disallow, 1 is allow HC, 2 is allow all clients (since Arma 3 1.49+)

// VOTING
voteMissionPlayers = 1;                // Tells the server how many people must connect so that it displays the mission selection screen.
voteThreshold = 0.33;                // 33% or more players need to vote for something, for example an admin or a new map, to become effective

// INGAME SETTINGS
disableVoN = ${von?then('0', '1')};                    // If set to 1, Voice over Net will not be available
vonCodec = 1;                    // If set to 1 then it uses IETF standard OPUS codec, if to 0 then it uses SPEEX codec (since Arma 3 update 1.58+)
vonCodecQuality = 30;                // since 1.62.95417 supports range 1-20 //since 1.63.x will supports range 1-30 //8kHz is 0-10, 16kHz is 11-20, 32kHz(48kHz) is 21-30
persistent = ${persistent?then('1', '0')};                    // If 1, missions still run on even after the last player disconnected.
timeStampFormat = "short";            // Set the timestamp format used on each report line in server-side RPT file. Possible values are "none" (default),"short","full".
BattlEye = ${battlEye?then('1', '0')};                    // Server to use BattlEye system

// SIGNATURE VERIFICATION
onUnsignedData = "kick (_this select 0)";    // unsigned data detected
onHackedData = "kick (_this select 0)";        // tampering of the signature detected
onDifferentData = "";                // data with a valid signature, but different version than the one present on server detected
