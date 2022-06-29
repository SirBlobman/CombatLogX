package com.github.sirblobman.combatlogx.listener;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.utility.MessageUtility;
import com.github.sirblobman.combatlogx.CombatPlugin;
import com.github.sirblobman.combatlogx.api.listener.CombatListener;
import com.github.sirblobman.combatlogx.api.manager.IDeathManager;

public final class ListenerDeath extends CombatListener {

    public ListenerDeath(CombatPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent e) {
        ConfigurationManager configurationManager = getPluginConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("punish.yml");
        String killTime = configuration.getString("kill-time");
        if (killTime == null || !killTime.equalsIgnoreCase("join")) {
            return;
        }

        Player player = e.getPlayer();
        PlayerDataManager playerDataManager = getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);
        if (!playerData.getBoolean("kill-on-join", false)) {
            return;
        }

        playerData.set("kill-on-join", false);
        playerDataManager.save(player);

        IDeathManager deathManager = getCombatLogX().getDeathManager();
        deathManager.kill(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent e) {
        IDeathManager deathManager = getCombatLogX().getDeathManager();
        deathManager.stopTracking(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        JavaPlugin javaPlugin = getJavaPlugin();
        IDeathManager deathManager = getDeathManager();

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.scheduleSyncDelayedTask(javaPlugin, () -> {
            deathManager.stopTracking(player);
        }, 1L);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        IDeathManager deathManager = getCombatLogX().getDeathManager();
        if (!deathManager.wasPunishKilled(player)) {
            return;
        }

        String message = getRandomDeathMessage(player);
        if (message != null) {
            String coloredMessage = MessageUtility.color(message);
            e.setDeathMessage(coloredMessage);
        }
    }

    private String getRandomDeathMessage(Player player) {
        ConfigurationManager configurationManager = getPluginConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("punish.yml");

        List<String> customDeathMessageList = configuration.getStringList("custom-death-message-list");
        if (customDeathMessageList.isEmpty()) {
            return null;
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        int customDeathMessageListSize = customDeathMessageList.size();
        int customDeathMessageIndex = random.nextInt(customDeathMessageListSize);
        String customDeathMessage = customDeathMessageList.get(customDeathMessageIndex);

        String playerName = player.getName();
        return customDeathMessage.replace("{player}", playerName);
    }
}
