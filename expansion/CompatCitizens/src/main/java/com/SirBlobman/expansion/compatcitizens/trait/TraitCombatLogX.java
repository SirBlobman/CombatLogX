package com.SirBlobman.expansion.compatcitizens.trait;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.compatcitizens.config.ConfigCitizens;
import com.SirBlobman.expansion.compatcitizens.config.ConfigData;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.NPCDamageEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitInfo;
import net.citizensnpcs.api.trait.trait.Inventory;

public class TraitCombatLogX extends Trait {
    public TraitCombatLogX() {super("combatlogx");}
    
    public static void onEnable() {
        TraitInfo traitInfoCLX = TraitInfo.create(TraitCombatLogX.class);
        CitizensAPI.getTraitFactory().registerTrait(traitInfoCLX);
    }
    
    private long ticksUntilRemove = 0;
    private UUID ownerUUID;
    
    public void setOwner(OfflinePlayer player) {
        if(player == null) return;
        
        this.ownerUUID = player.getUniqueId();
    }
    
    public OfflinePlayer getOwner() {
        return Bukkit.getOfflinePlayer(this.ownerUUID);
    }
    
    public void extendTimeUntilRemove() {
        Util.debug("[Citizens Compatibility] Extending time on NPC '" + getIdForNPC() + "'.");
        this.ticksUntilRemove = (20L * ConfigCitizens.getOption("citizens.npc.survival time", 30));
    }
    
    public String getIdForNPC() {
        return this.npc.getName() + ":" + this.npc.getId();
    }
    
    @Override
    public void run() {
        if(ticksUntilRemove <= 0) {
            this.npc.despawn(DespawnReason.PLUGIN);
            return;
        }
        
        this.ticksUntilRemove--;
    }
    
    @Override
    public void onAttach() {
        Util.debug("[Citizens Compatibility] NPC ID '" + getIdForNPC() + "' has been set to a CombatLogX NPC.");
        extendTimeUntilRemove();
    }
    
    @Override
    public void onDespawn() {
        OfflinePlayer owner = getOwner();
        if(owner == null) return;
        
        double health = 0.0D;
        Entity npcEntity = this.npc.getEntity();
        if(npcEntity instanceof LivingEntity) {
            LivingEntity npcLiving = (LivingEntity) npcEntity;
            health = npcLiving.getHealth();
        }
                
        ConfigData.force(owner, "last health", health);
        
        Location location = npcEntity.getLocation();
        ConfigData.force(owner, "last location", location);
        
        if(health > 0.0D && this.npc.hasTrait(Inventory.class) && ConfigCitizens.getOption("citizens.npc.store inventory", true)) {
            Inventory invTrait = this.npc.getTrait(Inventory.class);
            List<ItemStack> invContents = Util.newList(invTrait.getContents());
            ConfigData.force(owner, "last inventory", invContents);
        }
        
        ConfigData.force(owner, "punish", true);
        this.npc.destroy();
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onDamageNPC(NPCDamageEvent e) {
        NPC npc = e.getNPC();
        if(!npc.equals(this.npc)) return;
        
        if(ConfigCitizens.getOption("citizens.npc.reset timer on damage", false)) extendTimeUntilRemove();
    }
    
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onDeathNPC(NPCDeathEvent e) {
        NPC npc = e.getNPC();
        if(!npc.equals(this.npc)) return;
        
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