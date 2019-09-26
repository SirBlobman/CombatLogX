# CombatLogX API
This is the API module for CombatLogX.   
You can use it to create new expansions or to check stuff with your own plugin

## Events
A list of events with some examples is provided below
### com.SirBlobman.combatlogx.event.PlayerPreTagEvent
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

### com.SirBlobman.combatlogx.event.PlayerTagEvent
This event will be fired when a player is put into combat.  
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

### com.SirBlobman.combatlogx.event.PlayerUntagEvent
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

### com.SirBlobman.combatlogx.event.PlayerPunishEvent
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

### com.SirBlobman.combatlogx.event.PlayerCombatTimerChangeEvent
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

