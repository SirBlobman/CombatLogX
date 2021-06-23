package com.SirBlobman.combatlogx.expansion.notifier.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.nms.bossbar.BossBarHandler;
import com.github.sirblobman.api.utility.MessageUtility;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.SirBlobman.combatlogx.expansion.notifier.Notifier;

public class BossBarManager {
    private final Notifier expansion;
    private final List<UUID> disabledList;
    public BossBarManager(Notifier expansion) {
        this.expansion = expansion;
        this.disabledList = new ArrayList<>();
    }

    /**
     * @param player The player to toggle the boss bar for
     * @return true if the boss bar was toggled ON, false if the boss bar was toggled OFF
     */
    public boolean toggleBossBar(Player player) {
        if(player == null) return false;
        
        UUID uuid = player.getUniqueId();
        if(this.disabledList.contains(uuid)) {
            this.disabledList.remove(uuid);
            return true;
        }
        
        removeBossBar(player, true);
        this.disabledList.add(uuid);
        return false;
    }
    
    public boolean isDisabled(Player player) {
        if(player == null) return true;
        
        UUID uuid = player.getUniqueId();
        return this.disabledList.contains(uuid);
    }
    
    public void updateBossBar(Player player) {
        if(isDisabledGlobally()) return;
        if(isDisabled(player)) return;
    
        ICombatLogX plugin = this.expansion.getPlugin();
        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        BossBarHandler bossBarHandler = multiVersionHandler.getBossBarHandler();
        
        String message = getTimerMessage(player);
        double progress = getProgress(player);
        String color = getColor();
        String style = getStyle();
        bossBarHandler.updateBossBar(player, message, progress, color, style);
    }
    
    public void removeBossBar(Player player, boolean onDisable) {
        ICombatLogX plugin = this.expansion.getPlugin();
        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        BossBarHandler bossBarHandler = multiVersionHandler.getBossBarHandler();
        
        if(onDisable || isDisabledGlobally() || isDisabled(player)) {
            bossBarHandler.removeBossBar(player);
            return;
        }
    
        String message = getEndedMessage(player);
        String color = getColor();
        String style = getStyle();
        bossBarHandler.updateBossBar(player, message, 0.0D, color, style);
        
        Runnable task = () -> bossBarHandler.removeBossBar(player);
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskLater(plugin.getPlugin(), task, 20L);
    }
    
    private boolean isDisabledGlobally() {
        FileConfiguration config = this.expansion.getConfig("bossbar.yml");
        return !config.getBoolean("enabled");
    }
    
    private String getTimerMessage(Player player) {
        FileConfiguration config = this.expansion.getConfig("bossbar.yml");
        String timerMessageFormat = config.getString("message-timer");
        if(timerMessageFormat == null) return "";
        
        return MessageUtility.color(this.expansion.replacePlaceholders(player, timerMessageFormat));
    }
    
    private String getEndedMessage(Player player) {
        FileConfiguration config = this.expansion.getConfig("bossbar.yml");
        String endedMessageFormat = config.getString("message-ended");
        if(endedMessageFormat == null) return "";
        
        return MessageUtility.color(this.expansion.replacePlaceholders(player, endedMessageFormat));
    }
    
    private String getColor() {
        FileConfiguration config = this.expansion.getConfig("bossbar.yml");
        return config.getString("bar-color");
    }
    
    private String getStyle() {
        FileConfiguration config = this.expansion.getConfig("bossbar.yml");
        return config.getString("bar-style");
    }
    
    private double getProgress(Player player) {
        ICombatLogX plugin = this.expansion.getPlugin();
        FileConfiguration config = plugin.getConfig("config.yml");
        double timer = config.getInt("combat.timer");
    
        ICombatManager combatManager = plugin.getCombatManager();
        double secondsLeft = combatManager.getTimerSecondsLeft(player);
        if(secondsLeft < 0.0D) secondsLeft = 0.0D;
        
        return (secondsLeft / timer);
    }
}
