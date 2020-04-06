package com.SirBlobman.combatlogx.expansion.compatibility.citizens.utility;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.shaded.item.ItemUtil;
import com.SirBlobman.combatlogx.api.shaded.nms.AbstractNMS;
import com.SirBlobman.combatlogx.api.shaded.nms.EntityHandler;
import com.SirBlobman.combatlogx.api.shaded.nms.MultiVersionHandler;
import com.SirBlobman.combatlogx.api.shaded.nms.VersionUtil;
import com.SirBlobman.combatlogx.api.shaded.utility.Util;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.SirBlobman.combatlogx.expansion.compatibility.citizens.CompatibilityCitizens;
import com.SirBlobman.combatlogx.expansion.compatibility.citizens.trait.TraitCombatNPC;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Owner;

public final class NPCManager {
    private static Expansion expansion;
    public static void onEnable(Expansion exp) {
        TraitCombatNPC.onEnable(exp);
        expansion = exp;
    }

    public static void onDisable() {
        TraitCombatNPC.onDisable();
    }

    public static boolean isInvalid(NPC npc) {
        if(npc == null) return true;
        return !npc.hasTrait(TraitCombatNPC.class);
    }

    public static YamlConfiguration getData(OfflinePlayer player) {
        if(player == null) return null;
        
        ICombatLogX plugin = expansion.getPlugin();
        return plugin.getDataFile(player);
    }

    public static void saveData(OfflinePlayer player, YamlConfiguration data) {
        if(player == null || data == null) return;
        
        ICombatLogX plugin = expansion.getPlugin();
        plugin.saveDataFile(player, data);
    }

    public static void saveHealth(OfflinePlayer owner, NPC npc) {
        if(owner == null || npc == null || !npc.isSpawned()) return;
        Entity entity = npc.getEntity();

        double health = 0.0D;
        if(entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) entity;
            health = living.getHealth();
        }

        YamlConfiguration dataFile = getData(owner);
        dataFile.set("citizens-compatibility.last-health", health);
        saveData(owner, dataFile);
    }

    public static double loadHealth(Player player) {
        if(player == null) return 0.0D;

        YamlConfiguration dataFile = getData(player);
        double health = dataFile.getDouble("citizens-compatibility.last-health", player.getHealth());
        player.setHealth(health);

        dataFile.set("citizens-compatibility.last-health", null);
        saveData(player, dataFile);
        return health;
    }

    public static void saveLocation(OfflinePlayer owner, NPC npc) {
        if(owner == null || npc == null || !npc.isSpawned()) return;

        Entity entity = npc.getEntity();
        Location location = entity.getLocation();

        YamlConfiguration dataFile = getData(owner);
        dataFile.set("citizens-compatibility.last-location", location);
        saveData(owner, dataFile);
    }

    public static void loadLocation(Player player) {
        if(player == null) return;

        YamlConfiguration dataFile = getData(player);
        Object locationObject = dataFile.get("citizens-compatibility.last-location", null);
        if(!(locationObject instanceof Location)) return;

        Location location = (Location) locationObject;
        player.teleport(location);

        dataFile.set("citizens-compatibility.last-location", null);
        saveData(player, dataFile);
    }

    public static void saveInventory(Player player) {
        if(player == null) return;
        YamlConfiguration dataFile = getData(player);

        PlayerInventory playerInv = player.getInventory();
        List<ItemStack> contents = Util.newList(playerInv.getContents().clone());
        dataFile.set("citizens-compatibility.last-inventory", contents);

        int minorVersion = VersionUtil.getMinorVersion();
        if(minorVersion <= 8) {
            List<ItemStack> armor = Util.newList(playerInv.getArmorContents().clone());
            dataFile.set("citizens-compatibility.last-armor", armor);
        }

        saveData(player, dataFile);
    }

    @SuppressWarnings("unchecked")
    public static void loadInventory(Player player) {
        if(player == null) return;
        
        FileConfiguration config = expansion.getConfig("citizens-compatibility.yml");
        if(!config.getBoolean("npc-options.store-inventory")) return;

        PlayerInventory playerInv = player.getInventory();
        playerInv.clear();
        player.updateInventory();

        YamlConfiguration dataFile = getData(player);
        List<ItemStack> contentList = (List<ItemStack>) dataFile.getList("citizens-compatibility.last-inventory");
        dataFile.set("citizens-compatibility.last-inventory", null);
        if(contentList != null && !contentList.isEmpty()) {
            ItemStack[] contentArray = contentList.toArray(new ItemStack[0]);
            playerInv.setContents(contentArray);
        }

        int minorVersion = VersionUtil.getMinorVersion();
        if(minorVersion <= 8) {
            List<ItemStack> armorList = (List<ItemStack>) dataFile.getList("citizens-compatibility.last-arnor");
            dataFile.set("citizens-compatibility.last-armor", null);
            if(armorList != null && !armorList.isEmpty()) {
                ItemStack[] armorArray = armorList.toArray(new ItemStack[0]);
                playerInv.setArmorContents(armorArray);
            }
        }

        player.updateInventory();
        saveData(player, dataFile);
    }

    @SuppressWarnings("unchecked")
    public static void dropInventory(OfflinePlayer owner, NPC npc) {
        if(owner == null || npc == null || !npc.isSpawned()) return;
        
        FileConfiguration config = expansion.getConfig("citizens-compatibility.yml");
        if(!config.getBoolean("npc-options.store-inventory")) return;
        if(npc.hasTrait(Equipment.class)) npc.removeTrait(Equipment.class);

        YamlConfiguration dataFile = getData(owner);
        List<ItemStack> contentList = (List<ItemStack>) dataFile.getList("citizens-compatibility.last-inventory");
        List<ItemStack> armorList = (List<ItemStack>) dataFile.getList("citizens-compatibility.last-arnor");
        if(contentList == null || armorList == null) return;
        if(contentList.isEmpty() && armorList.isEmpty()) return;

        Entity entity = npc.getEntity();
        Location location = entity.getLocation();
        World world = location.getWorld();
        if(world == null) return;

        dataFile.set("citizens-compatibility.last-inventory", null);
        for(ItemStack item : contentList) {
            if(ItemUtil.isAir(item)) continue;
            world.dropItemNaturally(location, item);
        }

        int minorVersion = VersionUtil.getMinorVersion();
        if(minorVersion <= 8) {
            dataFile.set("citizens-compatibility.last-armor", null);
            for(ItemStack item : armorList) {
                if(ItemUtil.isAir(item)) continue;
                world.dropItemNaturally(location, item);
            }
        }

        saveData(owner, dataFile);
    }

    public static void loadTagStatus(Player player) {
        if(player == null) return;
        
        FileConfiguration config = expansion.getConfig("citizens-compatibility.yml");
        if(!config.getBoolean("retag-player-on-login")) return;

        ICombatLogX plugin = expansion.getPlugin();
        ICombatManager combatManager = plugin.getCombatManager();
        combatManager.tag(player, null, PlayerPreTagEvent.TagType.UNKNOWN, PlayerPreTagEvent.TagReason.UNKNOWN);
    }

    public static void createNPC(CompatibilityCitizens expansion, Player player, LivingEntity enemy) {
        if(expansion == null || player == null) return;

        Location location = player.getLocation().clone();
        EntityType npcType = getMobType(expansion);
        String playerName = player.getName();

        NPCRegistry registry = CitizensAPI.getNPCRegistry();
        NPC npc = registry.createNPC(npcType, playerName);
        npc.removeTrait(Owner.class);

        TraitCombatNPC combatTrait = npc.getTrait(TraitCombatNPC.class);
        combatTrait.setOwner(player);
        if(enemy instanceof Player) {
            Player enemyPlayer = (Player) enemy;
            combatTrait.setEnemy(enemyPlayer);
        }

        boolean spawned = npc.spawn(location);
        if(!spawned) {
            Logger logger = expansion.getLogger();
            logger.warning("Failed to spawn a Combat NPC for '" + playerName + "'.");
            return;
        }

        setOptions(expansion, npc, player);
        SentinelManager.setSentinelOptions(expansion, npc, player, enemy);
    }

    private static EntityType getMobType(CompatibilityCitizens expansion) {
        FileConfiguration config = expansion.getConfig("citizens-compatibility.yml");
        String mobTypeString = config.getString("npc-options.mob-type");

        try {
            EntityType mobType = EntityType.valueOf(mobTypeString);
            if(!mobType.isAlive()) throw new IllegalArgumentException();
            return mobType;
        } catch(IllegalArgumentException | NullPointerException ex) {
            Logger logger = expansion.getLogger();
            logger.warning("Invalid NPC Mob Type '" + mobTypeString + "'. Defaulting to PLAYER");
            return EntityType.PLAYER;
        }
    }

    private static void setOptions(CompatibilityCitizens expansion, NPC npc, Player player) {
        if(expansion == null || npc == null || player == null) return;

        FileConfiguration config = expansion.getConfig("citizens-compatibility.yml");
        if(config.getBoolean("npc-options.store-inventory")) {
            saveInventory(player);
            transferInventoryToNPC(player, npc);
        }

        npc.setProtected(false);
        setLivingOptions(expansion, npc, player);
    }

    private static void setLivingOptions(CompatibilityCitizens expansion, NPC npc, Player player) {
        if(expansion == null || npc == null || player == null) return;

        Entity entity = npc.getEntity();
        if(!(entity instanceof LivingEntity)) return;
        LivingEntity living = (LivingEntity) entity;
    
        ICombatLogX plugin = expansion.getPlugin();
        MultiVersionHandler<?> multiVersionHandler = plugin.getMultiVersionHandler();
        AbstractNMS nmsHandler = multiVersionHandler.getInterface();
        EntityHandler entityHandler = nmsHandler.getEntityHandler();
    
        double maxHealth = Math.max(player.getHealth(), entityHandler.getMaxHealth(player));
        entityHandler.setMaxHealth(living, maxHealth);

        double health = player.getHealth();
        living.setHealth(health);

        setMobTargetable(expansion, npc);
    }

    private static void setMobTargetable(CompatibilityCitizens expansion, NPC npc) {
        if(expansion == null || npc == null) return;

        FileConfiguration config = expansion.getConfig("citizens-compatibility.yml");
        if(!config.getBoolean("npc-options.mob-target")) return;

        Entity entity = npc.getEntity();
        EntityType npcType = entity.getType();
        if(!npcType.isAlive()) return;

        LivingEntity living = (LivingEntity) entity;
        List<Entity> nearbyList = living.getNearbyEntities(16, 16, 16);
        for(Entity nearby : nearbyList) {
            if(!(nearby instanceof Monster)) continue;

            Monster monster = (Monster) nearby;
            monster.setTarget(living);
        }
    }

    private static void transferInventoryToNPC(Player player, NPC npc) {
        if(player == null || npc == null) return;
        PlayerInventory playerInv = player.getInventory();

        Entity entity = npc.getEntity();
        if(entity instanceof Player || entity instanceof Zombie || entity instanceof Skeleton) {
            ItemStack helmet = copyItem(playerInv.getHelmet());
            ItemStack chestplate = copyItem(playerInv.getChestplate());
            ItemStack leggings = copyItem(playerInv.getLeggings());
            ItemStack boots = copyItem(playerInv.getBoots());

            Equipment equipment = npc.getTrait(Equipment.class);
            equipment.set(Equipment.EquipmentSlot.HELMET, helmet);
            equipment.set(Equipment.EquipmentSlot.CHESTPLATE, chestplate);
            equipment.set(Equipment.EquipmentSlot.LEGGINGS, leggings);
            equipment.set(Equipment.EquipmentSlot.BOOTS, boots);

            int minorVersion = VersionUtil.getMinorVersion();
            if(minorVersion <= 8) {
                @SuppressWarnings("deprecation")
                ItemStack handItem = copyItem(playerInv.getItemInHand());
                equipment.set(Equipment.EquipmentSlot.HAND, handItem);
            } else {
                ItemStack mainHandItem = copyItem(playerInv.getItemInMainHand());
                ItemStack offHandItem = copyItem(playerInv.getItemInOffHand());
                equipment.set(Equipment.EquipmentSlot.HAND, mainHandItem);
                equipment.set(Equipment.EquipmentSlot.OFF_HAND, offHandItem);
            }
        }

        playerInv.clear();
        player.updateInventory();
    }

    private static ItemStack copyItem(ItemStack item) {
        return ItemUtil.isAir(item) ? ItemUtil.getAir() : item.clone();
    }

    public static NPC getNPC(OfflinePlayer player) {
        if(player == null) return null;

        UUID uuid = player.getUniqueId();
        return getNPC(uuid);
    }

    public static NPC getNPC(UUID uuid) {
        if(uuid == null) return null;

        NPCRegistry registry = CitizensAPI.getNPCRegistry();
        for(NPC npc : registry) {
            if(isInvalid(npc)) continue;

            TraitCombatNPC combatNPC = npc.getTrait(TraitCombatNPC.class);
            OfflinePlayer npcOwner = combatNPC.getOwner();
            if(npcOwner == null) continue;

            UUID ownerId = npcOwner.getUniqueId();
            if(ownerId.equals(uuid)) return npc;
        }

        return null;
    }
}