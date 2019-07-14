package com.SirBlobman.combatlogx.utility;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.SirBlobman.api.nms.NMS_Handler;
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

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

public class CombatUtil implements Runnable {
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
        if (!ENEMIES.containsKey(uuid)) return false;
        
        LivingEntity enemy = getEnemy(p);
        return (enemy != null);
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
        for (UUID uuid : copy.keySet()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) list.add(p);
            else COMBAT.remove(uuid);
        }
        
        return list;
    }
    
    /**
     * @return A list of entities linked to a player in combat
     */
    private static List<LivingEntity> getLinkedEnemies() {
        List<LivingEntity> list = Util.newList();
        
        Map<UUID, LivingEntity> copy = Util.newMap(ENEMIES);
        for (Entry<UUID, LivingEntity> e : copy.entrySet()) {
            UUID uuid = e.getKey();
            LivingEntity enemy = e.getValue();
            
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && isInCombat(p)) {
                if (enemy != null) list.add(enemy);
            } else ENEMIES.remove(uuid);
        }
        
        return list;
    }
    
    /**
     * Get the {@link OfflinePlayer} of that is linked to this enemy
     *
     * @param enemy The entity to check
     * @return {@link OfflinePlayer} from the linked UUID<br/>
     * {@code null} if this entity is not linked
     */
    public static OfflinePlayer getByEnemy(LivingEntity enemy) {
        List<LivingEntity> list = getLinkedEnemies();
        if(!list.contains(enemy)) return null;
        
        
        Map<UUID, LivingEntity> copy = Util.newMap(ENEMIES);
        for (Entry<UUID, LivingEntity> e : copy.entrySet()) {
            LivingEntity check = e.getValue();
            if (enemy.equals(check)) {
                UUID uuid = e.getKey();
                return Bukkit.getOfflinePlayer(uuid);
            }
        }
        
        return null;
    }
    
    /**
     * Put a player into combat<br/>
     * This will fire {@link PlayerPreTagEvent} and {@link PlayerTagEvent}
     *
     * @param player The player to tag
     * @param enemy  The enemy that will tag them (can be null)
     * @param type   The type of entity that {@code enemy} is
     * @param reason The reason that this player will be tagged
     * @return {@code true} if the player was tagged, {@code false} otherwise
     */
    public static boolean tag(Player player, LivingEntity enemy, TagType tagType, TagReason tagReason) {
        if(isInCombat(player)) {
            if(tagType == TagType.MOB && !ConfigOptions.COMBAT_MOBS) return false;
            
            long systemMillis = System.currentTimeMillis();
            long millisToAdd = (ConfigOptions.OPTION_TIMER * 1000L);
            long endMillis = (systemMillis + millisToAdd);
            
            UUID uuid = player.getUniqueId();
            COMBAT.put(uuid, endMillis);
            
            if(enemy == null) ENEMIES.putIfAbsent(uuid, null);
            else ENEMIES.put(uuid, enemy);
            
            return true;
        }
        
        PlayerPreTagEvent preTagEvent = new PlayerPreTagEvent(player, enemy, tagType, tagReason);
        PluginUtil.call(preTagEvent);
        if(preTagEvent.isCancelled()) return false;
        
        long systemMillis = System.currentTimeMillis();
        long millisToAdd = (ConfigOptions.OPTION_TIMER * 1000L);
        long endMillis = (systemMillis + millisToAdd);
        
        PlayerTagEvent tagEvent = new PlayerTagEvent(player, enemy, tagType, tagReason, endMillis);
        PluginUtil.call(tagEvent);
        endMillis = tagEvent.getEndTime();
        
        if(!isInCombat(player)) {
            String enemyType = (enemy == null ? EntityType.UNKNOWN.name() : enemy.getType().name());
            String enemyName = (enemy == null ? ConfigLang.get("messages.unknown entity name") : NMS_Handler.getMinorVersion() == 7 ? (enemy instanceof Player ? ((Player) enemy).getName() : enemyType) : enemy.getName());
            
            String message = "";
            if(tagType == TagType.MOB) {
                List<String> keys = Util.newList("{mob_name}", "{mob_type}");
                List<?> vals = Util.newList(enemyName, enemyType);
                if (tagReason == TagReason.ATTACKED) {
                    String format = ConfigLang.getWithPrefix("messages.combat.tagged.attacked by mob");
                    message = Util.formatMessage(format, keys, vals);
                } 
                else if (tagReason == TagReason.ATTACKER) {
                    String format = ConfigLang.getWithPrefix("messages.combat.tagged.attacker of mob");
                    message = Util.formatMessage(format, keys, vals);
                } 
                else message = ConfigLang.getWithPrefix("messages.combat.tagged.unknown");
            } 
            else if(tagType == TagType.PLAYER) {
                List<String> keys = Util.newList("{name}");
                List<?> vals = Util.newList(enemyName);
                if (tagReason == TagReason.ATTACKED) {
                    String format = ConfigLang.getWithPrefix("messages.combat.tagged.attacked by player");
                    message = Util.formatMessage(format, keys, vals);
                } 
                else if (tagReason == TagReason.ATTACKER) {
                    String format = ConfigLang.getWithPrefix("messages.combat.tagged.attacker of player");
                    message = Util.formatMessage(format, keys, vals);
                } 
                else message = ConfigLang.getWithPrefix("messages.combat.tagged.unknown");
            } 
            else message = "";
            
            Util.sendMessage(player, message);
        }
        
        UUID uuid = player.getUniqueId();
        COMBAT.put(uuid, endMillis);
        
        if(enemy == null) ENEMIES.putIfAbsent(uuid, null);
        else ENEMIES.put(uuid, enemy);
        
        return true;
    }
    
    /**
     * Remove a player from combat<br/>
     * This will fire {@link PlayerUntagEvent}
     *
     * @param player      The player to untag
     * @param reason The reason they are being untagged
     */
    public static void untag(Player player, UntagReason reason) {
        if(!isInCombat(player)) return;
        UUID uuid = player.getUniqueId();
        COMBAT.remove(uuid);
        
        LivingEntity enemy = ENEMIES.remove(uuid);
        
        PlayerUntagEvent event = new PlayerUntagEvent(player, reason, enemy);
        PluginUtil.call(event);
    }
    
    /**
     * @param p The player to check
     * @return The time (in seconds) left in combat
     */
    public static int getTimeLeft(Player player) {
        if(!isInCombat(player)) return -1;
        
        UUID uuid = player.getUniqueId();
        long systemMillis = System.currentTimeMillis();
        long combatEnd = COMBAT.get(uuid);
        long millisLeft = (combatEnd - systemMillis);
        return ((int) (millisLeft / 1000L));
    }
    
    /**
     * Punish a player<br/>
     * You should only punish them if they log out during combat<br/>
     * This will fire {@link PlayerPunishEvent}
     *
     * @param player The player to punish
     * @param reason The reason they are being punished
     * @param previousEnemy (can be null) The last enemy of {@code player}
     */
    public static void punish(Player player, PunishReason reason, LivingEntity previousEnemy) {
        if (reason == PunishReason.DISCONNECTED && !ConfigOptions.PUNISH_ON_QUIT) return;
        if (reason == PunishReason.KICKED && !ConfigOptions.PUNISH_ON_KICK) return;
        if (reason == PunishReason.UNKNOWN && !ConfigOptions.PUNISH_ON_EXPIRE) return;
        
        PlayerPunishEvent event = new PlayerPunishEvent(player, reason, previousEnemy);
        PluginUtil.call(event);
        if (!event.isCancelled()) forcePunish(player);
    }
    
    public static void forcePunish(Player player) {
        if (ConfigOptions.PUNISH_KILL) player.setHealth(0.0D);
        if (ConfigOptions.PUNISH_SUDO) {
            List<String> commandList = ConfigOptions.PUNISH_SUDO_COMMANDS;
            for(String command : commandList) {
                if(command.startsWith("[CONSOLE]")) {
                    String cmd = getSudoCommand(command.substring(9), player);
                    CommandSender console = Bukkit.getConsoleSender();
                    Bukkit.dispatchCommand(console, cmd);
                    continue;
                }
                
                if(command.startsWith("[PLAYER]")) {
                    String cmd = getSudoCommand(command.substring(8), player);
                    player.performCommand(cmd);
                    continue;
                }
                
                if(command.startsWith("[OP]")) {
                    String cmd = getSudoCommand(command.substring(4), player);
                    if(player.isOp()) {
                        player.performCommand(cmd);
                        continue;
                    }
                    
                    player.setOp(true);
                    player.performCommand(cmd);
                    player.setOp(false);
                    continue;
                }
            }
        }
    }
    
    public static String getSudoCommand(String command, Player player) {
        command = command.replace("{player}", player.getName());
        
        try {
            if(PluginUtil.isEnabled("PlaceholderAPI", "extended_clip")) {
                Class<?> class_PlaceholderAPI = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
                Method method_PlaceholderAPI_setPlaceholders = class_PlaceholderAPI.getMethod("setPlaceholders", OfflinePlayer.class, String.class);
                command = (String) method_PlaceholderAPI_setPlaceholders.invoke(null, player, command);
            }
            
            if(PluginUtil.isEnabled("MVdWPlaceholderAPI")) {
                Class<?> class_PlaceholderAPI = Class.forName("be.maximvdw.placeholderapi.PlaceholderAPI");
                Method method_PlaceholderAPI_replacePlaceholders = class_PlaceholderAPI.getMethod("replacePlaceholders", OfflinePlayer.class, String.class);
                command = (String) method_PlaceholderAPI_replacePlaceholders.invoke(null, player, command);
            }
        } catch(Exception ex) {
            Util.debug("An error occurred while trying to get placeholders through reflection.");
            if(ConfigOptions.OPTION_DEBUG) ex.printStackTrace();
        }
        
        return command;
    }
    
    @Override
    public void run() {
        List<Player> combatPlayerList = Bukkit.getOnlinePlayers().stream().filter(CombatUtil::isInCombat).collect(Collectors.toList());
        for(Player player : combatPlayerList) {
            int timeLeft = getTimeLeft(player);
            if(timeLeft <= 0) untag(player, UntagReason.EXPIRE);
            
            PlayerCombatTimerChangeEvent event = new PlayerCombatTimerChangeEvent(player, timeLeft);
            PluginUtil.call(event);
        }
    }
}