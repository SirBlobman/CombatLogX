package combatlogx.expansion.loot.protection.listener;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.language.Replacer;
import com.github.sirblobman.api.object.WorldXYZ;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.event.PlayerPunishEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.manager.IDeathManager;
import com.github.sirblobman.combatlogx.api.object.UntagReason;

import combatlogx.expansion.loot.protection.event.QueryPickupEvent;
import combatlogx.expansion.loot.protection.object.ProtectedItem;
import net.jodah.expiringmap.ExpiringMap;

public class ListenerLootProtection extends ExpansionListener {
    private final Set<UUID> messageCooldownSet;
    private final ExpiringMap<UUID, ProtectedItem> protectedItemMap;
    private final Map<WorldXYZ, ConcurrentLinkedQueue<ProtectedItem>> pendingProtectionMap;
    private final Map<UUID, UUID> enemyMap;

    public ListenerLootProtection(final Expansion expansion) {
        super(expansion);

        ConfigurationManager configurationManager = expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");

        this.messageCooldownSet = Collections.newSetFromMap(ExpiringMap.builder()
                .expiration(configuration.getLong("message-cooldown", 1), TimeUnit.SECONDS).build());
        this.pendingProtectionMap = ExpiringMap.builder()
                .expiration(configuration.getLong("loot-protection-time", 30), TimeUnit.SECONDS).build();
        this.protectedItemMap = ExpiringMap.builder()
                .expiration(configuration.getLong("loot-protection-time", 30), TimeUnit.SECONDS).build();
        this.enemyMap = ExpiringMap.builder()
                .expiration(configuration.getLong("loot-protection-time", 30), TimeUnit.SECONDS).build();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityItemPickup(EntityPickupItemEvent e) {
        Item itemEntity = e.getItem();
        if (!contains(itemEntity)) {
            return;
        }

        Entity entity = e.getEntity();
        if (!(entity instanceof Player)) {
            e.setCancelled(true);
            return;
        }

        Player player = (Player) entity;
        UUID itemEntityId = itemEntity.getUniqueId();
        ProtectedItem protectedItem = this.protectedItemMap.get(itemEntityId);

        UUID playerId = player.getUniqueId();
        QueryPickupEvent queryPickupEvent = new QueryPickupEvent(player, protectedItem);
        Bukkit.getPluginManager().callEvent(queryPickupEvent);

        if (!protectedItem.getOwnerUUID().equals(playerId) && !queryPickupEvent.isCancelled()) {
            e.setCancelled(true);
            if (!this.messageCooldownSet.contains(playerId)) {
                long expireMillisLeft = this.protectedItemMap.getExpectedExpiration(itemEntityId);
                long expireSecondsLeft = TimeUnit.MILLISECONDS.toSeconds(expireMillisLeft);
                String timeLeft = Long.toString(expireSecondsLeft);

                Replacer replacer = message -> message.replace("{time}", timeLeft);
                sendMessageWithPrefix(player, "expansion.loot-protection.protected", replacer);
                this.messageCooldownSet.add(playerId);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onHopperItemPickup(InventoryPickupItemEvent e) {
        Item itemEntity = e.getItem();
        if (contains(itemEntity)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPunish(PlayerPunishEvent e) {
        List<Entity> enemyList = e.getEnemies();
        if (enemyList.isEmpty()) {
            return;
        }

        Entity previousEnemy = enemyList.get(0);
        if (previousEnemy == null) {
            return;
        }

        Player player = e.getPlayer();
        PlayerDataManager playerDataManager = getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);

        YamlConfiguration mainConfiguration = getCombatLogX().getConfigurationManager().get("config.yml");
        String killTimeString = mainConfiguration.getString("kill-time", "QUIT");
        UUID previousEnemyId = previousEnemy.getUniqueId();

        if (killTimeString.equals("JOIN")) {
            playerData.set("loot-protection-enemy", previousEnemyId.toString());
            playerDataManager.save(player);
            return;
        }

        UUID playerId = player.getUniqueId();
        this.enemyMap.put(playerId, previousEnemyId);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onUntag(PlayerUntagEvent e) {
        List<Entity> enemyList = e.getPreviousEnemies();
        if (enemyList.isEmpty()) {
            return;
        }

        Entity previousEnemy = enemyList.get(0);
        if (previousEnemy == null) {
            return;
        }

        Player player = e.getPlayer();
        UUID playerId = player.getUniqueId();
        UUID previousEnemyId = previousEnemy.getUniqueId();

        UntagReason untagReason = e.getUntagReason();
        if (untagReason == UntagReason.SELF_DEATH) {
            this.enemyMap.put(playerId, previousEnemyId);
        }

        if (untagReason == UntagReason.ENEMY_DEATH) {
            this.enemyMap.put(previousEnemyId, playerId);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        ICombatLogX combatLogX = getCombatLogX();
        IDeathManager deathManager = combatLogX.getDeathManager();
        YamlConfiguration configuration = getExpansionConfigurationManager().get("config.yml");

        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (configuration.getBoolean("only-protect-after-log", false)
                    && !deathManager.wasPunishKilled(player)) {
                return;
            }
        }

        UUID entityId = entity.getUniqueId();
        UUID enemyId = this.enemyMap.get(entityId);
        if (!checkVoidKill(e) && entity instanceof Player) {
            Player player = (Player) entity;
            PlayerDataManager playerDataManager = getPlayerDataManager();
            YamlConfiguration playerData = playerDataManager.get(player);
            String enemyIdString = playerData.getString("loot-protection-enemy");

            if (enemyIdString != null) {
                playerData.set("loot-protection-enemy", null);
                playerDataManager.save(player);
                enemyId = UUID.fromString(enemyIdString);
            }
        }

        if (enemyId == null) {
            return;
        }

        Entity enemy = Bukkit.getEntity(enemyId);
        if (enemy == null) {
            return;
        }

        enemyId = enemy.getUniqueId();
        WorldXYZ entityLocation = WorldXYZ.from(entity);
        ConcurrentLinkedQueue<ProtectedItem> protectedItemQueue = new ConcurrentLinkedQueue<>();

        List<ItemStack> dropList = e.getDrops();
        for (ItemStack drop : dropList) {
            ProtectedItem protectedItem = new ProtectedItem(entityLocation, drop);
            protectedItem.setOwnerUUID(enemyId);
            protectedItemQueue.add(protectedItem);
        }

        this.pendingProtectionMap.put(entityLocation, protectedItemQueue);
        String entityName = (entity.getCustomName() == null ? entity.getName() : entity.getCustomName());

        long timeLeftMillis = this.protectedItemMap.getExpiration();
        long timeLeftSeconds = TimeUnit.MILLISECONDS.toSeconds(timeLeftMillis);
        String timeLeft = Long.toString(timeLeftSeconds);

        Replacer replacer = message -> message.replace("{time}", timeLeft)
                .replace("{enemy}", entityName);
        sendMessageWithPrefix(enemy, "expansion.loot-protection.enemy-died", replacer);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemSpawn(ItemSpawnEvent e) {
        if (this.pendingProtectionMap.isEmpty()) {
            return;
        }

        WorldXYZ location = WorldXYZ.from(e.getLocation());
        if (!this.pendingProtectionMap.containsKey(location)) {
            return;
        }

        Item itemEntity = e.getEntity();
        UUID itemEntityId = itemEntity.getUniqueId();
        ConcurrentLinkedQueue<ProtectedItem> protectedItemQueue = this.pendingProtectionMap.get(location);

        for (ProtectedItem protectedItem : protectedItemQueue) {
            ItemStack protectedItemStack = protectedItem.getItemStack();
            ItemStack itemEntityStack = itemEntity.getItemStack();
            if (protectedItemStack.equals(itemEntityStack)) {
                protectedItem.setItemUUID(itemEntityId);
                this.protectedItemMap.put(itemEntityId, protectedItem);
                protectedItemQueue.remove(protectedItem);

                if (protectedItemQueue.isEmpty()) {
                    this.pendingProtectionMap.remove(location);
                }

                return;
            }
        }
    }

    private boolean contains(Item item) {
        UUID uuid = item.getUniqueId();
        return this.protectedItemMap.containsKey(uuid);
    }

    private boolean checkVoidKill(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        EntityDamageEvent lastDamageEvent = entity.getLastDamageCause();
        if (lastDamageEvent == null) {
            return false;
        }

        DamageCause lastDamageCause = lastDamageEvent.getCause();
        if (lastDamageCause != DamageCause.VOID) {
            return false;
        }

        ConfigurationManager configurationManager = getExpansionConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        if (configuration.getBoolean("return-void-items", true)) {
            UUID entityId = entity.getUniqueId();
            UUID enemyId = this.enemyMap.get(entityId);
            if (enemyId == null) {
                return true;
            }

            Entity enemy = Bukkit.getEntity(enemyId);
            if (!(enemy instanceof Player)) {
                return true;
            }

            Player enemyPlayer = (Player) enemy;
            World enemyWorld = enemy.getWorld();
            Location enemyLocation = enemy.getLocation();
            List<ItemStack> dropList = e.getDrops();

            ItemStack[] dropArray = dropList.toArray(new ItemStack[0]);
            PlayerInventory enemyInventory = enemyPlayer.getInventory();
            Map<Integer, ItemStack> leftoverDrops = enemyInventory.addItem(dropArray);
            leftoverDrops.forEach((slot, drop) -> enemyWorld.dropItem(enemyLocation, drop, itemEntity -> {
                ProtectedItem protectedItem = new ProtectedItem(enemyLocation, drop);
                protectedItem.setItemUUID(itemEntity.getUniqueId());
                protectedItem.setOwnerUUID(enemyId);
                this.protectedItemMap.put(protectedItem.getItemUUID(), protectedItem);
            }));

            dropList.clear();
            return true;
        }

        return false;
    }
}
