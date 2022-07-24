package com.github.sirblobman.combatlogx.command.combatlogx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.bukkit.command.CommandSender;

import com.github.sirblobman.api.language.Replacer;
import com.github.sirblobman.api.utility.MessageUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.command.CombatLogCommand;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.Expansion.State;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionDescription;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;

public final class SubCommandAbout extends CombatLogCommand {
    public SubCommandAbout(ICombatLogX plugin) {
        super(plugin, "about");
        setPermissionName("combatlogx.command.combatlogx.about");
    }

    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            Set<String> valueSet = getExpansionNames();
            return getMatching(args[0], valueSet);
        }

        return Collections.emptyList();
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            return false;
        }

        String expansionName = args[0];
        Optional<Expansion> optionalExpansion = getExpansion(expansionName);
        if (!optionalExpansion.isPresent()) {
            Replacer replacer = message -> message.replace("{target}", expansionName);
            sendMessageWithPrefix(sender, "error.unknown-expansion", replacer, true);
            return true;
        }

        Expansion expansion = optionalExpansion.get();
        sendExpansionInformation(sender, expansion);
        return true;
    }

    private void sendExpansionInformation(CommandSender sender, Expansion expansion) {
        String name = expansion.getName();
        String prefix = expansion.getPrefix();
        State state = expansion.getState();

        ExpansionDescription information = expansion.getDescription();
        String description = information.getDescription();
        List<String> authorList = information.getAuthors();
        String authorString = String.join(", ", authorList);
        String version = information.getVersion();

        List<String> messageList = new ArrayList<>();
        messageList.add("&f");
        messageList.add("&f&lExpansion Information for &a" + name + "&f&l:");
        messageList.add("&f&lDisplay Name: &7" + prefix);
        messageList.add("&f&lVersion: &7" + version);
        messageList.add("&f&lState: &7" + state);
        messageList.add("&f");
        messageList.add("&f&lDescription: &7" + description);
        messageList.add("&f&lAuthors: &7" + authorString);

        List<String> colorList = MessageUtility.colorList(messageList);
        for (String message : colorList) {
            sender.sendMessage(message);
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
