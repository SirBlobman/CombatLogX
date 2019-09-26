package com.SirBlobman.combatlogx.command;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.SirBlobman.api.nms.NMS_Handler;
import com.SirBlobman.api.utility.Util;
import com.SirBlobman.combatlogx.CombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.api.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;
import com.SirBlobman.combatlogx.utility.UpdateChecker;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public class CommandCombatLogX implements TabExecutor {
    private final CombatLogX plugin;
    public CommandCombatLogX(CombatLogX plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1) return null;

        String sub = args[0].toLowerCase();
        if(args.length == 1) {
            List<String> subCommandList = Util.newList("help", "reload", "version", "tag", "untag");
            return subCommandList.stream().filter(sub::startsWith).collect(Collectors.toList());
        }

        return Util.newList();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1) return false;

        String sub = args[0].toLowerCase();
        String[] newArgs = args.length < 2 ? new String[0] : Arrays.copyOfRange(args, 1, args.length);
        switch(sub) {
            case "help": return helpCommand(sender);
            case "version": return versionCommand(sender);

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

    private boolean checkNoPermission(CommandSender sender, String permission) {
        if(sender.hasPermission(permission)) return false;

        String message = this.plugin.getLanguageMessageColoredWithPrefix("errors.no-permission").replace("{permission}", permission);
        this.plugin.sendMessage(sender, message);
        return true;
    }

    private Player getTarget(CommandSender sender, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if(target == null) {
            String message = this.plugin.getLanguageMessageColoredWithPrefix("errors.invalid-target").replace("{target}", targetName);
            this.plugin.sendMessage(sender, message);
            return null;
        }
        return target;
    }

    private String[] colorMultiple(String... messages) {
        String[] colored = new String[messages.length];
        for(int i = 0; i < messages.length; i++) {
            String string = messages[i];
            colored[i] = Util.color(string);
        }
        return colored;
    }

    private boolean helpCommand(CommandSender sender) {
        if(checkNoPermission(sender, "combatlogx.command.combatlogx.help")) return true;

        String helpMessage = this.plugin.getLanguageMessageColored("commands.combatlogx.help-message-list");
        String[] message = helpMessage.split(Pattern.quote("\n"));

        this.plugin.sendMessage(sender, message);
        return true;
    }

    private boolean reloadConfigCommand(CommandSender sender) {
        if(checkNoPermission(sender, "combatlogx.command.combatlogx.reload")) return true;

        try {
            this.plugin.reloadConfig("config.yml");
            this.plugin.reloadConfig("language.yml");
            ExpansionManager.reloadConfigs();
        } catch(Exception ex) {
            String message1 = Util.color("&f&l[&6CombatLogX&f&l] &cAn error has occurred while loading your configurations. &cPlease check console for further details.");
            String message2 = Util.color("&f&l[&6CombatLogX&f&l[ &c&lError Message: &7" + ex.getMessage());

            this.plugin.sendMessage(sender, message1, message2);
            ex.printStackTrace();
            return true;
        }

        String message = this.plugin.getLanguageMessageColoredWithPrefix("commands.combatlogx.reloaded");
        this.plugin.sendMessage(sender, message);
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

        String messagePath = "commands.combatlogx." + (isTagged ? "tag-player" : "tag-player-fail");
        String message = this.plugin.getLanguageMessageColoredWithPrefix(messagePath).replace("{target}", targetName);

        this.plugin.sendMessage(sender, message);
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
            String message = this.plugin.getLanguageMessageColoredWithPrefix("errors.target-not-in-combat").replace("{target}", targetName);
            this.plugin.sendMessage(sender, message);
            return true;
        }

        combatManager.untag(target, PlayerUntagEvent.UntagReason.EXPIRE);
        String message = this.plugin.getLanguageMessageColoredWithPrefix("commands.combatlogx.untag-player").replace("{target}", targetName);

        this.plugin.sendMessage(sender, message);
        return true;
    }

    private boolean versionCommand(CommandSender sender) {
        if(checkNoPermission(sender, "combatlogx.command.combatlogx.version")) return true;

        Runnable task = () -> checkVersion(sender);
        this.plugin.sendMessage(sender, "Getting version information for CombatLogX...");

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskAsynchronously(this.plugin, task);
        return true;
    }

    private void checkVersion(CommandSender sender) {
        String pluginVersion = UpdateChecker.getPluginVersion(this.plugin);
        String spigotVersion = UpdateChecker.getSpigotVersion();

        String[] message1 = colorMultiple(
                "&f",
                "&f&lServer Version: &7" + Bukkit.getVersion(),
                "&f&lBukkit Version: &7" + Bukkit.getBukkitVersion(),
                "&f&lMinecraft Version: &7" + NMS_Handler.getMinecraftVersion(),
                "&f&lNMS Version: &7" + NMS_Handler.getNetMinecraftServerVersion(),
                "&f",
                "&f&lCombatLogX by SirBlobman",
                "&f&lLatest Version: &7v" + spigotVersion,
                "&f&lInstalled Version: &7v" + pluginVersion,
                "&f",
                "&7&oGetting expansion versions...",
                "&f"
        );
        this.plugin.sendMessage(sender, message1);

        List<Expansion> expansionList = ExpansionManager.getExpansions();
        if(expansionList.isEmpty()) {
            String message2 = Util.color("  &f&lYou do not have any expansions installed.");
            this.plugin.sendMessage(sender, message2);
            return;
        }

        for(Expansion expansion : expansionList) {
            String expName = expansion.getName();
            String expVersion = expansion.getVersion();

            String message3 = Util.color("  &f&l" + expName + " &7v" + expVersion);
            this.plugin.sendMessage(sender, message3);
        }
    }
}