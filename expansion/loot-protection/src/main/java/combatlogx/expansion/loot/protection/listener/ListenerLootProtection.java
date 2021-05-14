package combatlogx.expansion.loot.protection.listener;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.language.Replacer;
import com.github.sirblobman.combatlogx.CombatPlugin;
import com.github.sirblobman.combatlogx.api.event.PlayerPunishEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.object.UntagReason;
import com.github.sirblobman.combatlogx.listener.ListenerDeath;
import combatlogx.expansion.loot.protection.event.QueryPickupEvent;
import combatlogx.expansion.loot.protection.object.ProtectedItem;
import net.jodah.expiringmap.ExpiringMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class ListenerLootProtection extends ExpansionListener {

    private final Set<UUID> messageCooldown;
    private final ExpiringMap<UUID, ProtectedItem> protectedItems;
    private final Map<Location, ConcurrentLinkedQueue<ProtectedItem>> pendingProtection;
    private final Map<UUID, UUID> enemyMap;
    private final YamlConfiguration configuration;
    private final PlayerDataManager playerDataManager;

    public ListenerLootProtection(final Expansion expansion) {
        super(expansion);
        ConfigurationManager configurationManager = getCombatLogX().getConfigurationManager();
        this.configuration = configurationManager.get("config.yml");
        this.messageCooldown = Collections.newSetFromMap(ExpiringMap.builder().expiration(configuration.getLong("message-cooldown", 1), TimeUnit.SECONDS).build());
        this.pendingProtection = ExpiringMap.builder().expiration(configuration.getLong("loot-protection-time", 30), TimeUnit.SECONDS).build();
        this.protectedItems = ExpiringMap.builder().expiration(configuration.getLong("loot-protection-time", 30), TimeUnit.SECONDS).build();
        this.enemyMap = ExpiringMap.builder().expiration(configuration.getLong("loot-protection-time", 30), TimeUnit.SECONDS).build();
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
                    Replacer replacer = message -> message.replace("{time}", "" + TimeUnit.MILLISECONDS.toSeconds(protectedItems.getExpectedExpiration(event.getItem().getUniqueId())));
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
        if(event.getPreviousEnemy() == null) return;
        YamlConfiguration playerData = playerDataManager.get(event.getPlayer());
        YamlConfiguration configuration = getCombatLogX().getConfigurationManager().get("punish.yml");
        String killOptionString = configuration.getString("kill-time", "QUIT");
        UUID enemyUUID = event.getPreviousEnemy().getUniqueId();
        if(killOptionString.equals("JOIN")) {
            playerData.set("loot-protection-enemy", enemyUUID);
            getCombatLogX().saveData(event.getPlayer());
            return;
        }
        enemyMap.put(event.getPlayer().getUniqueId(), enemyUUID);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onUntag(PlayerUntagEvent event) {
        if(event.getPreviousEnemy() == null) return;
        UUID enemyUUID = event.getPreviousEnemy().getUniqueId();
        UUID playerUUID = event.getPlayer().getUniqueId();
        if(event.getUntagReason() == UntagReason.SELF_DEATH) {
            enemyMap.put(playerUUID, enemyUUID);
        }

        if(event.getUntagReason() == UntagReason.ENEMY_DEATH) {
            enemyMap.put(enemyUUID, playerUUID);
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(EntityDeathEvent event) {
        if(!(getCombatLogX() instanceof CombatPlugin)) return;
        CombatPlugin clx = (CombatPlugin) getCombatLogX();
        ListenerDeath listenerDeath = clx.getDeathListener();
        Entity entity = event.getEntity();
        if(entity instanceof Player && configuration.getBoolean("only-protect-after-log", false) && !listenerDeath.contains((Player) entity)) {
            return;
        }

        UUID enemyUUID = enemyMap.get(entity.getUniqueId());
        if(!checkVoidKill(event) && entity instanceof Player) {
            Player player = (Player) entity;
            YamlConfiguration playerData = playerDataManager.get(player);
            String enemyUUIDString = playerData.getString("loot-protection-enemy");

            if(enemyUUIDString != null) {
                playerData.set("loot-protection-enemy", null);
                playerDataManager.save(player);
                enemyUUID = UUID.fromString(enemyUUIDString);
            }
        }

        if(enemyUUID == null) return;
        Entity enemy = Bukkit.getEntity(enemyUUID);
        if(enemy == null) return;
        Location location = toBlockLocation(entity.getLocation());

        ConcurrentLinkedQueue<ProtectedItem> list = new ConcurrentLinkedQueue<>();
        final Location finalLocation = location;
        event.getDrops().forEach((item -> {
            ProtectedItem protectedItem = new ProtectedItem(finalLocation, item);
            protectedItem.setOwnerUUID(enemy.getUniqueId());
            list.add(protectedItem);
        }));
        pendingProtection.put(toBlockLocation(location), list);
        String name = entity.getCustomName() == null ? entity.getName() : entity.getCustomName();
        Replacer replacer = message -> message.replace("{time}", "" + TimeUnit.MILLISECONDS.toSeconds(protectedItems.getExpiration())).replace("{enemy}", name);
        getLanguageManager().sendMessage(enemy, "expansion.loot-protection.enemy-died", replacer, true);
    }

    private boolean checkVoidKill(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if(entity.getLastDamageCause() != null && entity.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.VOID && configuration.getBoolean("return-void-items", true)) {
            UUID enemyUUID = enemyMap.get(entity.getUniqueId());
            if(enemyUUID == null) return true;
            Entity enemy = Bukkit.getEntity(enemyUUID);
            if(!(enemy instanceof Player)) return true;
            Player enemyPlayer = (Player) enemy;
            Location location = enemy.getLocation();
            for(final ItemStack droppedItem : event.getDrops()) {
                enemyPlayer.getInventory().addItem(droppedItem).values().forEach((item) -> {
                    enemy.getWorld().dropItem(location, item, (itemEntity) -> {
                        ProtectedItem protectedItem = new ProtectedItem(location, item);
                        protectedItem.setItemUUID(itemEntity.getUniqueId());
                        protectedItem.setOwnerUUID(enemyUUID);
                        protectedItems.put(protectedItem.getItemUUID(), protectedItem);
                    });
                });
            }
            event.getDrops().clear();
            return true;
        }
        return false;
    }

    @EventHandler(priority = EventPriority.MONITOR)
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
