package com.SirBlobman.expansion.cheatprevention;

import java.io.File;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.event.PlayerCombatTimerChangeEvent;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent.UntagReason;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.cheatprevention.config.ConfigCheatPrevention;

public class CheatPrevention implements CLXExpansion, Listener {
	public String getUnlocalizedName() {return "CheatPrevention";}
	public String getName() {return "Cheat Prevention";}
	public String getVersion() {return "13.1";}

	public static File FOLDER;

	@Override
	public void enable() {
		FOLDER = getDataFolder();
		ConfigCheatPrevention.load();
		PluginUtil.regEvents(this);
	}

	@Override
	public void disable() {

	}

	@Override
	public void onConfigReload() {
		ConfigCheatPrevention.load();
	}

	@EventHandler
	public void onUntag(PlayerUntagEvent e) {
		Player p = e.getPlayer();
		UntagReason reason = e.getUntagReason();
		SchedulerUtil.runLater(5L, () -> {
			if(reason == UntagReason.EXPIRE) {
				String perm = ConfigCheatPrevention.FLIGHT_ENABLE_PERMISSION;
				if(perm != null && !perm.isEmpty()) {
					if(p.hasPermission(perm)) {
						p.setAllowFlight(true);
						p.setFlying(true);
					}
				}
			}
		});
	}

	@EventHandler
	public void onChangeTimer(PlayerCombatTimerChangeEvent e) {
		Player p = e.getPlayer();

		if(ConfigCheatPrevention.GAMEMODE_CHANGE_WHEN_TAGGED) {
			GameMode pgm = p.getGameMode();
			String smode = ConfigCheatPrevention.GAMEMODE_GAMEMODE;
			GameMode gm = GameMode.valueOf(smode);
			if(pgm != gm) {
				p.setGameMode(gm);
				List<String> keys = Util.newList("{gamemode}");
				List<?> vals = Util.newList(gm.name());
				String format = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.gamemode.change");
				String msg = Util.formatMessage(format, keys, vals);
				Util.sendMessage(p, msg);
			}
		}

		if(!ConfigCheatPrevention.FLIGHT_ALLOW_DURING_COMBAT) {
			if(p.isFlying() || p.getAllowFlight()) {
				p.setFlying(false);
				p.setAllowFlight(false);
				String msg = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.flight.disabled");
				Util.sendMessage(p, msg);
			}
		}

		if(!ConfigCheatPrevention.FLIGHT_ALLOW_ELYTRAS) {
			if(p.isGliding()) {
				p.setGliding(false);
				String msg = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.elytra.disabled");
				Util.sendMessage(p, msg);
			}
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onToggleFlight(PlayerToggleFlightEvent e) {
		Player p = e.getPlayer();
		if(!ConfigCheatPrevention.FLIGHT_ALLOW_DURING_COMBAT && CombatUtil.isInCombat(p)) {
			if(e.isFlying()) {
				e.setCancelled(true);
				p.setAllowFlight(false);
				p.setFlying(false);

				String error = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.flight.not allowed");
				Util.sendMessage(p, error);
			}
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onToggleElytra(EntityToggleGlideEvent e) {
		Entity en = e.getEntity();
		if(en instanceof Player) {
			Player p = (Player) en;
			if(!ConfigCheatPrevention.FLIGHT_ALLOW_ELYTRAS && CombatUtil.isInCombat(p)) {
				if(e.isGliding()) {
					e.setCancelled(true);
					p.setGliding(false);

					String error = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.elytra.not allowed");
					Util.sendMessage(p, error);
				}
			}
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onChangeGameMode(PlayerGameModeChangeEvent e) {
		Player p = e.getPlayer();
		if(ConfigCheatPrevention.GAMEMODE_CHANGE_WHEN_TAGGED && CombatUtil.isInCombat(p)) {
			GameMode pgm = e.getNewGameMode();
			String smode = ConfigCheatPrevention.GAMEMODE_GAMEMODE;
			GameMode gm = GameMode.valueOf(smode);
			if(pgm != gm) {
				e.setCancelled(true);
				p.setGameMode(gm);

				String error = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.gamemode.not allowed");
				Util.sendMessage(p, error);
			}
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onCommand(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		if(CombatUtil.isInCombat(p)) {
			String message = e.getMessage();
			String[] split = message.split(" ");
			if(split.length > 1) {
				String cmd = message.toLowerCase();
				List<String> list = ConfigCheatPrevention.BLOCKED_COMMANDS_LIST;
				for(String blocked : list) {
					if(ConfigCheatPrevention.BLOCKED_COMMANDS_IS_WHITELIST) {
						if(!blocked.startsWith(cmd)) {
							e.setCancelled(true);
							List<String> keys = Util.newList("{command}");
							List<?> vals = Util.newList("/" + cmd);
							String format = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.command.not allowed");
							String error = Util.formatMessage(format, keys, vals);
							Util.sendMessage(p, error);
						}
					} else {
						if(blocked.startsWith(cmd)) {
							e.setCancelled(true);
							List<String> keys = Util.newList("{command}");
							List<?> vals = Util.newList("/" + cmd);
							String format = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.command.not allowed");
							String error = Util.formatMessage(format, keys, vals);
							Util.sendMessage(p, error);
						}
					}
				}
			} else {
				String cmd = split[0].toLowerCase();
				List<String> blockedCommands = ConfigCheatPrevention.BLOCKED_COMMANDS_LIST;
				if(ConfigCheatPrevention.BLOCKED_COMMANDS_IS_WHITELIST) {
					if(!blockedCommands.contains(cmd)) {
						e.setCancelled(true);
						List<String> keys = Util.newList("{command}");
						List<?> vals = Util.newList("/" + cmd);
						String format = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.command.not allowed");
						String error = Util.formatMessage(format, keys, vals);
						Util.sendMessage(p, error);
					}
				} else {
					if(blockedCommands.contains(cmd)) {
						e.setCancelled(true);
						List<String> keys = Util.newList("{command}");
						List<?> vals = Util.newList("/" + cmd);
						String format = ConfigLang.getWithPrefix("messages.expansions.cheat prevention.command.not allowed");
						String error = Util.formatMessage(format, keys, vals);
						Util.sendMessage(p, error);
					}
				}
			}
		}
	}
}