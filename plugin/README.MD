# CombatLogX Plugin

This is the plugin module for CombatLogX.  
It contains all the implementations, commands, and listeners that will be used on the Spigot server.

## Commands

- **/combatlogx**:
    - **Aliases:** `/clx`, `/ctx`, `/combatlog`, `/combattagx`, `/combattag`
    - **/combatlogx help:** View the list of sub-commands for CombatLogX.
    - **/combatlogx about &lt;expansion&gt;:** View information about an expansion.
    - **/combatlogx reload:** Reload the configuration files for the plugin and expansions.
    - **/combatlogx toggle &lt;bossbar/actionbar/scoreboard&gt;:** Toggle the action bar, boss bar, and scoreboard for
      yourself.
    - **/combatlogx tag &lt;player&gt;:** Force a player to be tagged into combat.
    - **/combatlogx untag &lt;player&gt;:** Force a player to be untagged from combat.
    - **/combatlogx version:** View version information and a list of enabled expansions.

- **/combat-timer:**
    - **Aliases:** `/combattimer`, `/combattime`, `/ctime`, `/clt`, `/ct`
    - **/combat-timer:** Check how much time you have left in combat.
    - **/combat-timer &lt;player&gt;:** Check how much time a player has left in combat.

## Permissions

- **combatlogx.bypass:** Default bypass permission, can be changed in the config.yml file.
- **combatlogx.bypass.force.field:** Default bypass permission for the force field, can be changed in the configuration
  file for the force field expansion.
- **combatlogx.command.combat-timer:** Gives access to the `/combat-timer` command.
- **combatlogx.command.togglepvp:** Gives access to the `/togglepvp` command from the NewbieHelper expansion.
- **combatlogx.command.togglepvp.admin:** Gives access to the admin features of the `/togglepvp` command.
- **combatlogx.command.combatlogx:** Gives access to the base `/combatlogx` command, but not the sub-commands.
- **combatlogx.command.combatlogx.toggle:** Gives access to `/combatlogx toggle` command to toggle boss bar, action bar,
  and scoreboard.
- **combatlogx.command.combatlogx.about:** Gives access to information about expansions with `/combatlogx about`.
- **combatlogx.command.combatlogx.help:** Gives access to information about CombatLogX commands with
  `/combatlogx help`.
- **combatlogx.command.combatlogx.reload:** Gives access to reload configurations with `/combatlogx reload`.
- **combatlogx.command.combatlogx.version:** Gives access to version information with `/combatlogx version`.
- **combatlogx.command.combatlogx.tag:** Gives access to force tag players with `/combatlogx tag`.
- **combatlogx.command.combatlogx.untag:** Gives access to force players out of combat with `/combatlogx untag`.
