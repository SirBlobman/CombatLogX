package combatlogx.expansion.loot.protection.listener;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;

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
import org.bukkit.plugin.PluginManager;

import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.replacer.LongReplacer;
import com.github.sirblobman.api.language.replacer.Replacer;
import com.github.sirblobman.api.language.replacer.StringReplacer;
import com.github.sirblobman.api.location.BlockLocation;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.configuration.PunishConfiguration;
import com.github.sirblobman.combatlogx.api.event.PlayerPunishEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.manager.IDeathManager;
import com.github.sirblobman.combatlogx.api.object.KillTime;
import com.github.sirblobman.combatlogx.api.object.UntagReason;

import combatlogx.expansion.loot.protection.LootProtectionExpansion;
import combatlogx.expansion.loot.protection.configuration.LootProtectionConfiguration;
import combatlogx.expansion.loot.protection.event.QueryPickupEvent;
import combatlogx.expansion.loot.protection.object.ProtectedItem;
import net.jodah.expiringmap.ExpiringMap;

public class ListenerLootProtection extends ExpansionListener {
    private final LootProtectionExpansion expansion;
    private final Set<UUID> messageCooldownSet;
    private final ExpiringMap<UUID, ProtectedItem> protectedItemMap;
    private final Map<BlockLocation, ConcurrentLinkedQueue<ProtectedItem>> pendingProtectionMap;
    private final Map<UUID, UUID> enemyMap;

    public ListenerLootProtection(@NotNull LootProtectionExpansion expansion) {
        super(expansion);
        this.expansion = expansion;

        LootProtectionConfiguration configuration = getConfiguration();
        int messageCooldown = configuration.getMessageCooldown();
        int lootProtectionTime = configuration.getLootProtectionTime();

        this.messageCooldownSet = Collections.newSetFromMap(ExpiringMap.builder()
                .expiration(messageCooldown, TimeUnit.SECONDS).build());
        this.pendingProtectionMap = ExpiringMap.builder().expiration(lootProtectionTime, TimeUnit.SECONDS).build();
        this.protectedItemMap = ExpiringMap.builder().expiration(lootProtectionTime, TimeUnit.SECONDS).build();
        this.enemyMap = ExpiringMap.builder().expiration(lootProtectionTime, TimeUnit.SECONDS).build();
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
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.callEvent(queryPickupEvent);

        if (!protectedItem.getOwnerUUID().equals(playerId) && !queryPickupEvent.isCancelled()) {
            e.setCancelled(true);
            if (!this.messageCooldownSet.contains(playerId)) {
                long expireMillisLeft = this.protectedItemMap.getExpectedExpiration(itemEntityId);
                long expireSecondsLeft = TimeUnit.MILLISECONDS.toSeconds(expireMillisLeft);

                Replacer replacer = new LongReplacer("{time}", expireSecondsLeft);
                LanguageManager languageManager = getLanguageManager();
                languageManager.sendMessageWithPrefix(player, "expansion.loot-protection.protected", replacer);
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

        ICombatLogX plugin = getCombatLogX();
        PunishConfiguration punishConfiguration = plugin.getPunishConfiguration();
        KillTime killTime = punishConfiguration.getKillTime();
        UUID previousEnemyId = previousEnemy.getUniqueId();

        if (killTime == KillTime.JOIN) {
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

        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (isOnlyProtectAfterLog() && !deathManager.wasPunishKilled(player)) {
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
        BlockLocation entityLocation = BlockLocation.from(entity);
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

        Replacer timeReplacer = new LongReplacer("{time}", timeLeftSeconds);
        Replacer enemyReplacer = new StringReplacer("{enemy}", entityName);
        LanguageManager languageManager = getLanguageManager();
        languageManager.sendMessageWithPrefix(enemy, "expansion.loot-protection.enemy-died",
                timeReplacer, enemyReplacer);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemSpawn(ItemSpawnEvent e) {
        if (this.pendingProtectionMap.isEmpty()) {
            return;
        }

        BlockLocation location = BlockLocation.from(e.getLocation());
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

    private @NotNull LootProtectionExpansion getLootProtection() {
        return this.expansion;
    }

    private @NotNull LootProtectionConfiguration getConfiguration() {
        LootProtectionExpansion expansion = getLootProtection();
        return expansion.getConfiguration();
    }

    private boolean isOnlyProtectAfterLog() {
        LootProtectionConfiguration configuration = getConfiguration();
        return configuration.isOnlyProtectAfterLog();
    }

    private boolean isReturnVoidItems() {
        LootProtectionConfiguration configuration = getConfiguration();
        return configuration.isReturnVoidItems();
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

        if (isReturnVoidItems()) {
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
