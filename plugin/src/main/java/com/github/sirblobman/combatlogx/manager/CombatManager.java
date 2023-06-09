package com.github.sirblobman.combatlogx.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.PluginManager;

import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.replacer.ComponentReplacer;
import com.github.sirblobman.api.language.replacer.Replacer;
import com.github.sirblobman.api.nms.EntityHandler;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.nms.ServerHandler;
import com.github.sirblobman.api.utility.paper.PaperChecker;
import com.github.sirblobman.api.utility.paper.PaperHelper;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.configuration.MainConfiguration;
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
import com.github.sirblobman.api.shaded.adventure.text.Component;

public final class CombatManager extends Manager implements ICombatManager {
    private final Map<UUID, TagInformation> combatMap;

    public CombatManager(@NotNull ICombatLogX plugin) {
        super(plugin);
        this.combatMap = new ConcurrentHashMap<>();
    }

    @Override
    public boolean tag(@NotNull Player player, @Nullable Entity enemy, @NotNull TagType tagType,
                       @NotNull TagReason tagReason) {
        int timerSeconds = getMaxTimerSeconds(player);
        long timerMillis = (timerSeconds * 1_000L);

        long systemMillis = System.currentTimeMillis();
        long endMillis = (systemMillis + timerMillis);
        return tag(player, enemy, tagType, tagReason, endMillis);
    }

    @Override
    public boolean tag(@NotNull Player player, @Nullable Entity enemy, @NotNull TagType tagType,
                       @NotNull TagReason tagReason, long customEndMillis) {
        ICombatLogX plugin = getCombatLogX();
        if (player.hasMetadata("NPC")) {
            plugin.printDebug("player is an NPC and can't be tagged.");
            return false;
        }

        if (failsPreTagEvent(player, enemy, tagType, tagReason)) {
            plugin.printDebug("The PlayerPreTagEvent was cancelled.");
            return false;
        }

        MainConfiguration configuration = plugin.getConfiguration();
        double minimumTps = configuration.getMinimumTps();
        if (minimumTps > 0.0D) {
            double tps = getServerTPS();
            if (tps < minimumTps) {
                plugin.printDebug("Server TPS: " + tps);
                plugin.printDebug("Minimum TPS: " + tps);
                plugin.printDebug("The server tps is too low to tag players.");
                return false;
            }
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
    public void untag(@NotNull Player player, @NotNull UntagReason untagReason) {
        if (!isInCombat(player)) {
            return;
        }

        TagInformation tagInformation = getTagInformation(player);
        if (tagInformation == null) {
            return;
        }

        UUID playerId = player.getUniqueId();
        this.combatMap.remove(playerId);

        ICombatLogX plugin = getCombatLogX();
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
    public void untag(@NotNull Player player, @NotNull Entity enemy, @NotNull UntagReason untagReason) {
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
    public boolean isInCombat(@NotNull Player player) {
        TagInformation tagInformation = getTagInformation(player);
        return (tagInformation != null);
    }

    @Override
    public @NotNull Set<UUID> getPlayerIdsInCombat() {
        Set<UUID> playerIdSet = this.combatMap.keySet();
        return Collections.unmodifiableSet(playerIdSet);
    }

    @Override
    public @NotNull List<Player> getPlayersInCombat() {
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

    @Override
    public TagInformation getTagInformation(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        return this.combatMap.get(playerId);
    }

    @Override
    public int getMaxTimerSeconds(@NotNull Player player) {
        ICombatLogX plugin = getCombatLogX();
        MainConfiguration configuration = plugin.getConfiguration();
        TimerType timerType = configuration.getTimerType();

        if (timerType == TimerType.PERMISSION) {
            return getPermissionTimerSeconds(player);
        }

        return getGlobalTimerSeconds();
    }

    @Override
    public @Nullable Permission getBypassPermission() {
        ICombatLogX combatLogX = getCombatLogX();
        MainConfiguration configuration = combatLogX.getConfiguration();
        return configuration.getBypassPermission();
    }

    @Override
    public boolean canBypass(@NotNull Player player) {
        Permission bypassPermission = getBypassPermission();
        if (bypassPermission == null) {
            return false;
        }

        return player.hasPermission(bypassPermission);
    }

    private int getGlobalTimerSeconds() {
        ICombatLogX combatLogX = getCombatLogX();
        MainConfiguration configuration = combatLogX.getConfiguration();
        return configuration.getDefaultTimer();
    }

    private int getPermissionTimerSeconds(@NotNull Player player) {
        int defaultTimer = getGlobalTimerSeconds();
        Set<PermissionAttachmentInfo> permissionAttachmentInfoSet = player.getEffectivePermissions();
        if (permissionAttachmentInfoSet.isEmpty()) {
            return defaultTimer;
        }

        Set<String> permissionNumberStrings = new HashSet<>();
        for (PermissionAttachmentInfo permissionAttachmentInfo : permissionAttachmentInfoSet) {
            if (!permissionAttachmentInfo.getValue()) {
                continue;
            }

            String permissionName = permissionAttachmentInfo.getPermission();
            if (permissionName.startsWith("combatlogx.timer.")) {
                String timerPart = permissionName.substring("combatlogx.timer.".length());
                permissionNumberStrings.add(timerPart);
            }
        }

        if (permissionNumberStrings.isEmpty()) {
            return defaultTimer;
        }

        int lowestTimer = Integer.MAX_VALUE;
        boolean foundValue = false;

        for (String permission : permissionNumberStrings) {
            try {
                int value = Integer.parseInt(permission);
                lowestTimer = Math.min(lowestTimer, value);
                foundValue = true;
            } catch (NumberFormatException ignored) {
                // Ignored Exception
            }
        }

        return (foundValue ? lowestTimer : defaultTimer);
    }

    private @NotNull Component getUnknownEnemy(@NotNull Player player) {
        ICombatLogX plugin = getCombatLogX();
        LanguageManager languageManager = plugin.getLanguageManager();
        return languageManager.getMessage(player, "placeholder.unknown-enemy");
    }

    private @NotNull Component getEntityName(@NotNull Player player, @Nullable Entity entity) {
        if (entity == null) {
            return getUnknownEnemy(player);
        }

        if (PaperChecker.hasNativeComponentSupport()) {
            Component customName = PaperHelper.getCustomName(entity);
            if (customName != null) {
                return customName;
            }
        }

        ICombatLogX plugin = getCombatLogX();
        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        EntityHandler entityHandler = multiVersionHandler.getEntityHandler();

        String entityName = entityHandler.getName(entity);
        return Component.text(entityName);
    }

    private @NotNull Component getEntityType(@NotNull Player player, @Nullable Entity entity) {
        if (entity == null) {
            return getUnknownEnemy(player);
        }

        EntityType entityType = entity.getType();
        String entityTypeName = entityType.name();
        return Component.text(entityTypeName);
    }

    private boolean failsPreTagEvent(@NotNull Player player, @Nullable Entity enemy, @NotNull TagType tagType,
                                     @NotNull TagReason tagReason) {
        PlayerPreTagEvent event = new PlayerPreTagEvent(player, enemy, tagType, tagReason);
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.callEvent(event);
        return event.isCancelled();
    }

    private void sendTagMessage(@NotNull Player player, @Nullable Entity enemy, @NotNull TagType tagType,
                                @NotNull TagReason tagReason) {
        if (tagType == TagType.DAMAGE) {
            return;
        }

        Component enemyName = getEntityName(player, enemy);
        Component enemyType = getEntityType(player, enemy);
        String tagReasonString = tagReason.name().toLowerCase(Locale.US);
        String tagTypeString = tagType.name().toLowerCase(Locale.US);

        Replacer enemyNameReplacer = new ComponentReplacer("{enemy}", enemyName);
        Replacer enemyTypeReplacer = new ComponentReplacer("{mob_type}", enemyType);
        String languagePath = ("tagged." + tagReasonString + "." + tagTypeString);

        ICombatLogX plugin = getCombatLogX();
        LanguageManager languageManager = plugin.getLanguageManager();
        languageManager.sendModifiableMessageWithPrefix(player, languagePath, enemyNameReplacer, enemyTypeReplacer);
    }

    private double getServerTPS() {
        if (PaperChecker.isPaper()) {
            try {
                return PaperHelper.getServer1mTps();
            } catch (NoSuchMethodError ignored) {
                // Ignored Error
            }
        }

        ICombatLogX plugin = getCombatLogX();
        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        ServerHandler serverHandler = multiVersionHandler.getServerHandler();
        return serverHandler.getServerTps1m();
    }
}
