package com.SirBlobman.combatlogx.expansion.compatibility.citizens.manager;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent.TagReason;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent.TagType;
import com.SirBlobman.combatlogx.api.shaded.item.ItemUtil;
import com.SirBlobman.combatlogx.api.shaded.nms.AbstractNMS;
import com.SirBlobman.combatlogx.api.shaded.nms.EntityHandler;
import com.SirBlobman.combatlogx.api.shaded.nms.MultiVersionHandler;
import com.SirBlobman.combatlogx.api.shaded.nms.VersionUtil;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.SirBlobman.combatlogx.expansion.compatibility.citizens.CompatibilityCitizens;
import com.SirBlobman.combatlogx.expansion.compatibility.citizens.trait.TraitCombatLogX;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.TraitFactory;
import net.citizensnpcs.api.trait.TraitInfo;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;
import net.citizensnpcs.api.trait.trait.Inventory;
import net.citizensnpcs.api.trait.trait.Owner;

public class NPCManager {
    private final CompatibilityCitizens expansion;
    private TraitInfo traitInfo = null;
    public NPCManager(CompatibilityCitizens expansion) {
        this.expansion = Objects.requireNonNull(expansion, "expansion must not be null!");
    }
    
    public void registerTrait() {
        try {
            Class<TraitInfo> class_TraitInfo = TraitInfo.class;
            Constructor<TraitInfo> constructor = class_TraitInfo.getDeclaredConstructor(Class.class);
            constructor.setAccessible(true);
            
            this.traitInfo = constructor.newInstance(TraitCombatLogX.class);
            this.traitInfo.withSupplier(() -> new TraitCombatLogX(this.expansion));
            
            TraitFactory traitFactory = CitizensAPI.getTraitFactory();
            traitFactory.registerTrait(traitInfo);
        } catch(ReflectiveOperationException ex) {
            Logger logger = this.expansion.getLogger();
            logger.log(Level.WARNING, "Failed to register the CombatLogX NPC Trait:", ex);
        }
    }
    
    public void onDisable() {
        NPCRegistry npcRegistry = CitizensAPI.getNPCRegistry();
        for(NPC npc : npcRegistry) {
            if(isInvalid(npc)) continue;
            npc.destroy();
        }
        
        if(this.traitInfo != null) {
            TraitFactory traitFactory = CitizensAPI.getTraitFactory();
            traitFactory.deregisterTrait(traitInfo);
        }
    }
    
    public boolean isInvalid(NPC npc) {
        if(npc == null) return true;
        if(!npc.hasTrait(TraitCombatLogX.class)) return true;
        
        TraitCombatLogX traitCombatLogX = npc.getTrait(TraitCombatLogX.class);
        return (traitCombatLogX.getOwner() == null);
    }
    
    public YamlConfiguration getData(OfflinePlayer player) {
        if(player == null) return new YamlConfiguration();
        
        ICombatLogX plugin = this.expansion.getPlugin();
        return plugin.getDataFile(player);
    }
    
    public void setData(OfflinePlayer player, YamlConfiguration config) {
        if(player == null || config == null) return;
        
        ICombatLogX plugin = this.expansion.getPlugin();
        plugin.saveDataFile(player, config);
    }
    
    public void saveHealth(NPC npc) {
        if(isInvalid(npc)) return;
        TraitCombatLogX traitCombatLogX = npc.getTrait(TraitCombatLogX.class);
        OfflinePlayer owner = traitCombatLogX.getOwner();
        
        YamlConfiguration config = getData(owner);
        if(npc.isSpawned()) {
            Entity entity = npc.getEntity();
            if(entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                double health = livingEntity.getHealth();
                
                config.set("citizens-compatibility.last-health", health);
                setData(owner, config);
                return;
            }
        }
    
        config.set("citizens-compatibility.last-health", 0.0D);
        setData(owner, config);
    }
    
    public double loadHealth(Player player) {
        if(player == null) return 0.0D;
        double playerHealth = player.getHealth();
        
        YamlConfiguration config = getData(player);
        double health = config.getDouble("citizens-compatibility.last-health", playerHealth);
        
        ICombatLogX plugin = this.expansion.getPlugin();
        MultiVersionHandler<?> multiVersionHandler = plugin.getMultiVersionHandler();
        AbstractNMS nmsHandler = multiVersionHandler.getInterface();
        EntityHandler entityHandler = nmsHandler.getEntityHandler();
        
        double maxHealth = entityHandler.getMaxHealth(player);
        double value = Math.min(health, maxHealth);
        player.setHealth(value);
        
        config.set("citizens-compatibility.last-health", null);
        setData(player, config);
        return value;
    }
    
    public void saveLocation(NPC npc) {
        if(isInvalid(npc)) return;
        TraitCombatLogX traitCombatLogX = npc.getTrait(TraitCombatLogX.class);
        OfflinePlayer owner = traitCombatLogX.getOwner();
        
        YamlConfiguration config = getData(owner);
        if(npc.isSpawned()) {
            Entity entity = npc.getEntity();
            Location location = entity.getLocation();
            config.set("citizens-compatibility.last-location", location);
        } else {
            Location location = npc.getStoredLocation();
            config.set("citizens-compatibility.last-location", location);
        }
        
        setData(owner, config);
    }
    
    public void loadLocation(Player player) {
        if(player == null) return;
        YamlConfiguration config = getData(player);
        
        Object object = config.get("citizens-compatibility.last-location", null);
        if(!(object instanceof Location)) return;
        
        Location location = (Location) object;
        player.teleport(location);
    }
    
    public void saveInventory(Player player) {
        if(player == null) return;
        YamlConfiguration data = getData(player);
        
        PlayerInventory playerInventory = player.getInventory();
        ItemStack[] contents = playerInventory.getContents().clone();
        int contentsLength = contents.length;
        
        for(int slot = 0; slot < contentsLength; slot++) {
            ItemStack item = contents[slot];
            data.set("citizens-compatibility.last-inventory." + slot, item);
        }
        
        int minorVersion = VersionUtil.getMinorVersion();
        if(minorVersion < 9) {
            ItemStack[] armorContents = playerInventory.getArmorContents().clone();
            int armorContentsLength = armorContents.length;
            
            for(int slot = 0; slot < armorContentsLength; slot++) {
                ItemStack item = armorContents[slot];
                data.set("citizens-compatibility.last-armor." + slot, item);
            }
        }
        
        setData(player, data);
    }
    
    public void loadInventory(Player player) {
        if(player == null) return;
        YamlConfiguration data = getData(player);
        
        FileConfiguration config = this.expansion.getConfig("citizens-compatibility.yml");
        if(!config.getBoolean("npc-options.store-inventory", true)) return;
        
        PlayerInventory playerInventory = player.getInventory();
        playerInventory.clear();
        
        ConfigurationSection inventorySection = data.getConfigurationSection("citizens-compatibility.last-inventory");
        if(inventorySection != null) {
            Set<String> slotKeys = inventorySection.getKeys(false);
            for(String slotKey : slotKeys) {
                try {
                    int slot = Integer.parseInt(slotKey);
                    ItemStack item = inventorySection.getItemStack(slotKey);
                    item = (ItemUtil.isAir(item) ? ItemUtil.getAir() : item.clone());
                    playerInventory.setItem(slot, item);
                } catch(NumberFormatException ignored) {}
            }
        }
        
        int minorVersion = VersionUtil.getMinorVersion();
        if(minorVersion < 9) {
            ConfigurationSection armorSection = data.getConfigurationSection("citizens-compatibility.last-armor");
            if(armorSection != null) {
                ItemStack[] armorContents = playerInventory.getArmorContents();
                Set<String> slotKeys = armorSection.getKeys(false);
                for(String slotKey : slotKeys) {
                    try {
                        int slot = Integer.parseInt(slotKey);
                        ItemStack item = armorSection.getItemStack(slotKey);
                        armorContents[slot] = (ItemUtil.isAir(item) ? ItemUtil.getAir() : item.clone());
                    } catch(NumberFormatException ignored) {}
                }
                playerInventory.setArmorContents(armorContents);
            }
        }
        
        player.updateInventory();
    }
    
    public void dropInventory(NPC npc) {
        if(isInvalid(npc)) return;
        FileConfiguration config = this.expansion.getConfig("citizens-compatibility.yml");
        if(!config.getBoolean("npc-options.store-inventory", true)) return;
        
        TraitCombatLogX traitCombatLogX = npc.getTrait(TraitCombatLogX.class);
        OfflinePlayer owner = traitCombatLogX.getOwner();
        YamlConfiguration data = getData(owner);
        
        Location location;
        if(npc.isSpawned()) {
            Entity entity = npc.getEntity();
            location = entity.getLocation();
        } else {
            location = npc.getStoredLocation();
        }
        
        if(location == null) return;
        World world = location.getWorld();
        if(world == null) return;
        
        if(npc.hasTrait(Equipment.class)) npc.removeTrait(Equipment.class);
        if(npc.hasTrait(Inventory.class)) npc.removeTrait(Inventory.class);
        
        ConfigurationSection inventorySection = data.getConfigurationSection("citizens-compatibility.last-inventory");
        if(inventorySection != null) {
            Set<String> slotKeys = inventorySection.getKeys(false);
            for(String slotKey : slotKeys) {
                try {
                    ItemStack item = inventorySection.getItemStack(slotKey);
                    if(ItemUtil.isAir(item)) continue;
                    world.dropItemNaturally(location, item.clone());
                } catch(NumberFormatException ignored) {}
            }
        }
        
        int minorVersion = VersionUtil.getMinorVersion();
        if(minorVersion < 9) {
            ConfigurationSection armorSection = data.getConfigurationSection("citizens-compatibility.last-armor");
            if(armorSection != null) {
                Set<String> slotKeys = armorSection.getKeys(false);
                for(String slotKey : slotKeys) {
                    try {
                        ItemStack item = armorSection.getItemStack(slotKey);
                        if(ItemUtil.isAir(item)) continue;
                        world.dropItemNaturally(location, item.clone());
                    } catch(NumberFormatException ignored) {}
                }
            }
        }
        
        data.set("citizens-compatibility.last-inventory", null);
        data.set("citizens-compatibility.last-armor", null);
        setData(owner, data);
    }
    
    public void loadTagStatus(Player player) {
        if(player == null) return;
        
        FileConfiguration config = this.expansion.getConfig("citizens-compatibility.yml");
        if(!config.getBoolean("retag-player-on-login", true)) return;
        
        ICombatLogX plugin = this.expansion.getPlugin();
        ICombatManager combatManager = plugin.getCombatManager();
        combatManager.tag(player, null, TagType.UNKNOWN, TagReason.UNKNOWN);
    }
    
    public void createNPC(Player player, LivingEntity enemy) {
        if(player == null) return;
        
        Location location = player.getLocation().clone();
        EntityType bukkitType = getMobType();
        String playerName = player.getName();
        
        NPCRegistry npcRegistry = CitizensAPI.getNPCRegistry();
        NPC npc = npcRegistry.createNPC(bukkitType, playerName);
        npc.removeTrait(Owner.class);
        
        TraitCombatLogX traitCombatLogX = npc.getTrait(TraitCombatLogX.class);
        traitCombatLogX.extendLife();
        
        traitCombatLogX.setOwner(player);
        if(enemy instanceof Player) {
            Player enemyPlayer = (Player) enemy;
            traitCombatLogX.setEnemy(enemyPlayer);
        }
        
        boolean spawned = npc.spawn(location);
        if(!spawned) {
            Logger logger = this.expansion.getLogger();
            logger.warning("An NPC could not be spawned for " + playerName + ".");
            return;
        }
        setOptions(npc, player);
        
        SentinelManager sentinelManager = this.expansion.getSentinelManager();
        FileConfiguration config = this.expansion.getConfig("citizens-compatibility.yml");
        if(sentinelManager != null && config.getBoolean("sentinel-options.enable-sentinel", true)) {
            sentinelManager.setOptions(npc, player, enemy);
        }
    }
    
    public NPC getNPC(OfflinePlayer player) {
        if(player == null) return null;
        
        UUID uuid = player.getUniqueId();
        return getNPC(uuid);
    }
    
    public NPC getNPC(UUID uuid) {
        if(uuid == null) return null;
        
        NPCRegistry npcRegistry = CitizensAPI.getNPCRegistry();
        for(NPC npc : npcRegistry) {
            if(isInvalid(npc)) continue;
            
            TraitCombatLogX traitCombatLogX = npc.getTrait(TraitCombatLogX.class);
            OfflinePlayer owner = traitCombatLogX.getOwner();
            if(owner == null) continue;
            
            UUID ownerId = owner.getUniqueId();
            if(uuid.equals(ownerId)) return npc;
        }
        
        return null;
    }
    
    private EntityType getMobType() {
        FileConfiguration config = this.expansion.getConfig("citizens-compatibility.yml");
        String mobTypeString = config.getString("npc-options.mob-type", "PLAYER");
        
        try {
            EntityType bukkitType = EntityType.valueOf(mobTypeString);
            if(!bukkitType.isAlive()) throw new IllegalArgumentException("mobType is not alive!");
            return bukkitType;
        } catch(Exception ex) {
            Logger logger = this.expansion.getLogger();
            logger.warning("Invalid NPC EntityType '" + mobTypeString + "'. Default to PLAYER.");
            return EntityType.PLAYER;
        }
    }
    
    private void setOptions(NPC npc, Player player) {
        if(npc == null || player == null) return;
        
        FileConfiguration config = this.expansion.getConfig("citizens-compatibility.yml");
        if(config.getBoolean("npc-options.store-inventory", true)) {
            saveInventory(player);
            transferInventory(player, npc);
        }
        
        npc.setProtected(false);
        setLivingOptions(npc, player);
    }
    
    private void setLivingOptions(NPC npc, Player player) {
        if(npc == null || player == null) return;
        
        Entity entity = npc.getEntity();
        if(!(entity instanceof LivingEntity)) return;
        LivingEntity livingEntity = (LivingEntity) entity;
        
        ICombatLogX plugin = this.expansion.getPlugin();
        MultiVersionHandler<?> multiVersionHandler = plugin.getMultiVersionHandler();
        AbstractNMS nmsHandler = multiVersionHandler.getInterface();
        EntityHandler entityHandler = nmsHandler.getEntityHandler();
        
        double health = player.getHealth();
        double maxHealth = entityHandler.getMaxHealth(player);
        double maxHealthValue = Math.max(health, maxHealth);
        
        entityHandler.setMaxHealth(livingEntity, maxHealthValue);
        livingEntity.setHealth(health);
        
        setMobTargetable(npc);
    }
    
    private void setMobTargetable(NPC npc) {
        if(npc == null) return;
        
        FileConfiguration config = this.expansion.getConfig("citizens-compatibility.yml");
        if(!config.getBoolean("npc-options.mob-target", true)) return;
        
        Entity entity = npc.getEntity();
        if(!(entity instanceof LivingEntity)) return;
        LivingEntity livingEntity = (LivingEntity) entity;
        
        List<Entity> nearbyEntityList = livingEntity.getNearbyEntities(16.0D, 16.0D, 16.0D);
        for(Entity nearby : nearbyEntityList) {
            if(nearby instanceof Player) continue;
            if(!(nearby instanceof Monster)) continue;
            
            Monster monster = (Monster) nearby;
            monster.setTarget(livingEntity);
        }
    }
    
    @SuppressWarnings("deprecation")
    private void transferInventory(Player player, NPC npc) {
        if(player == null || npc == null) return;
        PlayerInventory playerInventory = player.getInventory();
        
        try {
            Equipment trait = npc.getTrait(Equipment.class);
            
            ItemStack helmet = copyItem(playerInventory.getHelmet());
            trait.set(EquipmentSlot.HELMET, helmet);
            
            ItemStack chestplate = copyItem(playerInventory.getChestplate());
            trait.set(EquipmentSlot.CHESTPLATE, chestplate);
            
            ItemStack leggings = copyItem(playerInventory.getLeggings());
            trait.set(EquipmentSlot.LEGGINGS, leggings);
            
            ItemStack boots = copyItem(playerInventory.getBoots());
            trait.set(EquipmentSlot.BOOTS, boots);
            
            int minorVersion = VersionUtil.getMinorVersion();
            if(minorVersion < 9) {
                ItemStack handItem = copyItem(playerInventory.getItemInHand());
                trait.set(EquipmentSlot.HAND, handItem);
            } else {
                ItemStack mainHandItem = copyItem(playerInventory.getItemInMainHand());
                trait.set(EquipmentSlot.HAND, mainHandItem);
                
                ItemStack offHandItem = copyItem(playerInventory.getItemInOffHand());
                trait.set(EquipmentSlot.OFF_HAND, offHandItem);
            }
        } catch(UnsupportedOperationException | IllegalArgumentException ignored) {}
        
        playerInventory.clear();
        player.updateInventory();
    }
    
    private ItemStack copyItem(ItemStack item) {
        return (ItemUtil.isAir(item) ? ItemUtil.getAir() : item.clone());
    }
}