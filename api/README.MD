# CombatLogX API
This is the API module for CombatLogX.  
You can use it to create new expansions or to check stuff with your own plugin.

## Maven Repository
A lot of developers use maven to build projects more easily.

**Repository: SirBlobman Public**
```xml
<repositories>
    <!-- SirBlobman Public Repository -->
    <repository>
        <id>sirblobman-public</id>
        <url>https://nexus.sirblobman.xyz/repository/public/</url>
    </repository>
</repositories>
```

**Dependency: CombatLogX API**
```xml
<dependencies>
    <!-- CombatLogX API -->
    <dependency>
        <groupId>com.github.sirblobman.combatlogx</groupId>
        <artifactId>api</artifactId>
        <version>11.0.0.0-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

## Events
A list of events and their uses can be found in the `com.github.sirblobman.combatlogx.api.event` package.  
You can see more information about them below:
- **PlayerPreTagEvent:** Triggered before a player gets tagged. (cancellable)
- **PlayerTagEvent:** Triggered when a player gets tagged into combat (not cancellable).
- **PlayerReTagEvent:** Triggered when a player already has a timer, but they are tagged again. (cancellable)
- **PlayerUntagEvent:** Triggered when a player gets out of combat.
- **PlayerPunishEvent:** Triggered when a player is going to be punished for logging out (cancellable).

## Other API Uses
CombatLogX has many uses, but some common examples are provided below.  
Good luck with your coding!

**Example 01: Check if a player is in combat.**
```java
public boolean isInCombat(Player player) {
    // You need to ensure that CombatLogX is enabled before using it for anything.
    ICombatLogX plugin = (ICombatLogX) Bukkit.getPluginManager().getPlugin("CombatLogX");
    ICombatManager combatManager = plugin.getCombatManager();
    return combatManager.isInCombat(player);
}
```
