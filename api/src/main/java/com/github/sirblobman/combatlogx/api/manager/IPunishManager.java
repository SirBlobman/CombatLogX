package com.github.sirblobman.combatlogx.api.manager;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.object.UntagReason;

public interface IPunishManager {
    /**
     * Load all known punishments from the configuration
     */
    void loadPunishments();

    /**
     * Punish a player for logging out during combat.
     * Also called when expire punishing is enabled in the configuration.
     *
     * @param player        The {@link Player} to punish.
     * @param punishReason  The original reason that the player was removed from combat.
     * @param previousEnemy The original enemy of the player.
     * @return {@code true} if the plugin was able to punish the player successfully.
     */
    boolean punish(Player player, UntagReason punishReason, LivingEntity previousEnemy);

    /**
     * Get the total amount of times a player was punished.
     * If the punishment tracker is disabled, this will always return a value of zero.
     *
     * @param player The {@link Player} to check.
     * @return The amount of times the player was punished.
     */
    long getPunishmentCount(OfflinePlayer player);
}
