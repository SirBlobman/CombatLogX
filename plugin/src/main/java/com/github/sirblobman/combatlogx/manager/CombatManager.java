package com.github.sirblobman.combatlogx.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.Replacer;
import com.github.sirblobman.api.nms.EntityHandler;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.utility.MessageUtility;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.event.PlayerEnemyRemoveEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerPreTagEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerReTagEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.manager.ITimerManager;
import com.github.sirblobman.combatlogx.api.object.CombatTag;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;
import com.github.sirblobman.combatlogx.api.object.TimerType;
import com.github.sirblobman.combatlogx.api.object.UntagReason;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CombatManager extends Manager implements ICombatManager {
    private final Map<UUID, TagInformation> combatMap;

    private Permission bypassPermission;

    public CombatManager(ICombatLogX plugin) {
        super(plugin);
        this.combatMap = new ConcurrentHashMap<>();
        this.bypassPermission = null;
    }

    @Override
    public boolean tag(Player player, Entity enemy, TagType tagType, TagReason tagReason) {
        int timerSeconds = getMaxTimerSeconds(player);
        long timerMillis = (timerSeconds * 1_000L);

        long systemMillis = System.currentTimeMillis();
        long endMillis = (systemMillis + timerMillis);
        return tag(player, enemy, tagType, tagReason, endMillis);
    }

    @Override
    public boolean tag(Player player, Entity enemy, TagType tagType, TagReason tagReason, long customEndMillis) {
        Validate.notNull(player, "player must not be null!");
        Validate.notNull(tagType, "tagType must not be null!");
        Validate.notNull(tagReason, "tagReason must not be null!");
        ICombatLogX plugin = getCombatLogX();

        if (player.hasMetadata("NPC")) {
            plugin.printDebug("player is an NPC and can't be tagged.");
            return false;
        }

        if (failsPreTagEvent(player, enemy, tagType, tagReason)) {
            plugin.printDebug("The PlayerPreTagEvent was cancelled.");
            return false;
        }

        boolean alreadyInCombat = isInCombat(player);
        plugin.printDebug("Previous Combat Status: " + alreadyInCombat);
        PluginManager pluginManager = Bukkit.getPluginManager();

        if (alreadyInCombat) {
            PlayerReTagEvent event = new PlayerReTagEvent(player, enemy, tagType, tagReason, customEndMillis);
            pluginManager.callEvent(event);
            if (event.isCancelled()) {
                return false;
            }

            customEndMillis = event.getEndTime();
        } else {
            PlayerTagEvent event = new PlayerTagEvent(player, enemy, tagType, tagReason, customEndMillis);
            pluginManager.callEvent(event);

            customEndMillis = event.getEndTime();
            sendTagMessage(player, enemy, tagType, tagReason);
        }


        UUID playerId = player.getUniqueId();
        CombatTag combatTag = new CombatTag(enemy, tagType, tagReason, customEndMillis);
        TagInformation tagInformation = this.combatMap.computeIfAbsent(playerId, key -> new TagInformation(player));
        tagInformation.addTag(combatTag);

        String playerName = player.getName();
        plugin.printDebug("Successfully put player '" + playerName + "' into combat.");
        return true;
    }

    @Override
    public void untag(Player player, UntagReason untagReason) {
        Validate.notNull(player, "player must not be null!");
        Validate.notNull(untagReason, "untagReason must not be null!");
        ICombatLogX plugin = getCombatLogX();

        if (!isInCombat(player)) {
            return;
        }

        TagInformation tagInformation = getTagInformation(player);
        if (tagInformation == null) {
            return;
        }

        UUID playerId = player.getUniqueId();
        this.combatMap.remove(playerId);

        ITimerManager timerManager = plugin.getTimerManager();
        timerManager.remove(player);

        PluginManager pluginManager = Bukkit.getPluginManager();
        List<Entity> enemyList = tagInformation.getEnemies();
        for (Entity entity : enemyList) {
            PlayerEnemyRemoveEvent event = new PlayerEnemyRemoveEvent(player, untagReason, entity);
            pluginManager.callEvent(event);
        }

        PlayerUntagEvent event = new PlayerUntagEvent(player, untagReason, enemyList);
        pluginManager.callEvent(event);
    }

    @Override
    public void untag(Player player, Entity enemy, UntagReason untagReason) {
        Validate.notNull(player, "player must not be null!");
        Validate.notNull(enemy, "enemy must not be null!");
        Validate.notNull(untagReason, "untagReason must not be null!");
        if (!isInCombat(player)) {
            return;
        }

        TagInformation tagInformation = getTagInformation(player);
        if (tagInformation == null || !tagInformation.isEnemy(enemy)) {
            return;
        }

        tagInformation.removeEnemy(enemy);
        PluginManager pluginManager = Bukkit.getPluginManager();
        PlayerEnemyRemoveEvent event = new PlayerEnemyRemoveEvent(player, untagReason, enemy);
        pluginManager.callEvent(event);

        if (tagInformation.isExpired()) {
            untag(player, untagReason);
        }
    }

    @Override
    public boolean isInCombat(Player player) {
        Validate.notNull(player, "player must not be null!");

        TagInformation tagInformation = getTagInformation(player);
        return (tagInformation != null);
    }

    @NotNull
    @Override
    public Set<UUID> getPlayerIdsInCombat() {
        Set<UUID> playerIdSet = this.combatMap.keySet();
        return Collections.unmodifiableSet(playerIdSet);
    }

    @NotNull
    @Override
    public List<Player> getPlayersInCombat() {
        Set<UUID> playerIdSet = getPlayerIdsInCombat();
        List<Player> playerList = new ArrayList<>();

        for (UUID playerId : playerIdSet) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                playerList.add(player);
            }
        }

        return Collections.unmodifiableList(playerList);
    }

    @Nullable
    @Override
    @Deprecated
    public Entity getEnemy(Player player) {
        Validate.notNull(player, "player must not be null!");

        TagInformation tagInformation = getTagInformation(player);
        if (tagInformation == null) {
            return null;
        }

        List<Entity> enemyList = tagInformation.getEnemies();
        if (enemyList.isEmpty()) {
            return null;
        }

        return enemyList.get(0);
    }

    @Override
    public TagInformation getTagInformation(Player player) {
        Validate.notNull(player, "player must not be null!");

        UUID playerId = player.getUniqueId();
        return this.combatMap.get(playerId);
    }

    @Nullable
    @Override
    @Deprecated
    public Player getByEnemy(Entity enemy) {
        Validate.notNull(enemy, "enemy must not be null!");

        List<Player> playerList = getPlayersInCombat();
        for (Player player : playerList) {
            TagInformation tagInformation = getTagInformation(player);
            if (tagInformation == null) {
                continue;
            }

            if (tagInformation.isEnemy(enemy)) {
                return player;
            }
        }

        return null;
    }

    @Override
    @Deprecated
    public long getTimerLeftMillis(Player player) {
        Validate.notNull(player, "player must not be null!");

        TagInformation tagInformation = getTagInformation(player);
        if (tagInformation == null) {
            return 0L;
        }

        return tagInformation.getMillisLeftCombined();
    }

    @Override
    @Deprecated
    public int getTimerLeftSeconds(Player player) {
        double timerLeftMillis = getTimerLeftMillis(player);
        if (timerLeftMillis <= 0.0D) {
            return 0;
        }

        double secondsLeft = (timerLeftMillis / 1_000.0D);
        return (int) Math.ceil(secondsLeft);
    }

    @Override
    public int getMaxTimerSeconds(Player player) {
        ICombatLogX plugin = getCombatLogX();
        ConfigurationManager configurationManager = plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        String timerTypeString = configuration.getString("timer.type");

        TimerType timerType = TimerType.parse(timerTypeString);
        return (timerType == TimerType.PERMISSION ? getPermissionTimerSeconds(player) : getGlobalTimerSeconds());
    }

    @Override
    public Permission getBypassPermission() {
        return this.bypassPermission;
    }

    @Override
    public boolean canBypass(Player player) {
        Permission bypassPermission = getBypassPermission();
        if (bypassPermission == null) {
            return false;
        }

        return player.hasPermission(bypassPermission);
    }

    @Override
    public void onReload() {
        ICombatLogX plugin = getCombatLogX();
        ConfigurationManager configurationManager = plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");

        String permissionName = configuration.getString("bypass-permission");
        if (permissionName == null || permissionName.isEmpty()) {
            this.bypassPermission = null;
        } else {
            String description = "CombatLogX Bypass Permission";
            this.bypassPermission = new Permission(permissionName, description, PermissionDefault.FALSE);
        }
    }

    private int getGlobalTimerSeconds() {
        ICombatLogX plugin = getCombatLogX();
        ConfigurationManager configurationManager = plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        return configuration.getInt("timer.default-timer", 10);
    }

    private int getPermissionTimerSeconds(Player player) {
        Set<PermissionAttachmentInfo> permissionAttachmentInfoSet = player.getEffectivePermissions();
        Set<String> permissionSet = permissionAttachmentInfoSet.parallelStream()
                .filter(PermissionAttachmentInfo::getValue)
                .map(PermissionAttachmentInfo::getPermission)
                .filter(permission -> permission.startsWith("combatlogx.timer."))
                .map(permission -> permission.substring("combatlogx.timer.".length()))
                .collect(Collectors.toSet());
        if (permissionSet.isEmpty()) {
            return getGlobalTimerSeconds();
        }

        int lowestTimer = Integer.MAX_VALUE;
        boolean foundValue = false;
        for (String permission : permissionSet) {
            try {
                int value = Integer.parseInt(permission);
                lowestTimer = Math.min(lowestTimer, value);
                foundValue = true;
            } catch (NumberFormatException ignored) {
                // Ignored Exception
            }
        }

        return (foundValue ? lowestTimer : getGlobalTimerSeconds());
    }

    private String getEntityName(Player player, Entity entity) {
        ICombatLogX plugin = getCombatLogX();

        if (entity == null) {
            LanguageManager languageManager = plugin.getLanguageManager();
            String message = languageManager.getMessageString(player, "placeholder.unknown-enemy", null);
            return MessageUtility.color(message);
        }

        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        EntityHandler entityHandler = multiVersionHandler.getEntityHandler();
        return entityHandler.getName(entity);
    }

    private String getEntityType(Player player, Entity entity) {
        ICombatLogX plugin = getCombatLogX();

        if (entity == null) {
            LanguageManager languageManager = plugin.getLanguageManager();
            String message = languageManager.getMessageString(player, "placeholder.unknown-enemy", null);
            return MessageUtility.color(message);
        }

        EntityType entityType = entity.getType();
        return entityType.name();
    }

    private boolean failsPreTagEvent(Player player, Entity enemy, TagType tagType, TagReason tagReason) {
        PlayerPreTagEvent event = new PlayerPreTagEvent(player, enemy, tagType, tagReason);
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.callEvent(event);
        return event.isCancelled();
    }

    private void sendTagMessage(Player player, Entity enemy, TagType tagType, TagReason tagReason) {
        if (tagType == TagType.DAMAGE) {
            return;
        }

        String enemyName = getEntityName(player, enemy);
        String enemyType = getEntityType(player, enemy);
        String tagReasonString = tagReason.name().toLowerCase(Locale.US);
        String tagTypeString = tagType.name().toLowerCase(Locale.US);

        Replacer replacer = message -> message.replace("{enemy}", enemyName)
                .replace("{mob_type}", enemyType);
        String languagePath = ("tagged." + tagReasonString + "." + tagTypeString);

        ICombatLogX plugin = getCombatLogX();
        plugin.sendMessageWithPrefix(player, languagePath, replacer);
    }
}
