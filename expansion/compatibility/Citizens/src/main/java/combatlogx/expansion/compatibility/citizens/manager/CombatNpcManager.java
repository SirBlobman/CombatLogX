package combatlogx.expansion.compatibility.citizens.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.nms.EntityHandler;
import com.github.sirblobman.api.nms.ItemHandler;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.utility.ItemUtility;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;

import combatlogx.expansion.compatibility.citizens.CitizensExpansion;
import combatlogx.expansion.compatibility.citizens.object.CombatNPC;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;
import net.citizensnpcs.api.trait.trait.Owner;
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

    protected CitizensExpansion getExpansion() {
        return this.expansion;
    }

    protected ICombatLogX getCombatLogX() {
        CitizensExpansion expansion = getExpansion();
        return expansion.getPlugin();
    }

    public CombatNPC getCombatNPC(NPC npc) {
        if(npc == null) return null;

        UUID uuid = npc.getUniqueId();
        return this.npcCombatMap.getOrDefault(uuid, null);
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
        } catch(IllegalStateException ignored) {}

        saveNPC(owner, originalNPC);
        originalNPC.destroy();

        this.playerNpcMap.remove(owner.getUniqueId());
        this.npcCombatMap.remove(originalNPC.getUniqueId());
    }

    public void removeAll() {
        Map<UUID, CombatNPC> copyMap = new HashMap<>(this.playerNpcMap);
        this.playerNpcMap.clear();

        Collection<CombatNPC> npcCollection = copyMap.values();
        npcCollection.forEach(this::remove);
    }

    public void createNPC(Player player) {
        if(player == null || player.hasMetadata("NPC")) {
            printDebug("player was null or an NPC, not spawning.");
            return;
        }

        UUID uuid = player.getUniqueId();
        String playerName = player.getName();
        printDebug("Spawning NPC for player '" + playerName + "'.");

        EntityType entityType = getEntityType();
        NPCRegistry npcRegistry = CitizensAPI.getNPCRegistry();
        NPC npc = npcRegistry.createNPC(entityType, playerName);
        printDebug("Created NPC with entity type " + entityType + ".");

        Location location = player.getLocation();
        boolean spawn = npc.spawn(location);
        if(!spawn) {
            printDebug("Failed to spawn an NPC. (npc.spawn() returned false)");
            return;
        }

        Entity entity = npc.getEntity();
        if(!(entity instanceof LivingEntity)) {
            printDebug("NPC for player '" + playerName + "' is not a LivingEntity, removing...");
            npc.destroy();
            return;
        }

        LivingEntity livingEntity = (LivingEntity) entity;
        npc.setProtected(false);

        if(npc.hasTrait(Owner.class)) {
            npc.removeTrait(Owner.class);
        }

        ICombatLogX plugin = this.expansion.getPlugin();
        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        EntityHandler entityHandler = multiVersionHandler.getEntityHandler();

        double health = player.getHealth();
        double maxHealth = entityHandler.getMaxHealth(livingEntity);
        if(maxHealth < health) entityHandler.setMaxHealth(livingEntity, health);
        livingEntity.setHealth(health);

        YamlConfiguration configuration = getConfiguration();
        if(configuration.getBoolean("mob-target")) {
            double radius = configuration.getDouble("mob-target-radius");
            List<Entity> nearbyEntityList = livingEntity.getNearbyEntities(radius, radius, radius);
            for(Entity nearby : nearbyEntityList) {
                if(!(nearby instanceof Monster)) continue;
                Monster monster = (Monster) nearby;
                monster.setTarget(livingEntity);
            }
        }

        CombatNPC combatNPC = new CombatNPC(this.expansion, npc, player);
        this.playerNpcMap.put(uuid, combatNPC);
        this.npcCombatMap.put(npc.getUniqueId(), combatNPC);

        ICombatManager combatManager = plugin.getCombatManager();
        LivingEntity enemyEntity = combatManager.getEnemy(player);
        if(enemyEntity instanceof Player) combatNPC.setEnemy((Player) enemyEntity);

        saveLocation(player, npc);
        equipNPC(player, npc);
        saveInventory(player);

        CitizensExpansion citizensExpansion = getExpansion();
        if(citizensExpansion.isSentinelEnabled()) {
            if(enemyEntity != null && configuration.getBoolean("attack-first")) {SentinelTrait sentinelTrait = npc.getOrAddTrait(SentinelTrait.class);
                sentinelTrait.setInvincible(false);
                sentinelTrait.respawnTime = -1;

                UUID enemyId = enemyEntity.getUniqueId();
                String enemyIdString = enemyId.toString();

                SentinelTargetLabel targetLabel = new SentinelTargetLabel("uuid:" + enemyIdString);
                targetLabel.addToList(sentinelTrait.allTargets);
            }
        }

        combatNPC.start();
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

    public void equipNPC(Player player, NPC npc) {
        PlayerInventory playerInventory = player.getInventory();
        Equipment equipmentTrait = npc.getOrAddTrait(Equipment.class);

        ItemStack helmet = playerInventory.getHelmet();
        if(!ItemUtility.isAir(helmet)) equipmentTrait.set(EquipmentSlot.HELMET, helmet);

        ItemStack chestplate = playerInventory.getChestplate();
        if(!ItemUtility.isAir(chestplate)) equipmentTrait.set(EquipmentSlot.CHESTPLATE, chestplate);

        ItemStack leggings = playerInventory.getLeggings();
        if(!ItemUtility.isAir(leggings)) equipmentTrait.set(EquipmentSlot.LEGGINGS, leggings);

        ItemStack boots = playerInventory.getBoots();
        if(!ItemUtility.isAir(boots)) equipmentTrait.set(EquipmentSlot.BOOTS, boots);

        int minorVersion = VersionUtility.getMinorVersion();
        if(minorVersion < 9) {
            int slot = playerInventory.getHeldItemSlot();
            ItemStack heldItem = playerInventory.getItem(slot);
            if(heldItem != null) equipmentTrait.set(EquipmentSlot.HAND, heldItem);
        } else {
            ItemStack mainHand = playerInventory.getItemInMainHand();
            if(!ItemUtility.isAir(mainHand)) equipmentTrait.set(EquipmentSlot.HAND, mainHand);

            ItemStack offHand = playerInventory.getItemInOffHand();
            if(!ItemUtility.isAir(offHand)) equipmentTrait.set(EquipmentSlot.OFF_HAND, offHand);
        }
    }

    public void saveInventory(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        ItemStack[] contents = playerInventory.getContents().clone();
        YamlConfiguration playerData = getData(player);

        int contentsLength = contents.length;
        for(int slot = 0; slot < contentsLength; slot++) {
            ItemStack item = contents[slot];
            playerData.set("citizens-compatibility.inventory." + slot, item);
        }

        int minorVersion = VersionUtility.getMinorVersion();
        if(minorVersion <= 8) {
            ItemStack[] armorContents = playerInventory.getArmorContents().clone();
            int armorContentsLength = armorContents.length;
            for(int slot = 0; slot < armorContentsLength; slot++) {
                ItemStack item = armorContents[slot];
                playerData.set("citizens-compatibility.armor." + slot, item);
            }
        }

        saveData(player);
        playerInventory.clear();
        player.updateInventory();
    }

    public void restoreInventory(Player player) {
        printDebug("Restoring inventory contents...");
        restoreInventoryContents(player);

        printDebug("Restoring armor contents...");
        restoreArmorContents(player);
    }

    private void restoreInventoryContents(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        playerInventory.clear();

        YamlConfiguration playerData = getData(player);
        ConfigurationSection inventorySection =
                playerData.getConfigurationSection("citizens-compatibility.inventory");
        if(inventorySection == null) {
            printDebug("Did not find stored inventory, not restoring.");
            return;
        }

        int playerInventorySize = playerInventory.getSize();
        for(int slot = 0; slot < playerInventorySize; slot++) {
            String path = Integer.toString(slot);
            ItemStack item = inventorySection.getItemStack(path);
            playerInventory.setItem(slot, item);
            printDebug("Restored item '" + toString(item) + "' in slot '" + slot + "'.");
        }

        printDebug("Clearing stored items.");
        playerData.set("citizens-compatibility.inventory", null);
        saveData(player);

        player.updateInventory();
        printDebug("Done.");
    }

    private void restoreArmorContents(Player player) {
        int minorVersion = VersionUtility.getMinorVersion();
        if(minorVersion > 8) {
            printDebug("Version greater than 1.8, armor already restored by inventory restore.");
            return;
        }

        PlayerInventory playerInventory = player.getInventory();
        playerInventory.clear();

        YamlConfiguration playerData = getData(player);
        ConfigurationSection armorSection =
                playerData.getConfigurationSection("citizens-compatibility.armor");
        if(armorSection == null) {
            printDebug("Did not find stored armor, not restoring.");
            return;
        }

        ItemStack[] armorContents = playerInventory.getArmorContents();
        int armorContentsSize = armorContents.length;

        for(int slot = 0; slot < armorContentsSize; slot++) {
            String path = Integer.toString(slot);
            ItemStack item = armorSection.getItemStack(path);
            armorContents[slot] = item;
            printDebug("Restored armor '" + toString(item) + "' in slot '" + slot + "'.");
        }

        playerInventory.setArmorContents(armorContents);

        printDebug("Clearing stored items.");
        playerData.set("citizens-compatibility.armor", null);
        saveData(player);

        player.updateInventory();
        printDebug("Done.");
    }

    public void dropInventory(CombatNPC npc) {
        OfflinePlayer offlineOwner = npc.getOfflineOwner();
        YamlConfiguration data = getData(offlineOwner);

        NPC originalNPC = npc.getOriginalNPC();
        Location location = getLocation(originalNPC);
        if(location == null) return;

        World world = location.getWorld();
        if(world == null) return;

        ConfigurationSection inventorySection = data.getConfigurationSection("citizens-compatibility.inventory");
        if(inventorySection != null) {
            Map<String, Object> valueMap = inventorySection.getValues(false);
            List<ItemStack> itemList = valueMap.values().stream().filter(value -> value instanceof ItemStack).map(ItemStack.class::cast).collect(Collectors.toList());

            for(ItemStack item : itemList) {
                if(ItemUtility.isAir(item)) continue;
                world.dropItemNaturally(location, item);
            }

            data.set("citizens.compatibility.inventory", null);
        }

        ConfigurationSection armorSection = data.getConfigurationSection("citizens-compatibility.armor");
        if(armorSection != null) {
            Map<String, Object> valueMap = armorSection.getValues(false);
            List<ItemStack> itemList = valueMap.values().stream().filter(value -> value instanceof ItemStack).map(ItemStack.class::cast).collect(Collectors.toList());

            for(ItemStack item : itemList) {
                if(ItemUtility.isAir(item)) continue;
                world.dropItemNaturally(location, item);
            }

            data.set("citizens.compatibility.armor", null);
        }

        saveData(offlineOwner);
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
        if(!npc.isSpawned()) return 0.0D;
        Entity entity = npc.getEntity();
        if(!(entity instanceof LivingEntity)) return 0.0D;

        LivingEntity livingEntity = (LivingEntity) entity;
        return livingEntity.getHealth();
    }

    private Location getLocation(NPC npc) {
        if(!npc.isSpawned()) return npc.getStoredLocation();
        Entity entity = npc.getEntity();
        return entity.getLocation();
    }

    private YamlConfiguration getConfiguration() {
        ConfigurationManager configurationManager = this.expansion.getConfigurationManager();
        return configurationManager.get("citizens.yml");
    }

    private EntityType getEntityType() {
        YamlConfiguration configuration = getConfiguration();
        String entityTypeName = configuration.getString("mob-type");
        try {
            if(entityTypeName == null) throw new IllegalStateException();
            String value = entityTypeName.toUpperCase();

            EntityType entityType = EntityType.valueOf(value);
            if(!entityType.isAlive()) throw new IllegalStateException();

            return entityType;
        } catch(Exception ex) {
            Logger logger = this.expansion.getLogger();
            logger.warning("Unknown or non-living mob-type '" + entityTypeName + "' for NPCs!");
            logger.warning("Defaulting to PLAYER");
            configuration.set("mob-type", "PLAYER");
            return EntityType.PLAYER;
        }
    }

    private void printDebug(String message) {
        ConfigurationManager pluginConfigurationManager = getCombatLogX().getConfigurationManager();
        YamlConfiguration configuration = pluginConfigurationManager.get("config.yml");
        if(!configuration.getBoolean("debug-mode")) return;

        Logger logger = getExpansion().getLogger();
        logger.info("[Debug] " + message);
    }

    private String toString(ItemStack item) {
        if(item == null) return "null";

        ICombatLogX combatLogX = getCombatLogX();
        MultiVersionHandler multiVersionHandler = combatLogX.getMultiVersionHandler();
        ItemHandler itemHandler = multiVersionHandler.getItemHandler();
        return itemHandler.toNBT(item);
    }
}
