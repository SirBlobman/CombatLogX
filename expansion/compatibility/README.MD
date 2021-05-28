# CombatLogX Compatibility Expansions
Compatibility expansions add features to CombatLogX based on the plugin that they add compatibility for.

## Compatibility Table
<table>
    <thead>
        <tr>
            <th>Plugin</th>
            <th>Compatibility Type</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>
                <ul>
                    <li>ASkyBlock</li>
                    <li>BSkyBlock</li>
                    <li>FabledSkyBlock</li>
                    <li>IridiumSkyblock</li>
                    <li>SuperiorSkyblock</li>
                    <li>uSkyBlock</li>
                </ul>
            </td>
            <td>Island team members cannot tag each other into combat.</td>
        </tr>
        <tr>
            <td>
                <ul>
                    <li>Factions</li>
                    <li>FactionsX</li>
                    <li>FactionsUUID</li>
                    <li>FactionsUUID-Legacy</li>
                    <li>LegacyFactions</li>
                    <li>KingdomsX</li>
                    <li>Lands</li>
                    <li>GriefDefender</li>
                    <li>GriefPrevention</li>
                    <li>PreciousStones</li>
                    <li>ProtectionStones</li>
                    <li>RedProtect</li>
                    <li>Residence</li>
                    <li>Towny</li>
                    <li>UltimateClaims</li>
                    <li>WorldGuard</li>
                </ul>
            </td>
            <td>Safe Zone and non-pvp region force-field / no-entry compatibility.</td>
        </tr>
        <tr>
            <td>
                <ul>
                    <li>PlaceholderAPI</li>
                    <li>MVdWPlaceholderAPI</li>
                </ul>
            </td>
            <td>Placeholders will be registered to these plugins.</td>
        </tr>
        <tr>
            <td>
                <ul>
                    <li>iDisguise</li>
                    <li>LibsDisguises</li>
                    <li>Essentials</li>
                </ul>
            </td>
            <td>Players in vanish cannot tag players and cannot be tagged.</td>
        </tr>
        <tr>
            <td>CrackShot</td>
            <td>Weapons will be linked to the player that activates them.</td>
        </tr>
        <tr>
            <td>Citizens</td>
            <td>An NPC will be spawned when a player leaves during combat.</td>
        </tr>
    </tbody>
</table>

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
| `combatlogx_enemy_world}` | This shows the world that the enemy is currently in (or unknown). |
| `combatlogx_enemy_x` | This shows the x position of the enemy (or unknown). |
| `combatlogx_enemy_y` | This shows the y position of the enemy (or unknown). |
| `combatlogx_enemy_z` | This shows the z position of the enemy (or unknown). |
| `combatlogx_newbie_helper_pvp_status` | (Requires Newbie Helper) This placeholder shows whether a player has pvp enabled or not. |
| `combatlogx_newbie_helper_protected` | (Requires Newbie Helper) This placeholder shows a boolean (true/false). The value is true when the player has newbie protection and false when they do not. |
