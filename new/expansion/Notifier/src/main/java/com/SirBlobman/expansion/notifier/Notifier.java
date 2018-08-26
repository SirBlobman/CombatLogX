package com.SirBlobman.expansion.notifier;

import java.io.File;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.event.PlayerCombatTimerChangeEvent;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.notifier.config.ConfigNotifier;
import com.SirBlobman.expansion.notifier.utility.ActionBarUtil;
import com.SirBlobman.expansion.notifier.utility.BossBarUtil;
import com.SirBlobman.expansion.notifier.utility.ScoreboardUtil;

import be.maximvdw.featherboard.api.FeatherBoardAPI;

public class Notifier implements CLXExpansion, Listener {
	public String getUnlocalizedName() {return "Notifier";}
	public String getVersion() {return "13.1";}
	
	public static File FOLDER;
	
	@Override
	public void enable() {
		FOLDER = getDataFolder();
		ConfigNotifier.load();
		PluginUtil.regEvents(this);
	}
	
	@Override
	public void disable() {
		Bukkit.getOnlinePlayers().forEach(p -> {
			BossBarUtil.removeBossBar(p, true); 
			ActionBarUtil.removeActionBar(p);
		});
	}
	
	@Override
	public void onConfigReload() {
		ConfigNotifier.load();
	}
	
	@EventHandler
	public void onTimerChange(PlayerCombatTimerChangeEvent e) {
		Player p = e.getPlayer();
		
		if(ConfigNotifier.BOSS_BAR_ENABLED) BossBarUtil.updateBossBar(p);
		if(ConfigNotifier.ACTION_BAR_ENABLED) ActionBarUtil.updateActionBar(p);
		if(ConfigNotifier.SCORE_BOARD_ENABLED) {
			if(ConfigNotifier.SCORE_BOARD_USE_FEATHERBOARD) {
				try {
					File folder1 = new File(".").getAbsoluteFile().getParentFile();
					File folder2 = new File(folder1, "plugins");
					File folder3 = new File(folder2, "FeatherBoard");
					File folder4 = new File(folder3, "scoreboards");
					File file = new File(folder4, "combatlogx.yml");
					if(!file.exists()) {
						folder4.mkdirs();
						Class<Config> clazz = Config.class;
						Method method = clazz.getDeclaredMethod("copyFromJar", String.class, File.class);
						method.setAccessible(true);
						method.invoke(null, "FeatherBoard/scoreboards/combatlogx.yml", folder2);
						method.setAccessible(false);
						Bukkit.dispatchCommand(Util.CONSOLE, "fb reload");
					}
				} catch(Throwable ex) {
					String error = "Failed to create default FeatherBoard scoreboard. You will have to create your own.";
					print(error);
					ex.printStackTrace();
				}
				
				FeatherBoardAPI.showScoreboard(p, "combatlogx");
			} else ScoreboardUtil.updateScoreBoard(p);
		}
	}
	
	@EventHandler
	public void onUntag(PlayerUntagEvent e) {
		Player p = e.getPlayer();
		
		if(ConfigNotifier.BOSS_BAR_ENABLED) BossBarUtil.removeBossBar(p, false);
		if(ConfigNotifier.ACTION_BAR_ENABLED) ActionBarUtil.removeActionBar(p);
		if(ConfigNotifier.SCORE_BOARD_ENABLED) {
			if(ConfigNotifier.SCORE_BOARD_USE_FEATHERBOARD) {
				SchedulerUtil.runLater(20L, () -> {
					FeatherBoardAPI.removeScoreboardOverride(p, "combatlogx"); 
					FeatherBoardAPI.resetDefaultScoreboard(p);
				});
			} else ScoreboardUtil.removeScoreBoard(p);
		}
	}
}