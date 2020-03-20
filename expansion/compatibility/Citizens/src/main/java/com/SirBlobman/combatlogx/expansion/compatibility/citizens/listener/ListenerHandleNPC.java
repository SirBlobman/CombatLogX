package com.SirBlobman.combatlogx.expansion.compatibility.citizens.listener;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.SirBlobman.combatlogx.expansion.compatibility.citizens.CompatibilityCitizens;
import com.SirBlobman.combatlogx.expansion.compatibility.citizens.trait.TraitCombatNPC;
import com.SirBlobman.combatlogx.expansion.compatibility.citizens.utility.NPCManager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.event.NPCDespawnEvent;
import net.citizensnpcs.api.npc.NPC;

public class ListenerHandleNPC implements Listener {
    private final CompatibilityCitizens expansion;
    public ListenerHandleNPC(CompatibilityCitizens expansion) {
        this.expansion = expansion;
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onDeath(NPCDeathEvent e) {
        NPC npc = e.getNPC();
        if(NPCManager.isInvalid(npc)) return;
        
        checkForDeathMessage(e);
        e.getDrops().clear();
        e.setDroppedExp(0);
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void beforeDamage(NPCDamageByEntityEvent e) {
        FileConfiguration config = this.expansion.getConfig("citizens-compatibility.yml");
        if(!config.getBoolean("npc-options.stay-on-damage")) return;

        NPC npc = e.getNPC();
        if(NPCManager.isInvalid(npc)) return;

        TraitCombatNPC combatNPC = npc.getTrait(TraitCombatNPC.class);
        combatNPC.extendTimeUntilRemove();
    }

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void beforeDespawn(NPCDespawnEvent e) {
        DespawnReason reason = e.getReason();
        if(reason == DespawnReason.PENDING_RESPAWN) return;

        NPC npc = e.getNPC();
        if(NPCManager.isInvalid(npc)) return;

        TraitCombatNPC combatNPC = npc.getTrait(TraitCombatNPC.class);
        OfflinePlayer owner = combatNPC.getOwner();
        if(owner == null) return;

        if(reason == DespawnReason.DEATH) NPCManager.dropInventory(owner, npc);
        NPCManager.saveHealth(owner, npc);
        NPCManager.saveLocation(owner, npc);

        YamlConfiguration dataFile = NPCManager.getData(owner);
        dataFile.set("citizens-compatibility.punish-next-join", true);
        NPCManager.saveData(owner, dataFile);

        Runnable task = npc::destroy;
        JavaPlugin plugin = this.expansion.getPlugin().getPlugin();
        Bukkit.getScheduler().runTaskLater(plugin, task, 1L);
    }
    
    private void checkForDeathMessage(NPCDeathEvent e) {
        NPC npc = e.getNPC();
        if(NPCManager.isInvalid(npc)) return;
        
        TraitCombatNPC traitCombatNPC = npc.getTrait(TraitCombatNPC.class);
        OfflinePlayer owner = traitCombatNPC.getOwner();
    
        try {
            Class<?> class_NPCDeathEvent = e.getClass();
            Field field_event = class_NPCDeathEvent.getDeclaredField("event");
            field_event.setAccessible(true);
            
            Object event = field_event.get(e);
            if(!(event instanceof PlayerDeathEvent)) return;
            
            PlayerDeathEvent deathEvent = (PlayerDeathEvent) event;
            String deathMessage = deathEvent.getDeathMessage();
            if(deathMessage == null) return;
    
            YamlConfiguration dataFile = NPCManager.getData(owner);
            dataFile.set("citizens-compatibility.last-death-message", deathEvent);
            NPCManager.saveData(owner, dataFile);
        } catch(ReflectiveOperationException ex) {
            Logger logger = this.expansion.getLogger();
            logger.log(Level.WARNING, "An error occurred while checking an NPC Death Event:", ex);
        }
    }
}
