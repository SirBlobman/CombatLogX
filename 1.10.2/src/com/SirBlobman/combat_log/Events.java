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

public class Events implements Listener
{
	private static YamlConfiguration config = Config.load();
	private static String prefix = Config.option("messages.prefix");
	private static boolean pots = config.getBoolean("options.remove potions");
	private static boolean gm = config.getBoolean("options.change gamemode");
	private static boolean fly = config.getBoolean("options.prevent flight");
	private static boolean self = config.getBoolean("options.self combat");
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void cmd(PlayerCommandPreprocessEvent e)
	{
		Player p = e.getPlayer();
		String cmd = e.getMessage();
		List<String> blocked = config.getStringList("blocked commands");
		if(Combat.inCombat(p))
		{
			for(String c : blocked)
			{
				boolean b1 = (cmd.startsWith(c));
				boolean b2 = (cmd.startsWith("/" + c));
				if(b1 || b2)
				{
					e.setCancelled(true);
					String denied = prefix + Config.option("messages.blocked", "/" + c);
					p.sendMessage(denied);
					break;
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void quit(PlayerQuitEvent e)
	{
		Player p = e.getPlayer();
		boolean b1 = Combat.inCombat(p);
		boolean b2 = config.getBoolean("options.punish loggers");
		if(b1)
		{
			p.setHealth(0.0D);
			e.setQuitMessage(prefix + Config.option("messages.quit", p.getName()));
			if(b2)
			{
				List<String> punish = config.getStringList("punish commands");
				for(String c : punish)
				{
					String r = c.replace("{player}", p.getName());
					ConsoleCommandSender ccs = Bukkit.getConsoleSender();
					Bukkit.dispatchCommand(ccs, r);
				}
			}
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
		if(!self)
		{
			boolean b1 = same(damaged, damager);
			if(b1) return;
		}
		
		
		List<String> worlds = config.getStringList("disabled worlds");
		World w = damaged.getWorld();
		String world = w.getName();
		if(worlds.contains(world)) return;
		
		boolean b1 = (damaged instanceof Player);
		boolean b2 = (damager instanceof Player);
		boolean b3 = (damager instanceof Projectile);
		if(b1 && b2)
		{
			Player ded = (Player) damaged;
			Player der = (Player) damager;
			both(ded, der);
		}
		if(b1 && b3)
		{
			Projectile p = (Projectile) damager;
			ProjectileSource ps = p.getShooter();
			if(ps instanceof Player)
			{
				Player ded = (Player) damaged;
				Player der = (Player) ps;
				both(ded, der);
			}
		}
	}
	
	@EventHandler
	public void die(PlayerDeathEvent e)
	{
		Player p = e.getEntity();
		if(Combat.inCombat(p)) 
		{
			Combat.remove(p);
			String expire = prefix + Config.option("messages.expire");
			p.sendMessage(expire);
		}
	}
	
	private void both(Player ded, Player der)
	{
		boolean pvp1 = Combat.inCombat(ded);
		boolean pvp2 = Combat.inCombat(der);
		String target = prefix + Config.option("messages.target", der.getName());
		String attack = prefix + Config.option("messages.attack", ded.getName());
		if(!pvp1) ded.sendMessage(target);
		if(!pvp2) der.sendMessage(attack);
		Combat.add(ded, der);
		if(pots)
		{
			List<String> potions = config.getStringList("banned potions");
			for(String p : potions)
			{
				PotionEffectType pet = PotionEffectType.getByName(p);
				if(pet != null)
				{
					if(ded.hasPotionEffect(pet)) ded.removePotionEffect(pet);
					if(der.hasPotionEffect(pet)) der.removePotionEffect(pet);
				}
			}
		}
		if(gm)
		{
			boolean gm1 = (der.getGameMode() == GameMode.CREATIVE);
			if(gm1) der.setGameMode(GameMode.SURVIVAL);
		}
		if(fly)
		{
			ded.setAllowFlight(false); 
			ded.setFlying(false);
			der.setAllowFlight(false); 
			der.setFlying(false);
		}
	}
	
	private boolean same(Entity e1, Entity e2)
	{
		if(e1 instanceof Projectile)
		{
			Projectile p = (Projectile) e1;
			e1 = (Entity) p.getShooter();
		}
		if(e2 instanceof Projectile)
		{
			Projectile p = (Projectile) e2;
			e2 = (Entity) p.getShooter();
		}
		
		String name1 = e1.getName();
		String name2 = e2.getName();
		if(name1.equals(name2)) return true;
		return false;
	}
}