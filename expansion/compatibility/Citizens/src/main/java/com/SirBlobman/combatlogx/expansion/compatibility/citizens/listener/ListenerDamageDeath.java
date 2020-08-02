package com.SirBlobman.combatlogx.expansion.compatibility.citizens.listener;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.SirBlobman.combatlogx.expansion.compatibility.citizens.CompatibilityCitizens;
import com.SirBlobman.combatlogx.expansion.compatibility.citizens.manager.NPCManager;
import com.SirBlobman.combatlogx.expansion.compatibility.citizens.trait.TraitCombatLogX;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.event.NPCDespawnEvent;
import net.citizensnpcs.api.npc.NPC;

public class ListenerDamageDeath implements Listener {
    private final CompatibilityCitizens expansion;
    public ListenerDamageDeath(CompatibilityCitizens expansion) {
        this.expansion = expansion;
    }
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onDeath(NPCDeathEvent e) {
        NPC npc = e.getNPC();
        NPCManager npcManager = this.expansion.getNPCManager();
        if(npcManager.isInvalid(npc)) return;
        
        e.getDrops().clear();
        e.setDroppedExp(0);
        checkForDeathMessage(e);
    }
    
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        if(player.hasMetadata("NPC")) return;
        
        NPCManager npcManager = this.expansion.getNPCManager();
        YamlConfiguration data = npcManager.getData(player);
        String deathMessage = data.getString("citizens-compatibility.last-death-message");
        if(deathMessage == null) return;
        
        e.setDeathMessage(null);
        player.sendMessage(deathMessage);
        
        data.set("citizens-compatibility.last-death-message", null);
        npcManager.setData(player, data);
    }
    
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onDamage(NPCDamageByEntityEvent e) {
        FileConfiguration config = this.expansion.getConfig("citizens-compatibility.yml");
        if(!config.getBoolean("npc-options.stay-on-damage", false)) return;
        
        NPC npc = e.getNPC();
        NPCManager npcManager = this.expansion.getNPCManager();
        if(npcManager.isInvalid(npc)) return;
        
        TraitCombatLogX traitCombatLogX = npc.getTrait(TraitCombatLogX.class);
        traitCombatLogX.extendLife();
    }
    
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onDespawn(NPCDespawnEvent e) {
        DespawnReason despawnReason = e.getReason();
        if(despawnReason == DespawnReason.PENDING_RESPAWN) return;
        
        NPC npc = e.getNPC();
        NPCManager npcManager = this.expansion.getNPCManager();
        if(npcManager.isInvalid(npc)) return;
        
        TraitCombatLogX traitCombatLogX = npc.getTrait(TraitCombatLogX.class);
        OfflinePlayer owner = traitCombatLogX.getOwner();
        if(owner == null) return;
        
        if(despawnReason == DespawnReason.DEATH) npcManager.dropInventory(npc);
        npcManager.saveHealth(npc);
        npcManager.saveLocation(npc);
        
        YamlConfiguration data = npcManager.getData(owner);
        data.set("citizens-compatibility.punish-next-join", true);
        npcManager.setData(owner, data);
        
        JavaPlugin plugin = this.expansion.getPlugin().getPlugin();
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskLater(plugin, (Runnable) npc::destroy, 1L);
    }
    
    private void checkForDeathMessage(NPCDeathEvent e) {
        NPC npc = e.getNPC();
        NPCManager npcManager = this.expansion.getNPCManager();
        if(npcManager.isInvalid(npc)) return;
        
        TraitCombatLogX traitCombatLogX = npc.getTrait(TraitCombatLogX.class);
        OfflinePlayer owner = traitCombatLogX.getOwner();
        if(owner == null) return;
        
        try {
            Class<? extends NPCDeathEvent> class_NPCDeathEvent = e.getClass();
            Field field_event = class_NPCDeathEvent.getDeclaredField("event");
            field_event.setAccessible(true);
            
            Object object_event = field_event.get(e);
            if(!(object_event instanceof PlayerDeathEvent)) return;
            
            PlayerDeathEvent playerDeathEvent = (PlayerDeathEvent) object_event;
            String deathMessage = playerDeathEvent.getDeathMessage();
            if(deathMessage == null) return;
            
            YamlConfiguration data = npcManager.getData(owner);
            data.set("citizens-compatibility.last-death-message", deathMessage);
            npcManager.setData(owner, data);
        } catch(ReflectiveOperationException ex) {
            Logger logger = this.expansion.getLogger();
            logger.log(Level.WARNING, "An error occurred while checking an NPCDeathEvent:", ex);
        }
    }
}