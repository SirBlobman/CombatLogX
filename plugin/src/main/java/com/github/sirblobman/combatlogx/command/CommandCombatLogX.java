package com.github.sirblobman.combatlogx.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.core.CorePlugin;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.Replacer;
import com.github.sirblobman.api.update.UpdateManager;
import com.github.sirblobman.api.utility.MessageUtility;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.ICombatManager;
import com.github.sirblobman.combatlogx.api.command.CombatLogCommand;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.Expansion.State;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionDescription;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;
import com.github.sirblobman.combatlogx.api.object.UntagReason;

import org.jetbrains.annotations.NotNull;

public final class CommandCombatLogX extends CombatLogCommand {
    public CommandCombatLogX(ICombatLogX plugin) {
        super(plugin, "combatlogx");
    }

    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) {
            List<String> valueList = Arrays.asList("help", "reload", "version", "tag", "toggle", "untag", "about");
            return getMatching(valueList, args[0]);
        }

        if(args.length == 2) {
            String sub = args[0].toLowerCase();
            if(sub.equals("about")) {
                ICombatLogX plugin = getCombatLogX();
                ExpansionManager expansionManager = plugin.getExpansionManager();
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
    protected boolean execute(CommandSender sender, String[] args) {
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

        String expansionName = args[0];
        ICombatLogX plugin = getCombatLogX();
        ExpansionManager expansionManager = plugin.getExpansionManager();
        Optional<Expansion> optionalExpansion = expansionManager.getExpansion(expansionName);

        if(!optionalExpansion.isPresent()) {
            Replacer replacer = message -> message.replace("{target}", expansionName);
            sendMessageWithPrefix(sender, "error.unknown-expansion", replacer, true);
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

        ICombatLogX plugin = getCombatLogX();
        ConfigurationManager configurationManager = plugin.getConfigurationManager();
        configurationManager.reload("config.yml");
        configurationManager.reload("commands.yml");
        configurationManager.reload("punish.yml");
        configurationManager.reload("language.yml");

        LanguageManager languageManager = getLanguageManager();
        languageManager.reloadLanguages();

        ExpansionManager expansionManager = plugin.getExpansionManager();
        expansionManager.reloadConfigs();

        sendMessageWithPrefix(sender, "command.combatlogx.reload-success", null, true);
        return true;
    }

    private boolean tagCommand(CommandSender sender, String[] args) {
        if(!checkPermission(sender, "combatlogx.command.combatlogx.tag", true)) return true;
        if(args.length < 1) return false;

        Player target = findTarget(sender, args[0]);
        if(target == null) return true;

        String targetName = target.getName();
        Replacer replacer = message -> message.replace("{target}", targetName);

        ICombatLogX plugin = getCombatLogX();
        ICombatManager combatManager = plugin.getCombatManager();
        boolean successfulTag = combatManager.tag(target, null, TagType.UNKNOWN, TagReason.UNKNOWN);
        String messagePath = ("command.combatlogx." + (successfulTag ? "tag-player" : "tag-failure"));

        sendMessageWithPrefix(sender, messagePath, replacer, true);
        return true;
    }

    private boolean untagCommand(CommandSender sender, String[] args) {
        if(!checkPermission(sender, "combatlogx.command.combatlogx.untag", true)) return true;
        if(args.length < 1) return false;

        Player target = findTarget(sender, args[0]);
        if(target == null) return true;

        String targetName = target.getName();
        Replacer replacer = message -> message.replace("{target}", targetName);

        ICombatLogX plugin = getCombatLogX();
        ICombatManager combatManager = plugin.getCombatManager();
        if(!combatManager.isInCombat(target)) {
            sendMessageWithPrefix(sender, "error.target-not-in-combat", replacer, true);
            return true;
        }

        combatManager.untag(target, UntagReason.EXPIRE);
        sendMessageWithPrefix(sender, "command.combatlogx.untag-player", replacer, true);
        return true;
    }

    private boolean versionCommand(CommandSender sender) {
        if(!checkPermission(sender, "combatlogx.command.combatlogx.version", true)) return true;
        List<String> messageList = new ArrayList<>();
        messageList.add("&f");

        try {
            String javaVersion = System.getProperty("java.version");
            String javaVendor = System.getProperty("java.vendor");
            messageList.add("&f&lJava Version: &7" + javaVersion + " (" + javaVendor + ")");
        } catch(Exception ex) {
            messageList.add("&f&lJava Version: &7Unknown");
        }

        messageList.add("&f&lServer Version: &7" + Bukkit.getVersion());
        messageList.add("&f&lBukkit Version: &7" + Bukkit.getBukkitVersion());
        messageList.add("&f&lMinecraft Version: &7" + VersionUtility.getMinecraftVersion());
        messageList.add("&f&lNMS Version: &7" + VersionUtility.getNetMinecraftServerVersion());
        messageList.add("&f");
        messageList.add("&f&lDependency Information:");

        PluginManager pluginManager = Bukkit.getPluginManager();
        PluginDescriptionFile description = getCombatLogX().getPlugin().getDescription();
        List<String> dependencyList = new ArrayList<>(description.getDepend());
        dependencyList.addAll(description.getSoftDepend());

        for(String dependencyName : dependencyList) {
            Plugin plugin = pluginManager.getPlugin(dependencyName);
            if(plugin == null) continue;

            PluginDescriptionFile pluginDescription = plugin.getDescription();
            String fullName = pluginDescription.getFullName();
            messageList.add("&f&l- &7" + fullName);
        }

        messageList.add("&f");
        messageList.add("&f&lCombatLogX by SirBlobman");
        String pluginVersion = getPluginVersion();
        String spigotVersion = getSpigotVersion();
        messageList.add("&f&lPlugin Version: &7" + pluginVersion);
        messageList.add("&f&lSpigot Version: &7" + spigotVersion);
        messageList.add("&f");

        ExpansionManager expansionManager = getCombatLogX().getExpansionManager();
        List<Expansion> enabledExpansionList = expansionManager.getEnabledExpansions();
        messageList.add("&f&lEnabled Expansions (&7" + enabledExpansionList.size() + "&f&l):");

        for(Expansion expansion : enabledExpansionList) {
            String expansionName = expansion.getName();
            String messageLine = ("&f&l - &7" + expansionName);
            messageList.add(messageLine);
        }

        List<String> finalMessage = MessageUtility.colorList(messageList);
        finalMessage.forEach(sender::sendMessage);

        if(!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(ChatColor.RED + "This command works better in the server console.");
        }

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
        PlayerDataManager playerDataManager = getCombatLogX().getPlayerDataManager();
        LanguageManager languageManager = getLanguageManager();

        YamlConfiguration configuration = playerDataManager.get(player);
        boolean currentValue = configuration.getBoolean(value, true);
        configuration.set(value, !currentValue);
        playerDataManager.save(player);

        boolean status = configuration.getBoolean(value, true);
        String statusPath = ("placeholder.toggle." + (status ? "enabled" : "disabled"));
        String statusString = languageManager.getMessage(player, statusPath, null, true);
        Replacer replacer = message -> message.replace("{status}", statusString);

        String messagePath = ("expansion.toggle-" + value);
        sendMessageWithPrefix(player, messagePath, replacer, true);
    }

    @NotNull
    private String getSpigotVersion() {
        CorePlugin corePlugin = JavaPlugin.getPlugin(CorePlugin.class);
        UpdateManager updateManager = corePlugin.getUpdateManager();
        String spigotVersion = updateManager.getSpigotVersion(getCombatLogX().getPlugin());
        return (spigotVersion == null ? "Update Checker Disabled!" : spigotVersion);
    }

    @NotNull
    public String getPluginVersion() {
        ICombatLogX plugin = getCombatLogX();
        PluginDescriptionFile description = plugin.getPlugin().getDescription();
        return description.getVersion();
    }
}
