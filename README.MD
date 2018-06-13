<h1>CombatLogX</h1>
CombatLogX is a simple combat tagging plugin<br/>
You can also abbreviate it as CLX<br/>
For planned features, you can check the 'To Do.list' file<br/>

<h1>Version Information</h1>
The format is as follows:<br/>
<i>major</i>.<i>minor</i>.<i>patch</i>.<i>expansion</i><br/>
<br/>
<i>expansion</i> will change when an expansion is changed or updated<br/>
<i>patch</i> will change when I make small changes that don't affect anything major<br/>
<i>minor</i> will change when I add a new expansion or create more effective changes<br/>
<i>major</i> will change when I modify the API or make a lot of changes that affect the plugin<br/>

<h1>Expansion Information</h1>
Expansions are similar to plugins, but instead of going in <i>/plugins/</i> they go in <i>/plugins/CombatLogX/expansions/</i><br/>
They can be used to add new features to CombatLogX or add Compatibility with other plugins<br/>

<h2>1) Cheat Prevention</h2>
The cheat prevention expansion can be found inside the 'CombatLogX.zip' file as <i>CheatPrevention.jar</i><br/>
CheatPrevention can be used for the followings while a player is tagged:<br/>
<ol type="1">
  <li>Blocking Commands</li>
  <li>Preventing teleportation</li>
  <li>Preventing gamemode changing (creative)</li>
  <li>Preventing flight</li>
  <li>Preventing inventory opening</li>
  <li>Removing potions or effects</li>
</ol><br/><br/>

<h2>2) NotCombatLogX</h2>
The NotCombatLogX expansion can be found inside the 'CombatLogX.zip' file as <i>NotCombatLogX.jar</i><br/>
NotCombatLogX tags players for non-combat related damage.<br/>
The following damage can cause a player to be tagged:<br/>
<ul>
  <li>All Damage (if enabled in config)</li>
  <li>Fall</li>
  <li>Projectile</li>
  <li>Block Explosion</li>
  <li>Drowning</li>
  <li>Lava</li>
</ul><br/><br/>

<h2>3) Notifier</h2>
The Notifier expansion can be found inside the 'CombatLogX.zip' file as <i>Notifier.jar</i><br/>
Notifier displays information related to CombatLogX for the player to see.<br/>
This expansion has the following features:<br/>
<ol type="1">
  <li>Scoreboard (1.7+)</li>
  <li>Boss Bar (1.10+)</li>
  <li>Action Bar (1.8+)</li>
</ol><br/>
There are also also three scoreboard variables that can be used:<br/>
<b>{time_left}:</b> Displays the amount of time the tagged player has in combat before being untagged<br/>
<b>{enemy_name}:</b> Displays the name of the entity that caused the player to be tagged, or "None"<br/>
<b>{enemy_health}:</b> Displays the health of the entity that caused the player to be tagged, or "None"<br/><br/>

<h2>4) Rewards</h2>
The Rewards expansion can be found inside the 'CombatLogX.zip' file as <i>Rewards.jar</i><br/>
Rewards will allow you to give items and money to players for killing other players.<br/>
There is a config file for different commands if you need to change the default<br/><br/>

<h2>5) Citizens Compatibility</h2>
The Citizens Compatibility expansion can be found inside the 'CombatLogX.zip' file as <i>CompatCitizens.jar</i><br/>
This expansion creates NPCs of the players that logged out so that there are no easy kills<br/>
Citizens can be found here: https://www.spigotmc.org/resources/citizens.13811/<br/><br/>

<h2>6) CrackShot Compatibility</h2>
The CrackShot Compatibility expansion can be found inside the 'CombatLogX.zip' file as <i>CompatCrackShot.jar</i><br/>
This expansion allows player's to be tagged by player's and mobs that have weapons and projectiles used in CrackShot<br/>
CrackShot can be found here: https://www.spigotmc.org/resources/crackshot-guns.48301/<br/><br/>

<h2>7) Factions Compatibility</h2>
The Factions Compatibility expansion can be found inside the 'CombatLogX.zip' file as <i>CompatFactions.jar</i><br/>
This expansion allows for the use of safezones in Factions, FactionsUUID, SavageFactions, and LegacyFactions.<br/>
The expansion also prevents faction members from tagging each other<br/>
The links for different faction plugins can be found here:<br/>
Factions: https://www.spigotmc.org/resources/factions.1900/<br/>
UUID: https://www.spigotmc.org/resources/factionsuuid.1035/<br/>
Legacy: https://www.spigotmc.org/resources/legacyfactions.40122/<br/>
Savage: https://www.spigotmc.org/resources/savagefactions-factionsuuid-reimagined-1-7-1-12.52891/<br/><br/>

<h2>8) Parties Compatibility</h2>
The Parties Compatibility expansion can be found inside the 'CombatLogX.zip' file as <i>CompatParties.jar</i><br/>
This expansion prevents people in the same party from tagging each other<br/>
Parties can be found here: https://www.spigotmc.org/resources/parties.3709/<br/><br/>

<h2>9) PlaceholderAPI Compatibility</h2>
The PlaceholderAPI Compatibility expansion can be found inside the 'CombatLogX.zip' file as <i>CompatPlaceholderAPI.jar</i><br/>
Since this is compatible with MVdWPlaceholderAPI, you can use these placeholders with Featherboard<br/>
This expansion puts the following placeholders into MVdWPlaceholderAPI and PlaceholderAPI. You may need to replace { and } with %
<ul>
<li>{combatlogx_time_left} - See how much time is left</li>
<li>{combatlogx_enemy_name} - Name of your enemy (entity type if not named)</li>
<li>{combatlogx_enemy_health} - How much health your enemy has</li>
</ul><br/>
The placeholder APIs can be found here:<br/>
PlaceholderAPI: https://www.spigotmc.org/resources/placeholderapi.6245/<br/>
MVdWPlaceholderAPI: https://www.spigotmc.org/resources/mvdwplaceholderapi.11182/<br/><br/>

<h2>10) PreciousStones Compatibility</h2>
The PreciousStones Compatibility expansion can be found inside the 'CombatLogX.zip' file as <i>CompatPreciousStones.jar</i><br/>
This expansion allows for the use of no-pvp areas in PreciousStones<br/>
PreciousStones can be found here: https://www.spigotmc.org/resources/preciousstones.5270/<br/><br/>

<h2>11) Towny Compatibility</h2>
The Towny Compatibility expansion can be found inside the 'CombatLogX.zip' file as <i>CompatTowny.jar</i><br/>
This expansion allows for the use of no-pvp towns in Towny<br/>
Towny can be found here: http://towny.palmergames.com/<br/><br/>

<h2>12) WorldGuard Compatibility</h2>
The WorldGuard Compatibility expansion can be found inside the 'CombatLogX.zip' file as <i>CompatWorldGuard.jar</i><br/>
This expansion allows for the use of no-pvp and no-mob zones in WorldGuard<br/>
To set a no pvp zone, use "/rg flag <region> pvp deny"<br/>
To set a no mob zone, use "/rg flag <region> mob-spawning deny"<br/>
WorldGuard can be found here: https://dev.bukkit.org/projects/worldguard<br/><br/>


<hr/>
This guide is constantly being updated, if you have any suggestions you can contact SirBlobman here:<br/>
<b>Discord:</b> SirBlobman#7235<br/>
<b>SpigotMC:</b> <a href=https://www.spigotmc.org/members/sirblobman.73161/>SirBlobman's Profile</a><br/>
