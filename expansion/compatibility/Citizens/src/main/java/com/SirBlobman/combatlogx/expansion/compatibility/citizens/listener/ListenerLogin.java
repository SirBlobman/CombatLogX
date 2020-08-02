package com.SirBlobman.combatlogx.expansion.compatibility.citizens.listener;

import java.util.UUID;
import java.util.logging.Logger;

import com.SirBlobman.combatlogx.expansion.compatibility.citizens.CompatibilityCitizens;
import com.SirBlobman.combatlogx.expansion.compatibility.citizens.manager.NPCManager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;

import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.NPC;

public class ListenerLogin implements Listener {
    private final CompatibilityCitizens expansion;
    public ListenerLogin(CompatibilityCitizens expansion) {
        this.expansion = expansion;
    }
    
    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void beforeLogin(AsyncPlayerPreLoginEvent e) {
        FileConfiguration config = this.expansion.getConfig("citizens-compatibility.yml");
        if(!config.getBoolean("prevent-login", false)) return;
        
        UUID uuid = e.getUniqueId();
        NPCManager npcManager = this.expansion.getNPCManager();
        NPC npc = npcManager.getNPC(uuid);
        if(npc == null) return;
        
        String message = this.expansion.getPlugin().getLanguageMessageColored("citizens-join-deny");
        e.setKickMessage(message);
        e.setLoginResult(Result.KICK_OTHER);
    }
    
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.setCanPickupItems(false);
        
        NPCManager npcManager = this.expansion.getNPCManager();
        NPC npc = npcManager.getNPC(player);
        if(npc != null) npc.despawn(DespawnReason.PLUGIN);
        
        Runnable task = () -> {
            punish(player);
            player.setCanPickupItems(true);
        };
        
        JavaPlugin plugin = this.expansion.getPlugin().getPlugin();
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskLater(plugin, task, 1L);
    }
    
    private void punish(Player player) {
        if(player == null) return;
        
        NPCManager npcManager = this.expansion.getNPCManager();
        YamlConfiguration data = npcManager.getData(player);
        if(!data.getBoolean("citizens-compatibility.punish-next-join")) return;
        
        Logger logger = this.expansion.getLogger();
        logger.info("Player Data Config: " + data.saveToString());
        
        npcManager.loadLocation(player);
        double health = npcManager.loadHealth(player);
        
        if(health > 0.0D) {
            npcManager.loadInventory(player);
            npcManager.loadTagStatus(player);
        }
        
        data.set("citizens-compatibility.punish-next-join", false);
        data.set("citizens-compatibility.last-location", null);
        data.set("citizens-compatibility.last-health", null);
        data.set("citizens-compatibility.last-inventory", null);
        data.set("citizens-compatibility.last-armor", null);
        npcManager.setData(player, data);
    }
}