package com.SirBlobman.expansion.towny;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.expansion.towny.config.ConfigTowny;
import com.SirBlobman.expansion.towny.config.ConfigTowny.NoEntryMode;
import com.SirBlobman.expansion.towny.utility.TownyUtil;

public class CompatTowny implements CLXExpansion, Listener {
	public String getUnlocalizedName() {return "CompatTowny";}
	public String getName() {return "Towny Compatibility";}
	public String getVersion() {return "13.1";}
	
	public static File FOLDER;

	@Override
	public void enable() {
		if(PluginUtil.isEnabled("Towny")) {
			FOLDER = getDataFolder();
			ConfigTowny.load();
			PluginUtil.regEvents(this);
		} else {
			String error = "Towny is not installed. Automatically disabling...";
			print(error);
			Expansions.unloadExpansion(this);
		}
	}

	@Override
	public void disable() {
		
	}

	@Override
	public void onConfigReload() {
		if(PluginUtil.isEnabled("Towny")) ConfigTowny.load();
	}
	
	@EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true) 
	public void onEnterTown(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if(CombatUtil.isInCombat(player)) {
			Location to = e.getTo().clone();
			Location from = e.getFrom().clone();
			if(TownyUtil.isSafeZone(to)) preventEntry(e, player, from, to);
		}
	}

	private static List<UUID> MESSAGE_COOLDOWN = Util.newList();
	private void preventEntry(Cancellable e, Player player, Location from, Location to) {
		if(CombatUtil.hasEnemy(player)) {
			LivingEntity enemy = CombatUtil.getEnemy(player);

			NoEntryMode nem = ConfigTowny.getNoEntryMode();
			switch(nem) {
			case CANCEL:
				e.setCancelled(true);
				break;
			case TELEPORT:
				player.teleport(enemy);
				break;
			case KNOCKBACK:
				if(!TownyUtil.isSafeZone(from)) {
					Vector v = getVector(from, to);
					player.setVelocity(v);
				}
				break;
			case KILL:
				player.setHealth(0.0D);
				break;
			}

			UUID uuid = player.getUniqueId();
			if(!MESSAGE_COOLDOWN.contains(uuid)) {
				String msg = ConfigLang.getWithPrefix("messages.expansions.towny compatibility.no entry");
				player.sendMessage(msg);

				MESSAGE_COOLDOWN.add(uuid);
				SchedulerUtil.runLater(ConfigTowny.NO_ENTRY_MESSAGE_COOLDOWN * 20L, () -> MESSAGE_COOLDOWN.remove(uuid));
			}
		}
	}
	
	private Vector getVector(Location from, Location to) {
		Vector vfrom = from.toVector();
		Vector vto = to.toVector();
		Vector sub = vfrom.subtract(vto);
		Vector norm = sub.normalize();
		Vector mult = norm.multiply(ConfigTowny.NO_ENTRY_KNOCKBACK_STRENGTH);
		return mult.setY(0.0D);
	}
}