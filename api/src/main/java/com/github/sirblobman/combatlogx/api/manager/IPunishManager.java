package com.github.sirblobman.combatlogx.api.manager;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.ICombatLogXNeeded;
import com.github.sirblobman.combatlogx.api.object.UntagReason;

public interface IPunishManager extends ICombatLogXNeeded {
    /**
     * Load all known punishments from the configuration
     */
    void loadPunishments();

    /**
     * Punish a player for logging out during combat.
     * Also called when expire punishing is enabled in the configuration.
     *
     * @param player          The {@link Player} to punish.
     * @param punishReason    The original reason that the player was removed from combat.
     * @param previousEnemies The list of enemies that the player had when they were untagged.
     * @return {@code true} if the plugin was able to punish the player successfully.
     */
    boolean punish(Player player, UntagReason punishReason, List<Entity> previousEnemies);

    /**
     * Get the total amount of times a player was punished.
     * If the punishment tracker is disabled, this will always return a value of zero.
     *
     * @param player The {@link Player} to check.
     * @return The amount of times the player was punished.
     */
    long getPunishmentCount(OfflinePlayer player);
}
