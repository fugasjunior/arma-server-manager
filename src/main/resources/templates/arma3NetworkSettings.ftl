<#-- @ftlvariable name="" type="cz.forgottenempire.servermanager.serverinstance.entities.Arma3NetworkSettings" -->

<#if maxMessagesSend??>
    MaxMsgSend = ${maxMessagesSend};
</#if>
<#if maxSizeGuaranteed??>
    MaxSizeGuaranteed = ${maxSizeGuaranteed};
</#if>
<#if maxSizeNonguaranteed??>
    MaxSizeNonguaranteed = ${maxSizeNonguaranteed};
</#if>
<#if minBandwidth??>
    MinBandwidth = ${minBandwidth};
</#if>
<#if maxBandwidth??>
    MaxBandwidth = ${maxBandwidth};
</#if>
<#if minErrorToSend??>
    MinErrorToSend = ${minErrorToSend};
</#if>
<#if minErrorToSendNear??>
    MinErrorToSendNear = ${minErrorToSendNear};
</#if>
<#if maxPacketSize??>
    class sockets
    {
    maxPacketSize = ${maxPacketSize};
    };
</#if>
<#if maxCustomFileSize??>
    MaxCustomFileSize = ${maxCustomFileSize};
</#if>