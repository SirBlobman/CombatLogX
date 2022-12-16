package com.github.sirblobman.combatlogx.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.event.PlayerPunishEvent;
import com.github.sirblobman.combatlogx.api.manager.IDeathManager;
import com.github.sirblobman.combatlogx.api.manager.IPlaceholderManager;
import com.github.sirblobman.combatlogx.api.manager.IPunishManager;
import com.github.sirblobman.combatlogx.api.object.UntagReason;
import com.github.sirblobman.combatlogx.object.SpecialPunishment;

public final class PunishManager extends Manager implements IPunishManager {
    private final List<String> punishCommandList;
    private final List<SpecialPunishment> specialPunishmentList;

    public PunishManager(ICombatLogX plugin) {
        super(plugin);
        this.punishCommandList = new ArrayList<>();
        this.specialPunishmentList = new ArrayList<>();
    }

    @Override
    public void loadPunishments() {
        this.punishCommandList.clear();
        this.specialPunishmentList.clear();

        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("commands.yml");
        List<String> punishCommandList = configuration.getStringList("punish-command-list");
        this.punishCommandList.addAll(punishCommandList);

        if (configuration.getBoolean("special-punish-commands-enabled")) {
            ConfigurationSection section = configuration.getConfigurationSection("special-punish-commands");
            loadSpecialPunishments(section);
        }
    }

    @Override
    public boolean punish(Player player, UntagReason punishReason, List<Entity> enemyList) {
        PlayerPunishEvent punishEvent = new PlayerPunishEvent(player, punishReason, enemyList);
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.callEvent(punishEvent);

        if (punishEvent.isCancelled()) {
            return false;
        }

        increasePunishmentCount(player);
        runKillCheck(player, enemyList);

        runPunishCommands(player, enemyList, this.punishCommandList);
        runSpecialPunishments(player, enemyList);
        return true;
    }

    @Override
    public long getPunishmentCount(OfflinePlayer player) {
        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("punish.yml");
        if (!configuration.getBoolean("enable-punishment-counter")) {
            return 0L;
        }

        PlayerDataManager playerDataManager = getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);
        return playerData.getLong("punishment-count", 0L);
    }

    private void increasePunishmentCount(OfflinePlayer player) {
        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("punish.yml");
        if (!configuration.getBoolean("enable-punishment-counter")) {
            return;
        }

        PlayerDataManager playerDataManager = getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);

        long currentCount = playerData.getLong("punishment-count", 0L);
        playerData.set("punishment-count", currentCount + 1L);
        playerDataManager.save(player);
    }

    private void resetPunishmentCount(OfflinePlayer player) {
        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("punish.yml");
        if (!configuration.getBoolean("enable-punishment-counter")) {
            return;
        }

        PlayerDataManager playerDataManager = getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);
        playerData.set("punishment-count", 0L);
        playerDataManager.save(player);
    }

    private void runKillCheck(Player player, List<Entity> enemyList) {
        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("punish.yml");
        String killOptionString = configuration.getString("kill-time");
        if (killOptionString == null) {
            killOptionString = "QUIT";
        }

        ICombatLogX plugin = getCombatLogX();
        if (killOptionString.equals("QUIT")) {
            IDeathManager deathManager = plugin.getDeathManager();
            deathManager.kill(player, enemyList);
        }

        if (killOptionString.equals("JOIN")) {
            PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
            YamlConfiguration playerData = playerDataManager.get(player);
            playerData.set("kill-on-join", true);
            playerDataManager.save(player);
        }
    }

    private void runPunishCommands(Player player, List<Entity> enemyList, List<String> punishCommandList) {
        if (punishCommandList.isEmpty()) {
            return;
        }

        ICombatLogX plugin = getCombatLogX();
        IPlaceholderManager placeholderManager = plugin.getPlaceholderManager();
        placeholderManager.runReplacedCommands(player, enemyList, punishCommandList);
    }

    private void loadSpecialPunishments(ConfigurationSection sectionSpecial) {
        Validate.notNull(sectionSpecial, "sectionSpecial must not be null!");
        printDebug("Loading special punishments...");

        Set<String> keySet = sectionSpecial.getKeys(false);
        for (String key : keySet) {
            ConfigurationSection section = sectionSpecial.getConfigurationSection(key);
            if (section == null) {
                printDebug("Skipping special punishment '" + key + "' because the section is not valid.");
                continue;
            }

            int minAmount = section.getInt("amount.min");
            if (minAmount < 1) {
                printDebug("Skipping special punishment '" + key + "' because min amount must be"
                        + "greater than zero.");
                continue;
            }

            int maxAmount = section.getInt("amount.max");
            if (maxAmount < minAmount) {
                printDebug("Skipping special punishment '" + key + "' because max amount must be"
                        + "greater than or equal to the min amount.");
                continue;
            }

            boolean reset = section.getBoolean("reset");
            List<String> commandList = section.getStringList("command-list");
            if (commandList.isEmpty()) {
                printDebug("Skipping special punishment '" + key + "' because the command list is empty.");
                continue;
            }

            SpecialPunishment specialPunishment = new SpecialPunishment(key, minAmount, maxAmount, reset, commandList);
            this.specialPunishmentList.add(specialPunishment);
        }

        int specialPunishmentCount = this.specialPunishmentList.size();
        printDebug("Successfully loaded " + specialPunishmentCount + " special punishment(s).");
    }

    private void runSpecialPunishments(Player player, List<Entity> enemyList) {
        printDebug("Detected runSpecialPunishments method...");

        long punishmentCount = getPunishmentCount(player);
        printDebug("Punishment Count: " + punishmentCount);

        boolean reset = false;
        for (SpecialPunishment specialPunishment : this.specialPunishmentList) {
            String id = specialPunishment.getId();
            printDebug("Running punishment with id '" + id + "'...");

            int min = specialPunishment.getMinAmount();
            printDebug("Minimum: " + min);

            if (punishmentCount < min) {
                printDebug("Punishment minimum not met, ignoring.");
                continue;
            }

            int max = specialPunishment.getMaxAmount();
            printDebug("Maximum: " + max);

            if (punishmentCount > max) {
                printDebug("Punishment count currently over maximum, ignoring.");
                continue;
            }

            if (specialPunishment.isReset()) {
                printDebug("Punishment reset is enabled.");
                reset = true;
            }

            List<String> commandList = specialPunishment.getCommandList();
            printDebug("Command List: " + commandList);

            runPunishCommands(player, enemyList, commandList);
            printDebug("Finished punishment with id '" + id + "'.");
        }

        if (reset) {
            printDebug("Resetting punishment count for player '" + player.getName() + ".");
            resetPunishmentCount(player);
        }

        printDebug("Finished running special punishments.");
    }
}
