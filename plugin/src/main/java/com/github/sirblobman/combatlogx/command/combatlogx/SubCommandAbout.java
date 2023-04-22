package com.github.sirblobman.combatlogx.command.combatlogx;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.bukkit.command.CommandSender;

import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.replacer.Replacer;
import com.github.sirblobman.api.language.replacer.StringReplacer;
import com.github.sirblobman.api.shaded.adventure.text.Component;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogCommand;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.Expansion.State;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionDescription;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;

import org.jetbrains.annotations.NotNull;

public final class SubCommandAbout extends CombatLogCommand {
    public SubCommandAbout(@NotNull ICombatLogX plugin) {
        super(plugin, "about");
        setPermissionName("combatlogx.command.combatlogx.about");
    }

    @Override
    protected @NotNull List<String> onTabComplete(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length == 1) {
            Set<String> valueSet = getExpansionNames();
            return getMatching(args[0], valueSet);
        }

        return Collections.emptyList();
    }

    @Override
    protected boolean execute(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length < 1) {
            return false;
        }

        String expansionName = args[0];
        Optional<Expansion> optionalExpansion = getExpansion(expansionName);
        if (!optionalExpansion.isPresent()) {
            Replacer replacer = new StringReplacer("{target}", expansionName);
            sendMessageWithPrefix(sender, "error.unknown-expansion", replacer);
            return true;
        }

        Expansion expansion = optionalExpansion.get();
        sendExpansionInformation(sender, expansion);
        return true;
    }

    private void sendExpansionInformation(@NotNull CommandSender sender, @NotNull Expansion expansion) {
        String name = expansion.getName();
        String prefix = expansion.getPrefix();
        State state = expansion.getState();

        ExpansionDescription information = expansion.getDescription();
        String description = information.getDescription();
        String website = information.getWebsite();
        List<String> authorList = information.getAuthors();
        String authorString = String.join(", ", authorList);
        String version = information.getVersion();

        Replacer[] replacerArray = {
                new StringReplacer("{name}", name),
                new StringReplacer("{prefix}", prefix),
                new StringReplacer("{version}", version),
                new StringReplacer("{state}", state.name()),
                new StringReplacer("{description}", description),
                new StringReplacer("{website}", website == null ? "N/A" : website),
                new StringReplacer("{authors}", authorString)
        };

        LanguageManager languageManager = getLanguageManager();
        String messageKey = "command.combatlogx.expansion-information";
        List<Component> messageList = languageManager.getMessageList(sender, messageKey, replacerArray);
        for (Component message : messageList) {
            languageManager.sendMessage(sender, message);
        }
    }

    private Set<String> getExpansionNames() {
        ExpansionManager expansionManager = getExpansionManager();
        Set<String> expansionNameSet = new HashSet<>();

        List<Expansion> expansionList = expansionManager.getAllExpansions();
        for (Expansion expansion : expansionList) {
            String expansionName = expansion.getName();
            expansionNameSet.add(expansionName);
        }

        return Collections.unmodifiableSet(expansionNameSet);
    }

    private Optional<Expansion> getExpansion(String name) {
        ExpansionManager expansionManager = getExpansionManager();
        return expansionManager.getExpansion(name);
    }
}
