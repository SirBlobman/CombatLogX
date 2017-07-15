package com.SirBlobman.combatlogx;

import com.SirBlobman.combatlogx.compat.CustomBoss;
import com.SirBlobman.combatlogx.compat.CustomScore;
import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.listener.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.listener.event.PlayerUntagEvent.UntagCause;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.google.common.collect.Maps;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Combat implements Runnable {
	private static final Server SERVER = Bukkit.getServer();
	private static final PluginManager PM = SERVER.getPluginManager();
	
	private static List<Player> hadFlightBefore = Util.newList();
	private static HashMap<Player, Long> inCombat = Maps.newHashMap();
	private static Map<Player, Damageable> enemies = Util.newMap();
	private static Map<Player, CustomScore> scores = Util.newMap();
	
	public static void add(Player p, Damageable enemy) {
		if(!CombatUtil.bypass(p)) {
			for(String s : Config.CHEAT_PREVENT_BLOCKED_POTIONS) {
				PotionEffectType pet = PotionEffectType.getByName(s);
				if(pet == null) {continue;}
				else {if(p.hasPotionEffect(pet)) p.removePotionEffect(pet);}
			}
			
			if(Config.OPTION_COMBAT_SUDO_ENABLE && !in(p)) {
				for(String s : Config.OPTION_COMBAT_SUDO_COMMANDS) {
					String cmd = s.replace("{player}", p.getName());
					p.performCommand(cmd);
				}
			}

			if(Config.CHEAT_PREVENT_DISABLE_FLIGHT) {
				p.setFlying(false);
				p.setAllowFlight(false);
				if(Config.CHEAT_PREVENT_ENABLE_FLIGHT) {
					hadFlightBefore.add(p);
				}
			}

			if(Config.CHEAT_PREVENT_CHANGE_GAMEMODE) {
				String mode = Config.CHEAT_PREVENT_CHANGE_GAMEMODE_MODE;
				try {
					GameMode gm = GameMode.valueOf(mode);
					p.setGameMode(gm);
				} catch(Throwable ex) {
					String error = "Invalid GameMode in 'combat.yml': " + mode;
					Util.print(error);
				}
			}
			
			long time1 = System.currentTimeMillis();
			long time2 = Config.OPTION_TIMER * 1000L;
			long time3 = time1 + time2;
			inCombat.put(p, time3);
			enemies.put(p, enemy);
		}
	}
	
	public static Damageable enemy(Player p) {
		if(enemies.containsKey(p)) {
			Damageable le = enemies.get(p);
			return le;
		} else return null;
	}
	
	public static void remove(Player p) {
		inCombat.remove(p);
		enemies.remove(p);
		
		if(hadFlightBefore.contains(p)) {
			hadFlightBefore.remove(p);
			p.setAllowFlight(true);
			p.setFlying(true);
		}
		
		if(scores.containsKey(p)) {
			CustomScore cs = scores.get(p);
			cs.close();
		}
		
		if(Config.OPTION_BOSS_BAR) {
			CustomBoss.remove(p);
		}
		
		String expire = Util.format(Config.MESSAGE_PREFIX + Config.MESSAGE_EXPIRE);
		p.sendMessage(expire);
	}
	
	public static int timeLeft(Player p) {
		long time1 = System.currentTimeMillis();
		long time2 = inCombat.containsKey(p) ? inCombat.get(p) : 0L;
		if(time2 <= 0L) return 0;
		int time3 = (int) ((time2 - time1) / 1000);
		return time3;
	}
	
	public static boolean in(Player p) {
		return inCombat.containsKey(p);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void run() {
		Map<Player, Long> c2 = (Map<Player, Long>) inCombat.clone();
		Set<Player> set = c2.keySet();
		for(Player p : set) {
			int time = timeLeft(p);
			if(time <= 0) {
				PlayerUntagEvent PUE = new PlayerUntagEvent(p, UntagCause.TIME);
				PM.callEvent(PUE);
			} else {
				if(Config.OPTION_SCORE_BOARD) {
					if(scores.containsKey(p)) {
						CustomScore cs = scores.get(p);
						cs.update();
						cs.changeEnemy(enemies.get(p));
						scores.put(p, cs);
					} else {
						CustomScore cs = new CustomScore(p, enemies.get(p));
						cs.update();
						scores.put(p, cs);
					}
				} 
				
				if(Config.OPTION_ACTION_BAR) {
					int left = timeLeft(p);
					String time_left = Integer.toString(left);
					String action = Util.formatMessage(Config.MESSAGE_ACTION_BAR, Util.newList("{time_left}"), Util.newList(time_left));
					Util.action(p, action);
				} 
				
				if(Config.OPTION_BOSS_BAR) {
					Util.boss(p);
				}
			}
		}
	}
}