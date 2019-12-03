package com.SirBlobman.combatlogx.expansion.compatibility.citizens.listener;

import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.expansion.compatibility.citizens.CitizensCompatibility;
import com.SirBlobman.combatlogx.expansion.compatibility.citizens.utility.NPCManager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class ListenerCombat implements Listener {
    private final CitizensCompatibility expansion;
    public ListenerCombat(CitizensCompatibility expansion) {
        this.expansion = expansion;
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void beforeTag(PlayerPreTagEvent e) {
        FileConfiguration config = this.expansion.getConfig("citizens-compatibility.yml");
        if(config.getBoolean("npc-tagging")) return;

        LivingEntity enemy = e.getEnemy();
        if(enemy == null || !enemy.hasMetadata("NPC")) return;

        e.setCancelled(true);
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void beforeResurrect(EntityResurrectEvent e) {
        FileConfiguration config = this.expansion.getConfig("citizens-compatibility.yml");
        if(!config.getBoolean("npc-options.prevent-resurrect")) return;

        LivingEntity entity = e.getEntity();
        if(!entity.hasMetadata("NPC")) return;

        NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
        if(NPCManager.isInvalid(npc)) return;

        e.setCancelled(true);
    }
}