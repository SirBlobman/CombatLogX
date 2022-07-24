<!DOCTYPE html>
<html>
    <body>
        <h1>CombatLogX Expansion: PlaceholderAPI Compatibility</h1>
        <p>The PlaceholderAPI Compatibility expansion for CombatLogX adds placeholders to plugins that use PlaceholderAPI.</p>
        <h2>Valid Placeholders</h2>
        <table>
            <thead>
                <tr>
                    <th>Placeholder</th>
                    <th>Description</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td><code>%combatlogx_time_left%</code></td>
                    <td>This is the amount of time (in seconds) that a player has left in combat. The zero value can be customized in the language files.</td>
                </tr>
                <tr>
                    <td><code>%combatlogx_in_combat%</code></td>
                    <td>This displays different configurable values when a player is in combat and when they are not.</td>
                </tr>
                <tr>
                    <td><code>%combatlogx_status%</code></td>
                    <td>This displays different configurable values when a player is in combat and when they are not.</td>
                </tr>
                <tr>
                    <td><code>%combatlogx_punishment_count%</code></td>
                    <td>This displays the amount of times a player has been punished for logging out during combat.</td>
                </tr>
                <tr>
                    <td><code>%combatlogx_enemy_name%</code></td>
                    <td>This shows the name of the current enemy entity (or unknown).</td>
                </tr>
                <tr>
                    <td><code>%combatlogx_enemy_diplay_name%</code></td>
                    <td>This shows the display name of the current enemy entity (or unknown).</td>
                </tr>
                <tr>
                    <td><code>%combatlogx_enemy_health%</code></td>
                    <td>This displays the health decimal of the current enemy entity (or unknown)</td>
                </tr>
                <tr>
                    <td><code>%combatlogx_enemy_health_rounded%</code></td>
                    <td>This shows the health integer of the current enemy entity (or unknown)</td>
                </tr>
                <tr>
                    <td><code>%combatlogx_enemy_hearts%</code></td>
                    <td>This shows the hearts of the current enemy entity (or unknown). If an enemy has more than 10 hearts an integer will be shown.</td>
                </tr>
                <tr>
                    <td><code>%combatlogx_enemy_world%</code></td>
                    <td>This shows the world that the enemy is currently in (or unknown).</td>
                </tr>
                <tr>
                    <td><code>%combatlogx_enemy_x%</code></td>
                    <td>This shows the x position of the enemy (or unknown).</td>
                </tr>
                <tr>
                    <td><code>%combatlogx_enemy_y%</code></td>
                    <td>This shows the y position of the enemy (or unknown).</td>
                </tr>
                <tr>
                    <td><code>%combatlogx_enemy_z%</code></td>
                    <td>This shows the z position of the enemy (or unknown).</td>
                </tr>
                <tr>
                    <td><code>%combatlogx_newbie_helper_pvp_status%</code></td>
                    <td>(Requires Newbie Helper) This placeholder shows whether a player has pvp enabled or not.</td>
                </tr>
                <tr>
                    <td><code>%combatlogx_newbie_helper_protected%</code></td>
                    <td>(Requires Newbie Helper) This placeholder shows a boolean (true/false). The value is true when the player has newbie protection and false when they do not.</td>
                </tr>
                <tr>
                    <td><code>%combatlogx_enemy_&lt;placeholder_without_%&gt;%</code></td>
                    <td>This placeholder will show any other PlaceholderAPI placeholder from the enemy. If the enemy is not a player, "Unknown" will be shown instead.</td>
                </tr>
            </tbody>
        </table>
    </body>
</html>
