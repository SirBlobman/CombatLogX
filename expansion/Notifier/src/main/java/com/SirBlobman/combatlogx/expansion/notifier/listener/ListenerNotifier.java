package com.SirBlobman.combatlogx.expansion.notifier.listener;

import com.SirBlobman.combatlogx.api.event.PlayerCombatTimerChangeEvent;
import com.SirBlobman.combatlogx.api.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.api.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.expansion.notifier.Notifier;
import com.SirBlobman.combatlogx.expansion.notifier.hook.HookMVdWPlaceholderAPI;
import com.SirBlobman.combatlogx.expansion.notifier.hook.HookTitleManager;
import com.SirBlobman.combatlogx.expansion.notifier.manager.ActionBarManager;
import com.SirBlobman.combatlogx.expansion.notifier.manager.BossBarManager;
import com.SirBlobman.combatlogx.expansion.notifier.manager.ScoreBoardManager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class ListenerNotifier implements Listener {
    private final Notifier expansion;
    public ListenerNotifier(Notifier expansion) {
        this.expansion = expansion;
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onTimerChange(PlayerCombatTimerChangeEvent e) {
        ActionBarManager actionBarManager = this.expansion.getActionBarManager();
        BossBarManager bossBarManager = this.expansion.getBossBarManager();
        ScoreBoardManager scoreBoardManager = this.expansion.getScoreBoardManager();
        
        Player player = e.getPlayer();
        actionBarManager.updateActionBar(player);
        bossBarManager.updateBossBar(player);
        scoreBoardManager.updateScoreboard(player);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onTag(PlayerTagEvent e) {
        Player player = e.getPlayer();
        FileConfiguration config = this.expansion.getConfig("mvdw.yml");
        
        if(config.getBoolean("FeatherBoard.enabled")) {
            String trigger = config.getString("FeatherBoard.trigger");
            HookMVdWPlaceholderAPI.enableTrigger("FeatherBoard", trigger, player);
        }

        if(config.getBoolean("AnimatedNames.enabled")) {
            String trigger = config.getString("AnimatedNames.trigger");
            HookMVdWPlaceholderAPI.enableTrigger("AnimatedNames", trigger, player);
        }

        HookTitleManager.disableScoreboard(this.expansion, player);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onUntag(PlayerUntagEvent e) {
        ActionBarManager actionBarManager = this.expansion.getActionBarManager();
        BossBarManager bossBarManager = this.expansion.getBossBarManager();
        ScoreBoardManager scoreBoardManager = this.expansion.getScoreBoardManager();
        
        Player player = e.getPlayer();
        JavaPlugin plugin = this.expansion.getPlugin().getPlugin();
        FileConfiguration config = this.expansion.getConfig("mvdw.yml");
        
        Runnable task = () -> {
            actionBarManager.removeActionBar(player);
            bossBarManager.removeBossBar(player, false);
            scoreBoardManager.removeScoreboard(player);
            
            if(config.getBoolean("FeatherBoard.enabled")) {
                String trigger = config.getString("FeatherBoard.trigger");
                HookMVdWPlaceholderAPI.disableTrigger("FeatherBoard", trigger, player);
            }
    
            if(config.getBoolean("AnimatedNames.enabled")) {
                String trigger = config.getString("AnimatedNames.trigger");
                HookMVdWPlaceholderAPI.disableTrigger("AnimatedNames", trigger, player);
            }
            
            HookTitleManager.restoreScoreboard(this.expansion, player);
        };
        
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskLater(plugin, task, 1L);
    }
}