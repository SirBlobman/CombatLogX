package com.github.sirblobman.combatlogx.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.PluginManager;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.Replacer;
import com.github.sirblobman.api.nms.EntityHandler;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.event.PlayerPreTagEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerReTagEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.manager.ITimerManager;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;
import com.github.sirblobman.combatlogx.api.object.TimerType;
import com.github.sirblobman.combatlogx.api.object.UntagReason;
import com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper;

public final class CombatManager implements ICombatManager {
    private final ICombatLogX plugin;
    private final Map<UUID, Long> combatMap;
    private final Map<UUID, LivingEntity> enemyMap;
    
    public CombatManager(ICombatLogX plugin) {
        this.plugin = Validate.notNull(plugin, "plugin must not be null!");
        this.combatMap = new ConcurrentHashMap<>();
        this.enemyMap = new ConcurrentHashMap<>();
    }
    
    @Override
    public boolean tag(Player player, LivingEntity enemy, TagType tagType, TagReason tagReason) {
        int timerSeconds = getMaxTimerSeconds(player);
        long timerMillis = (timerSeconds * 1_000L);
        
        long systemMillis = System.currentTimeMillis();
        long endMillis = (systemMillis + timerMillis);
        return tag(player, enemy, tagType, tagReason, endMillis);
    }
    
    @Override
    public boolean tag(Player player, LivingEntity enemy, TagType tagType, TagReason tagReason,
                       long customEndMillis) {
        Validate.notNull(player, "player must not be null!");
        Validate.notNull(tagType, "tagType must not be null!");
        Validate.notNull(tagReason, "tagReason must not be null!");
        if(player.hasMetadata("NPC")) {
            return false;
        }
        
        if(failsPreTagEvent(player, enemy, tagType, tagReason)) {
            this.plugin.printDebug("The PlayerPreTagEvent was cancelled.");
            return false;
        }
        
        boolean alreadyInCombat = isInCombat(player);
        this.plugin.printDebug("Previous Combat Status: " + alreadyInCombat);
        PluginManager pluginManager = Bukkit.getPluginManager();
        
        if(alreadyInCombat) {
            PlayerReTagEvent event = new PlayerReTagEvent(player, enemy, tagType, tagReason, customEndMillis);
            pluginManager.callEvent(event);
            if(event.isCancelled()) {
                return false;
            }
            
            customEndMillis = event.getEndTime();
        } else {
            PlayerTagEvent event = new PlayerTagEvent(player, enemy, tagType, tagReason, customEndMillis);
            pluginManager.callEvent(event);
            customEndMillis = event.getEndTime();
            sendTagMessage(player, enemy, tagType, tagReason);
        }
        
        UUID uuid = player.getUniqueId();
        this.combatMap.put(uuid, customEndMillis);
        if(enemy != null) {
            this.enemyMap.put(uuid, enemy);
        }
        
        String playerName = player.getName();
        this.plugin.printDebug("Successfully put player '" + playerName + "' into combat.");
        return true;
    }
    
    @Override
    public void untag(Player player, UntagReason untagReason) {
        Validate.notNull(player, "player must not be null!");
        Validate.notNull(untagReason, "untagReason must not be null!");
        if(!isInCombat(player)) return;
        
        UUID uuid = player.getUniqueId();
        this.combatMap.remove(uuid);
        
        ITimerManager timerManager = this.plugin.getTimerManager();
        timerManager.remove(player);
        
        LivingEntity previousEnemy = this.enemyMap.remove(uuid);
        PlayerUntagEvent event = new PlayerUntagEvent(player, untagReason, previousEnemy);
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.callEvent(event);
    }
    
    @Override
    public boolean isInCombat(Player player) {
        Validate.notNull(player, "player must not be null!");
        UUID uuid = player.getUniqueId();
        return this.combatMap.containsKey(uuid);
    }
    
    @Override
    public List<Player> getPlayersInCombat() {
        List<Player> playerList = new ArrayList<>();
        Set<UUID> keySet = new HashSet<>(this.combatMap.keySet());
        for(UUID uuid : keySet) {
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) {
                this.combatMap.remove(uuid);
                continue;
            }
            playerList.add(player);
        }
        return playerList;
    }
    
    @Override
    public LivingEntity getEnemy(Player player) {
        Validate.notNull(player, "player must not be null!");
        UUID uuid = player.getUniqueId();
        return this.enemyMap.getOrDefault(uuid, null);
    }
    
    @Override
    public OfflinePlayer getByEnemy(LivingEntity enemy) {
        Validate.notNull(enemy, "enemy must not be null!");
        if(!this.enemyMap.containsValue(enemy)) return null;
        
        Set<Entry<UUID, LivingEntity>> entrySet = this.enemyMap.entrySet();
        for(Entry<UUID, LivingEntity> entry : entrySet) {
            LivingEntity value = entry.getValue();
            if(!enemy.equals(value)) continue;
            
            UUID uuid = entry.getKey();
            return Bukkit.getOfflinePlayer(uuid);
        }
        
        return null;
    }
    
    @Override
    public long getTimerLeftMillis(Player player) {
        Validate.notNull(player, "player must not be null!");
        if(!isInCombat(player)) return -1L;
        
        UUID uuid = player.getUniqueId();
        long endMillis = this.combatMap.get(uuid);
        long systemMillis = System.currentTimeMillis();
        return (endMillis - systemMillis);
    }
    
    @Override
    public int getTimerLeftSeconds(Player player) {
        double millisLeft = getTimerLeftMillis(player);
        double secondsLeft = (millisLeft / 1_000.0D);
        return (int) Math.ceil(secondsLeft);
    }
    
    @Override
    public int getMaxTimerSeconds(Player player) {
        ConfigurationManager configurationManager = this.plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        String timerTypeString = configuration.getString("timer.type");
        
        TimerType timerType = TimerType.parse(timerTypeString);
        return (timerType == TimerType.PERMISSION ? getPermissionTimerSeconds(player) : getGlobalTimerSeconds());
    }
    
    @Override
    public String replaceVariables(Player player, LivingEntity enemy, String string) {
        String playerName = player.getName();
        String enemyName = getEntityName(player, enemy);
        
        String timeLeft = PlaceholderHelper.getTimeLeft(this.plugin, player);
        String timeLeftDecimal = PlaceholderHelper.getTimeLeftDecimal(this.plugin, player);
        String inCombat = PlaceholderHelper.getInCombat(this.plugin, player);
        String combatStatus = PlaceholderHelper.getStatus(this.plugin, player);
        String punishmentCount = PlaceholderHelper.getPunishmentCount(this.plugin, player);
        
        String enemyDisplayName = PlaceholderHelper.getEnemyDisplayName(this.plugin, player);
        String enemyHealth = PlaceholderHelper.getEnemyHealth(this.plugin, player);
        String enemyHearts = PlaceholderHelper.getEnemyHearts(this.plugin, player);
        String enemyHeartsCount = PlaceholderHelper.getEnemyHeartsCount(this.plugin, player);
        String enemyHealthRounded = PlaceholderHelper.getEnemyHealthRounded(this.plugin, player);
        String enemyWorldName = PlaceholderHelper.getEnemyWorld(this.plugin, player);
        String enemyX = PlaceholderHelper.getEnemyX(this.plugin, player);
        String enemyY = PlaceholderHelper.getEnemyY(this.plugin, player);
        String enemyZ = PlaceholderHelper.getEnemyZ(this.plugin, player);
        
        String newString = string.replace("{player}", playerName)
                .replace("{time_left}", timeLeft)
                .replace("{time_left_decimal}", timeLeftDecimal)
                .replace("{in_combat}", inCombat)
                .replace("{status}", combatStatus)
                .replace("{punishment_count}", punishmentCount)
                .replace("{enemy}", enemyName)
                .replace("{enemy_name}", enemyName)
                .replace("{enemy_display_name}", enemyDisplayName)
                .replace("{enemy_health}", enemyHealth)
                .replace("{enemy_hearts}", enemyHearts)
                .replace("{enemy_hearts_count}", enemyHeartsCount)
                .replace("{enemy_health_rounded}", enemyHealthRounded)
                .replace("{enemy_world}", enemyWorldName)
                .replace("{enemy_x}", enemyX)
                .replace("{enemy_y}", enemyY)
                .replace("{enemy_z}", enemyZ);
        
        return replaceMVdW(player, replacePAPI(player, newString));
    }
    
    private int getGlobalTimerSeconds() {
        ConfigurationManager configurationManager = this.plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        return configuration.getInt("timer.default-timer", 10);
    }
    
    private int getPermissionTimerSeconds(Player player) {
        Set<PermissionAttachmentInfo> permissionAttachmentInfoSet = player.getEffectivePermissions();
        Set<String> permissionSet = permissionAttachmentInfoSet.stream()
                .filter(PermissionAttachmentInfo::getValue)
                .map(PermissionAttachmentInfo::getPermission)
                .filter(permission -> permission.startsWith("combatlogx.timer."))
                .map(permission -> permission.substring("combatlogx.timer.".length()))
                .collect(Collectors.toSet());
        if(permissionSet.isEmpty()) return getGlobalTimerSeconds();
        
        int lowestTimer = Integer.MAX_VALUE;
        boolean foundValue = false;
        for(String permission : permissionSet) {
            try {
                int value = Integer.parseInt(permission);
                lowestTimer = Math.min(lowestTimer, value);
                foundValue = true;
            } catch(NumberFormatException ignored) {
            }
        }
        
        return (foundValue ? lowestTimer : getGlobalTimerSeconds());
    }
    
    private String getEntityName(Player player, LivingEntity entity) {
        if(entity == null) {
            LanguageManager languageManager = this.plugin.getLanguageManager();
            return languageManager.getMessage(player, "placeholder.unknown-enemy", null, true);
        }
        
        MultiVersionHandler multiVersionHandler = this.plugin.getMultiVersionHandler();
        EntityHandler entityHandler = multiVersionHandler.getEntityHandler();
        return entityHandler.getName(entity);
    }
    
    private String getEntityType(Player player, LivingEntity entity) {
        if(entity == null) {
            LanguageManager languageManager = this.plugin.getLanguageManager();
            return languageManager.getMessage(player, "placeholder.unknown-enemy", null, true);
        }
    
        EntityType entityType = entity.getType();
        return entityType.name();
    }
    
    private String replacePAPI(Player player, String string) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        if(!pluginManager.isPluginEnabled("PlaceholderAPI")) return string;
        return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, string);
    }
    
    private String replaceMVdW(Player player, String string) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        if(!pluginManager.isPluginEnabled("MVdWPlaceholderAPI")) return string;
        return be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(player, string);
    }
    
    private boolean failsPreTagEvent(Player player, LivingEntity enemy, TagType tagType, TagReason tagReason) {
        PlayerPreTagEvent event = new PlayerPreTagEvent(player, enemy, tagType, tagReason);
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.callEvent(event);
        return event.isCancelled();
    }
    
    private void sendTagMessage(Player player, LivingEntity enemy, TagType tagType, TagReason tagReason) {
        if(tagType == TagType.DAMAGE) {
            return;
        }
        
        String enemyName = getEntityName(player, enemy);
        String enemyType = getEntityType(player, enemy);
        String tagReasonString = tagReason.name().toLowerCase(Locale.US);
        String tagTypeString = tagType.name().toLowerCase(Locale.US);
        
        Replacer replacer = message -> message.replace("{enemy}", enemyName)
                .replace("{mob_type}", enemyType);
        String languagePath = ("tagged." + tagReasonString + "." + tagTypeString);
        this.plugin.sendMessageWithPrefix(player, languagePath, replacer, true);
    }
}
