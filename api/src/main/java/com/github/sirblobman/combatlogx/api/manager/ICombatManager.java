package com.github.sirblobman.combatlogx.api.manager;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;
import com.github.sirblobman.combatlogx.api.object.UntagReason;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ICombatManager {
    /**
     * Tag a player into combat.
     *
     * @param player The {@link Player} to tag.
     * @param enemy The enemy that caused the player to be tagged. Can be {@code null}
     * @param tagType The type of tag, can be {@link TagType#UNKNOWN}
     * @param tagReason The reason for being tagged, can be {@link TagReason#UNKNOWN}
     * @return {@code true} if the player was successfully tagged.
     */
    boolean tag(Player player, Entity enemy, TagType tagType, TagReason tagReason);

    /**
     * Tag a player into combat.
     *
     * @param player The {@link Player} to tag.
     * @param enemy The enemy that caused the player to be tagged. Can be {@code null}
     * @param tagType The type of tag, can be {@link TagType#UNKNOWN}
     * @param tagReason The reason for being tagged, can be {@link TagReason#UNKNOWN}
     * @param customEndMillis A custom timestamp for ending combat if the player is not tagged again.
     * @return {@code true} if the player was successfully tagged.
     */
    boolean tag(Player player, Entity enemy, TagType tagType, TagReason tagReason, long customEndMillis);

    /**
     * Remove a player from combat with all enemies.
     *
     * @param player The {@link Player} to remove.
     * @param untagReason The reason for removing the player. Usually {@link UntagReason#EXPIRE}
     */
    void untag(Player player, UntagReason untagReason);

    /**
     * Remove a player from combat with a specific enemy.
     *
     * @param player The {@link Player} to remove.
     * @param enemy The enemy to remove.
     * @param untagReason The reason for removing the player. Usually {@link UntagReason#EXPIRE}
     */
    void untag(Player player, Entity enemy, UntagReason untagReason);

    /**
     * Check if a player is tagged into combat.
     *
     * @param player The {@link Player} to check.
     * @return {@code true} if the player is currently tagged into combat.
     */
    boolean isInCombat(Player player);


    /**
     * @return A list of player ids that are currently tagged into combat.
     */
    @NotNull
    Set<UUID> getPlayerIdsInCombat();

    /**
     * @return A list of players that are currently tagged into combat.
     */
    @NotNull
    List<Player> getPlayersInCombat();

    /**
     * Get the current enemy of a player.
     *
     * @param player The {@link Player} to check.
     * @return The current enemy of the player or {@code null} if the player does not have one.
     * @deprecated CombatLogX now supports multiple enemies
     * @see #getTagInformation(Player)
     */
    @Nullable
    @Deprecated
    Entity getEnemy(Player player);

    /**
     * Get combat tag information for the specified player.
     * @param player The {@link Player} to check.
     * @return Information about a players combat tag, or {@code null} if the player is not tagged into combat.
     */
    @Nullable
    TagInformation getTagInformation(Player player);

    /**
     * Attempt to get a player based on an enemy entity.
     * This method may cause performance issues and should only be used when you have no other options.
     *
     * @param enemy The enemy to check.
     * @return The current {@link Player} linked to this enemy, or {@code null} if one does not exist.
     * @deprecated CombatLogX now supports multiple enemies
     * @see #getTagInformation(Player)
     */
    @Nullable
    @Deprecated
    Player getByEnemy(Entity enemy);

    /**
     * Get the amount of milliseconds a player has left until their combat tag expires.
     *
     * @param player The {@link Player} to check.
     * @return When the player is tagged into combat, this will return an amount of milliseconds.
     * When the player is not in combat, this method will return {@code 0}.
     * @deprecated CombatLogX now supports multiple enemies
     * @see #getTagInformation(Player)
     */
    @Deprecated
    long getTimerLeftMillis(Player player);

    /**
     * Get the amount of seconds a player has left until their combat tag expires.
     *
     * @param player The {@link Player} to check.
     * @return When the player is tagged into combat, a positive number will be returned.
     * When the player is not in combat, this method will return {@code 0}.
     * @deprecated CombatLogX now supports multiple enemies
     * @see #getTagInformation(Player)
     */
    @Deprecated
    int getTimerLeftSeconds(Player player);

    /**
     * Get the amount of seconds in combat this player will be tagged for.
     *
     * @param player The {@link Player} to check.
     * @return A number of seconds based on a permission or a global configuration setting.
     */
    int getMaxTimerSeconds(Player player);

    /**
     * Replace variables in a string related to a player's combat status.
     * <br/>
     * Known Variables:
     * <br/>
     * {player}, {time_left}, {time_left_decimal}, {in_combat}, {status},
     * {punishment_count}, {enemy}, {enemy_name}, {enemy_display_name},
     * {enemy_health}, {enemy_hearts}, {enemy_hearts_count}, {enemy_health_rounded},
     * {enemy_world}, {enemy_x}, {enemy_y}, {enemy_z}
     *
     * @param player The {@link Player} to use.
     * @param enemy The current enemy of the player. Can be {@code null}.
     * @param string The string that will have variables replaced.
     * @return A new string with certain variables replaced.
     */
    String replaceVariables(Player player, LivingEntity enemy, String string);

    /**
     * @return The current bypass permission, or {@code null} if one is not set.
     */
    @Nullable Permission getBypassPermission();

    /**
     * Check if a player is able to bypass a combat tag.
     * @param player The {@link Player} to check.
     * @return {@code true} if the player can bypass a combat tag.
     */
    boolean canBypass(Player player);

    /**
     * This method is executed when the `/clx reload` command is executed.
     */
    void onReload();
}
