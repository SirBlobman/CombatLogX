package com.github.sirblobman.combatlogx.command.combatlogx;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.Replacer;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogPlayerCommand;

public final class CommandCombatLogXToggle extends CombatLogPlayerCommand {
    public CommandCombatLogXToggle(ICombatLogX plugin) {
        super(plugin, "toggle");
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        if (args.length == 1) {
            return getMatching(args[0], "actionbar", "bossbar", "scoreboard");
        }

        return Collections.emptyList();
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        if (!checkPermission(player, "combatlogx.command.combatlogx.toggle", true)) {
            return true;
        }

        if (args.length < 1) {
            return false;
        }

        String sub = args[0].toLowerCase(Locale.US);
        List<String> validToggleList = Arrays.asList("actionbar", "bossbar", "scoreboard");
        if (!validToggleList.contains(sub)) {
            return false;
        }

        toggleValue(player, sub);
        return true;
    }

    private void toggleValue(Player player, String value) {
        ICombatLogX plugin = getCombatLogX();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        LanguageManager languageManager = getLanguageManager();

        YamlConfiguration playerData = playerDataManager.get(player);
        boolean currentValue = playerData.getBoolean(value, true);
        playerData.set(value, !currentValue);
        playerDataManager.save(player);

        boolean status = playerData.getBoolean(value, true);
        String statusPath = ("placeholder.toggle." + (status ? "enabled" : "disabled"));
        String statusString = languageManager.getMessage(player, statusPath, null, true);
        Replacer replacer = message -> message.replace("{status}", statusString);

        String messagePath = ("command.combatlogx.toggle-" + value);
        sendMessageWithPrefix(player, messagePath, replacer, true);
    }
}
