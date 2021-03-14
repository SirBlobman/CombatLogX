# CombatLogX API
This is the API module for CombatLogX.   
You can use it to create new expansions or to check stuff with your own plugin

# Maven Repository
A lot of developers use maven to build projects more easily.  
CombatLogX has a custom maven repository for you to use.

**Repository: SirBlobman Public**
```xml
<!-- SirBlobman Public Repository -->
<repository>
    <id>sirblobman-public</id>
    <url>https://nexus.sirblobman.xyz/repository/public/</url>
</repository>
```

**Dependency: CombatLogX API**
```xml
<dependency>
    <groupId>com.SirBlobman.combatlogx</groupId>
    <artifactId>CombatLogX-API</artifactId>
    <version>10.0.0.0-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

## Events
A list of events and examples are provided below.
### com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent
This event will be fired before a player gets tagged by CombatLogX  
You can cancel it to prevent the player from being tagged.  

**Example:** *Prevent SirBlobman from being tagged*
```java
@EventHandler(ignoreCancelled=true)
public void beforeTag(PlayerPreTagEvent e) {
    Player player = e.getPlayer();
    String name = player.getName();
    if(name.equals("SirBlobman")) {
        e.setCancelled(true);
    }
}
```

### com.SirBlobman.combatlogx.api.event.PlayerTagEvent
This event will be fired when a player is put into combat for the first time  
You can use this to close menus and do other things in your plugins.

**Example:** *Give the player a rotten flesh when they get into combat*
```java
@EventHandler
public void onTag(PlayerTagEvent e) {
    Player player = e.getPlayer();
    PlayerInventory playerInv = player.getInventory();
    
    ItemStack rottenFlesh = new ItemStack(Material.ROTTEN_FLESH, 1);
    playerInv.addItem(rottenFlesh);
}
```

### com.SirBlobman.combatlogx.api.event.PlayerReTagEvent
This event will be fired when a player is already in combat, but their timer is extended  
That usually occurs when they are tagged again or another plugin tags them during combat

**Example:** *Tell the player every time they are tagged*
```java
@EventHandler
public void onTag(PlayerReTagEvent e) {
    Player player = e.getPlayer();
    player.sendMessage("You've been tagged again!");
}
```

### com.SirBlobman.combatlogx.api.event.PlayerUntagEvent
This event will be fired when a player is removed from combat.  
This can be used to punish players for logging out or reward them for waiting until expiration

**Example:** *Clear the inventory of a player that quit during combat*
```java
@EventHandler
public void onUntag(PlayerUntagEvent e) {
    Player player = e.getPlayer();
    UntagReason untagReason = e.getUntagReason();
    
    if(untagReason == UntagReason.QUIT) {
        PlayerInventory playerInv = player.getInventory();
        playerInv.clear();
    }
}
```

### com.SirBlobman.combatlogx.api.event.PlayerPunishEvent
This event is fired before the punish commands are executed by CombatLogX  
If the event is cancelled, the punishments will not run.

**Example:** *Prevent a player with a permission from being punished*
```java
@EventHandler(ignoreCancelled=true)
public void beforePunish(PlayerPunishEvent e) {
    Player player = e.getPlayer();
    if(player.hasPermission("custom.donator.permission")) {
        e.setCancelled(true);
        player.sendMessage("You were saved from an evil punishment, but please don't log out during combat.");
    }
}
```

### com.SirBlobman.combatlogx.api.event.PlayerCombatTimerChangeEvent
This event is fired for every second of a player's time during combat.  
You can use this to update scoreboards and other things.

**Example:** *Send a message for every time the timer updates*
```java
@EventHandler
public void onTimerChange(PlayerCombatTimerChangeEvent e) {
    Player player = e.getPlayer();
    int secondsLeft = e.getSecondsLeft();
    player.sendMessage("You have " + secondsLeft + " seconds left in combat.");
}
```

## Other API Uses
CombatLogX has many uses, but some common examples are provided below.  
Good luck with your coding!

**Example:** *Check if a Player is in Combat*
```java
public boolean isInCombat(Player player) {
    // Make sure to check that CombatLogX is enabled before using it for anything.
    ICombatLogX plugin = (ICombatLogX) Bukkit.getPluginManager().getPlugin("CombatLogX");
    ICombatManager combatManager = plugin.getCombatManager();
    return combatManager.isInCombat(player);
}
```