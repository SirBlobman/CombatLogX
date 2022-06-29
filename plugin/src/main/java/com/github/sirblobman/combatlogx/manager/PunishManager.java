package com.github.sirblobman.combatlogx.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.event.PlayerPunishEvent;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.manager.IDeathManager;
import com.github.sirblobman.combatlogx.api.manager.IPunishManager;
import com.github.sirblobman.combatlogx.api.object.UntagReason;
import com.github.sirblobman.combatlogx.api.utility.CommandHelper;
import com.github.sirblobman.combatlogx.object.SpecialPunishment;

public final class PunishManager implements IPunishManager {
    private final ICombatLogX plugin;
    private final List<String> punishCommandList;
    private final List<SpecialPunishment> specialPunishmentList;

    public PunishManager(ICombatLogX plugin) {
        this.plugin = Validate.notNull(plugin, "plugin must not be null!");
        this.punishCommandList = new ArrayList<>();
        this.specialPunishmentList = new ArrayList<>();
    }

    @Override
    public void loadPunishments() {
        this.punishCommandList.clear();
        this.specialPunishmentList.clear();

        ConfigurationManager configurationManager = this.plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("commands.yml");
        List<String> punishCommandList = configuration.getStringList("punish-command-list");
        this.punishCommandList.addAll(punishCommandList);

        if (configuration.getBoolean("special-punish-commands-enabled")) {
            ConfigurationSection section = configuration.getConfigurationSection("special-punish-commands");
            loadSpecialPunishments(section);
        }
    }

    @Override
    public boolean punish(Player player, UntagReason punishReason, LivingEntity previousEnemy) {
        PlayerPunishEvent punishEvent = new PlayerPunishEvent(player, punishReason, previousEnemy);
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.callEvent(punishEvent);

        if (punishEvent.isCancelled()) {
            return false;
        }

        increasePunishmentCount(player);
        runKillCheck(player);

        runPunishCommands(player, previousEnemy, this.punishCommandList);
        runSpecialPunishments(player, previousEnemy);
        return true;
    }

    @Override
    public long getPunishmentCount(OfflinePlayer player) {
        ConfigurationManager configurationManager = this.plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("punish.yml");
        if (!configuration.getBoolean("enable-punishment-counter")) {
            return 0L;
        }

        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);
        return playerData.getLong("punishment-count", 0L);
    }

    private void increasePunishmentCount(OfflinePlayer player) {
        ConfigurationManager configurationManager = this.plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("punish.yml");
        if (!configuration.getBoolean("enable-punishment-counter")) {
            return;
        }

        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);

        long currentCount = playerData.getLong("punishment-count", 0L);
        playerData.set("punishment-count", currentCount + 1L);
        playerDataManager.save(player);
    }

    private void resetPunishmentCount(OfflinePlayer player) {
        ConfigurationManager configurationManager = this.plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("punish.yml");
        if (!configuration.getBoolean("enable-punishment-counter")) {
            return;
        }

        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);
        playerData.set("punishment-count", 0L);
        playerDataManager.save(player);
    }

    private void runKillCheck(Player player) {
        ConfigurationManager configurationManager = this.plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("punish.yml");
        String killOptionString = configuration.getString("kill-time");
        if (killOptionString == null) {
            killOptionString = "QUIT";
        }

        if (killOptionString.equals("QUIT")) {
            IDeathManager deathManager = this.plugin.getDeathManager();
            deathManager.kill(player);
        }

        if (killOptionString.equals("JOIN")) {
            YamlConfiguration playerData = this.plugin.getData(player);
            playerData.set("kill-on-join", true);
            this.plugin.saveData(player);
        }
    }

    private void runPunishCommands(Player player, LivingEntity previousEnemy, List<String> punishCommandList) {
        if (punishCommandList.isEmpty()) {
            return;
        }

        ICombatManager combatManager = this.plugin.getCombatManager();
        for (String punishCommand : punishCommandList) {
            String replacedCommand = combatManager.replaceVariables(player, previousEnemy, punishCommand);
            if (replacedCommand.startsWith("[PLAYER]")) {
                String command = replacedCommand.substring("[PLAYER]".length());
                CommandHelper.runAsPlayer(this.plugin, player, command);
                continue;
            }

            if (replacedCommand.startsWith("[OP]")) {
                String command = replacedCommand.substring("[OP]".length());
                CommandHelper.runAsOperator(this.plugin, player, command);
                continue;
            }

            CommandHelper.runAsConsole(this.plugin, replacedCommand);
        }
    }

    private void loadSpecialPunishments(ConfigurationSection sectionSpecial) {
        Validate.notNull(sectionSpecial, "sectionSpecial must not be null!");
        Logger logger = this.plugin.getLogger();
        logger.info("Loading special punishments...");

        Set<String> keySet = sectionSpecial.getKeys(false);
        for (String key : keySet) {
            ConfigurationSection section = sectionSpecial.getConfigurationSection(key);
            if (section == null) {
                logger.warning("Skipping special punishment '" + key + "' because the section is not valid.");
                continue;
            }

            int minAmount = section.getInt("amount.min");
            if (minAmount < 1) {
                logger.warning("Skipping special punishment '" + key + "' because min amount must be"
                        + "greater than zero.");
                continue;
            }

            int maxAmount = section.getInt("amount.max");
            if (maxAmount < minAmount) {
                logger.warning("Skipping special punishment '" + key + "' because max amount must be"
                        + "greater than or equal to the min amount.");
                continue;
            }

            boolean reset = section.getBoolean("reset");
            List<String> commandList = section.getStringList("command-list");
            if (commandList.isEmpty()) {
                logger.warning("Skipping special punishment '" + key + "' because the command list is empty.");
                continue;
            }

            SpecialPunishment specialPunishment = new SpecialPunishment(minAmount, maxAmount, reset, commandList);
            this.specialPunishmentList.add(specialPunishment);
        }

        int specialPunishmentCount = this.specialPunishmentList.size();
        logger.info("Successfully loaded " + specialPunishmentCount + " special punishment(s).");
    }

    private void runSpecialPunishments(Player player, LivingEntity previousEnemy) {
        long punishmentCount = getPunishmentCount(player);
        boolean reset = false;

        for (SpecialPunishment specialPunishment : this.specialPunishmentList) {
            int min = specialPunishment.getMinAmount();
            if (punishmentCount < min) {
                continue;
            }

            int max = specialPunishment.getMaxAmount();
            if (punishmentCount > max) {
                continue;
            }

            if (specialPunishment.isReset()) {
                reset = true;
            }

            List<String> commandList = specialPunishment.getCommandList();
            runPunishCommands(player, previousEnemy, commandList);
        }

        if (reset) {
            resetPunishmentCount(player);
        }
    }
}
