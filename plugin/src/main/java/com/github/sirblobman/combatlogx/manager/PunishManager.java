package com.github.sirblobman.combatlogx.manager;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.configuration.CommandConfiguration;
import com.github.sirblobman.combatlogx.api.configuration.PunishConfiguration;
import com.github.sirblobman.combatlogx.api.event.PlayerPunishEvent;
import com.github.sirblobman.combatlogx.api.manager.IDeathManager;
import com.github.sirblobman.combatlogx.api.manager.IPlaceholderManager;
import com.github.sirblobman.combatlogx.api.manager.IPunishManager;
import com.github.sirblobman.combatlogx.api.object.KillTime;
import com.github.sirblobman.combatlogx.api.object.SpecialPunishCommand;
import com.github.sirblobman.combatlogx.api.object.UntagReason;

public final class PunishManager extends Manager implements IPunishManager {
    public PunishManager(@NotNull ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public boolean punish(@NotNull Player player, @NotNull UntagReason punishReason, @NotNull List<Entity> enemyList) {
        PlayerPunishEvent punishEvent = new PlayerPunishEvent(player, punishReason, enemyList);
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.callEvent(punishEvent);

        if (punishEvent.isCancelled()) {
            return false;
        }

        increasePunishmentCount(player);
        runKillCheck(player, enemyList);

        ICombatLogX plugin = getCombatLogX();
        CommandConfiguration commandConfiguration = plugin.getCommandConfiguration();

        List<String> punishCommandList = commandConfiguration.getPunishCommands();
        if (!punishCommandList.isEmpty()) {
            runPunishCommands(player, enemyList, punishCommandList);
        }

        if (commandConfiguration.isSpecialPunishCommandsEnabled()) {
            runSpecialPunishments(player, enemyList);
        }

        return true;
    }

    @Override
    public long getPunishmentCount(@NotNull OfflinePlayer player) {
        ICombatLogX combatLogX = getCombatLogX();
        PunishConfiguration punishConfiguration = combatLogX.getPunishConfiguration();

        if (punishConfiguration.isEnablePunishmentCounter()) {
            PlayerDataManager playerDataManager = getPlayerDataManager();
            if (playerDataManager.hasData(player)) {
                YamlConfiguration playerData = playerDataManager.get(player);
                return playerData.getLong("punishment-count", 0L);
            }

            return 0L;
        }

        return 0L;
    }

    private void increasePunishmentCount(@NotNull OfflinePlayer player) {
        ICombatLogX combatLogX = getCombatLogX();
        PunishConfiguration punishConfiguration = combatLogX.getPunishConfiguration();
        if (!punishConfiguration.isEnablePunishmentCounter()) {
            return;
        }

        PlayerDataManager playerDataManager = getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);
        long currentCount = playerData.getLong("punishment-count", 0L);

        playerData.set("punishment-count", currentCount + 1L);
        playerDataManager.save(player);
    }

    @Override
    public void resetPunishmentCount(@NotNull OfflinePlayer player) {
        ICombatLogX combatLogX = getCombatLogX();
        PunishConfiguration punishConfiguration = combatLogX.getPunishConfiguration();
        if (!punishConfiguration.isEnablePunishmentCounter()) {
            return;
        }

        PlayerDataManager playerDataManager = getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);

        playerData.set("punishment-count", 0L);
        playerDataManager.save(player);
    }

    private void runKillCheck(@NotNull Player player, @NotNull List<Entity> enemyList) {
        ICombatLogX combatLogX = getCombatLogX();
        PunishConfiguration punishConfiguration = combatLogX.getPunishConfiguration();
        KillTime killTime = punishConfiguration.getKillTime();

        switch (killTime) {
            case JOIN:
                killOnJoin(player);
                break;
            case QUIT:
                killOnQuit(player, enemyList);
                break;
            default:
                break;
        }
    }

    private void killOnJoin(@NotNull OfflinePlayer player) {
        ICombatLogX plugin = getCombatLogX();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);
        playerData.set("kill-on-join", true);
        playerDataManager.save(player);
    }

    private void killOnQuit(@NotNull Player player, @NotNull List<Entity> enemyList) {
        ICombatLogX plugin = getCombatLogX();
        IDeathManager deathManager = plugin.getDeathManager();
        deathManager.kill(player, enemyList);
    }

    private void runPunishCommands(@NotNull Player player, @NotNull List<Entity> enemyList,
                                   @NotNull List<String> punishCommandList) {
        if (punishCommandList.isEmpty()) {
            return;
        }

        ICombatLogX plugin = getCombatLogX();
        IPlaceholderManager placeholderManager = plugin.getPlaceholderManager();
        placeholderManager.runReplacedCommands(player, enemyList, punishCommandList);
    }

    private void runSpecialPunishments(@NotNull Player player, @NotNull List<Entity> enemyList) {
        printDebug("Detected runSpecialPunishments method...");

        long punishmentCount = getPunishmentCount(player);
        printDebug("Punishment Count: " + punishmentCount);

        ICombatLogX combatLogX = getCombatLogX();
        CommandConfiguration commandConfiguration = combatLogX.getCommandConfiguration();
        List<SpecialPunishCommand> specialPunishCommandList = commandConfiguration.getSpecialPunishCommandList();

        boolean reset = false;
        for (SpecialPunishCommand specialPunishment : specialPunishCommandList) {
            int min = specialPunishment.getAmountMin();
            int max = specialPunishment.getAmountMax();
            if (punishmentCount < min || punishmentCount > max) {
                continue;
            }

            if (specialPunishment.isReset()) {
                reset = true;
            }

            List<String> commandList = specialPunishment.getCommands();
            runPunishCommands(player, enemyList, commandList);
        }

        if (reset) {
            resetPunishmentCount(player);
        }
    }
}
