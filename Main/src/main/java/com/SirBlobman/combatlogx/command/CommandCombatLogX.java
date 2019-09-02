package com.SirBlobman.combatlogx.command;

import com.SirBlobman.api.nms.NMS_Handler;
import com.SirBlobman.combatlogx.CombatLogX;
import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.expansion.Expansions;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.SchedulerUtil;
import com.SirBlobman.combatlogx.utility.UpdateUtil;
import com.SirBlobman.combatlogx.utility.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

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
            List<String> valid = Util.newList("reload", "tag", "toggle", "untag", "version");
            return Util.getMatching(valid, sub);
        }

        if(args.length == 2) {
            if(sub.equals("tag") || sub.equals("untag")) return null;
            if(sub.equals("toggle")) {
                List<String> valid = Util.newList("bossbar", "actionbar", "scoreboard");
                return Util.getMatching(valid, sub);
            }
        }

        return Util.newList();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1) return false;

        String sub = args[0].toLowerCase();
        String[] newArgs = (args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0]);
        switch(sub) {
            case "help":
                return helpCommand(sender);

            case "reload":
            case "reloadconfig":
            case "config":
                return reloadConfigCommand(sender);

            case "tag":
            case "forcetag":
                return tagPlayerCommand(sender, newArgs);

            case "untag":
            case "forceuntag":
                return untagPlayerCommand(sender, newArgs);

            case "version": return versionCommand(sender);
            case "toggle": return toggleCommand(sender, newArgs);

            default: return false;
        }
    }

    private boolean checkNoPermission(CommandSender sender, String permission) {
        if(sender.hasPermission(permission)) return false;

        String message = ConfigLang.getWithPrefix("messages.commands.no permission").replace("{permission}", permission);
        Util.sendMessage(sender, message);
        return true;
    }

    private Player getTarget(CommandSender sender, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        if(target == null) {
            String message = ConfigLang.getWithPrefix("messages.commands.invalid target").replace("{target}", targetName);
            Util.sendMessage(sender, message);
            return null;
        }
        return target;
    }

    private boolean helpCommand(CommandSender sender) {
        if(checkNoPermission(sender, "combatlogx.command.combatlogx.help")) return true;

        String message = ConfigLang.get("messages.commands.combatlogx.help");
        Util.sendMessage(sender, message);
        return true;
    }

    private boolean reloadConfigCommand(CommandSender sender) {
        if(checkNoPermission(sender, "combatlogx.command.combatlogx.reload")) return true;

        ConfigOptions.load();
        ConfigLang.load();
        Expansions.reloadConfigs();;

        String message = ConfigLang.getWithPrefix("messages.commands.combatlogx.reloaded");
        Util.sendMessage(sender, message);
        return true;
    }

    private boolean tagPlayerCommand(CommandSender sender, String[] args) {
        if(checkNoPermission(sender, "combatlogx.command.combatlogx.tag")) return true;
        if(args.length < 1) return false;

        String targetName = args[0];
        Player target = getTarget(sender, targetName);
        if(target == null) return true;
        targetName = target.getName();

        CombatUtil.tag(target, null, PlayerTagEvent.TagType.UNKNOWN, PlayerTagEvent.TagReason.UNKNOWN);
        String message = ConfigLang.getWithPrefix("messages.commands.combatlogx.tag").replace("{target}", targetName);
        Util.sendMessage(sender, message);
        return true;
    }

    private boolean untagPlayerCommand(CommandSender sender, String[] args) {
        if(checkNoPermission(sender, "combatlogx.command.combatlogx.untag")) return true;
        if(args.length < 1) return false;

        String targetName = args[0];
        Player target = getTarget(sender, targetName);
        if(target == null) return true;
        targetName = target.getName();

        if(CombatUtil.isInCombat(target)) {
            CombatUtil.untag(target, PlayerUntagEvent.UntagReason.EXPIRE);
            String message = ConfigLang.getWithPrefix("messages.commands.combatlogx.untag").replace("{target}", targetName);
            Util.sendMessage(sender, message);
            return true;
        }

        String message = ConfigLang.getWithPrefix("messages.commands.combatlogx.not in combat");
        Util.sendMessage(sender, message);
        return true;
    }

    private boolean versionCommand(CommandSender sender) {
        if(checkNoPermission(sender, "combatlogx.command.combatlogx.version")) return true;
        Runnable task = () -> {
            String pluginVersion = UpdateUtil.getPluginVersion(this.plugin);
            String spigotVersion = UpdateUtil.getSpigotVersion();

            String[] message = Util.color(
                    "&f",
                    "&f&lFull Version: &7" + Bukkit.getVersion(),
                    "&f&lBukkit Version: &7" + Bukkit.getBukkitVersion(),
                    "&f&lMinecraft Version: &7" + NMS_Handler.getMinecraftVersion(),
                    "&f&lNMS Version: &7" + NMS_Handler.getNetMinecraftServerVersion(),
                    "&f",
                    "&f&lCombatLogX by SirBlobman",
                    "&f",
                    "&f&lLatest Spigot Version: &7v" + spigotVersion,
                    "&f&lInstalled Version: &7v" + pluginVersion,
                    "&f",
                    "&7&oGetting expansion versions...",
                    "&f"
            );
            Util.sendMessage(sender, message);

            List<CLXExpansion> expansionList = Expansions.getExpansions();
            if(expansionList.isEmpty()) {
                String message2 = Util.color("  &f&lYou do not have any expansions.");
                Util.sendMessage(sender, message2);
                return;
            }

            for(CLXExpansion expansion : expansionList) {
                String expansionName = expansion.getName();
                String expansionVersion = expansion.getVersion();
                String message3 = Util.color("  &f&l" + expansionName + " &7v" + expansionVersion);
                Util.sendMessage(sender, message3);
            }
        };

        Util.sendMessage(sender, "Getting version information for CombatLogX...");
        SchedulerUtil.runNowAsync(task);
        return true;
    }

    private boolean toggleCommand(CommandSender sender, String[] args) {
        if(checkNoPermission(sender, "combatlogx.expansion.notifier.command.toggle")) return true;
        if(args.length < 1) return false;

        if(!Expansions.isEnabled("Notifier")) {
            Util.sendMessage(sender, "Notifier is not installed.");
            return true;
        }

        if(!(sender instanceof Player)) {
            Util.sendMessage(sender, "Only players can toggle stuff.");
            return true;
        }

        Player player = (Player) sender;
        String toggleType = args[1].toLowerCase();
        switch(toggleType) {
            case "score":
            case "scoreboard":
            case "sidebar":
                return toggleScoreboard(player);

            case "boss":
            case "bossbar":
            case "topbar":
                return toggleBossBar(player);

            case "action":
            case "actionbar":
            case "bottombar":
            case "itembar":
                return toggleActionBar(player);

            default: return false;
        }
    }

    private boolean toggleScoreboard(Player player) {
        try {
            Class<?> class_ScoreboardUtil = Class.forName("com.SirBlobman.expansion.notifier.utility.ScoreboardUtil");
            Method method_toggle = class_ScoreboardUtil.getDeclaredMethod("toggle", Player.class);

            boolean enabled = (boolean) method_toggle.invoke(null, player);
            Util.sendMessage(player, "Scoreboard: " + (enabled ? "ON" : "OFF"));
            return true;
        } catch(ClassNotFoundException ex) {
            Util.sendMessage(player, "Notifier is not installed!");
            return true;
        } catch(ReflectiveOperationException ex) {
            Util.sendMessage(player, "Failed to toggle scoreboard, please contact an admin.");
            ex.printStackTrace();
            return true;
        }
    }

    private boolean toggleBossBar(Player player) {
        try {
            Class<?> class_BossBarUtil = Class.forName("com.SirBlobman.expansion.notifier.utility.BossBarUtil");
            Method method_toggle = class_BossBarUtil.getDeclaredMethod("toggle", Player.class);

            boolean enabled = (boolean) method_toggle.invoke(null, player);
            Util.sendMessage(player, "BossBar: " + (enabled ? "ON" : "OFF"));
            return true;
        } catch(ClassNotFoundException ex) {
            Util.sendMessage(player, "Notifier is not installed!");
            return true;
        } catch(ReflectiveOperationException ex) {
            Util.sendMessage(player, "Failed to toggle bossbar, please contact an admin.");
            ex.printStackTrace();
            return true;
        }
    }

    private boolean toggleActionBar(Player player) {
        try {
            Class<?> class_ActionBarUtil = Class.forName("com.SirBlobman.expansion.notifier.utility.ActionBarUtil");
            Method method_toggle = class_ActionBarUtil.getDeclaredMethod("toggle", Player.class);

            boolean enabled = (boolean) method_toggle.invoke(null, player);
            Util.sendMessage(player, "ActionBar: " + (enabled ? "ON" : "OFF"));
            return true;
        } catch(ClassNotFoundException ex) {
            Util.sendMessage(player, "Notifier is not installed!");
            return true;
        } catch(ReflectiveOperationException ex) {
            Util.sendMessage(player, "Failed to toggle actionbar, please contact an admin.");
            ex.printStackTrace();
            return true;
        }
    }
}