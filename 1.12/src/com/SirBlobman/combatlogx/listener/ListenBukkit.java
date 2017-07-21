package com.SirBlobman.combatlogx.listener;

import com.SirBlobman.combatlogx.Combat;
import com.SirBlobman.combatlogx.config.Config;
import com.SirBlobman.combatlogx.listener.event.PlayerCombatEvent;
import com.SirBlobman.combatlogx.listener.event.PlayerCombatLogEvent;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.LegacyUtil;
import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.projectiles.ProjectileSource;

import java.util.List;

public class ListenBukkit implements Listener {
	private static final Server SERVER = Bukkit.getServer();
	private static final PluginManager PM = SERVER.getPluginManager();

	@EventHandler(priority=EventPriority.HIGHEST) 
	public void eve(EntityDamageByEntityEvent e) {
		if(e.isCancelled()) return;
		double damage = e.getDamage();
		Entity damager = e.getDamager();
		Entity damaged = e.getEntity();

		List<String> worlds = Config.OPTION_DISABLED_WORLDS;
		World w = damaged.getWorld();
		String name = w.getName();
		if(worlds.contains(name)) return;

		if(damager instanceof Projectile) {
			if(e.getCause() == DamageCause.FALL) return;
			Projectile p = (Projectile) damager;
			ProjectileSource ps = p.getShooter();
			if(ps instanceof Entity) {
				Entity en = (Entity) ps;
				damager = en;
			} else return;
		}

		if(damager instanceof Player) {
			Player p = (Player) damager;
			boolean can = CombatUtil.canAttack(p, damaged);
			if(can) {
				Damageable d = (Damageable) damaged;
				PlayerCombatEvent pce = new PlayerCombatEvent(p, d, damage, true);
				Util.callEvents(pce);
			}
		}

		if(damaged instanceof Player && damager instanceof Damageable) {
			Player p = (Player) damaged;
			Damageable d = (Damageable) damager;
			boolean can = CombatUtil.canEntityAttackPlayer(d, p);
			if(can) {
				PlayerCombatEvent pce = new PlayerCombatEvent(p, d, damage, false);
				Util.callEvents(pce);
			}
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void pvp(PlayerCombatEvent e) {
		if(e.isCancelled()) return;
		Damageable r = e.getDamager();
		Damageable d = e.getDamaged();
		boolean attacker = e.isPlayerAttacker();
		if(attacker) {
			Player p = (Player) r;
			String pname = LegacyUtil.name(p);
			if(CombatUtil.canBeTagged(p)) {
				Damageable enemy = d;
				String ename = LegacyUtil.name(enemy);
				List<String> list1 = Util.newList("{attacker}", "{target}");
				List<String> list2 = Util.newList(pname, ename);
				if(enemy instanceof Player) {
					String msg = Util.formatMessage(Config.MESSAGE_ATTACK, list1, list2);
					if(!Combat.in(p)) Util.sendMessage(p, msg);
				} else {
					String msg = Util.formatMessage(Config.MESSAGE_ATTACK_MOB, list1, list2);
					if(!Combat.in(p)) Util.sendMessage(p, msg);
				}
				Combat.add(p, enemy);
			}
		} else {
			Player p = (Player) d;
			String pname = LegacyUtil.name(p);
			if(CombatUtil.canBeTagged(p)) {
				Damageable enemy = r;
				String ename = LegacyUtil.name(enemy);
				List<String> list1 = Util.newList("{attacker}", "{target}");
				List<String> list2 = Util.newList(ename, pname);
				if(enemy instanceof Player) {
					String msg = Util.formatMessage(Config.MESSAGE_TARGET, list1, list2);
					if(!Combat.in(p)) Util.sendMessage(p, msg);
				} else {
					String msg = Util.formatMessage(Config.MESSAGE_TARGET_MOB, list1, list2);
					if(!Combat.in(p)) Util.sendMessage(p, msg);
				}
				Combat.add(p, enemy);
			}
		}
	}

	@EventHandler
	public void die(PlayerDeathEvent e) {
		Player p = e.getEntity();
		if(Combat.in(p)) Combat.remove(p);
	}

	@EventHandler
	public void inv(InventoryOpenEvent e) {
		if(Config.CHEAT_PREVENT_OPEN_INVENTORIES) {
			HumanEntity he = e.getPlayer();
			if(he instanceof Player) {
				Player p = (Player) he;
				if(Combat.in(p)) {
					Inventory i = e.getInventory();
					InventoryType it = i.getType();
					if(it != InventoryType.PLAYER) {
						e.setCancelled(true);
						String msg = Config.MESSAGE_OPEN_INVENTORY;
						Util.sendMessage(p, msg);
					}
				}
			}
		}
	}

	@EventHandler
	public void cmd(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		if(Combat.in(p)) {
			String msg = e.getMessage();
			String cm = msg.toLowerCase();
			boolean mode = Config.CHEAT_PREVENT_BLOCKED_COMMANDS_MODE;
			List<String> list = Config.CHEAT_PREVENT_BLOCKED_COMMANDS;
			if(!mode) {
				for(String cmd : list) {
					cmd = cmd.toLowerCase();
					if(cm.startsWith("/" + cmd)) {
						e.setCancelled(true);
						List<String> list1 = Util.newList("{command}"), list2 = Util.newList(msg);
						String msg1 = Util.formatMessage(Config.MESSAGE_BLOCKED_COMMAND, list1, list2);
						Util.sendMessage(p, msg1);
						break;
					}
				}
			} else {
				e.setCancelled(true);
				for(String cmd : list) {
					cmd = cmd.toLowerCase();
					if(cm.startsWith("/" + cmd)) {
						e.setCancelled(false);
						List<String> list1 = Util.newList("{command}"), list2 = Util.newList(msg);
						String msg1 = Util.formatMessage(Config.MESSAGE_BLOCKED_COMMAND, list1, list2);
						Util.sendMessage(p, msg1);
						break;
					}
				}
			}
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void quit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if(Combat.in(p)) {
			PlayerCombatLogEvent PCLE = new PlayerCombatLogEvent(p);
			PM.callEvent(PCLE);
			Combat.remove(p);
		}
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void kick(PlayerKickEvent e) {
		if(!Config.PUNISH_ON_KICK) {
			Player p = e.getPlayer();
			Combat.remove(p);
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void move(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(Combat.in(p)) {
			if(Config.CHEAT_PREVENT_NO_ENTRY) {
				Location to = e.getTo();
				if(!CombatUtil.canPvP(to)) {
					e.setCancelled(true);
					Location from = e.getFrom();
					p.teleport(from);
					String msg = Util.format(Config.MESSAGE_NO_ENTRY);
					Util.sendMessage(p, msg);
				}
			}
		}
	}

	@EventHandler
	public void quit(PlayerCombatLogEvent e) {
		Player p = e.getPlayer();
		if(Config.PUNISH_KILL_PLAYER) p.setHealth(0.0D);		
		if(Config.PUNISH_CONSOLE) {
			for(String s : Config.PUNISH_COMMANDS_CONSOLE) {
				String cmd = s.replace("{player}", p.getName());
				ConsoleCommandSender ccs = Bukkit.getConsoleSender();
				Bukkit.dispatchCommand(ccs, cmd);
			}
		}

		if(Config.PUNISH_SUDO_LOGGERS) {
			for(String s : Config.PUNISH_COMMANDS_LOGGERS) {
				String cmd = s.replace("{player}", p.getName());
				p.performCommand(cmd);
			}
		}

		if(Config.PUNISH_ON_QUIT_MESSAGE) {
			List<String> l1 = Util.newList("{player}"), l2 = Util.newList(p.getName());
			String msg = Util.formatMessage(Config.MESSAGE_QUIT, l1, l2);
			Util.broadcast(msg);
		}
	}
}