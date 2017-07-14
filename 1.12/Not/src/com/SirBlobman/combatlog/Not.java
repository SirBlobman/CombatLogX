package com.SirBlobman.combatlog;

import com.SirBlobman.combatlog.config.Config;
import com.SirBlobman.combatlog.utility.CombatUtil;
import com.SirBlobman.combatlog.utility.Util;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;

import java.io.File;

public class Not extends JavaPlugin implements Listener {
	public static final Server SERVER = Bukkit.getServer();
	public static final PluginManager PM = SERVER.getPluginManager();
	public static Not INSTANCE;
	public static File FOLDER;
	public static CombatLogX CL;
	
	@Override
	public void onEnable() {
		if(PM.isPluginEnabled("CombatLogX")) {
			CombatLogX cl = CombatLogX.instance;
			if(cl != null) {
				INSTANCE = this;
				FOLDER = getDataFolder();
				CL = cl;
				NConfig.load();
				PM.registerEvents(this, this);
				if(Config.OPTION_CHECK_UPDATES) NotUpdate.print();
			} else {
				String error = "This plugin requires CombatLogX! Get it here:";
				String site = "https://www.spigotmc.org/resources/combatlogx.31689/";
				System.out.println(error);
				System.out.println(site);
				PM.disablePlugin(this);
			}
		}
	}
	
	@Override
	public void onDisable() {}
	
	@EventHandler
	public void damage(EntityDamageEvent e) {
		Entity en = e.getEntity();
		if(en instanceof Player) {
			Player p = (Player) en;
			double d = e.getDamage();
			DamageCause dc = e.getCause();
			if(dc == DamageCause.DROWNING && NConfig.DROWNING) {
				String msg = Util.color(Config.MESSAGE_PREFIX + NConfig.MSG_DROWNING);
				if(!Combat.in(p)) p.sendMessage(msg);
				call(p, d);
			} else if(dc == DamageCause.BLOCK_EXPLOSION && NConfig.EXPLOSION) {
				String msg = Util.color(Config.MESSAGE_PREFIX + NConfig.MSG_EXPLOSION);
				if(!Combat.in(p)) p.sendMessage(msg);
				call(p, d);
			} else if(dc == DamageCause.LAVA && NConfig.LAVA) {
				String msg = Util.color(Config.MESSAGE_PREFIX + NConfig.MSG_LAVA);
				if(!Combat.in(p)) p.sendMessage(msg);
				call(p, d);
			} else if(dc == DamageCause.FALL && NConfig.FALL) {
				String msg = Util.color(Config.MESSAGE_PREFIX + NConfig.MSG_FALL);
				if(!Combat.in(p)) p.sendMessage(msg);
				call(p, d);
			}
			else if(dc == DamageCause.PROJECTILE && NConfig.PROJECTILE) {
				EntityDamageByEntityEvent ed = (EntityDamageByEntityEvent) e;
				Entity enr = ed.getDamager();
				if(enr instanceof Projectile) {
					Projectile pr = (Projectile) enr;
					ProjectileSource ps = pr.getShooter();
					if(ps instanceof Entity) return;
					else {
						String msg = Util.color(Config.MESSAGE_PREFIX + NConfig.MSG_PROJECTILE);
						if(!Combat.in(p)) p.sendMessage(msg);
						call(p, d);
					}
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void s(SpecialCombatEvent e) {
		if(e.isCancelled()) return;
		Player p = e.getPlayer();
		if(CombatUtil.bypass(p)) Combat.add(p, null);
	}
	
	private void call(Player p, double damage) {
		SpecialCombatEvent sce = new SpecialCombatEvent(p, damage);
		PM.callEvent(sce);
	}
}