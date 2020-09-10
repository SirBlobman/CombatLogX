package com.SirBlobman.combatlogx.command;

import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.SirBlobman.api.command.Command;
import com.SirBlobman.api.configuration.ConfigurationManager;
import com.SirBlobman.api.configuration.PlayerDataManager;
import com.SirBlobman.api.language.LanguageManager;
import com.SirBlobman.api.language.Replacer;
import com.SirBlobman.api.update.UpdateChecker;
import com.SirBlobman.api.utility.MessageUtility;
import com.SirBlobman.api.utility.VersionUtility;
import com.SirBlobman.combatlogx.CombatPlugin;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.Expansion.State;
import com.SirBlobman.combatlogx.api.expansion.ExpansionDescription;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;
import com.SirBlobman.combatlogx.api.object.TagReason;
import com.SirBlobman.combatlogx.api.object.TagType;
import com.SirBlobman.combatlogx.api.object.UntagReason;
import com.SirBlobman.combatlogx.manager.CombatManager;

public class CommandCombatLogX extends Command {
    private final CombatPlugin plugin;
    public CommandCombatLogX(CombatPlugin plugin) {
        super(plugin, "combatlogx");
        this.plugin = plugin;
    }

    @Override
    public LanguageManager getLanguageManager() {
        return this.plugin.getLanguageManager();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) {
            List<String> valueList = Arrays.asList("help", "reload", "version", "tag", "toggle", "untag", "about");
            return getMatching(valueList, args[0]);
        }

        if(args.length == 2) {
            String sub = args[0].toLowerCase();
            if(sub.equals("about")) {
                ExpansionManager expansionManager = this.plugin.getExpansionManager();
                List<Expansion> expansionList = expansionManager.getAllExpansions();
                List<String> valueList = expansionList.stream().map(Expansion::getName).collect(Collectors.toList());
                return getMatching(valueList, args[1]);
            }

            if(sub.equals("toggle")) {
                List<String> valueList = Arrays.asList("bossbar", "actionbar", "scoreboard");
                return getMatching(valueList, args[1]);
            }

            if(sub.equals("tag") || sub.equals("untag")) {
                Set<String> valueSet = getOnlinePlayerNames();
                return getMatching(valueSet, args[1]);
            }
        }

        return Collections.emptyList();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(args.length < 1) return false;
        String[] newArgs = (args.length < 2 ? new String[0] : Arrays.copyOfRange(args, 1, args.length));

        String sub = args[0].toLowerCase();
        switch(sub) {
            case "about":
                return aboutCommand(sender, newArgs);

            case "help":
            case "?":
                return helpCommand(sender);

            case "reload":
            case "reloadconfig":
            case "config":
                return reloadCommand(sender);

            case "tag":
            case "add":
            case "forcetag":
                return tagCommand(sender, newArgs);

            case "toggle":
                return toggleCommand(sender, newArgs);

            case "untag":
            case "remove":
            case "forceuntag":
                return untagCommand(sender, newArgs);

            case "version":
            case "ver":
                return versionCommand(sender);

            default: break;
        }

        return false;
    }

    private boolean aboutCommand(CommandSender sender, String[] args) {
        if(!checkPermission(sender, "combatlogx.command.combatlogx.about", true)) return true;
        if(args.length < 1) return false;

        LanguageManager languageManager = getLanguageManager();
        ExpansionManager expansionManager = this.plugin.getExpansionManager();

        String expansionName = args[0];
        Optional<Expansion> optionalExpansion = expansionManager.getExpansion(expansionName);
        if(!optionalExpansion.isPresent()) {
            Replacer replacer = message -> message.replace("{target}", expansionName);
            languageManager.sendMessage(sender, "error.unknown-expansion", replacer, true);
            return true;
        }

        Expansion expansion = optionalExpansion.get();
        String name = expansion.getName();
        String displayName = expansion.getPrefix();
        State state = expansion.getState();

        ExpansionDescription expansionDescription = expansion.getDescription();
        String description = expansionDescription.getDescription();
        List<String> authorList = expansionDescription.getAuthors();
        String authorsString = String.join(", ", authorList);
        String version = expansionDescription.getVersion();

        List<String> messageList = new ArrayList<>();
        messageList.add("&f&lExpansion Information for &a" + name + "&f&l:");
        messageList.add("&f&lDisplay Name: &7" + displayName);
        messageList.add("&f&lVersion: &7" + version);
        messageList.add("&f&lState: &7" + state);
        messageList.add("&f");
        messageList.add("&f&lDescription: &7" + description);
        messageList.add("&f&lAuthors: &7" + authorsString);
        messageList = MessageUtility.colorList(messageList);

        messageList.forEach(sender::sendMessage);
        return true;
    }

    private boolean helpCommand(CommandSender sender) {
        if(!checkPermission(sender, "combatlogx.command.combatlogx.help", true)) return true;
        LanguageManager languageManager = getLanguageManager();
        languageManager.sendMessage(sender, "command.combatlogx.help-message-list", null, true);
        return true;
    }

    private boolean reloadCommand(CommandSender sender) {
        if(!checkPermission(sender, "combatlogx.command.combatlogx.reload", true)) return true;
        ConfigurationManager configurationManager = this.plugin.getConfigurationManager();
        configurationManager.reload("config.yml");
        configurationManager.reload("commands.yml");
        configurationManager.reload("force-field.yml");
        configurationManager.reload("punish.yml");

        configurationManager.reload("language.yml");
        configurationManager.reload("language/en_us.lang.yml");

        ExpansionManager expansionManager = this.plugin.getExpansionManager();
        expansionManager.reloadConfigs();

        LanguageManager languageManager = getLanguageManager();
        languageManager.sendMessage(sender, "command.combatlogx.reload-success", null, true);
        return true;
    }

    private boolean tagCommand(CommandSender sender, String[] args) {
        if(!checkPermission(sender, "combatlogx.command.combatlogx.tag", true)) return true;
        if(args.length < 1) return false;

        Player target = findTarget(sender, args[0]);
        if(target == null) return true;

        String targetName = target.getName();
        Replacer replacer = message -> message.replace("{target}", targetName);

        CombatManager combatManager = this.plugin.getCombatManager();
        boolean successfulTag = combatManager.tag(target, null, TagType.UNKNOWN, TagReason.UNKNOWN);
        String messagePath = ("command.combatlogx." + (successfulTag ? "tag-player" : "tag-failure"));

        LanguageManager languageManager = getLanguageManager();
        languageManager.sendMessage(sender, messagePath, replacer, true);
        return true;
    }

    private boolean untagCommand(CommandSender sender, String[] args) {
        if(!checkPermission(sender, "combatlogx.command.combatlogx.untag", true)) return true;
        if(args.length < 1) return false;

        Player target = findTarget(sender, args[0]);
        if(target == null) return true;

        String targetName = target.getName();
        Replacer replacer = message -> message.replace("{target}", targetName);
        LanguageManager languageManager = getLanguageManager();

        CombatManager combatManager = this.plugin.getCombatManager();
        if(!combatManager.isInCombat(target)) {
            languageManager.sendMessage(sender, "error.target-not-in-combat", replacer, true);
            return true;
        }

        combatManager.untag(target, UntagReason.EXPIRE);
        languageManager.sendMessage(sender, "command.combatlogx.untag-player", replacer, true);
        return true;
    }

    private boolean versionCommand(CommandSender sender) {
        if(!checkPermission(sender, "combatlogx.command.combatlogx.untag", true)) return true;
        List<String> messageList = new ArrayList<>();
        messageList.add("&f");
        messageList.add("&f&lServer Version: &7" + Bukkit.getVersion());
        messageList.add("&f&lBukkit Version: &7" + Bukkit.getBukkitVersion());
        messageList.add("Minecraft Version: &7" + VersionUtility.getMinecraftVersion());
        messageList.add("NMS Version: &7" + VersionUtility.getNetMinecraftServerVersion());
        messageList.add("&f");
        messageList.add("&f&lCombatLogX by SirBlobman");

        UpdateChecker updateChecker = this.plugin.getUpdateChecker();
        String pluginVersion = updateChecker.getPluginVersion();
        String spigotVersion = updateChecker.getSpigotVersion();
        messageList.add("&f&lPlugin Version: &7" + pluginVersion);
        messageList.add("&f&lSpigot Version: &7" + spigotVersion);
        messageList.add("&f");

        ExpansionManager expansionManager = this.plugin.getExpansionManager();
        List<Expansion> enabledExpansionList = expansionManager.getEnabledExpansions();
        messageList.add("&f&lEnabled Expansions (&7" + enabledExpansionList.size() + "&f&l):");

        for(Expansion expansion : enabledExpansionList) {
            String expansionName = expansion.getName();
            String messageLine = ("&f&l - &7" + expansionName);
            messageList.add(messageLine);
        }

        List<String> finalMessage = MessageUtility.colorList(messageList);
        finalMessage.forEach(sender::sendMessage);
        return true;
    }

    private boolean toggleCommand(CommandSender sender, String[] args) {
        if(args.length < 1) return false;
        if(!checkPermission(sender, "combatlogx.command.combatlogx.toggle", true)) return true;
        if(!(sender instanceof Player)) {
            sendMessageOrDefault(sender, "error.player-only", "", null, true);
            return true;
        }

        Player player = (Player) sender;
        String sub = args[0].toLowerCase();
        if(sub.equals("bossbar")) {
            toggleValue(player, "bossbar");
            return true;
        }

        if(sub.equals("actionbar")) {
            toggleValue(player, "actionbar");
            return true;
        }

        if(sub.equals("scoreboard")) {
            toggleValue(player, "scoreboard");
            return true;
        }

        return false;
    }

    private void toggleValue(Player player, String value) {
        PlayerDataManager playerDataManager = this.plugin.getPlayerDataManager();
        LanguageManager languageManager = getLanguageManager();

        YamlConfiguration configuration = playerDataManager.get(player);
        boolean currentValue = configuration.getBoolean(value, true);
        configuration.set(value, !currentValue);
        playerDataManager.save(player);

        boolean status = configuration.getBoolean(value, true);
        String statusPath = ("placeholder.toggle." + (status ? "enabled" : "disabled"));
        String statusString = languageManager.getMessageColored(player, statusPath);
        Replacer replacer = message -> message.replace("{status}", statusString);

        String messagePath = ("expansion.toggle-" + value);
        languageManager.sendMessage(player, messagePath, replacer, true);
    }
}