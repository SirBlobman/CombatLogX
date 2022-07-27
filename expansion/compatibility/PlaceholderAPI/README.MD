# CombatLogX Expansion: PlaceholderAPI Compatibility
The PlaceholderAPI Compatibility expansion for CombatLogX adds placeholders to plugins that use PlaceholderAPI.

## Basic Placeholders
These placeholders don't require anything special to be used.

| PlaceholderAPI Placeholder     | Description                                                                           |
|--------------------------------|---------------------------------------------------------------------------------------|
| %combatlogx_enemy_count%       | The amount of player/entity enemies the player has.                                   |
| %combatlogx_in_combat%         | The amount of combat tags the player has.                                             |
| %combatlogx_player%            | The name of the player.                                                               |
| %combatlogx_punishment_count%  | The amount of combat tags the player has.                                             |
| %combatlogx_status%            | The amount of combat tags the player has.                                             |
| %combatlogx_tag_count%         | The amount of combat tags the player has.                                             |
| %combatlogx_time_left%         | The total amount of seconds the player has left in combat with all enemies. (Integer) |
| %combatlogx_time_left_decimal% | The total amount of seconds the player has left in combat with all enemies. (Decimal) |

## Enemy Placeholders
Players can have multiple enemies.
These placeholders can have an index to view information about a specific enemy.
Example: %combatlogx_time_left_1% to get the time left with the first enemy.

| PlaceholderAPI Placeholder                                 | Description                                                                                                 |
|------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------|
| %combatlogx_time_left_&lt;index&gt;%                       | Get the total amount of seconds left in combat with a specific enemy. (Integer)                             |
| %combatlogx_time_left_decimal_&lt;index&gt;%               | Get the total amount of seconds left in combat with a specific enemy. (Decimal)                             |
| %combatlogx_current_enemy_&lt;placeholder&gt;%             | Get a placeholder for the current enemy. If the player doesn't have an enemy, it will show N/A              |
| %combatlogx_specific_enemy_&lt;index>_&lt;placeholder&gt;% | Get a placeholder for a specific enemy number. If a player doesn't have that many enemies, it will show N/A |

| Enemy Placeholder      | Description                                                                                      | Example                          |
|------------------------|--------------------------------------------------------------------------------------------------|----------------------------------|
| name                   | The name of the enemy.                                                                           | SirBlobman                       |
| type                   | The entity type of the enemy.                                                                    | PLAYER                           |
| display_name           | The display name of the enemy.                                                                   | &a&oBlobman                      |
| health                 | The current health of the enemy.                                                                 | 8.46                             |
| health_rounded         | The current health of the enemy, but rounded.                                                    | 9                                |
| hearts                 | The current hearts of the enemy (Symbol)                                                         | &#x2764;&#x2764;&#x2764;&#x2764; |
| hearts_count           | The current hearts of the enemy (Integer)                                                        | 4                                |
| world                  | The name of the world that the enemy is in.                                                      | world_the_end                    |
| x                      | The current x position of the enemy.                                                             | -257                             |
| y                      | The current y position of the enemy.                                                             | 11                               |
| z                      | The current z position of the enemy.                                                             | 500                              |
| &lt;placeholderapi&gt; | Any other valid PlaceholderAPI placeholder without the % symbols. Only works for player enemies. | &lt;varies&gt;                   |

## Newbie Helper Placeholders
These placeholders require the `Newbie Helper` expansion.

| Placeholder                           | Description                                                                   |
|---------------------------------------|-------------------------------------------------------------------------------|
| %combatlogx_newbie_helper_pvp_status% | Shows if the player has pvp enabled or disabled via the `/togglepvp` command. |
| %combatlogx_newbie_helper_protected%  | Shows if the player is currently protected or not.                            |
