package com.SirBlobman.expansion.compatcitizens.listener;

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

import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.compatcitizens.config.ConfigCitizens;
import com.SirBlobman.expansion.compatcitizens.config.ConfigData;
import com.SirBlobman.expansion.compatcitizens.trait.TraitCombatLogX;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.event.NPCDespawnEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Inventory;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;

public class ListenHandleNPCs implements Listener {
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onDamageNPC(NPCDamageByEntityEvent e) {
        NPC npc = e.getNPC();
        if(!npc.hasTrait(TraitCombatLogX.class)) return;
        
        TraitCombatLogX trait = npc.getTrait(TraitCombatLogX.class);
        if(ConfigCitizens.getOption("citizens.npc.reset timer on damage", false)) trait.extendTimeUntilRemove();
    }
    
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onDespawnNPC(NPCDespawnEvent e) {
        Util.debug("[Citizens Compatibility] NPCDespawnEvent triggered, checking if an NPC needs to be saved.");
        
        DespawnReason reason = e.getReason();
        if(reason == DespawnReason.PENDING_RESPAWN) {
            Util.debug("DespawnReason was Pending Respawn, ignoring...");
            return;
        }
        
        NPC npc = e.getNPC();
        if(!npc.hasTrait(TraitCombatLogX.class)) {
            Util.debug("The npc was not a combatlogx npc, ignoring...");
            return;
        }
        TraitCombatLogX trait = npc.getTrait(TraitCombatLogX.class);
        
        OfflinePlayer owner = trait.getOwner();
        if(owner == null) {
            Util.debug("The NPC didn't have an owner, ignoring...");
            return;
        }
        
        double health = 0.0D;
        Entity npcEntity = npc.getEntity();
        if(npcEntity instanceof LivingEntity) {
            LivingEntity npcLiving = (LivingEntity) npcEntity;
            health = npcLiving.getHealth();
        }
        Util.debug("NPC Health: " + health);
                
        ConfigData.force(owner, "last health", health);
        
        Location location = npcEntity.getLocation();
        Util.debug("NPC Location: " + location);
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
        Util.debug("Set NPC owner to be punished on next join");
        
        SchedulerUtil.runLater(5L, () -> npc.destroy());
    }
    
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onDeathNPC(NPCDeathEvent e) {
        NPC npc = e.getNPC();
        if(!npc.hasTrait(TraitCombatLogX.class)) return;
        
        if(npc.hasTrait(Inventory.class)) {
            Inventory invTrait = npc.getTrait(Inventory.class);
            final ItemStack[] invContents = invTrait.getContents().clone();
            
            final ItemStack[] allAir = new ItemStack[100];
            Arrays.fill(allAir, new ItemStack(Material.AIR));
            invTrait.setContents(allAir);
            
            Location location = npc.getEntity().getLocation();
            World world = location.getWorld();
            for(ItemStack item : invContents) {
                if(item == null) continue;
                Material type = item.getType();
                if(type == Material.AIR) continue;
                
                world.dropItem(location, item);
            }
        }
        
        if(npc.hasTrait(Equipment.class)) {
            Equipment npcEquip = npc.getTrait(Equipment.class);
            ItemStack[] contents = npcEquip.getEquipment().clone();
            for(EquipmentSlot slot : EquipmentSlot.values()) npcEquip.set(slot, new ItemStack(Material.AIR));
            
            Location location = npc.getEntity().getLocation();
            World world = location.getWorld();
            for(ItemStack item : contents) {
                if(item == null) continue;
                Material type = item.getType();
                if(type == Material.AIR) continue;
                
                world.dropItem(location, item);
            }
        }
    }
    
    public  Map<String, Object> getInventoryData(NPC npc) {
        if(npc == null) return Util.newMap();
        
        Map<String, Object> inventoryData = Util.newMap();
        
        if(npc.hasTrait(Inventory.class)) {
            Inventory npcInv = npc.getTrait(Inventory.class);
            ItemStack[] contents = npcInv.getContents();
            
            List<ItemStack> itemList = Util.newList(contents);
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
        }
        
        return inventoryData;
    }
}