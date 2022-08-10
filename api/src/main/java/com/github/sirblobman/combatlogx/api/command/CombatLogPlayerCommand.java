package com.github.sirblobman.combatlogx.api.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.adventure.adventure.audience.Audience;
import com.github.sirblobman.api.adventure.adventure.platform.bukkit.BukkitAudiences;
import com.github.sirblobman.api.adventure.adventure.text.Component;
import com.github.sirblobman.api.adventure.adventure.text.TextComponent.Builder;
import com.github.sirblobman.api.command.PlayerCommand;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.Replacer;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CombatLogPlayerCommand extends PlayerCommand {
    private final ICombatLogX plugin;

    public CombatLogPlayerCommand(ICombatLogX plugin, String commandName) {
        super(plugin.getPlugin(), commandName);
        this.plugin = plugin;
    }

    @NotNull
    @Override
    protected final LanguageManager getLanguageManager() {
        return this.plugin.getLanguageManager();
    }

    @Override
    protected List<String> onTabComplete(Player player, String[] args) {
        return Collections.emptyList();
    }

    @Override
    protected boolean execute(Player player, String[] args) {
        return false;
    }

    protected final ICombatLogX getCombatLogX() {
        return this.plugin;
    }

    protected final ExpansionManager getExpansionManager() {
        ICombatLogX plugin = getCombatLogX();
        return plugin.getExpansionManager();
    }

    @NotNull
    protected final Component getMessageWithPrefix(@Nullable CommandSender audience, @NotNull String key,
                                                   @Nullable Replacer replacer) {
        LanguageManager languageManager = getLanguageManager();
        Component message = languageManager.getMessage(audience, key, replacer);
        if(Component.empty().equals(message)) {
            return Component.empty();
        }

        Component prefix = languageManager.getMessage(audience, "prefix", null);
        if(Component.empty().equals(prefix)) {
            return message;
        }

        Builder builder = Component.text();
        builder.append(prefix);
        builder.append(Component.space());
        builder.append(message);
        return builder.build();
    }

    protected final void sendMessageWithPrefix(@NotNull CommandSender audience, @NotNull String key,
                                               @Nullable Replacer replacer) {
        Component message = getMessageWithPrefix(audience, key, replacer);
        if(Component.empty().equals(message)) {
            return;
        }

        LanguageManager languageManager = getLanguageManager();
        BukkitAudiences audiences = languageManager.getAudiences();
        if(audiences == null) {
            return;
        }

        Audience realAudience = audiences.sender(audience);
        realAudience.sendMessage(message);
    }
}
