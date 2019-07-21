package com.SirBlobman.expansion.citizens.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.SirBlobman.api.nms.NMS_Handler;
import com.SirBlobman.api.utility.ItemUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.citizens.config.ConfigCitizens;
import com.SirBlobman.expansion.citizens.config.ConfigData;
import com.SirBlobman.expansion.citizens.trait.TraitCombatLogX;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.event.NPCDespawnEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;
import net.citizensnpcs.api.trait.trait.Inventory;

public class ListenHandleNPCs implements Listener {
    public static boolean isValid(NPC npc) {
        if(npc == null) return false;
        return npc.hasTrait(TraitCombatLogX.class);
    }
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void beforeDamage(NPCDamageByEntityEvent e) {
        NPC npc = e.getNPC();
        if(!isValid(npc)) return;
        
        TraitCombatLogX trait = npc.getTrait(TraitCombatLogX.class);
        if(ConfigCitizens.getOption("citizens.npc.reset timer on damage", false)) trait.extendTimeUntilRemove();
    }
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onDeath(NPCDeathEvent e) {
        NPC npc = e.getNPC();
        if(!isValid(npc)) return;
        
        e.getDrops().clear();
    }
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void beforeDespawn(NPCDespawnEvent e) {
        DespawnReason reason = e.getReason();
        if(reason == DespawnReason.PENDING_RESPAWN) return;
        
        NPC npc = e.getNPC();
        if(!isValid(npc)) return;
        
        if(reason == DespawnReason.DEATH) {    
            if(npc.hasTrait(Equipment.class)) {
                Equipment npcEquip = npc.getTrait(Equipment.class);
                ItemStack[] contents = npcEquip.getEquipment().clone();
                contents[0] = null;
                for(EquipmentSlot slot : EquipmentSlot.values()) npcEquip.set(slot, new ItemStack(Material.AIR));
                
                Location location = npc.getEntity().getLocation();
                World world = location.getWorld();
                for(ItemStack item : contents) {
                    if(ItemUtil.isAir(item)) continue;
                    world.dropItem(location, item);
                }
            }    
            
            if(npc.hasTrait(Inventory.class)) {
                Inventory invTrait = npc.getTrait(Inventory.class);
                final ItemStack[] invContents = invTrait.getContents().clone();
                
                final ItemStack[] allAir = new ItemStack[100];
                Arrays.fill(allAir, new ItemStack(Material.AIR));
                invTrait.setContents(allAir);
                
                Location location = npc.getEntity().getLocation();
                World world = location.getWorld();
                for(int slot = 0; slot < (4*9) && slot < invContents.length; slot++) {
                    ItemStack item = invContents[slot];
                    if(ItemUtil.isAir(item)) continue;
                    
                    world.dropItem(location, item);
                }
            }
        }
        
        TraitCombatLogX trait = npc.getTrait(TraitCombatLogX.class);
        OfflinePlayer owner = trait.getOwner();
        if(owner == null) return;
        
        double health = 0.0D;
        Entity npcEntity = npc.getEntity();
        if(npcEntity instanceof LivingEntity) {
            LivingEntity npcLiving = (LivingEntity) npcEntity;
            health = npcLiving.getHealth();
        }
        ConfigData.force(owner, "last health", health);
        
        Location location = npcEntity.getLocation();
        ConfigData.force(owner, "last location", location);
        
        if(npc.hasTrait(Inventory.class) && ConfigCitizens.getOption("citizens.npc.store inventory", true)) {
            if(health > 0.0D) {
                Map<String, Object> inventoryData = getInventoryData(npc);
                ConfigData.force(owner, "inventory data", inventoryData);
            } else {
                ConfigData.force(owner, "inventory data", null);
            }
        }
        
        ConfigData.force(owner, "punish", true);
        SchedulerUtil.runLater(1L, npc::destroy);
    }
    
    public static Map<String, Object> getInventoryData(NPC npc) {
        if(npc == null) return Util.newMap();
        
        Map<String, Object> inventoryData = Util.newMap();
        
        if(npc.hasTrait(Inventory.class)) {
            Inventory npcInv = npc.getTrait(Inventory.class);
            ItemStack[] contents = npcInv.getContents();
            int inventorySize = contents.length;
            
            ItemStack[] itemArray = new ItemStack[4*9];
            Arrays.fill(itemArray, new ItemStack(Material.AIR));
            for(int slot = 0; slot < inventorySize && slot < (4*9); slot++) {
                ItemStack item = contents[slot];
                itemArray[slot] = item;
            }
            List<ItemStack> itemList = Util.newList(itemArray);
            inventoryData.put("items", itemList);
        }
        
        if(npc.hasTrait(Equipment.class)) {
            Equipment npcEquip = npc.getTrait(Equipment.class);
            ItemStack itemHelmet = npcEquip.get(EquipmentSlot.HELMET);
            ItemStack itemChestplate = npcEquip.get(EquipmentSlot.CHESTPLATE);
            ItemStack itemLeggings = npcEquip.get(EquipmentSlot.LEGGINGS);
            ItemStack itemBoots = npcEquip.get(EquipmentSlot.BOOTS);
            
            inventoryData.put("helmet", itemHelmet);
            inventoryData.put("chestplate", itemChestplate);
            inventoryData.put("leggings", itemLeggings);
            inventoryData.put("boots", itemBoots);
            
            if(NMS_Handler.getMinorVersion() > 8) {
                ItemStack itemMain = npcEquip.get(EquipmentSlot.HAND);
                ItemStack itemOff = npcEquip.get(EquipmentSlot.OFF_HAND);
                inventoryData.put("main hand", itemMain);
                inventoryData.put("off hand", itemOff);
            }
        }
        
        return inventoryData;
    }
}