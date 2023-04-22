package combatlogx.expansion.compatibility.citizens.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;

import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.item.ArmorType;
import com.github.sirblobman.api.utility.ItemUtility;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.event.NPCDropItemEvent;
import com.github.sirblobman.combatlogx.api.object.CitizensSlotType;

import combatlogx.expansion.compatibility.citizens.CitizensExpansion;
import combatlogx.expansion.compatibility.citizens.object.StoredInventory;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;

public final class InventoryManager {
    private final CitizensExpansion expansion;
    private final Map<UUID, StoredInventory> storedInventoryMap;

    public InventoryManager(@NotNull CitizensExpansion expansion) {
        this.expansion = expansion;
        this.storedInventoryMap = new HashMap<>();
    }

    public void storeInventory(@NotNull Player player) {
        if (player.hasMetadata("NPC")) {
            throw new IllegalArgumentException("player must not be an NPC!");
        }

        PlayerInventory playerInventory = player.getInventory();
        StoredInventory storedInventory = StoredInventory.createFrom(playerInventory);

        UUID playerId = player.getUniqueId();
        this.storedInventoryMap.put(playerId, storedInventory);

        PlayerDataManager playerDataManager = getPlayerDataManager();
        YamlConfiguration configuration = playerDataManager.get(player);
        ConfigurationSection section = configuration.createSection("citizens-compatibility.stored-inventory");

        CitizensExpansion expansion = getExpansion();
        storedInventory.save(expansion, section);
        playerDataManager.save(player);
    }

    public @Nullable StoredInventory getStoredInventory(@NotNull OfflinePlayer player) {
        UUID playerId = player.getUniqueId();
        if (this.storedInventoryMap.containsKey(playerId)) {
            return this.storedInventoryMap.get(playerId);
        }

        PlayerDataManager playerDataManager = getPlayerDataManager();
        YamlConfiguration configuration = playerDataManager.get(player);
        String storagePath = ("citizens-compatibility.stored-inventory");
        ConfigurationSection section = configuration.getConfigurationSection(storagePath);
        if (section == null) {
            return null;
        }

        CitizensExpansion expansion = getExpansion();
        return StoredInventory.createFrom(expansion, section);
    }

    public void removeStoredInventory(@NotNull OfflinePlayer player) {
        UUID playerId = player.getUniqueId();
        this.storedInventoryMap.remove(playerId);

        PlayerDataManager playerDataManager = getPlayerDataManager();
        YamlConfiguration configuration = playerDataManager.get(player);
        String storagePath = ("citizens-compatibility.stored-inventory");

        configuration.set(storagePath, null);
        playerDataManager.save(player);
    }

    public void restoreInventory(@NotNull Player player) {
        if (player.hasMetadata("NPC")) {
            throw new IllegalArgumentException("player must not be an NPC!");
        }

        StoredInventory storedInventory = getStoredInventory(player);
        if (storedInventory == null) {
            return;
        }

        PlayerInventory playerInventory = player.getInventory();
        playerInventory.clear();

        for (int slot = 0; slot < 36; slot++) {
            ItemStack item = storedInventory.getItem(slot);
            playerInventory.setItem(slot, item);
        }

        playerInventory.setHelmet(storedInventory.getArmor(ArmorType.HELMET));
        playerInventory.setChestplate(storedInventory.getArmor(ArmorType.CHESTPLATE));
        playerInventory.setLeggings(storedInventory.getArmor(ArmorType.LEGGINGS));
        playerInventory.setBoots(storedInventory.getArmor(ArmorType.BOOTS));

        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion < 9) {
            restoreHandsLegacy(storedInventory, playerInventory);
        } else {
            restoreHandsModern(storedInventory, playerInventory);
        }

        removeStoredInventory(player);
        player.updateInventory();
    }

    public void dropInventory(@NotNull OfflinePlayer player, @NotNull Location location) {
        World world = location.getWorld();
        Validate.notNull(world, "location must have a valid world!");

        StoredInventory storedInventory = getStoredInventory(player);
        if (storedInventory == null) {
            return;
        }

        for (int slot = 0; slot < 36; slot++) {
            ItemStack item = storedInventory.getItem(slot);
            dropItem(item, player, location, CitizensSlotType.INVENTORY);
        }

        ArmorType[] armorTypeArray = ArmorType.values();
        for (ArmorType armorType : armorTypeArray) {
            ItemStack item = storedInventory.getArmor(armorType);
            dropItem(item, player, location, CitizensSlotType.ARMOR);
        }

        ItemStack item = storedInventory.getOffHandItem();
        dropItem(item, player, location, CitizensSlotType.OFFHAND);

        removeStoredInventory(player);
    }

    private void dropItem(@NotNull ItemStack item, @NotNull OfflinePlayer player, @NotNull Location location,
                          @NotNull CitizensSlotType type) {
        World world = location.getWorld();
        if (ItemUtility.isAir(item)) {
            return;
        }

        NPCDropItemEvent event = new NPCDropItemEvent(item, player, location, type);
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.callEvent(event);

        if (!event.isCancelled()) {
            world.dropItemNaturally(location, event.getItem());
        }
    }

    public void equipNPC(@NotNull OfflinePlayer player, @NotNull NPC npc) {
        Equipment equipmentTrait = npc.getOrAddTrait(Equipment.class);
        StoredInventory storedInventory = getStoredInventory(player);
        if (storedInventory == null) {
            return;
        }

        ItemStack mainHandItem = storedInventory.getMainHandItem();
        if (mainHandItem != null) {
            equipmentTrait.set(Equipment.EquipmentSlot.HAND, mainHandItem);
        }

        if (VersionUtility.getMinorVersion() > 8) {
            ItemStack offHandItem = storedInventory.getOffHandItem();
            if (offHandItem != null) {
                equipmentTrait.set(Equipment.EquipmentSlot.OFF_HAND, offHandItem);
            }
        }

        ArmorType[] armorTypeArray = ArmorType.values();
        for (ArmorType armorType : armorTypeArray) {
            ItemStack item = storedInventory.getArmor(armorType);
            if (item != null) {
                EquipmentSlot bukkitSlot = armorType.getEquipmentSlot();
                Equipment.EquipmentSlot slot = getNpcSlotFromBukkitSlot(bukkitSlot);
                equipmentTrait.set(slot, item);
            }
        }
    }

    private @NotNull CitizensExpansion getExpansion() {
        return this.expansion;
    }

    private @NotNull ICombatLogX getICombatLogX() {
        CitizensExpansion expansion = getExpansion();
        return expansion.getPlugin();
    }

    private @NotNull PlayerDataManager getPlayerDataManager() {
        ICombatLogX plugin = getICombatLogX();
        return plugin.getPlayerDataManager();
    }

    @SuppressWarnings("deprecation")
    private void restoreHandsLegacy(@NotNull StoredInventory storedInventory, @NotNull PlayerInventory inventory) {
        ItemStack item = storedInventory.getMainHandItem();
        inventory.setItemInHand(item);
    }

    private void restoreHandsModern(@NotNull StoredInventory storedInventory, @NotNull PlayerInventory inventory) {
        ItemStack mainHand = storedInventory.getMainHandItem();
        ItemStack offHand = storedInventory.getOffHandItem();
        inventory.setItemInMainHand(mainHand);
        inventory.setItemInOffHand(offHand);
    }

    private Equipment.EquipmentSlot getNpcSlotFromBukkitSlot(@NotNull EquipmentSlot slot) {
        if (VersionUtility.getMinorVersion() > 8) {
            if (slot == EquipmentSlot.OFF_HAND) {
                return Equipment.EquipmentSlot.OFF_HAND;
            }
        }

        switch (slot) {
            case HEAD:
                return Equipment.EquipmentSlot.HELMET;
            case CHEST:
                return Equipment.EquipmentSlot.CHESTPLATE;
            case LEGS:
                return Equipment.EquipmentSlot.LEGGINGS;
            case FEET:
                return Equipment.EquipmentSlot.BOOTS;
            case HAND:
                return Equipment.EquipmentSlot.HAND;
            default:
                return null;
        }
    }
}
