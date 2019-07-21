package com.SirBlobman.expansion.citizens.listener;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;

import com.SirBlobman.expansion.citizens.config.ConfigCitizens;
import com.SirBlobman.expansion.citizens.trait.TraitCombatLogX;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class ListenTotemNPC implements Listener {
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onTotemUse(EntityResurrectEvent e) {
        if(!ConfigCitizens.getOption("citizens.npc.prevent totem usage", true)) return;
        
        LivingEntity entity = e.getEntity();
        if(!entity.hasMetadata("NPC")) return;
        
        NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
        if(npc == null) return;
        
        if(npc.hasTrait(TraitCombatLogX.class)) e.setCancelled(true);
    }
}
