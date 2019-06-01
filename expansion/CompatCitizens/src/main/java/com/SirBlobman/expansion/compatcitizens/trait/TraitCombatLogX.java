package com.SirBlobman.expansion.compatcitizens.trait;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.compatcitizens.config.ConfigCitizens;
import com.SirBlobman.expansion.compatcitizens.config.ConfigData;

import java.util.List;
import java.util.UUID;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
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
    private UUID enemyUUID;
    
    public void setOwner(OfflinePlayer player) {
        if(player == null) return;
        
        this.ownerUUID = player.getUniqueId();
    }
    
    public OfflinePlayer getOwner() {
        return Bukkit.getOfflinePlayer(this.ownerUUID);
    }
    
    public void setEnemy(Player player) {
        if(player == null) return;
        
        this.enemyUUID = player.getUniqueId();
    }
    
    public Player getEnemy() {
        Player player = Bukkit.getPlayer(this.enemyUUID);
        return player;
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
            if(ConfigCitizens.getOption("citizens.npc.survive until enemy escape", false)) {
                Player enemy = getEnemy();
                if(enemy != null && !CombatUtil.isInCombat(enemy)) {
                    this.npc.despawn(DespawnReason.PLUGIN);
                }
            }
            
            if(ConfigCitizens.getOption("citizens.npc.survival time", 30) > 0) {
                this.npc.despawn(DespawnReason.PLUGIN);
            }
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
        
        if(this.npc.hasTrait(Inventory.class) && ConfigCitizens.getOption("citizens.npc.store inventory", true)) {
            if(health > 0.0D) {
                Inventory invTrait = this.npc.getTrait(Inventory.class);
                List<ItemStack> invContents = Util.newList(invTrait.getContents());
                ConfigData.force(owner, "last inventory", invContents);
            } else {
                List<ItemStack> empty = Util.newList();
                ConfigData.force(owner, "last inventory", empty);
            }
        }
        
        ConfigData.force(owner, "punish", true);
        this.npc.destroy();
    }
}