# CombatLogX
CombatLogX is a plugin made for the Spigot server software.  
It has many combat-related features to add to your server.  
One of the main features of this plugin are the Expansions, which are like modules that allow you to add different things and change how the plugin works.

## Main Features
* Custom expansions that change how the plugin works
* Tag players when they attack other players
* Punish players that log out during combat

## Jenkins Builds
CodeMC was nice enough to sponsor a CI Jenkins server.  
The build status is displayed below:  
[![Build Status](https://ci.codemc.io/job/SirBlobman/job/CombatLogX/badge/icon)](https://ci.codemc.io/job/SirBlobman/job/CombatLogX/)

## Expansions
If you want to find out more about the expansions, please visit this page:  
<https://www.spigotmc.org/wiki/combatlogx/>

## Developer API
If you want to create expansions or learn how to use the CombatLogX API, please visit this page:
<https://github.com/SirBlobman/CombatLogX/blob/master/api/README.MD>

## Support
Support is provided by creating a new issue, or by chatting with the Support Team on my discord.  
**Discord Link:** <https://discord.gg/XMq2agT>

## Compilation Instructions
1. Download the SirBlobmanAPI 1.13-SNAPSHOT code from the following repository:
https://github.com/SirBlobman/SirBlobmanAPI/tree/old/1.13-SNAPSHOT
2. Run the following in a terminal or command prompt: `mvn clean install`
3. Download the CombatLogX code from this repository.
4. Run the following in a terminal or command prompt: `mvn clean install`
5. You can find the `CombatLogX.zip` in the `builder/build` folder.