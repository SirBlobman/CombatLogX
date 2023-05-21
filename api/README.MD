# CombatLogX API

This is the API module for CombatLogX.  
You can use it to create new expansions or to check stuff with your own plugin.

## Maven Repository

A lot of developers use maven to build projects more easily.

**Repository: SirBlobman Public**

Maven:
```xml
<repositories>
    <!-- SirBlobman Public Repository -->
    <repository>
        <id>sirblobman-public</id>
        <url>https://nexus.sirblobman.xyz/public/</url>
    </repository>
</repositories>
```

Gradle Groovy:
```groovy
repositories {
    maven {
        name = 'sirblobman-public'
        url = 'https://nexus.sirblobman.xyz/public/'
    }
}
```

Gradle Kotlin:
```kotlin
repositories {
    maven("https://nexus.sirblobman.xyz/public/")
}
```

**Dependency: CombatLogX API**

Maven:
```xml
<dependencies>
    <!-- BlueSlimeCore -->
    <dependency>
        <groupId>com.github.sirblobman.api</groupId>
        <artifactId>core</artifactId>
        <version>2.9-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>
    
    <!-- CombatLogX API -->
    <dependency>
        <groupId>com.github.sirblobman.combatlogx</groupId>
        <artifactId>api</artifactId>
        <version>11.4-SNAPSHOT</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

Gradle Groovy:
```groovy
dependencies {
    compileOnly 'com.github.sirblobman.api:core:2.9-SNAPSHOT'
    compileOnly 'com.github.sirblobman.combatlogx:api:11.4-SNAPSHOT'
}
```

Gradle Kotlin:
```kotlin
dependencies {
    compileOnly("com.github.sirblobman.api:core:2.9-SNAPSHOT")
    compileOnly("com.github.sirblobman.combatlogx:api:11.4-SNAPSHOT")
}
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

**Example 01: Check if CombatLogX is enabled and get an instance of it.**

```java
public boolean isEnabled() {
    PluginManager pluginManager = Bukkit.getPluginManager();
    return pluginManager.isPluginEnabled("CombatLogX");
}

public ICombatLogX getAPI() {
    PluginManager pluginManager = Bukkit.getPluginManager();
    Plugin plugin = pluginManager.getPlugin("CombatLogX");
    return (ICombatLogX) plugin;
}
```

**Example 02: Check if a player is in combat.**

```java
public boolean isInCombat(Player player) {
    ICombatLogX plugin = getAPI();
    ICombatManager combatManager = plugin.getCombatManager();
    return combatManager.isInCombat(player);
}
```

**Example 03: Check if a player was killed by CombatLogX.**

```java
@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
public void onDeath(PlayerDeathEvent e) {
    Player player = e.getEntity();
    ICombatLogX plugin = getAPI();
    IDeathManager deathManager = plugin.getDeathManager();
    
    if(deathManager.wasPunishKilled(player)) {
        // Player was killed by CombatLogX
        e.setDeathMessage(player.getName() + " was killed for logging out during combat.");
    }
}
```
