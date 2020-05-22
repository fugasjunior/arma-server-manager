# Arma 3 Server GUI
A simple administration web app for managing your Arma 3 server

[Screenshots](https://imgur.com/a/jEkDCkc) 

## Features

- Start, stop, update your server while keeping track of its current state
- Configure the server without the need for modifying your server.cfg
- Download and manage Steam Workshop mods
- Upload custom scenarios from your local machine

## Installation
Before attempting to install this GUI, make sure you've successfully installed SteamCMD and Arma 3 dedicated server. 
You can follow [this guide](https://community.bistudio.com/wiki/Arma_3_Dedicated_Server). 

After you have your SteamCMD and Arma 3 dedicated server working, either download the GUI jar file or build it from the
source using `gradle assemble`.

In the same path, create a new directory `config` and put `application.properties.EXAMPLE` inside. Now, delete 
the extension .EXAMPLE, leaving you with `application.properties`. In this file, configure all the necessary properties.

Launch the application: `java -jar arma3-server-gui.jar`. Your GUI should now be available to access from the browser
on the set port, `8080` by default.
 
## Steam Workshop
For downloading workshop mods and keeping the server up-to-date, you're going to need a Steam account with 
a copy of Arma 3, otherwise, these features will not be available.

It's not necessary to have a separate account for downloading the mods and gaming. However, you might encounter some 
issues such as being disconnected from your Steam account when it's needed for mod download and updating.

If your account is protected by Steam Guard 2FA, you can optionally put in the verification token. This token should
be delivered to your account's email after the first attempt of updating the server and/or downloading a mod.

## Planned features
- Server logs access
- Managing bikeys
- Integrated search in Steam Workshop
- Download & update scenarios from Steam Workshop
- _Headless client support_

## Credits
This app is heavily based on Dahlgren's [Arma Server Admin](https://github.com/Dahlgren/arma-server-web-admin) project
and I took a lot of inspiration from it on how to make things work, especially when working with the Steam Workshop.
