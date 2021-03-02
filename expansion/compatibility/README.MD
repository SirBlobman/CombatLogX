# CombatLogX Compatibility Expansions
Compatibility expansions add features to CombatLogX based on the plugin that they add compatibility for.

## Compatibility Table
| Plugin | Compatibility Type |
| ------ | ------------------ |
| ASkyBlock, BSkyBlock, FabledSkyBlock, IridiumSkyblock, SuperiorSkyblock, uSkyBlock | Island team members cannot tag each other into combat. |
| Factions, FactionsX, FactionsUUID, FactionsUUID-Legacy, LegacyFactions, KingdomsX, Lands, GriefDefender, GriefPrevention, PreciousStones, ProtectionStones, RedProtect, Residence, Towny, UltimateClaims, WorldGuard | Safe Zone and non-pvp region force-field / no-entry compatibility. |
| PlaceholderAPI, MVdWPlaceholderAPI | Placeholders will be registered to these plugins. |
| iDisguise, Essentials, LibsDisguises | Players in vanish cannot tag players and cannot be tagged. |
| CrackShot | Weapons will be linked to the player that activates them. |
| Citizens | An NPC will be spawned when a player leaves during combat. |

## Placeholder List
Surround with `%%` for PlaceholderAPI or `{}` for MVdWPlaceholderAPI.

| Placeholder | Description |
| ----------- | ----------- |
| `combatlogx_time_left` | This is the amount of time (in seconds) that a player has left in combat. The zero value can be customized in the language files. |
| `combatlogx_in_combat` | This displays different configurable values when a player is in combat and when they are not. |
| `combatlogx_status` | This displays different configurable values when a player is in combat and when they are not. *Different than in_combat* |
| `combatlogx_enemy_name` | This shows the name of the current enemy entity (or unknown). |
| `combatlogx_enemy_health` | This displays the health decimal of the current enemy entity (or unknown) |
| `combatlogx_enemy_health_rounded` | This shows the health integer of the current enemy entity (or unknown) |
| `combatlogx_enemy_hearts` | This shows the hearts of the current enemy entity (or unknown). If an enemy has more than 10 hearts an integer will be shown. |
| `combatlogx_newbie_helper_pvp_status` | (Requires Newbie Helper) This placeholder shows whether a player has pvp enabled or not. |
| `combatlogx_newbie_helper_protected` | (Requires Newbie Helper) This placeholder shows a boolean (true/false). The value is true when the player has newbie protection and false when they do not. |