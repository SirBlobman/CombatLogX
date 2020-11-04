package combatlogx.expansion.newbie.helper.command;

import java.util.*;

import com.SirBlobman.api.command.Command;
import com.SirBlobman.api.language.LanguageManager;
import com.SirBlobman.api.language.Replacer;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;
import combatlogx.expansion.newbie.helper.manager.PVPManager;
import combatlogx.expansion.newbie.helper.manager.ProtectionManager;

public final class CommandTogglePVP extends Command {
    private final NewbieHelperExpansion expansion;
    public CommandTogglePVP(NewbieHelperExpansion expansion) {
        super(expansion.getPlugin().getPlugin(), "togglepvp");
        this.expansion = expansion;
    }

    @Override
    public LanguageManager getLanguageManager() {
        return this.expansion.getPlugin().getLanguageManager();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) {
            List<String> valueList = new ArrayList<>();
            Collections.addAll(valueList, "check", "on", "off");
            if(sender.hasPermission("combatlogx.command.togglepvp.admin")) valueList.add("admin");
            return getMatching(valueList, args[0]);
        }

        if(args.length == 2) {
            String sub = args[0].toLowerCase();
            if(sub.equals("check")) {
                Set<String> valueSet = getOnlinePlayerNames();
                return getMatching(valueSet, args[1]);
            }

            if(sub.equals("admin")) {
                List<String> valueList = Arrays.asList("on", "off");
                return getMatching(valueList, args[1]);
            }
        }

        if(args.length == 3 && args[0].toLowerCase().equals("admin")) {
            Set<String> valueSet = getOnlinePlayerNames();
            return getMatching(valueSet, args[2]);
        }

        return Collections.emptyList();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(args.length < 1) {
            if(sender instanceof Player) {
                Player player = (Player) sender;
                PVPManager pvpManager = this.expansion.getPVPManager();
                boolean pvpStatus = pvpManager.isDisabled(player);
                return (pvpStatus ? enableCommand(sender) : disableCommand(sender));
            }

            return false;
        }

        String sub = args[0].toLowerCase();
        String[] newArgs = (args.length < 2 ? new String[0] : Arrays.copyOfRange(args, 1, args.length));
        switch(sub) {
            case "check": return checkCommand(sender, newArgs);
            case "on": return enableCommand(sender);
            case "off": return disableCommand(sender);
            case "admin": return adminCommand(sender, newArgs);
            default: break;
        }

        return false;
    }

    private boolean checkCommand(CommandSender sender, String[] args) {
        if(args.length < 1) return false;
        Player target = findTarget(sender, args[0]);
        if(target == null) return true;
        String targetName = target.getName();

        LanguageManager languageManager = getLanguageManager();
        ProtectionManager protectionManager = this.expansion.getProtectionManager();
        PVPManager pvpManager = this.expansion.getPVPManager();
        boolean isProtected = protectionManager.isProtected(target);
        boolean pvpEnabled = !pvpManager.isDisabled(target);

        String protectedStatus = languageManager.getMessage(sender, "placeholder.toggle." + (isProtected ? "enabled" : "disabled"));
        String pvpStatus = languageManager.getMessage(sender, "placeholder.toggle." + (pvpEnabled ? "enabled" : "disabled"));
        Replacer replacer = message -> message.replace("{target}", targetName).replace("{protected}", protectedStatus).replace("{pvp}", pvpStatus);

        languageManager.sendMessage(sender, "expansion.newbie-helper.check-format", replacer, true);
        return true;
    }

    private boolean enableCommand(CommandSender sender) {
        if(!(sender instanceof Player)) {
            sendMessageOrDefault(sender, "error.player-only", "", null, true);
            return true;
        }

        Player player = (Player) sender;
        PVPManager pvpManager = this.expansion.getPVPManager();
        pvpManager.setPVP(player, true);

        sendToggleMessage(player);
        return true;
    }

    private boolean disableCommand(CommandSender sender) {
        if(!(sender instanceof Player)) {
            sendMessageOrDefault(sender, "error.player-only", "", null, true);
            return true;
        }

        Player player = (Player) sender;
        PVPManager pvpManager = this.expansion.getPVPManager();
        pvpManager.setPVP(player, false);

        sendToggleMessage(player);
        return true;
    }

    private boolean adminCommand(CommandSender sender, String[] args) {
        if(args.length < 1) return false;
        String sub = args[0].toLowerCase();
        String[] newArgs = (args.length < 2 ? new String[0] : Arrays.copyOfRange(args, 1, args.length));
        switch(sub) {
            case "on": return adminEnableCommand(sender, newArgs);
            case "off": return adminDisableCommand(sender, newArgs);
            default: break;
        }

        return false;
    }

    private boolean adminEnableCommand(CommandSender sender, String[] args) {
        if(args.length < 1) return false;
        Player target = findTarget(sender, args[0]);
        if(target == null) return true;

        PVPManager pvpManager = this.expansion.getPVPManager();
        pvpManager.setPVP(target, true);

        sendAdminToggleMessage(sender, target);
        return true;
    }

    private boolean adminDisableCommand(CommandSender sender, String[] args) {
        if(args.length < 1) return false;
        Player target = findTarget(sender, args[0]);
        if(target == null) return true;

        PVPManager pvpManager = this.expansion.getPVPManager();
        pvpManager.setPVP(target, false);

        sendAdminToggleMessage(sender, target);
        return true;
    }

    private void sendToggleMessage(Player player) {
        LanguageManager languageManager = getLanguageManager();
        PVPManager pvpManager = this.expansion.getPVPManager();
        boolean pvpEnabled = !pvpManager.isDisabled(player);

        String pvpStatus = languageManager.getMessage(player, "placeholder.toggle." + (pvpEnabled ? "enabled" : "disabled"));
        Replacer replacer = message -> message.replace("{status}", pvpStatus);
        languageManager.sendMessage(player, "expansion.newbie-helper.togglepvp.self", replacer, true);
    }

    private void sendAdminToggleMessage(CommandSender sender, Player target) {
        LanguageManager languageManager = getLanguageManager();
        PVPManager pvpManager = this.expansion.getPVPManager();
        boolean pvpEnabled = !pvpManager.isDisabled(target);

        String targetName = target.getName();
        String pvpStatus = languageManager.getMessage(sender, "placeholder.toggle." + (pvpEnabled ? "enabled" : "disabled"));
        Replacer replacer = message -> message.replace("{target}", targetName).replace("{status}", pvpStatus);
        languageManager.sendMessage(sender, "expansion.newbie-helper.togglepvp.admin", replacer, true);
    }
}