package com.github.sirblobman.combatlogx.command.combatlogx;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.shaded.adventure.text.Component;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogCommand;

import org.jetbrains.annotations.NotNull;

public final class SubCommandHelp extends CombatLogCommand {
    public SubCommandHelp(@NotNull ICombatLogX plugin) {
        super(plugin, "help");
        setPermissionName("combatlogx.command.combatlogx.help");
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, String @NotNull [] args) {
        LanguageManager languageManager = getLanguageManager();
        String messageKey = "command.combatlogx.help-message-list";

        List<Component> messageList = languageManager.getMessageList(sender, messageKey);
        for (Component message : messageList) {
            languageManager.sendMessage(sender, message);
        }

        return true;
    }
}
