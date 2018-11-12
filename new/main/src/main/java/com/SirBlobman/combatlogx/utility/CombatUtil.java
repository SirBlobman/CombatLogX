package com.SirBlobman.combatlogx.utility;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.event.PlayerCombatTimerChangeEvent;
import com.SirBlobman.combatlogx.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.event.PlayerPunishEvent;
import com.SirBlobman.combatlogx.event.PlayerPunishEvent.PunishReason;
import com.SirBlobman.combatlogx.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.event.PlayerTagEvent.TagReason;
import com.SirBlobman.combatlogx.event.PlayerTagEvent.TagType;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent.UntagReason;

public class CombatUtil implements Runnable {
	@Override
	public void run() {
		Bukkit.getOnlinePlayers().stream().filter(CombatUtil::isInCombat).forEach(p -> {
			int timeLeft = getTimeLeft(p);
			if(timeLeft <= 0) untag(p, UntagReason.EXPIRE);

			PlayerCombatTimerChangeEvent event = new PlayerCombatTimerChangeEvent(p, timeLeft);
			PluginUtil.call(event);
		});
	}

	private static Map<UUID, Long> COMBAT = Util.newMap();
	private static Map<UUID, LivingEntity> ENEMIES = Util.newMap();
	
	/**
	 * @param p The player to check
	 * @return {@code true} if the player is in combat, false if they are not
	 */
	public static boolean isInCombat(Player p) {
		UUID uuid = p.getUniqueId();
		return COMBAT.containsKey(uuid);
	}

	/**
	 * @param p The player to check
	 * @return {@code true} if the player has a non-null enemy, false otherwise
	 */
	public static boolean hasEnemy(Player p) {
		UUID uuid = p.getUniqueId();
		if(ENEMIES.containsKey(uuid)) {
			LivingEntity enemy = getEnemy(p);
			return (enemy != null);
		} else return false;
	}
	
	/**
	 * @param p The player to check
	 * @return The enemy of {@code p} as a LivingEntity<br/>
	 * {@code null} if the player does not have an enemy
	 * @see #hasEnemy(Player)
	 */
	public static LivingEntity getEnemy(Player p) {
		UUID uuid = p.getUniqueId();
		return ENEMIES.getOrDefault(uuid, null);
	}

	/**
	 * @return A list of all the players in combat
	 */
	public static List<Player> getPlayersInCombat() {
		List<Player> list = Util.newList();
		Map<UUID, Long> copy = Util.newMap(COMBAT);
		for(UUID uuid : copy.keySet()) {
			Player p = Bukkit.getPlayer(uuid);
			if(p != null) list.add(p);
			else COMBAT.remove(uuid);
		} return list;
	}

	/**
	 * @return A list of entities linked to a player in combat
	 */
	private static List<LivingEntity> getLinkedEnemies() {
		List<LivingEntity> list = Util.newList();
		Map<UUID, LivingEntity> copy = Util.newMap(ENEMIES);
		for(Entry<UUID, LivingEntity> e : copy.entrySet()) {
			UUID uuid = e.getKey();
			LivingEntity enemy = e.getValue();

			Player p = Bukkit.getPlayer(uuid);
			if(p != null && isInCombat(p)) {
				if(enemy != null) list.add(enemy);
			} else ENEMIES.remove(uuid);
		} return list;
	}
	
	/**
	 * Get the {@link OfflinePlayer} of that is linked to this enemy
	 * @param enemy The entity to check
	 * @return {@link OfflinePlayer} from the linked UUID<br/>
	 * {@code null} if this entity is not linked
	 */
	public static OfflinePlayer getByEnemy(LivingEntity enemy) {
		List<LivingEntity> list = getLinkedEnemies();
		if(list.contains(enemy)) {
			Map<UUID, LivingEntity> copy = Util.newMap(ENEMIES);
			for(Entry<UUID, LivingEntity> e : copy.entrySet()) {
				LivingEntity check = e.getValue();
				if(enemy.equals(check)) {
					UUID uuid = e.getKey();
					return Bukkit.getOfflinePlayer(uuid);
				}
			} return null;
		} else return null;
	}

	/**
	 * Put a player into combat<br/>
	 * This will fire {@link PlayerPreTagEvent} and {@link PlayerTagEvent}
	 * @param player The player to tag
	 * @param enemy The enemy that will tag them (can be null)
	 * @param type The type of entity that {@code enemy} is
	 * @param reason The reason that this player will be tagged
	 * @return {@code true} if the player was tagged, {@code false} otherwise
	 */
	public static boolean tag(Player player, LivingEntity enemy, TagType type, TagReason reason) {
	    if(isInCombat(player)) {
            long current = System.currentTimeMillis();
            long timeToAdd = (ConfigOptions.OPTION_TIMER * 1000L);
            long combatEnds = (current + timeToAdd);
            
            UUID uuid = player.getUniqueId();
            COMBAT.put(uuid, combatEnds);
            if(enemy == null && !hasEnemy(player)) ENEMIES.put(uuid, null);
            else ENEMIES.put(uuid, enemy);
            
            return true;
	    } else {
	        PlayerPreTagEvent pre = new PlayerPreTagEvent(player, enemy, type, reason);
	        PluginUtil.call(pre);

	        if(!pre.isCancelled()) {
	            long current = System.currentTimeMillis();
	            long timeToAdd = (ConfigOptions.OPTION_TIMER * 1000L);
	            long combatEnds = (current + timeToAdd);

	            PlayerTagEvent event = new PlayerTagEvent(player, enemy, type, reason, combatEnds);
	            PluginUtil.call(event);
	            
	            if(!isInCombat(player)) {
	                String msg;
	                String enemyName = (enemy == null) ? "unknown entity" : ((enemy.getCustomName() == null) ? enemy.getName() : enemy.getCustomName());
	                String enemyType = (enemy == null) ? EntityType.UNKNOWN.name() : enemy.getType().name();
	                
	                if(type == TagType.MOB) {
	                    List<String> keys = Util.newList("{mob_name}", "{mob_type}");
	                    List<?> vals = Util.newList(enemyName, enemyType);
	                    if(reason == TagReason.ATTACKED) {
	                        String format = ConfigLang.getWithPrefix("messages.combat.tagged.attacked by mob");
	                        msg = Util.formatMessage(format, keys, vals);
	                    } else if(reason == TagReason.ATTACKER) {
	                        String format = ConfigLang.getWithPrefix("messages.combat.tagged.attacker of mob");
	                        msg = Util.formatMessage(format, keys, vals);
	                    } else msg = ConfigLang.getWithPrefix("messages.combat.tagged.unknown");
	                } else if(type == TagType.PLAYER) {
	                    List<String> keys = Util.newList("{name}");
	                    List<?> vals = Util.newList(enemyName);
	                    if(reason == TagReason.ATTACKED) {
	                        String format = ConfigLang.getWithPrefix("messages.combat.tagged.attacked by player");
	                        msg = Util.formatMessage(format, keys, vals);
	                    } else if(reason == TagReason.ATTACKER) {
	                        String format = ConfigLang.getWithPrefix("messages.combat.tagged.attacker of player");
	                        msg = Util.formatMessage(format, keys, vals);
	                    } else msg = ConfigLang.getWithPrefix("messages.combat.tagged.unknown");
	                } else msg = "";
	                
	                Util.sendMessage(player, msg);
	            }
	            
	            UUID uuid = player.getUniqueId();
	            COMBAT.put(uuid, combatEnds);

	            if(enemy == null && !hasEnemy(player)) ENEMIES.put(uuid, null);
	            else ENEMIES.put(uuid, enemy);
	            
	            return true;
	        } else return false;
	    }
	}

	/**
	 * Remove a player from combat<br/>
	 * This will fire {@link PlayerUntagEvent}
	 * @param p The player to untag
	 * @param reason The reason they are being untagged
	 */
	public static void untag(Player p, UntagReason reason) {
		if(isInCombat(p)) {
			UUID uuid = p.getUniqueId();
			COMBAT.remove(uuid);
			ENEMIES.remove(uuid);

			PlayerUntagEvent event = new PlayerUntagEvent(p, reason);
			PluginUtil.call(event);
		}
	}

	/**
	 * @param p The player to check
	 * @return The time (in seconds) left in combat
	 */
	public static int getTimeLeft(Player p) {
		if(isInCombat(p)) {
			UUID uuid = p.getUniqueId();
			long system = System.currentTimeMillis();
			long combatEnds = COMBAT.get(uuid);
			long millisLeft = (combatEnds - system);
			return (int) (millisLeft / 1000L);
		} else return -1;
	}

	/**
	 * Punish a player<br/>
	 * You should only punish them if they log out during combat<br/>
	 * This will fire {@link PlayerPunishEvent}
	 * @param player The player to punish
	 * @param reason The reason they are being punished
	 */
	public static void punish(Player player, PunishReason reason) {
		if(reason == PunishReason.DISCONNECTED && !ConfigOptions.PUNISH_ON_QUIT) return;
		if(reason == PunishReason.KICKED && !ConfigOptions.PUNISH_ON_KICK) return;
		if(reason == PunishReason.UNKNOWN && !ConfigOptions.PUNISH_ON_EXPIRE) return;
		
		PlayerPunishEvent event = new PlayerPunishEvent(player, reason);
		PluginUtil.call(event);
		if(!event.isCancelled()) {	
			if(ConfigOptions.PUNISH_KILL) player.setHealth(0.0D);
			if(ConfigOptions.PUNISH_SUDO) {
				List<String> commands = ConfigOptions.PUNISH_SUDO_COMMANDS;
				
				commands.forEach(command -> {
					if(command.startsWith("[CONSOLE]")) {
						String cmd = command.substring(9).replace("{player}", player.getName());
						Bukkit.dispatchCommand(Util.CONSOLE, cmd);
					} 
					
					else if(command.startsWith("[PLAYER]")) {
						String cmd = command.substring(8).replace("{player}", player.getName());
						player.performCommand(cmd);
					} 
					
					else if(command.startsWith("[OP]")) {
						String cmd = command.substring(4).replace("{player}", player.getName());
						
						if(player.isOp()) player.performCommand(cmd);
						else {
							player.setOp(true);
							player.performCommand(cmd);
							player.setOp(false);
						}
					}
				});
			}
		}
	}
}