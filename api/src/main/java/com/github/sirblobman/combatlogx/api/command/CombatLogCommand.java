package com.github.sirblobman.combatlogx.api.command;

import java.util.Locale;

import org.bukkit.command.CommandSender;

import com.github.sirblobman.api.command.Command;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.Replacer;
import com.github.sirblobman.combatlogx.api.ICombatLogX;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CombatLogCommand extends Command {
    private final ICombatLogX plugin;

    public CombatLogCommand(ICombatLogX plugin, String commandName) {
        super(plugin.getPlugin(), commandName);
        this.plugin = plugin;
    }

    @NotNull
    @Override
    protected final LanguageManager getLanguageManager() {
        return this.plugin.getLanguageManager();
    }

    protected final ICombatLogX getCombatLogX() {
        return this.plugin;
    }

    protected final String getMessageWithPrefix(@Nullable CommandSender sender, @NotNull String key,
                                                @Nullable Replacer replacer, boolean color) {
        ICombatLogX plugin = getCombatLogX();
        LanguageManager languageManager = plugin.getLanguageManager();

        String message = languageManager.getMessage(sender, key, replacer, color);
        if(message.isEmpty()) return "";

        String prefix = languageManager.getMessage(sender, "prefix", null, true);
        return (prefix.isEmpty() ? message : String.format(Locale.US,"%s %s", prefix, message));
    }

    protected final void sendMessageWithPrefix(@NotNull CommandSender sender, @NotNull String key,
                                               @Nullable Replacer replacer, boolean color) {
        String message = getMessageWithPrefix(sender, key, replacer, color);
        if(!message.isEmpty()) sender.sendMessage(message);
    }
}
