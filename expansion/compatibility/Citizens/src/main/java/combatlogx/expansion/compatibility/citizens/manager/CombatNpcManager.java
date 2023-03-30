package combatlogx.expansion.compatibility.citizens.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitScheduler;

import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.nms.EntityHandler;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagInformation;

import combatlogx.expansion.compatibility.citizens.CitizensExpansion;
import combatlogx.expansion.compatibility.citizens.configuration.CitizensConfiguration;
import combatlogx.expansion.compatibility.citizens.configuration.SentinelConfiguration;
import combatlogx.expansion.compatibility.citizens.object.CombatNPC;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPC.Metadata;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.trait.Owner;
import org.jetbrains.annotations.Nullable;
import org.mcmonkey.sentinel.SentinelTrait;
import org.mcmonkey.sentinel.targeting.SentinelTargetLabel;

public final class CombatNpcManager {
    private final CitizensExpansion expansion;
    private final Map<UUID, CombatNPC> playerNpcMap;
    private final Map<UUID, CombatNPC> npcCombatMap;

    public CombatNpcManager(CitizensExpansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
        this.playerNpcMap = new ConcurrentHashMap<>();
        this.npcCombatMap = new ConcurrentHashMap<>();
    }

    private CitizensExpansion getExpansion() {
        return this.expansion;
    }

    private ICombatLogX getCombatLogX() {
        CitizensExpansion expansion = getExpansion();
        return expansion.getPlugin();
    }

    @Nullable
    public CombatNPC getCombatNPC(NPC npc) {
        if (npc == null) {
            return null;
        }

        UUID npcId = npc.getUniqueId();
        return this.npcCombatMap.getOrDefault(npcId, null);
    }

    public YamlConfiguration getData(OfflinePlayer player) {
        ICombatLogX plugin = getCombatLogX();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        return playerDataManager.get(player);
    }

    public void saveData(OfflinePlayer player) {
        ICombatLogX plugin = getCombatLogX();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        playerDataManager.save(player);
    }

    public void remove(CombatNPC combatNPC) {
        OfflinePlayer owner = combatNPC.getOfflineOwner();
        NPC originalNPC = combatNPC.getOriginalNPC();

        try {
            combatNPC.cancel();
        } catch (IllegalStateException ignored) {
            // Do Nothing
        }

        saveNPC(owner, originalNPC);
        this.playerNpcMap.remove(owner.getUniqueId());
        this.npcCombatMap.remove(originalNPC.getUniqueId());

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTask(getCombatLogX().getPlugin(), originalNPC::destroy);
    }

    public void removeAll() {
        Map<UUID, CombatNPC> copyMap = new HashMap<>(this.playerNpcMap);
        this.playerNpcMap.clear();

        Collection<CombatNPC> npcCollection = copyMap.values();
        npcCollection.forEach(this::remove);
    }

    public void createNPC(Player player) {
        if (player == null || player.hasMetadata("NPC")) {
            printDebug("player was null or an NPC, not spawning.");
            return;
        }

        UUID uuid = player.getUniqueId();
        String playerName = player.getName();
        printDebug("Spawning NPC for player '" + playerName + "'.");

        EntityType entityType = getEntityType();
        NPCRegistry npcRegistry = CitizensAPI.getNPCRegistry();

        NPC npc = npcRegistry.createNPC(entityType, playerName);
        npc.setProtected(false);
        npc.data().set(Metadata.SHOULD_SAVE, false);
        printDebug("Created NPC with entity type " + entityType + ".");

        Location location = player.getLocation();
        boolean spawn = npc.spawn(location);
        if (!spawn) {
            printDebug("Failed to spawn an NPC. (npc.spawn() returned false)");
            return;
        }

        Entity entity = npc.getEntity();
        if (!(entity instanceof LivingEntity)) {
            printDebug("NPC for player '" + playerName + "' is not a LivingEntity, removing...");
            npc.destroy();
            return;
        }

        LivingEntity livingEntity = (LivingEntity) entity;
        livingEntity.setNoDamageTicks(0);
        livingEntity.setMaximumNoDamageTicks(0);

        if (npc.hasTrait(Owner.class)) {
            npc.removeTrait(Owner.class);
        }

        ICombatLogX plugin = this.expansion.getPlugin();
        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        EntityHandler entityHandler = multiVersionHandler.getEntityHandler();

        double health = player.getHealth();
        double maxHealth = entityHandler.getMaxHealth(livingEntity);
        if (maxHealth < health) {
            entityHandler.setMaxHealth(livingEntity, health);
        }

        livingEntity.setHealth(health);
        CitizensConfiguration configuration = getConfiguration();
        if (configuration.isMobTarget()) {
            npc.data().set(Metadata.TARGETABLE, true);
            double radius = configuration.getMobTargetRadius();
            if (radius >= 0.0D) {
                forceTargetAllNearby(livingEntity, radius);
            }
        }

        CombatNPC combatNPC = new CombatNPC(this.expansion, npc, player);
        this.playerNpcMap.put(uuid, combatNPC);
        this.npcCombatMap.put(npc.getUniqueId(), combatNPC);

        ICombatManager combatManager = plugin.getCombatManager();
        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation != null) {
            Entity enemyEntity = tagInformation.getCurrentEnemy();
            if (enemyEntity instanceof Player) {
                combatNPC.setEnemy((Player) enemyEntity);
            }

            checkSentinel(npc, tagInformation);
        }

        saveLocation(player, npc);
        saveInventory(player);
        equipNPC(player, npc);

        combatNPC.start();
    }

    private void forceTargetAllNearby(LivingEntity entity, double radius) {
        List<Entity> nearbyEntityList = entity.getNearbyEntities(radius, radius, radius);
        for (Entity nearby : nearbyEntityList) {
            if (nearby instanceof Monster) {
                Monster monster = (Monster) nearby;
                monster.setTarget(entity);
            }
        }
    }

    public CombatNPC getNPC(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        return this.playerNpcMap.getOrDefault(uuid, null);
    }

    private void saveNPC(OfflinePlayer owner, NPC npc) {
        saveHealth(owner, npc);
        saveLocation(owner, npc);

        YamlConfiguration data = getData(owner);
        data.set("citizens-compatibility.punish", true);
        saveData(owner);
    }

    private void saveHealth(OfflinePlayer owner, NPC npc) {
        double health = getHealth(npc);
        YamlConfiguration data = getData(owner);
        data.set("citizens-compatibility.health", health);
        saveData(owner);
    }

    private void saveLocation(OfflinePlayer owner, NPC npc) {
        Location location = getLocation(npc);
        YamlConfiguration data = getData(owner);
        data.set("citizens-compatibility.location", location);
        saveData(owner);
    }

    public void saveInventory(Player player) {
        CitizensConfiguration configuration = getConfiguration();
        if (!configuration.isStoreInventory()) {
            return;
        }

        CitizensExpansion expansion = getExpansion();
        InventoryManager inventoryManager = expansion.getInventoryManager();
        inventoryManager.storeInventory(player);

        PlayerInventory playerInventory = player.getInventory();
        playerInventory.clear();
        player.updateInventory();
    }

    public void equipNPC(Player player, NPC npc) {
        CitizensExpansion expansion = getExpansion();
        InventoryManager inventoryManager = expansion.getInventoryManager();
        inventoryManager.equipNPC(player, npc);
    }

    public double loadHealth(Player player) {
        YamlConfiguration data = getData(player);
        return data.getDouble("citizens-compatibility.health");
    }

    public Location loadLocation(Player player) {
        YamlConfiguration data = getData(player);
        Object locationObject = data.get("citizens-compatibility.location");
        return (locationObject instanceof Location ? (Location) locationObject : null);
    }

    private double getHealth(NPC npc) {
        if (!npc.isSpawned()) {
            return 0.0D;
        }

        Entity entity = npc.getEntity();
        if (!(entity instanceof LivingEntity)) {
            return 0.0D;
        }

        LivingEntity livingEntity = (LivingEntity) entity;
        return livingEntity.getHealth();
    }

    public Location getLocation(NPC npc) {
        if (!npc.isSpawned()) {
            return npc.getStoredLocation();
        }

        Entity entity = npc.getEntity();
        return entity.getLocation();
    }

    private CitizensConfiguration getConfiguration() {
        CitizensExpansion expansion = getExpansion();
        return expansion.getCitizensConfiguration();
    }

    private EntityType getEntityType() {
        CitizensConfiguration configuration = getConfiguration();
        return configuration.getMobType();
    }

    private void printDebug(String message) {
        ICombatLogX combatLogX = getCombatLogX();
        if (combatLogX.isDebugModeDisabled()) {
            return;
        }

        Logger logger = getExpansion().getLogger();
        logger.info("[Debug] [CombatNpcManager] " + message);
    }

    private void checkSentinel(NPC npc, TagInformation tagInformation) {
        CitizensExpansion expansion = getExpansion();
        if (!expansion.isSentinelEnabled()) {
            return;
        }

        SentinelTrait sentinelTrait = npc.getOrAddTrait(SentinelTrait.class);
        sentinelTrait.setInvincible(false);
        sentinelTrait.respawnTime = -1;

        SentinelConfiguration configuration = expansion.getSentinelConfiguration();
        if (!configuration.isAttackFirst()) {
            return;
        }

        List<UUID> enemyIdList = tagInformation.getEnemyIds();
        for (UUID enemyId : enemyIdList) {
            String enemyIdString = String.format(Locale.US, "uuid:%s", enemyId);
            SentinelTargetLabel targetLabel = new SentinelTargetLabel(enemyIdString);
            targetLabel.addToList(sentinelTrait.allTargets);
        }
    }
}
