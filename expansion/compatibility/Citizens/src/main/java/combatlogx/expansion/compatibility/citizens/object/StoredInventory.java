package combatlogx.expansion.compatibility.citizens.object;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.github.sirblobman.api.item.ArmorType;
import com.github.sirblobman.api.item.ItemBuilder;
import com.github.sirblobman.api.nms.ItemHandler;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.api.xseries.XMaterial;
import com.github.sirblobman.combatlogx.api.ICombatLogX;

import combatlogx.expansion.compatibility.citizens.CitizensExpansion;
import org.jetbrains.annotations.Nullable;

public final class StoredInventory {
    private final Map<Integer, ItemStack> contentMap;
    private final Map<ArmorType, ItemStack> armorMap;
    private ItemStack mainHand;
    private ItemStack offHand;
    
    private StoredInventory() {
        this.contentMap = new HashMap<>();
        this.armorMap = new EnumMap<>(ArmorType.class);
        this.mainHand = new ItemBuilder(XMaterial.AIR).withAmount(1).build();
        this.offHand = new ItemBuilder(XMaterial.AIR).withAmount(1).build();
    }
    
    public static StoredInventory createFrom(PlayerInventory playerInventory) {
        Validate.notNull(playerInventory, "playerInventory must not be null!");
        
        StoredInventory storedInventory = new StoredInventory();
        storedInventory.setArmor(ArmorType.HELMET, playerInventory.getHelmet());
        storedInventory.setArmor(ArmorType.CHESTPLATE, playerInventory.getChestplate());
        storedInventory.setArmor(ArmorType.LEGGINGS, playerInventory.getLeggings());
        storedInventory.setArmor(ArmorType.BOOTS, playerInventory.getBoots());
        
        for(int slot = 0; slot < 36; slot++) {
            ItemStack item = playerInventory.getItem(slot);
            storedInventory.setItemStack(slot, item);
        }
        
        int minorVersion = VersionUtility.getMinorVersion();
        if(minorVersion < 9) {
            setHandLegacy(storedInventory, playerInventory);
        } else {
            setHandModern(storedInventory, playerInventory);
        }
        
        return storedInventory;
    }
    
    public static StoredInventory createFrom(CitizensExpansion expansion, ConfigurationSection configuration) {
        Validate.notNull(configuration, "configuration must not be null!");
        StoredInventory storedInventory = new StoredInventory();
        
        ArmorType[] armorTypeArray = ArmorType.values();
        for(ArmorType armorType : armorTypeArray) {
            String armorTypeName = armorType.name();
            String armorTypePath = ("armor." + armorTypeName);
            ItemStack item = loadItemStack(expansion, configuration, armorTypePath);
            storedInventory.setArmor(armorType, item);
        }
        
        ItemStack mainHand = loadItemStack(expansion, configuration, "main-hand");
        ItemStack offHand = loadItemStack(expansion, configuration, "off-hand");
        storedInventory.setMainHand(mainHand);
        storedInventory.setOffHand(offHand);
        
        for(int slot = 0; slot < 36; slot++) {
            String slotName = Integer.toString(slot);
            String slotPath = ("content." + slotName);
            ItemStack item = loadItemStack(expansion, configuration, slotPath);
            storedInventory.setItemStack(slot, item);
        }
        
        return storedInventory;
    }
    
    @SuppressWarnings("deprecation")
    private static void setHandLegacy(StoredInventory stored, PlayerInventory playerInventory) {
        ItemStack item = playerInventory.getItemInHand();
        stored.setMainHand(item);
        stored.setOffHand(null);
    }
    
    private static void setHandModern(StoredInventory stored, PlayerInventory playerInventory) {
        stored.setMainHand(playerInventory.getItemInMainHand());
        stored.setOffHand(playerInventory.getItemInOffHand());
    }
    
    @Nullable
    private static ItemStack loadItemStack(CitizensExpansion expansion, ConfigurationSection section, String path) {
        Validate.notNull(section, "section must not be null!");
        Validate.notEmpty(path, "path must not be empty!");
        
        if(!section.isSet(path)) {
            return null;
        }
        
        if(!section.isString(path)) {
            return null;
        }
        
        String value = section.getString(path);
        if(value == null) {
            return null;
        }
        
        ICombatLogX plugin = expansion.getPlugin();
        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        ItemHandler itemHandler = multiVersionHandler.getItemHandler();
        return itemHandler.fromBase64String(value);
    }
    
    private static void saveItemStack(CitizensExpansion expansion, ConfigurationSection section, String path,
                                      ItemStack item) {
        if(item == null) {
            section.set(path, null);
            return;
        }
        
        ICombatLogX plugin = expansion.getPlugin();
        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        ItemHandler itemHandler = multiVersionHandler.getItemHandler();
        
        String base64 = itemHandler.toBase64String(item);
        section.set(path, base64);
    }
    
    @Nullable
    public ItemStack getMainHandItem() {
        return (this.mainHand == null ? null : this.mainHand.clone());
    }
    
    @Nullable
    public ItemStack getOffHandItem() {
        return (this.offHand == null ? null : this.offHand.clone());
    }
    
    @Nullable
    public ItemStack getArmor(ArmorType type) {
        Validate.notNull(type, "type must not be null!");
        ItemStack item = this.armorMap.getOrDefault(type, null);
        return (item == null ? null : item.clone());
    }
    
    @Nullable
    public ItemStack getItem(int slot) {
        ItemStack item = this.contentMap.getOrDefault(slot, null);
        return (item == null ? null : item.clone());
    }
    
    public void save(CitizensExpansion expansion, ConfigurationSection configuration) {
        ItemStack mainHand = getMainHandItem();
        saveItemStack(expansion, configuration, "main-hand", mainHand);
        
        ItemStack offHand = getOffHandItem();
        saveItemStack(expansion, configuration, "off-hand", offHand);
        
        ArmorType[] armorTypeArray = ArmorType.values();
        for(ArmorType armorType : armorTypeArray) {
            ItemStack item = getArmor(armorType);
            String armorTypeName = armorType.name();
            String armorTypePath = ("armor." + armorTypeName);
            saveItemStack(expansion, configuration, armorTypePath, item);
        }
        
        for(int slot = 0; slot < 36; slot++) {
            ItemStack item = getItem(slot);
            String slotName = Integer.toString(slot);
            String slotPath = ("content." + slotName);
            saveItemStack(expansion, configuration, slotPath, item);
        }
    }
    
    private void setItemStack(int slot, ItemStack item) {
        if(item == null) {
            this.contentMap.remove(slot);
        } else {
            this.contentMap.put(slot, item.clone());
        }
    }
    
    private void setArmor(ArmorType type, ItemStack item) {
        Validate.notNull(type, "type must not be null!");
        if(item == null) {
            this.armorMap.remove(type);
        } else {
            this.armorMap.put(type, item.clone());
        }
    }
    
    private void setMainHand(ItemStack item) {
        this.mainHand = (item == null ? null : item.clone());
    }
    
    private void setOffHand(ItemStack item) {
        this.offHand = (item == null ? null : item.clone());
    }
}
