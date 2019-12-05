package com.SirBlobman.combatlogx.expansion.compatibility.citizens.listener;

import java.util.UUID;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.expansion.compatibility.citizens.CompatibilityCitizens;
import com.SirBlobman.combatlogx.expansion.compatibility.citizens.utility.NPCManager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.NPC;

public class ListenerPlayerLogin implements Listener {
    private final CompatibilityCitizens expansion;
    private final ICombatLogX plugin;
    public ListenerPlayerLogin(CompatibilityCitizens expansion) {
        this.expansion = expansion;
        this.plugin = this.expansion.getPlugin();
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void beforeLogin(AsyncPlayerPreLoginEvent e) {
        FileConfiguration config = this.expansion.getConfig("citizens-compatibility.yml");
        if(!config.getBoolean("prevent-login")) return;

        UUID uuid = e.getUniqueId();
        NPC npc = NPCManager.getNPC(uuid);
        if(NPCManager.isInvalid(npc)) return;

        String message = this.plugin.getLanguageMessageColored("citizens-join-deny");
        e.setKickMessage(message);
        e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.setCanPickupItems(false);

        NPC npc = NPCManager.getNPC(player);
        if(!NPCManager.isInvalid(npc)) npc.despawn(DespawnReason.PLUGIN);

        Runnable task = () -> {
            punish(player);
            player.setCanPickupItems(true);
        };
        JavaPlugin plugin = this.plugin.getPlugin();
        Bukkit.getScheduler().runTaskLater(plugin, task, 1L);
    }

    private void punish(Player player) {
        if(player == null) return;

        YamlConfiguration dataFile = NPCManager.getData(player);
        if(!dataFile.getBoolean("citizens-compatibility.punish-next-join")) return;

        NPCManager.loadLocation(player);
        NPCManager.loadTagStatus(player);
        double health = NPCManager.loadHealth(player);
        if(health > 0.0D) NPCManager.loadInventory(player);

        dataFile.set("citizens-compatibility.punish-next-join", false);
        NPCManager.saveData(player, dataFile);
    }
}