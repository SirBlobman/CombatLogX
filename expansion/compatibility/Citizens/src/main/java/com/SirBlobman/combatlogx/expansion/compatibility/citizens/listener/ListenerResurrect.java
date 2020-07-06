package com.SirBlobman.combatlogx.expansion.compatibility.citizens.listener;

import com.SirBlobman.combatlogx.expansion.compatibility.citizens.CompatibilityCitizens;
import com.SirBlobman.combatlogx.expansion.compatibility.citizens.manager.NPCManager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

public class ListenerResurrect implements Listener {
    private final CompatibilityCitizens expansion;
    public ListenerResurrect(CompatibilityCitizens expansion) {
        this.expansion = expansion;
    }
    
    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void beforeResurrect(EntityResurrectEvent e) {
        FileConfiguration config = this.expansion.getConfig("citizens-compatibility.yml");
        if(!config.getBoolean("npc-options.prevent-resurrect", true)) return;
        
        LivingEntity entity = e.getEntity();
        if(!entity.hasMetadata("NPC")) return;
        
        NPCRegistry npcRegistry = CitizensAPI.getNPCRegistry();
        NPC npc = npcRegistry.getNPC(entity);
        
        NPCManager npcManager = this.expansion.getNPCManager();
        if(npcManager.isInvalid(npc)) return;
        
        e.setCancelled(true);
    }
}