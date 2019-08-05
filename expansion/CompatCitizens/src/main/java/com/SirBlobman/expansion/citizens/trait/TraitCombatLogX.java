package com.SirBlobman.expansion.citizens.trait;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.citizens.config.ConfigCitizens;
import com.SirBlobman.expansion.citizens.listener.ListenHandleNPCs;

import java.util.UUID;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitInfo;

public class TraitCombatLogX extends Trait {
    public TraitCombatLogX() {super("combatlogx");}
    
    public static TraitInfo TRAIT_INFO;
    public static void onEnable() {
        TRAIT_INFO = TraitInfo.create(TraitCombatLogX.class);
        CitizensAPI.getTraitFactory().registerTrait(TRAIT_INFO);
    }
    
    public static void onDisable() {
        NPCRegistry registry = CitizensAPI.getNPCRegistry();
        for(NPC npc : registry) {
            if(!ListenHandleNPCs.isValid(npc)) continue;
            npc.destroy();
        }
        
        CitizensAPI.getTraitFactory().deregisterTrait(TRAIT_INFO);
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
}