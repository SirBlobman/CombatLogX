package com.SirBlobman.combatlogx.command;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import com.SirBlobman.combatlogx.CombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.api.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.Expansion.State;
import com.SirBlobman.combatlogx.api.expansion.ExpansionDescription;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;
import com.SirBlobman.combatlogx.api.shaded.nms.VersionUtil;
import com.SirBlobman.combatlogx.api.shaded.utility.MessageUtil;
import com.SirBlobman.combatlogx.api.shaded.utility.Util;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.SirBlobman.combatlogx.api.utility.ILanguageManager;
import com.SirBlobman.combatlogx.manager.LanguageManager;
import com.SirBlobman.combatlogx.update.UpdateChecker;

public class CommandCombatLogX implements TabExecutor {
    private final CombatLogX plugin;
    public CommandCombatLogX(CombatLogX plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 1) {
            String sub = args[0].toLowerCase();
            List<String> subCommandList = Util.newList("help", "reload", "version", "tag", "untag");
            return getMatching(subCommandList, sub);
        }
        
        if(args.length == 2) {
            String sub = args[0].toLowerCase();
            if(sub.equals("version") || sub.equals("about") || sub.equals("ver")) {
                List<String> expansionNameList = this.plugin.getExpansionManager().getAllExpansions().stream()
                        .map(expansion -> expansion.getDescription().getName()).collect(Collectors.toList());
                return getMatching(expansionNameList, args[1]);
            }
        }

        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1) return false;

        String sub = args[0].toLowerCase();
        String[] newArgs = args.length < 2 ? new String[0] : Arrays.copyOfRange(args, 1, args.length);
        switch(sub) {
            case "help": return helpCommand(sender);
            
            case "about":
            case "version":
            case "ver":
                return versionCommand(sender, newArgs);

            case "reload":
            case "reloadconfig":
            case "config":
                return reloadConfigCommand(sender);

            case "tag":
            case "add":
            case "forcetag":
                return tagPlayerCommand(sender, newArgs);

            case "untag":
            case "forceuntag":
            case "remove":
                return untagPlayerCommand(sender, newArgs);

            default: return false;
        }
    }
    
    private List<String> getMatching(List<String> valueList, String arg) {
        if(valueList == null || valueList.isEmpty() || arg == null) return Collections.emptyList();
        
        String lowerArg = arg.toLowerCase();
        List<String> matchList = new ArrayList<>();
        
        for(String value : valueList) {
            String lowerValue = value.toLowerCase();
            if(!lowerValue.startsWith(lowerArg)) continue;
            
            matchList.add(value);
        }
        
        return matchList;
    }

    private boolean checkNoPermission(CommandSender sender, String permission) {
        if(sender.hasPermission(permission)) return false;
        
        ILanguageManager languageManager = this.plugin.getLanguageManager();
        String message = languageManager.getMessageColoredWithPrefix("errors.no-permission").replace("{permission}", permission);
        languageManager.sendMessage(sender, message);
        return true;
    }

    private Player getTarget(CommandSender sender, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if(target == null) {
            ILanguageManager languageManager = this.plugin.getLanguageManager();
            String message = languageManager.getMessageColoredWithPrefix("errors.invalid-target").replace("{target}", targetName);
            languageManager.sendMessage(sender, message);
            return null;
        }
        return target;
    }

    private String[] colorMultiple(String... messages) {
        String[] colored = new String[messages.length];
        for(int i = 0; i < messages.length; i++) {
            String string = messages[i];
            colored[i] = MessageUtil.color(string);
        }
        return colored;
    }

    private boolean helpCommand(CommandSender sender) {
        if(checkNoPermission(sender, "combatlogx.command.combatlogx.help")) return true;
        
        ILanguageManager languageManager = this.plugin.getLanguageManager();
        String helpMessage = languageManager.getMessageColored("commands.combatlogx.help-message-list");

        String newLine = Pattern.quote("\n");
        String[] message = helpMessage.split(newLine);

        languageManager.sendMessage(sender, message);
        return true;
    }

    private boolean reloadConfigCommand(CommandSender sender) {
        if(checkNoPermission(sender, "combatlogx.command.combatlogx.reload")) return true;

        try {
            this.plugin.reloadConfig("config.yml");
            this.plugin.reloadConfig("language.yml");
            
            ExpansionManager expansionManager = this.plugin.getExpansionManager();
            expansionManager.reloadExpansionConfigs();
        } catch(Exception ex) {
            String exMessage = ex.getMessage();
            String message1 = MessageUtil.color("&f&l[&6CombatLogX&f&l] &cAn error has occurred while loading your configurations. &cPlease check console for further details.");
            String message2 = MessageUtil.color("&f&l[&6CombatLogX&f&l[ &c&lError Message: &7" + exMessage);
    
            LanguageManager languageManager = this.plugin.getLanguageManager();
            languageManager.sendMessage(sender, message1, message2);
            
            Logger logger = this.plugin.getLogger();
            logger.log(Level.WARNING, "An error occurred while reloading the config files:", ex);
            return true;
        }
    
        LanguageManager languageManager = this.plugin.getLanguageManager();
        String message = languageManager.getMessageColoredWithPrefix("commands.combatlogx.reloaded");
        languageManager.sendMessage(sender, message);
        return true;
    }

    private boolean tagPlayerCommand(CommandSender sender, String[] args) {
        if(checkNoPermission(sender, "combatlogx.command.combatlogx.tag")) return true;
        if(args.length < 1) return false;

        String targetName = args[0];
        Player target = getTarget(sender, targetName);
        if(target == null) return true;
        targetName = target.getName();

        ICombatManager combatManager = this.plugin.getCombatManager();
        boolean isTagged = combatManager.tag(target, null, PlayerPreTagEvent.TagType.UNKNOWN, PlayerPreTagEvent.TagReason.UNKNOWN);
    
        LanguageManager languageManager = this.plugin.getLanguageManager();
        String messagePath = "commands.combatlogx." + (isTagged ? "tag-player" : "tag-player-fail");
        String message = languageManager.getMessageColoredWithPrefix(messagePath).replace("{target}", targetName);
        languageManager.sendMessage(sender, message);
        return true;
    }

    private boolean untagPlayerCommand(CommandSender sender, String[] args) {
        if(checkNoPermission(sender, "combatlogx.command.combatlogx.untag")) return true;
        if(args.length < 1) return false;

        String targetName = args[0];
        Player target = getTarget(sender, targetName);
        if(target == null) return true;
        targetName = target.getName();

        ICombatManager combatManager = this.plugin.getCombatManager();
        if(!combatManager.isInCombat(target)) {
            LanguageManager languageManager = this.plugin.getLanguageManager();
            String message = languageManager.getMessageColoredWithPrefix("errors.target-not-in-combat").replace("{target}", targetName);
            languageManager.sendMessage(sender, message);
            return true;
        }
        combatManager.untag(target, PlayerUntagEvent.UntagReason.EXPIRE);
        
        LanguageManager languageManager = this.plugin.getLanguageManager();
        String message = languageManager.getMessageColoredWithPrefix("commands.combatlogx.untag-player").replace("{target}", targetName);
        languageManager.sendMessage(sender, message);
        return true;
    }

    private boolean versionCommand(CommandSender sender, String[] args) {
        if(checkNoPermission(sender, "combatlogx.command.combatlogx.version")) return true;

        if(args.length < 1) {
            LanguageManager languageManager = this.plugin.getLanguageManager();
            languageManager.sendMessage(sender, "Getting version information for CombatLogX...");
    
            Runnable task = () -> checkVersion(sender);
            BukkitScheduler scheduler = Bukkit.getScheduler();
            scheduler.runTaskAsynchronously(this.plugin, task);
            return true;
        }
    
        String expansionName = args[0];
        ExpansionManager expansionManager = this.plugin.getExpansionManager();
    
        Optional<Expansion> optionalExpansion = expansionManager.getExpansionByName(expansionName);
        if(!optionalExpansion.isPresent()) {
            sender.sendMessage("Could not find an expansion with the name '" + expansionName + "'.");
            return true;
        }
        
        Expansion expansion = optionalExpansion.get();
        State state = expansion.getState();
        ExpansionDescription description = expansion.getDescription();
        
        expansionName = description.getName();
        String displayName = description.getDisplayName();
        String version = description.getVersion();
        String descriptionText = description.getDescription();
        List<String> authorList = description.getAuthors();
        String authorString = String.join(", ", authorList);
        
        List<String> messageList = new ArrayList<>();
        messageList.add(MessageUtil.color("&6&lExpansion Information for &e" + expansionName));
        messageList.add(MessageUtil.color("&6&lState:&e " + state.name()));
        messageList.add(MessageUtil.color("&6&lVersion:&e " + version));
        
        if(displayName != null) messageList.add(MessageUtil.color("&6&lDisplay Name:&e " + displayName));
        if(descriptionText != null) messageList.add(MessageUtil.color("&6&lDescription:&e " + descriptionText));
        if(!authorList.isEmpty()) messageList.add(MessageUtil.color("&6&lAuthors:&e " + authorString));
        
        String[] messageArray = messageList.toArray(new String[0]);
        LanguageManager languageManager = this.plugin.getLanguageManager();
        languageManager.sendMessage(sender, messageArray);
        return true;
    }
    
    private void checkVersion(CommandSender sender) {
        UpdateChecker updateChecker = this.plugin.getUpdateChecker();
        String pluginVersion = updateChecker.getPluginVersion();
        String spigotVersion = updateChecker.getSpigotVersion();
        
        LanguageManager languageManager = this.plugin.getLanguageManager();
        String[] messageArray = colorMultiple(
                "&f",
                "&f&lServer Version: &7" + Bukkit.getVersion(),
                "&f&lBukkit Version: &7" + Bukkit.getBukkitVersion(),
                "&f&lMinecraft Version: &7" + VersionUtil.getMinecraftVersion(),
                "&f&lNMS Version: &7" + VersionUtil.getNetMinecraftServerVersion(),
                "&f",
                "&f&lCombatLogX by SirBlobman",
                "&f&lInstalled Version: &7v" + pluginVersion,
                "&f&lLatest Version: &7v" + spigotVersion,
                "&f",
                "&7&oGetting expansion versions...",
                "&f"
        );
        languageManager.sendMessage(sender, messageArray);
        
        ExpansionManager expansionManager = this.plugin.getExpansionManager();
        List<Expansion> expansionList = expansionManager.getEnabledExpansions();
        
        if(expansionList.isEmpty()) {
            String message = MessageUtil.color("  &f&lYou do not have any expansions installed.");
            languageManager.sendMessage(sender, message);
            return;
        }

        for(Expansion expansion : expansionList) {
            ExpansionDescription description = expansion.getDescription();
            String name = description.getDisplayName();
            String version = description.getVersion();

            String message = MessageUtil.color("  &f&l" + name + " &7v" + version);
            languageManager.sendMessage(sender, message);
        }
    }
}