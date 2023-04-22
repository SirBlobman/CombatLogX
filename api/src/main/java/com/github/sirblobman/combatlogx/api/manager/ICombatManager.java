package com.github.sirblobman.combatlogx.api.manager;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import com.github.sirblobman.combatlogx.api.ICombatLogXNeeded;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;
import com.github.sirblobman.combatlogx.api.object.UntagReason;

public interface ICombatManager extends ICombatLogXNeeded {
    /**
     * CombatTag a player into combat.
     *
     * @param player    The {@link Player} to tag.
     * @param enemy     The enemy that caused the player to be tagged. Can be {@code null}
     * @param tagType   The type of tag, can be {@link TagType#UNKNOWN}
     * @param tagReason The reason for being tagged, can be {@link TagReason#UNKNOWN}
     * @return {@code true} if the player was successfully tagged.
     */
    boolean tag(@NotNull Player player, @Nullable Entity enemy, @NotNull TagType tagType,
                @NotNull TagReason tagReason);

    /**
     * CombatTag a player into combat.
     *
     * @param player          The {@link Player} to tag.
     * @param enemy           The enemy that caused the player to be tagged. Can be {@code null}
     * @param tagType         The type of tag, can be {@link TagType#UNKNOWN}
     * @param tagReason       The reason for being tagged, can be {@link TagReason#UNKNOWN}
     * @param customEndMillis A custom timestamp for ending combat if the player is not tagged again.
     * @return {@code true} if the player was successfully tagged.
     */
    boolean tag(@NotNull Player player, @Nullable Entity enemy, @NotNull TagType tagType,
                @NotNull TagReason tagReason, long customEndMillis);

    /**
     * Remove a player from combat with all enemies.
     *
     * @param player      The {@link Player} to remove.
     * @param untagReason The reason for removing the player. Usually {@link UntagReason#EXPIRE}
     */
    void untag(@NotNull Player player, @NotNull UntagReason untagReason);

    /**
     * Remove a player from combat with a specific enemy.
     *
     * @param player      The {@link Player} to remove.
     * @param enemy       The enemy to remove.
     * @param untagReason The reason for removing the player. Usually {@link UntagReason#EXPIRE}
     */
    void untag(@NotNull Player player, @NotNull Entity enemy, @NotNull UntagReason untagReason);

    /**
     * Check if a player is tagged into combat.
     *
     * @param player The {@link Player} to check.
     * @return {@code true} if the player is currently tagged into combat.
     */
    boolean isInCombat(@NotNull Player player);


    /**
     * @return A list of player ids that are currently tagged into combat.
     */
    @NotNull Set<UUID> getPlayerIdsInCombat();

    /**
     * @return A list of players that are currently tagged into combat.
     */
    @NotNull List<Player> getPlayersInCombat();

    /**
     * Get combat tag information for the specified player.
     *
     * @param player The {@link Player} to check.
     * @return Information about a players combat tag, or {@code null} if the player is not tagged into combat.
     */
    @Nullable TagInformation getTagInformation(@NotNull Player player);

    /**
     * Get the amount of seconds in combat this player will be tagged for.
     *
     * @param player The {@link Player} to check.
     * @return A number of seconds based on a permission or a global configuration setting.
     */
    int getMaxTimerSeconds(@NotNull Player player);

    /**
     * @return The current bypass permission, or {@code null} if one is not set.
     */
    @Nullable Permission getBypassPermission();

    /**
     * Check if a player is able to bypass a combat tag.
     *
     * @param player The {@link Player} to check.
     * @return {@code true} if the player can bypass a combat tag.
     */
    boolean canBypass(@NotNull Player player);
}
