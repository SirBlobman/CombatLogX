package com.SirBlobman.combatlog.listener;

import com.SirBlobman.combatlog.Combat;
import com.SirBlobman.combatlog.config.Config;
import com.SirBlobman.combatlog.listener.event.PlayerCombatEvent;
import com.SirBlobman.combatlog.listener.event.PlayerCombatLogEvent;
import com.SirBlobman.combatlog.utility.CombatUtil;
import com.SirBlobman.combatlog.utility.LegacyUtil;
import com.SirBlobman.combatlog.utility.Util;

import org.bukkit.Bukkit;
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
		double damage = e.getDamage();
		if(damage > 0) {
			Entity damager = e.getDamager();
			Entity damaged = e.getEntity();
			
			List<String> worlds = Config.DISABLED_WORLDS;
			World w = damaged.getWorld();
			if(worlds.contains(w)) return;
			
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
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void pvp(PlayerCombatEvent e) {
		if(e.isCancelled()) return;
		Damageable r = e.getDamager();
		Damageable d = e.getDamaged();
		boolean attacker = e.isPlayerAttacker();
		if(attacker) {
			Player p = (Player) r;
			if(!CombatUtil.bypass(p)) {
				Damageable enemy = d;
				String ename = LegacyUtil.name(enemy);
				if(enemy instanceof Player) {
					String msg = Util.format(Config.MSG_PREFIX + Config.MSG_ATTACK, ename);
					p.sendMessage(msg);
				} else {
					String msg = Util.format(Config.MSG_PREFIX + Config.MSG_ATTACK_MOB, ename);
					p.sendMessage(msg);
				}
				Combat.add(p, enemy);
			}
		} else {
			Player p = (Player) d;
			if(!CombatUtil.bypass(p)) {
				Damageable enemy = r;
				String ename = LegacyUtil.name(enemy);
				if(enemy instanceof Player) {
					String msg = Util.format(Config.MSG_PREFIX + Config.MSG_TARGET, ename);
					p.sendMessage(msg);
				} else {
					String msg = Util.format(Config.MSG_PREFIX + Config.MSG_TARGET_MOB, ename);
					p.sendMessage(msg);
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
		if(Config.OPEN_INVENTORY) {
			HumanEntity he = e.getPlayer();
			if(he instanceof Player) {
				Player p = (Player) he;
				if(Combat.in(p)) {
					Inventory i = e.getInventory();
					InventoryType it = i.getType();
					if(it != InventoryType.PLAYER) {
						e.setCancelled(true);
						Util.msg(p, Config.MSG_INVENTORY);
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
			String[] command = msg.split(" ");
			String cm = command[0].toLowerCase();
			boolean mode = Config.BLOCKED_COMMANDS_MODE;
			List<String> list = Config.BLOCKED_COMMANDS;
			if(!mode) {
				for(String cmd : list) {
					if(cm.equals("/" + cmd)) {
						e.setCancelled(true);
						p.sendMessage(Util.color(Config.MSG_PREFIX + Util.format(Config.MSG_BLOCKED, cm)));
						break;
					}
				}
			} else {
				String t = cm.substring(1);
				if(list.contains(t)) e.setCancelled(false);
				else {
					e.setCancelled(true);
					p.sendMessage(Util.color(Config.MSG_PREFIX + Util.format(Config.MSG_BLOCKED, cm)));
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
		if(!Config.PUNISH_KICKED) {
			Player p = e.getPlayer();
			Combat.remove(p);
		}
	}
	
	@EventHandler
	public void quit(PlayerCombatLogEvent e) {
		Player p = e.getPlayer();
		if(Config.KILL_PLAYER) p.setHealth(0.0D);		
		if(Config.PUNISH_LOGGERS) {
			for(String s : Config.PUNISH_COMMANDS) {
				String cmd = s.replace("{player}", p.getName());
				ConsoleCommandSender ccs = Bukkit.getConsoleSender();
				Bukkit.dispatchCommand(ccs, cmd);
			}
		}
		
		if(Config.SUDO_LOGGERS) {
			for(String s : Config.SUDO_COMMANDS) {
				String cmd = s.replace("{player}", p.getName());
				p.performCommand(cmd);
			}
		}
		
		if(Config.QUIT_MESSAGE) {
			String msg = Util.format(Config.MSG_PREFIX + Config.MSG_QUIT, p.getName());
			Bukkit.broadcastMessage(msg);
		}
	}
}