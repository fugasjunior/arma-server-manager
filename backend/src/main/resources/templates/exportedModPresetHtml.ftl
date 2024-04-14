<#ftl output_format="HTML">
<?xml version="1.0" encoding="utf-8"?>
<html>
<!--Created by Arma Server Manager - https://github.com/fugasjunior/arma-server-manager-->
<head>
    <meta name="arma:Type" content="list"/>
    <meta name="arma:PresetName" content="${presetName}" />
    <meta name="generator" content="Arma Server Manager - https://github.com/fugasjunior/arma-server-manager"/>
    <title>Arma 3</title>
    <link href="https://fonts.googleapis.com/css?family=Roboto" rel="stylesheet" type="text/css"/>
    <style>
        body {
            margin: 0;
            padding: 0;
            color: #fff;
            background: #000;
        }

        body, th, td {
            font: 95%/1.3 Roboto, Segoe UI, Tahoma, Arial, Helvetica, sans-serif;
        }

        td {
            padding: 3px 30px 3px 0;
        }

        h1 {
            padding: 20px 20px 0 20px;
            color: white;
            font-weight: 200;
            font-family: segoe ui;
            font-size: 3em;
            margin: 0;
        }

        em {
            font-variant: italic;
            color: silver;
        }

        .before-list {
            padding: 5px 20px 10px 20px;
        }

        .mod-list {
            background: #222222;
            padding: 20px;
        }

        .footer {
            padding: 20px;
            color: gray;
        }

        a {
            color: #D18F21;
            text-decoration: underline;
        }

        a:hover {
            color: #F1AF41;
            text-decoration: none;
        }

        .from-steam {
            color: #449EBD;
        }

    </style>
</head>
<body>
<h1>Arma 3  - Preset <strong>${preset.name}</strong></h1>
<p class="before-list">
    <em>To import this preset, drag this file onto the Launcher window. Or click the MODS tab, then PRESET in the top
        right, then IMPORT at the bottom, and finally select this file.</em>
</p>
<div class="mod-list">
    <table>
        <#list preset.mods as mod>
            <tr data-type="ModContainer">
                <td data-type="DisplayName">${mod.name}</td>
                <td>
                    <span class="from-steam">Steam</span>
                </td>
                <td>
                    <a href="http://steamcommunity.com/sharedfiles/filedetails/?id=${mod.id}" data-type="Link">
                        http://steamcommunity.com/sharedfiles/filedetails/?id=${mod.id}
                    </a>
                </td>
            </tr>
        </#list>
    </table>
</div>
<div class="footer">
    <span>Created with <a href="https://github.com/fugasjunior/arma-server-manager">Arma Server Manager</a> by fugasjunior.</span>
</div>
</body>
</html>