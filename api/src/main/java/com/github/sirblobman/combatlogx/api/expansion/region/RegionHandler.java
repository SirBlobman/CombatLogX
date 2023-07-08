package com.github.sirblobman.combatlogx.api.expansion.region;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.util.Vector;

import com.github.sirblobman.api.folia.FoliaHelper;
import com.github.sirblobman.api.folia.scheduler.TaskScheduler;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.configuration.MainConfiguration;
import com.github.sirblobman.combatlogx.api.expansion.region.configuration.RegionExpansionConfiguration;
import com.github.sirblobman.combatlogx.api.expansion.region.task.KnockbackPlayerTask;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.NoEntryMode;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.object.TagType;

public abstract class RegionHandler<RE extends RegionExpansion> {
    private final RE expansion;
    private final Map<UUID, Long> cooldownMap;

    public RegionHandler(@NotNull RE expansion) {
        this.expansion = expansion;
        this.cooldownMap = new ConcurrentHashMap<>();
    }

    protected final @NotNull RE getExpansion() {
        return this.expansion;
    }

    protected final @NotNull Logger getLogger() {
        RE expansion = getExpansion();
        return expansion.getLogger();
    }

    protected final void printDebug(@NotNull String message) {
        RE expansion = getExpansion();
        ICombatLogX combatLogX = expansion.getPlugin();
        MainConfiguration configuration = combatLogX.getConfiguration();
        if (!configuration.isDebugMode()) {
            return;
        }

        Class<?> thisClass = getClass();
        String className = thisClass.getSimpleName();
        String logMessage = String.format(Locale.US, "[Debug] [%s] %s", className, message);

        Logger expansionLogger = getLogger();
        expansionLogger.info(logMessage);
    }

    private @NotNull RegionExpansionConfiguration getConfiguration() {
        RegionExpansion expansion = getExpansion();
        return expansion.getConfiguration();
    }

    public final void sendEntryDeniedMessage(@NotNull Player player, @NotNull TagInformation tagInformation) {
        TagType tagType = tagInformation.getCurrentTagType();
        String messagePath = getEntryDeniedMessagePath(tagType);
        if (messagePath == null) {
            return;
        }

        UUID playerId = player.getUniqueId();
        if (this.cooldownMap.containsKey(playerId)) {
            long expireMillis = this.cooldownMap.getOrDefault(playerId, 0L);
            long systemMillis = System.currentTimeMillis();
            if (systemMillis < expireMillis) {
                return;
            }
        }

        ICombatLogX plugin = this.expansion.getPlugin();
        LanguageManager languageManager = plugin.getLanguageManager();
        languageManager.sendMessageWithPrefix(player, messagePath);

        long cooldownSeconds = getEntryDeniedMessageCooldown();
        long cooldownMillis = TimeUnit.SECONDS.toMillis(cooldownSeconds);
        long systemMillis = System.currentTimeMillis();
        long expireMillis = (systemMillis + cooldownMillis);
        this.cooldownMap.put(playerId, expireMillis);
    }

    public final void preventEntry(@NotNull Cancellable e, @NotNull Player player,
                                   @NotNull TagInformation tagInformation, @NotNull Location fromLocation,
                                   @NotNull Location toLocation) {
        RegionExpansion expansion = getExpansion();
        ICombatLogX combatLogX = expansion.getPlugin();
        ICombatManager combatManager = combatLogX.getCombatManager();
        if (!combatManager.isInCombat(player)) {
            return;
        }

        Entity enemy = tagInformation.getCurrentEnemy();
        NoEntryMode noEntryMode = getNoEntryMode();
        switch (noEntryMode) {
            case KILL_PLAYER:
                player.setHealth(0.0D);
                break;

            case TELEPORT_TO_ENEMY:
                teleportToEnemy(player, e, enemy);
                break;

            case CANCEL_EVENT:
                e.setCancelled(true);
                break;

            case KNOCKBACK_PLAYER:
                knockbackPlayer(player, e, fromLocation, toLocation);
                break;

            default:
                break;
        }

        sendEntryDeniedMessage(player, tagInformation);
        customPreventEntry(e, player, tagInformation, fromLocation, toLocation);
    }

    private void teleportToEnemy(@NotNull Player player, @NotNull Cancellable e, @Nullable Entity enemy) {
        if (enemy == null) {
            e.setCancelled(true);
            return;
        }

        if (!player.teleport(enemy)) {
            e.setCancelled(true);
        }
    }

    private void knockbackPlayer(@NotNull Player player, @NotNull Cancellable e,
                                 @NotNull Location fromLocation, @NotNull Location toLocation) {
        e.setCancelled(true);

        if (player.isInsideVehicle()) {
            if (!player.leaveVehicle()) {
                return;
            }
        }

        if (isGliding(player)) {
            player.setGliding(false);
            Vector zero = new Vector(0.0D, 0.0D, 0.0D);
            player.setVelocity(zero);
        }

        RegionExpansion expansion = getExpansion();
        ICombatLogX combatLogX = expansion.getPlugin();
        FoliaHelper foliaHelper = combatLogX.getFoliaHelper();
        TaskScheduler scheduler = foliaHelper.getScheduler();

        double strength = getKnockbackStrength();
        KnockbackPlayerTask task = new KnockbackPlayerTask(combatLogX, player, fromLocation, toLocation, strength);
        scheduler.scheduleEntityTask(task);
    }

    protected void customPreventEntry(@NotNull Cancellable e, @NotNull Player player,
                                      @NotNull TagInformation tagInformation, @NotNull Location fromLocation,
                                      @NotNull Location toLocation) {
        // Override this to add custom stuff
    }

    public final long getEntryDeniedMessageCooldown() {
        RegionExpansionConfiguration configuration = getConfiguration();
        return configuration.getMessageCooldown();
    }

    public final @NotNull NoEntryMode getNoEntryMode() {
        RegionExpansionConfiguration configuration = getConfiguration();
        return configuration.getNoEntryMode();
    }

    public final double getKnockbackStrength() {
        RegionExpansionConfiguration configuration = getConfiguration();
        return configuration.getKnockbackStrength();
    }

    private boolean isGliding(@NotNull Player player) {
        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion < 9) {
            return false;
        }

        return player.isGliding();
    }

    public abstract String getEntryDeniedMessagePath(@NotNull TagType tagType);

    public abstract boolean isSafeZone(@NotNull Player player, @NotNull Location location, @NotNull TagInformation tag);
}
