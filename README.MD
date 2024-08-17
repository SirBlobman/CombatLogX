# CombatLogX

[![Code Size](https://img.shields.io/github/languages/code-size/SirBlobman/CombatLogX)](https://github.com/SirBlobman/CombatLogX/)
[![Build Status](https://jenkins.sirblobman.xyz/job/SirBlobman/job/CombatLogX/job/main/badge/icon)](https://jenkins.sirblobman.xyz/job/SirBlobman/job/CombatLogX/)
[![Crowdin Localization](https://badges.crowdin.net/combatlogx-spigot-plugin/localized.svg)](https://crowdin.com/project/combatlogx-spigot-plugin)

## Description

CombatLogX is a plugin made for the Spigot server software.  
It has many combat-related features to add to your server.  
One of the main features of this plugin are the Expansions, which are like modules that allow you to add different
things and change how the plugin works.

## Requirements

- Java 17
- [Spigot](https://spigotmc.org/), [Paper](https://papermc.io/download/paper), or [Folia](https://papermc.io/software/folia) 1.19.4-1.21.1
- [BlueSlimeCore](https://hangar.papermc.io/SirBlobman/BlueSlimeCore) 2.9.3 or higher.

## Main Features

- Custom expansions that change how the plugin works
- Tag players when they attack other players
- Punish players that log out during combat

## Installation Guide

1. Download the CombatLogX.zip file from Jenkins or SpigotMC.
2. Extract the contents of the CombatLogX.zip to your PC.
3. Click the stop button on the panel. If your server doesn't use a panel, type `stop` into the console.
4. Upload `CombatLogX.jar` and `BlueSlimeCore.jar` to your server `/plugins/` folder.
5. Upload the contents of `CombatLogX/expansions` to your server `/plugins/CombatLogX/expansions` folder.
6. If the files were uploaded and extracted correctly, your server should have the following files:
    - File: /plugins/CombatLogX.jar
    - File /plugins/BlueSlimeCore.jar
    - Folder: /plugins/CombatLogX/
    - Folder: /plugins/CombatLogX/expansions/
    - Multiple Files: /plugins/CombatLogX/expansions/*.jar
7. Delete the `CombatLogX.zip` file.
8. Restart your server using the panel or your startup script.
9. Edit the configuration files for the main plugin, languages, and expansions.
10. Type the command `/clx reload` to reload the configuration files.
11. If you want to remove an expansion, delete the jar file from `/plugins/CombatLogX/expansions`.

## Downloads

- [SpigotMC](https://www.spigotmc.org/resources/31689/)
- [Jenkins Beta Builds](https://jenkins.sirblobman.xyz/job/SirBlobman/job/CombatLogX/job/main/)

## Expansions

If you want to find out more about the expansions, please visit this page:  
[CombatLogX Documentation](https://www.spigotmc.org/resources/combatlogx.31689/field?field=documentation)

You can also open the README files in each expansion folder.

## Developer API

If you want to create expansions or learn how to use the CombatLogX API for V11/Beta, please visit this page:  
[API README](api/README.MD)

## Sponsors

The people or organizations listed below were kind enough to fund my open-source projects!

- Privately sponsored by [Alien Host](https://alienhost.net).
- Sponsored on GitHub by [feather64.net](https://feather64.net) owned by Derek Larson.

## Localization

A community translation project for CombatLogX is open.
If you want to help with translations, please visit our Crowdin page.   
<https://crowdin.com/project/combatlogx-spigot-plugin>

### Localization Credits

You can view a list of contributions made to CombatLogX on our Crowdin page.   
<https://crowdin.com/project/combatlogx-spigot-plugin/reports/top-members>

## Support

Support is provided by creating a new issue, or by chatting with the Support Team on my discord.  
**Discord Link:** <https://discord.gg/XMq2agT>
