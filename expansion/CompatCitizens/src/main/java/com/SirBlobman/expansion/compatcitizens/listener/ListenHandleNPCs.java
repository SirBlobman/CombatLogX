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

import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.compatcitizens.config.ConfigCitizens;
import com.SirBlobman.expansion.compatcitizens.config.ConfigData;
import com.SirBlobman.expansion.compatcitizens.trait.TraitCombatLogX;

import java.util.Arrays;
import java.util.List;

import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.event.NPCDespawnEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Inventory;

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
        DespawnReason reason = e.getReason();
        if(reason == DespawnReason.PENDING_RESPAWN) return;
        
        NPC npc = e.getNPC();
        if(!npc.hasTrait(TraitCombatLogX.class)) return;
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
                Inventory invTrait = npc.getTrait(Inventory.class);
                List<ItemStack> invContents = Util.newList(invTrait.getContents());
                ConfigData.force(owner, "last inventory", invContents);
            } else {
                List<ItemStack> empty = Util.newList();
                ConfigData.force(owner, "last inventory", empty);
            }
        }
        
        ConfigData.force(owner, "punish", true);
        npc.destroy();
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
    }
}