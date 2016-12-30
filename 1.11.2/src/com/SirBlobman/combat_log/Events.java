package com.SirBlobman.combat_log;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import com.SirBlobman.combat_log.utility.Util;

public class Events implements Listener
{
	private static YamlConfiguration config = Config.load();
	private static String prefix = Util.prefix;
	private static List<String> blockedCommands = config.getStringList("blocked commands");
	private static List<String> punishCommands  = config.getStringList("punish commands");
	private static List<String> disabledWorlds  = config.getStringList("disabled worlds");
	private static List<String> bannedPotions   = config.getStringList("banned potions");
	
	private static boolean bPotions  = config.getBoolean("options.remove potions");
	private static boolean bGameMode = config.getBoolean("options.change gamemode");
	private static boolean bFlight   = config.getBoolean("options.prevent flight");
	private static boolean bSelf     = config.getBoolean("options.self combat");
	private static boolean bMobs     = config.getBoolean("options.mobs combat");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void cmd(PlayerCommandPreprocessEvent e)
	{
		Player p = e.getPlayer();
		String c = e.getMessage();
		boolean combat = Combat.inCombat(p);
		if(combat)
		{
			for(String cmd : blockedCommands)
			{
				boolean b1 = c.startsWith(cmd);
				boolean b2 = c.startsWith("/" + cmd);
				boolean b3 = (b1 || b2);
				if(b3)
				{
					e.setCancelled(true);
					String blocked = Config.option("messages.blocked", '/' + c);
					String error = prefix + blocked;
					p.sendMessage(error);
					break;
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void quit(PlayerQuitEvent e)
	{
		Player p = e.getPlayer();
		String name = p.getName();
		String display = p.getDisplayName();
		boolean b1 = Combat.inCombat(p);
		if(b1)
		{
			p.setHealth(0.0D);
			String quit = prefix + Config.option("messages.quit", display);
			e.setQuitMessage(quit);
			
			boolean b2 = config.getBoolean("options.punish loggers");
			if(b2)
			{
				for(String cmd : punishCommands)
				{
					cmd = cmd.replace("{player}", name);
					ConsoleCommandSender ccs = Bukkit.getConsoleSender();
					Bukkit.dispatchCommand(ccs, cmd);
				}
			}
		}
	}
	
	@EventHandler
	public void die(PlayerDeathEvent e)
	{
		Player p = e.getEntity();
		boolean combat = Combat.inCombat(p);
		if(combat)
		{
			Combat.remove(p);
			String expire = prefix + Config.option("messages.expire");
			p.sendMessage(expire);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void pvp(EntityDamageByEntityEvent e)
	{
		if(e.isCancelled()) return;
		double damage = e.getDamage();
		if(damage <= 0.0D) return;
		
		Entity damaged = e.getEntity();
		Entity damager = e.getDamager();
		if(!bSelf)
		{
			boolean same = same(damaged, damager);
			if(same) return;
		}
		
		World w = damaged.getWorld();
		String world = w.getName();
		if(disabledWorlds.contains(world)) return;
		
		boolean b1 = (damager instanceof Projectile);
		if(b1)
		{
			Projectile p = (Projectile) damager;
			ProjectileSource ps = p.getShooter();
			if(ps instanceof Entity)
			{
				Entity shooter = (Entity) ps;
				damager = shooter;
			}
		}

		boolean b2 = (damaged instanceof Player);
		boolean b3 = (damager instanceof Player);
		if(bMobs) {if(b2 || b3) combat(damaged, damager);}
		else
		{
			if(b2 && b3)
			{
				Player ded = (Player) damaged;
				Player der = (Player) damager;
				pCombat(ded, der);
			}
		}
	}
	
	private void combat(Entity damaged, Entity damager)
	{
		boolean b1 = (damaged instanceof Player);
		boolean b2 = (damager instanceof Player);
		if(b1 && b2)
		{
			Player ded = (Player) damaged;
			Player der = (Player) damager;
			pCombat(ded, der);
		}
		else
		{
			if(b1) one((Player) damaged);
			if(b2) one((Player) damager);
		}
	}
	
	private void one(Player p)
	{
		Combat.add(p);
		if(bPotions)
		{
			for(String pot : bannedPotions)
			{
				PotionEffectType pet = PotionEffectType.getByName(pot);
				if(pet != null)
				{
					boolean has = p.hasPotionEffect(pet);
					if(has) p.removePotionEffect(pet);
				}
			}
		}
		if(bGameMode)
		{
			boolean gmc = (p.getGameMode() == GameMode.CREATIVE);
			if(gmc) p.setGameMode(GameMode.SURVIVAL);
		}
		if(bFlight)
		{
			p.setAllowFlight(false);
			p.setFlying(false);
		}
	}
	
	private void pCombat(Player ded, Player der)
	{
		boolean pvp1 = Combat.inCombat(ded);
		boolean pvp2 = Combat.inCombat(der);
		String target = prefix + Config.option("messages.target", der.getName());
		String attack = prefix + Config.option("messages.attack", ded.getName());
		if(!pvp1) ded.sendMessage(target);
		if(!pvp2) der.sendMessage(attack);
		one(ded);
		one(der);
	}
	
	private boolean same(Entity e1, Entity e2)
	{
		boolean b1 = (e1 instanceof Projectile);
		boolean b2 = (e2 instanceof Projectile);
		if(b1)
		{
			Projectile p = (Projectile) e1;
			ProjectileSource shooter = p.getShooter();
			if(shooter instanceof Entity) e1 = (Entity) shooter;
		}
		if(b2)
		{
			Projectile p = (Projectile) e2;
			ProjectileSource shooter = p.getShooter();
			if(shooter instanceof Entity) e2 = (Entity) shooter;
		}
		
		String name1 = e1.getName();
		String name2 = e2.getName();
		return name1.equals(name2);
	}
}