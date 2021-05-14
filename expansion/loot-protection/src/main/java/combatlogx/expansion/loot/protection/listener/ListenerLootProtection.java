package combatlogx.expansion.loot.protection.listener;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.language.Replacer;
import com.github.sirblobman.combatlogx.CombatPlugin;
import com.github.sirblobman.combatlogx.api.event.PlayerPunishEvent;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.listener.ListenerDeath;
import combatlogx.expansion.loot.protection.event.QueryPickupEvent;
import combatlogx.expansion.loot.protection.object.ProtectedItem;
import net.jodah.expiringmap.ExpiringMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class ListenerLootProtection extends ExpansionListener {

    private final Set<UUID> messageCooldown;
    private final ExpiringMap<UUID, ProtectedItem> protectedItems;
    private final Map<Location, ConcurrentLinkedQueue<ProtectedItem>> pendingProtection;
    private final Map<UUID, UUID> punishedPlayers;
    private final YamlConfiguration configuration;
    private final PlayerDataManager playerDataManager;

    public ListenerLootProtection(final Expansion expansion) {
        super(expansion);
        ConfigurationManager configurationManager = getCombatLogX().getConfigurationManager();
        this.configuration = configurationManager.get("config.yml");
        this.messageCooldown = Collections.newSetFromMap(ExpiringMap.builder().expiration(configuration.getLong("message-cooldown", 1), TimeUnit.SECONDS).build());
        this.pendingProtection = ExpiringMap.builder().expiration(configuration.getLong("loot-protection-time", 30), TimeUnit.SECONDS).build();
        this.protectedItems = ExpiringMap.builder().expiration(configuration.getLong("loot-protection-time", 30), TimeUnit.SECONDS).build();
        this.punishedPlayers = ExpiringMap.builder().expiration(configuration.getLong("loot-protection-time", 30), TimeUnit.SECONDS).build();
        this.playerDataManager = getCombatLogX().getPlayerDataManager();
    }

    public static Location toBlockLocation(Location location) {
        return new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public boolean contains(Item item) {
        UUID uuid = item.getUniqueId();
        return protectedItems.containsKey(uuid);
    }

    @EventHandler
    public void onEntityItemPickup(EntityPickupItemEvent event) {
        if(contains(event.getItem())) {
            if(!(event.getEntity() instanceof Player)) {
                event.setCancelled(true);
                return;
            }
            ProtectedItem item = this.protectedItems.get(event.getItem().getUniqueId());
            Player player = (Player) event.getEntity();
            UUID uuid = player.getUniqueId();
            QueryPickupEvent query = new QueryPickupEvent(player, item);
            Bukkit.getPluginManager().callEvent(query);
            if(!item.getOwnerUUID().equals(uuid) && !query.isCancelled()) {
                if(!messageCooldown.contains(uuid)) {
                    Replacer replacer = message ->
                            message.replace("{time}", "" + TimeUnit.MILLISECONDS.toSeconds(protectedItems.getExpiration(event.getItem().getUniqueId())));
                    getLanguageManager().sendMessage(player, "expansion.loot-protection.protected", replacer, true);
                    messageCooldown.add(uuid);
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHopperItemPickup(InventoryPickupItemEvent event) {
        if(contains(event.getItem())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPunish(PlayerPunishEvent event) {
        if(event.getPreviousEnemy() != null) {
            YamlConfiguration playerData = playerDataManager.get(event.getPlayer());
            YamlConfiguration configuration = getCombatLogX().getConfigurationManager().get("punish.yml");
            String killOptionString = configuration.getString("kill-time", "QUIT");
            UUID enemyUUID = event.getPreviousEnemy().getUniqueId();
            if(killOptionString.equals("JOIN")) {
                playerData.set("loot-protection-enemy", enemyUUID);
                getCombatLogX().saveData(event.getPlayer());
                return;
            }
            punishedPlayers.put(event.getPlayer().getUniqueId(), enemyUUID);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if(!(getCombatLogX() instanceof CombatPlugin)) return;
        CombatPlugin clx = (CombatPlugin) getCombatLogX();
        ListenerDeath listenerDeath = clx.getDeathListener();
        Player player = event.getEntity();
        if(configuration.getBoolean("only-protect-after-log", false) && !listenerDeath.contains(player)) return;

        YamlConfiguration playerData = playerDataManager.get(player);
        String enemyUUIDString = playerData.getString("loot-protection-enemy");
        UUID enemyUUID;
        if(enemyUUIDString != null) {
            playerData.set("loot-protection-enemy", null);
            playerDataManager.save(player);
            enemyUUID = UUID.fromString(enemyUUIDString);
        } else {
            enemyUUID = punishedPlayers.get(player.getUniqueId());
        }
        if(enemyUUID == null) return;
        Entity enemy = Bukkit.getEntity(enemyUUID);
        if(enemy == null) return;
        Location location = toBlockLocation(player.getLocation());
        if(player.getLastDamageCause() != null && player.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.VOID && configuration.getBoolean("return-void-items", true) && enemy instanceof Player) {
            Player enemyPlayer = (Player) enemy;
            location = toBlockLocation(enemy.getLocation());
            List<ItemStack> removeItems = new ArrayList<>();
            for(final ItemStack droppedItem : event.getDrops()) {
                enemyPlayer.getInventory().addItem(droppedItem).values().forEach((item) -> {
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                    removeItems.add(item);
                });
            }
            event.getDrops().removeAll(removeItems);
        }

        ConcurrentLinkedQueue<ProtectedItem> list = new ConcurrentLinkedQueue<>();
        final Location finalLocation = location;
        event.getDrops().forEach((item -> {
            ProtectedItem protectedItem = new ProtectedItem(finalLocation, item);
            protectedItem.setOwnerUUID(enemy.getUniqueId());
            list.add(protectedItem);
        }));
        pendingProtection.put(toBlockLocation(location), list);
        String name = enemy.getCustomName() == null ? enemy.getName() : enemy.getCustomName();
        Replacer replacer = message ->
                message.replace("{time}", "" + TimeUnit.MILLISECONDS.toSeconds(protectedItems.getExpiration())).replace("{enemy}", name);
        getLanguageManager().sendMessage(enemy, "expansion.loot-protection.enemy-died", replacer, true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemSpawn(ItemSpawnEvent event) {
        if(pendingProtection.isEmpty()) return;
        Location location = toBlockLocation(event.getLocation());
        if(!pendingProtection.containsKey(location)) return;
        ConcurrentLinkedQueue<ProtectedItem> items = pendingProtection.get(location);
        Item item = event.getEntity();
        for(final ProtectedItem protectedItem : items) {
            if(protectedItem.getItemStack().equals(item.getItemStack())) {
                protectedItem.setItemUUID(item.getUniqueId());
                protectedItems.put(protectedItem.getItemUUID(), protectedItem);
                items.remove(protectedItem);
                if(items.isEmpty()) pendingProtection.remove(location);
                return;
            }
        }
    }
}
