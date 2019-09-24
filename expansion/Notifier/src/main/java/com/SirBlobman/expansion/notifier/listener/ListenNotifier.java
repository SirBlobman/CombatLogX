package com.SirBlobman.expansion.notifier.listener;

import com.SirBlobman.combatlogx.CombatLogX;
import com.SirBlobman.combatlogx.event.PlayerCombatTimerChangeEvent;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent;
import com.SirBlobman.expansion.notifier.config.ConfigNotifier;
import com.SirBlobman.expansion.notifier.hook.MVDWUtil;
import com.SirBlobman.expansion.notifier.hook.TitleManagerUtil;
import com.SirBlobman.expansion.notifier.utility.ActionBarUtil;
import com.SirBlobman.expansion.notifier.utility.BossBarUtil;
import com.SirBlobman.expansion.notifier.utility.ScoreboardUtil;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public class ListenNotifier implements Listener {
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onTimerChange(PlayerCombatTimerChangeEvent e) {
		Player player = e.getPlayer();
		
		int timeLeft = e.getSecondsLeft();
		if(timeLeft < 1) return;
		
		if(ConfigNotifier.BOSS_BAR_ENABLED) BossBarUtil.updateBossBar(player);
		if(ConfigNotifier.ACTION_BAR_ENABLED) ActionBarUtil.updateActionBar(player);
		if(ConfigNotifier.SCORE_BOARD_ENABLED) {
			if(ConfigNotifier.SCORE_BOARD_USE_FEATHERBOARD) {
				MVDWUtil.enableScoreboard(player);
				return;
			}

			if(ConfigNotifier.SCORE_BOARD_TITLE_MANAGER_DISABLE) {
				PluginManager manager = Bukkit.getPluginManager();
				if(manager.isPluginEnabled("TitleManager")) TitleManagerUtil.disableScoreboard(player);
			}
			
			ScoreboardUtil.updateScoreBoard(player);
		}

		if(ConfigNotifier.ANIMATED_NAMES_USE) {
			MVDWUtil.enableAnimatedNameTrigger(player);
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onUntag(PlayerUntagEvent e) {
		Player player = e.getPlayer();
		
		if(ConfigNotifier.BOSS_BAR_ENABLED) BossBarUtil.removeBossBar(player, false);
		if(ConfigNotifier.ACTION_BAR_ENABLED) ActionBarUtil.removeActionBar(player, false);
		if(ConfigNotifier.SCORE_BOARD_ENABLED) {
			if(ConfigNotifier.SCORE_BOARD_USE_FEATHERBOARD) {
				MVDWUtil.disableScoreboard(player);
				return;
			}

			ScoreboardUtil.removeScoreBoard(player);
			if(ConfigNotifier.SCORE_BOARD_TITLE_MANAGER_RESTORE) {
				PluginManager manager = Bukkit.getPluginManager();
				if(manager.isPluginEnabled("TitleManager")) {
					Runnable task = () -> TitleManagerUtil.restoreScoreboard(player);
					Bukkit.getScheduler().runTaskLater(CombatLogX.INSTANCE, task, 5L);
				}
			}
		}

		if(ConfigNotifier.ANIMATED_NAMES_USE) {
			MVDWUtil.disableAnimatedNameTrigger(player);
		}
	}
}