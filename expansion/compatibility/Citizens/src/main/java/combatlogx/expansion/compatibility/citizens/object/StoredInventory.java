package combatlogx.expansion.compatibility.citizens.object;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.github.sirblobman.api.item.ArmorType;
import com.github.sirblobman.api.nms.ItemHandler;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.utility.ItemUtility;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;

import combatlogx.expansion.compatibility.citizens.CitizensExpansion;

public final class StoredInventory {
    private final Map<Integer, ItemStack> contentMap;
    private final Map<ArmorType, ItemStack> armorMap;
    private ItemStack mainHand;
    private ItemStack offHand;

    private StoredInventory() {
        this.contentMap = new HashMap<>();
        this.armorMap = new EnumMap<>(ArmorType.class);
        this.mainHand = null;
        this.offHand = null;
    }

    public static @NotNull StoredInventory createFrom(@NotNull PlayerInventory playerInventory) {
        StoredInventory storedInventory = new StoredInventory();
        storedInventory.setArmor(ArmorType.HELMET, playerInventory.getHelmet());
        storedInventory.setArmor(ArmorType.CHESTPLATE, playerInventory.getChestplate());
        storedInventory.setArmor(ArmorType.LEGGINGS, playerInventory.getLeggings());
        storedInventory.setArmor(ArmorType.BOOTS, playerInventory.getBoots());

        for (int slot = 0; slot < 36; slot++) {
            ItemStack item = playerInventory.getItem(slot);
            storedInventory.setItemStack(slot, item);
        }

        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion < 9) {
            setHandLegacy(storedInventory, playerInventory);
        } else {
            setHandModern(storedInventory, playerInventory);
        }

        return storedInventory;
    }

    public static @NotNull StoredInventory createFrom(@NotNull CitizensExpansion expansion,
                                                      @NotNull ConfigurationSection configuration) {
        StoredInventory storedInventory = new StoredInventory();
        ArmorType[] armorTypeArray = ArmorType.values();
        for (ArmorType armorType : armorTypeArray) {
            String armorTypeName = armorType.name();
            String armorTypePath = ("armor." + armorTypeName);
            ItemStack item = loadItemStack(expansion, configuration, armorTypePath);
            storedInventory.setArmor(armorType, item);
        }

        ItemStack mainHand = loadItemStack(expansion, configuration, "main-hand");
        ItemStack offHand = loadItemStack(expansion, configuration, "off-hand");
        storedInventory.setMainHand(mainHand);
        storedInventory.setOffHand(offHand);

        for (int slot = 0; slot < 36; slot++) {
            String slotName = Integer.toString(slot);
            String slotPath = ("content." + slotName);
            ItemStack item = loadItemStack(expansion, configuration, slotPath);
            storedInventory.setItemStack(slot, item);
        }

        return storedInventory;
    }

    @SuppressWarnings("deprecation")
    private static void setHandLegacy(@NotNull StoredInventory stored, @NotNull PlayerInventory playerInventory) {
        ItemStack item = playerInventory.getItemInHand();
        stored.setMainHand(item);
        stored.setOffHand(null);
    }

    private static void setHandModern(@NotNull StoredInventory stored, @NotNull PlayerInventory playerInventory) {
        stored.setMainHand(playerInventory.getItemInMainHand());
        stored.setOffHand(playerInventory.getItemInOffHand());
    }

    private static @Nullable ItemStack loadItemStack(@NotNull CitizensExpansion expansion,
                                                     @NotNull ConfigurationSection section, @NotNull String path) {
        if (!section.isSet(path)) {
            return null;
        }

        if (!section.isString(path)) {
            return null;
        }

        String value = section.getString(path);
        if (value == null) {
            return null;
        }

        ICombatLogX plugin = expansion.getPlugin();
        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        ItemHandler itemHandler = multiVersionHandler.getItemHandler();
        return itemHandler.fromBase64String(value);
    }

    private static void saveItemStack(@NotNull CitizensExpansion expansion, @NotNull ConfigurationSection section,
                                      @NotNull String path, @Nullable ItemStack item) {
        if (ItemUtility.isAir(item)) {
            section.set(path, null);
            return;
        }

        ICombatLogX plugin = expansion.getPlugin();
        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        ItemHandler itemHandler = multiVersionHandler.getItemHandler();

        String base64 = itemHandler.toBase64String(item);
        section.set(path, base64);
    }

    public @Nullable ItemStack getMainHandItem() {
        return (this.mainHand == null ? null : this.mainHand.clone());
    }

    public @Nullable ItemStack getOffHandItem() {
        return (this.offHand == null ? null : this.offHand.clone());
    }

    public @Nullable ItemStack getArmor(@NotNull ArmorType type) {
        ItemStack item = this.armorMap.get(type);
        return (item == null ? null : item.clone());
    }

    public @Nullable ItemStack getItem(int slot) {
        ItemStack item = this.contentMap.get(slot);
        return (item == null ? null : item.clone());
    }

    public void save(@NotNull CitizensExpansion expansion, @NotNull ConfigurationSection configuration) {
        ItemStack mainHand = getMainHandItem();
        saveItemStack(expansion, configuration, "main-hand", mainHand);

        ItemStack offHand = getOffHandItem();
        saveItemStack(expansion, configuration, "off-hand", offHand);

        ArmorType[] armorTypeArray = ArmorType.values();
        for (ArmorType armorType : armorTypeArray) {
            ItemStack item = getArmor(armorType);
            String armorTypeName = armorType.name();
            String armorTypePath = ("armor." + armorTypeName);
            saveItemStack(expansion, configuration, armorTypePath, item);
        }

        for (int slot = 0; slot < 36; slot++) {
            ItemStack item = getItem(slot);
            String slotName = Integer.toString(slot);
            String slotPath = ("content." + slotName);
            saveItemStack(expansion, configuration, slotPath, item);
        }
    }

    private void setItemStack(int slot, @Nullable ItemStack item) {
        if (item == null) {
            this.contentMap.remove(slot);
        } else {
            this.contentMap.put(slot, item.clone());
        }
    }

    private void setArmor(@NotNull ArmorType type, @Nullable ItemStack item) {
        if (item == null) {
            this.armorMap.remove(type);
        } else {
            this.armorMap.put(type, item.clone());
        }
    }

    private void setMainHand(@Nullable ItemStack item) {
        this.mainHand = (item == null ? null : item.clone());
    }

    private void setOffHand(@Nullable ItemStack item) {
        this.offHand = (item == null ? null : item.clone());
    }
}
