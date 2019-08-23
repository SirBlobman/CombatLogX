package com.SirBlobman.expansion.notifier.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.SirBlobman.combatlogx.event.PlayerCombatTimerChangeEvent;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent;
import com.SirBlobman.expansion.notifier.config.ConfigNotifier;
import com.SirBlobman.expansion.notifier.utility.ActionBarUtil;
import com.SirBlobman.expansion.notifier.utility.BossBarUtil;
import com.SirBlobman.expansion.notifier.utility.MVDWUtil;
import com.SirBlobman.expansion.notifier.utility.ScoreboardUtil;

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
			
			ScoreboardUtil.updateScoreBoard(player);
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
		}
	}
}