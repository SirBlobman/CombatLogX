package combatlogx.expansion.newbie.helper.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.Replacer;
import com.github.sirblobman.combatlogx.api.command.CombatLogCommand;

import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;
import combatlogx.expansion.newbie.helper.manager.PVPManager;
import combatlogx.expansion.newbie.helper.manager.ProtectionManager;

public final class CommandTogglePVP extends CombatLogCommand {
    private final NewbieHelperExpansion expansion;
    private final Map<UUID, Long> commandCooldownMap;

    public CommandTogglePVP(NewbieHelperExpansion expansion) {
        super(expansion.getPlugin(), "togglepvp");
        this.expansion = expansion;
        this.commandCooldownMap = new HashMap<>();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) {
            List<String> valueList = new ArrayList<>();
            Collections.addAll(valueList, "check", "on", "off");
            if(sender.hasPermission("combatlogx.command.togglepvp.admin")) {
                valueList.add("admin");
            }
            return getMatching(args[0], valueList);
        }

        if(args.length == 2) {
            String sub = args[0].toLowerCase();
            if(sub.equals("check")) {
                Set<String> valueSet = getOnlinePlayerNames();
                return getMatching(args[1], valueSet);
            }

            if(sub.equals("admin")) {
                return getMatching(args[1], "on", "off");
            }
        }

        if(args.length == 3 && args[0].equalsIgnoreCase("admin")) {
            Set<String> valueSet = getOnlinePlayerNames();
            return getMatching(args[2], valueSet);
        }

        return Collections.emptyList();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(hasCooldown(player)) {
                sendCooldownMessage(player);
                return true;
            }

            addCooldown(player);
        }

        if(args.length == 0) return commandToggle(sender);
        String sub = args[0].toLowerCase(Locale.US);
        String[] newArgs = (args.length < 2 ? new String[0] : Arrays.copyOfRange(args, 1, args.length));

        switch(sub) {
            case "admin": return commandAdmin(sender, newArgs);
            case "check": return commandCheck(sender, newArgs);

            case "enable":
            case "on":
                return commandEnable(sender);

            case "off":
            case "disable":
                return commandDisable(sender);

            default: break;
        }

        return false;
    }

    private boolean commandToggle(CommandSender sender) {
        if(!(sender instanceof Player)) {
            sendMessage(sender, "error.player-only", null, true);
            return true;
        }

        Player player = (Player) sender;
        PVPManager pvpManager = this.expansion.getPVPManager();

        boolean pvpDisabled = pvpManager.isDisabled(player);
        return (pvpDisabled ? commandEnable(player) : commandDisable(player));
    }

    private boolean commandCheck(CommandSender sender, String[] args) {
        if(args.length < 1) return false;
        Player target = findTarget(sender, args[0]);
        if(target == null) return true;
        String targetName = target.getName();

        LanguageManager languageManager = getLanguageManager();
        ProtectionManager protectionManager = this.expansion.getProtectionManager();
        PVPManager pvpManager = this.expansion.getPVPManager();
        boolean isProtected = protectionManager.isProtected(target);
        boolean pvpEnabled = !pvpManager.isDisabled(target);

        String protectedStatus = languageManager.getMessage(sender, "placeholder.toggle."
                + (isProtected ? "enabled" : "disabled"), null, true);
        String pvpStatus = languageManager.getMessage(sender, "placeholder.toggle."
                + (pvpEnabled ? "enabled" : "disabled"), null, true);
        Replacer replacer = message -> message.replace("{target}", targetName)
                .replace("{protected}", protectedStatus).replace("{pvp}", pvpStatus);

        sendMessageWithPrefix(sender, "expansion.newbie-helper.check-format", replacer, true);
        return true;
    }

    private boolean commandEnable(CommandSender sender) {
        if(!(sender instanceof Player)) {
            sendMessage(sender, "error.player-only", null, true);
            return true;
        }

        Player player = (Player) sender;
        PVPManager pvpManager = this.expansion.getPVPManager();
        pvpManager.setPVP(player, true);

        sendToggleMessage(player);
        return true;
    }

    private boolean commandDisable(CommandSender sender) {
        if(!(sender instanceof Player)) {
            sendMessage(sender, "error.player-only", null, true);
            return true;
        }

        Player player = (Player) sender;
        PVPManager pvpManager = this.expansion.getPVPManager();
        pvpManager.setPVP(player, false);

        sendToggleMessage(player);
        return true;
    }

    private boolean commandAdmin(CommandSender sender, String[] args) {
        if(args.length < 1) return false;
        String sub = args[0].toLowerCase();
        String[] newArgs = (args.length < 2 ? new String[0] : Arrays.copyOfRange(args, 1, args.length));
        switch(sub) {
            case "on": return commandAdminEnable(sender, newArgs);
            case "off": return commandAdminDisable(sender, newArgs);
            default: break;
        }

        return false;
    }

    private boolean commandAdminEnable(CommandSender sender, String[] args) {
        if(args.length < 1) return false;
        Player target = findTarget(sender, args[0]);
        if(target == null) return true;

        PVPManager pvpManager = this.expansion.getPVPManager();
        pvpManager.setPVP(target, true);

        sendAdminToggleMessage(sender, target);
        return true;
    }

    private boolean commandAdminDisable(CommandSender sender, String[] args) {
        if(args.length < 1) return false;
        Player target = findTarget(sender, args[0]);
        if(target == null) return true;

        PVPManager pvpManager = this.expansion.getPVPManager();
        pvpManager.setPVP(target, false);

        sendAdminToggleMessage(sender, target);
        return true;
    }

    private boolean hasCooldown(Player player) {
        ConfigurationManager configurationManager = this.expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        long cooldownSeconds = configuration.getLong("pvp-toggle-cooldown", 0L);
        if(cooldownSeconds <= 0L) return false;

        UUID uuid = player.getUniqueId();
        long cooldownExpireMillis = this.commandCooldownMap.getOrDefault(uuid, 0L);
        long systemTimeMillis = System.currentTimeMillis();
        return (systemTimeMillis < cooldownExpireMillis);
    }

    private void addCooldown(Player player) {
        ConfigurationManager configurationManager = this.expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        long cooldownSeconds = configuration.getLong("pvp-toggle-cooldown", 0L);
        if(cooldownSeconds <= 0L) return;

        long systemTimeMillis = System.currentTimeMillis();
        long cooldownMillis = TimeUnit.SECONDS.toMillis(cooldownSeconds);
        long cooldownExpireMillis = (systemTimeMillis + cooldownMillis);

        UUID uuid = player.getUniqueId();
        this.commandCooldownMap.put(uuid, cooldownExpireMillis);

    }

    private void sendToggleMessage(Player player) {
        LanguageManager languageManager = getLanguageManager();
        PVPManager pvpManager = this.expansion.getPVPManager();
        boolean pvpEnabled = !pvpManager.isDisabled(player);

        String pvpStatus = languageManager.getMessage(player, "placeholder.toggle."
                + (pvpEnabled ? "enabled" : "disabled"), null, true);
        Replacer replacer = message -> message.replace("{status}", pvpStatus);
        sendMessageWithPrefix(player, "expansion.newbie-helper.togglepvp.self", replacer, true);
    }

    private void sendAdminToggleMessage(CommandSender sender, Player target) {
        LanguageManager languageManager = getLanguageManager();
        PVPManager pvpManager = this.expansion.getPVPManager();
        boolean pvpEnabled = !pvpManager.isDisabled(target);

        String targetName = target.getName();
        String pvpStatus = languageManager.getMessage(sender, "placeholder.toggle."
                + (pvpEnabled ? "enabled" : "disabled"), null, true);
        Replacer replacer = message -> message.replace("{target}", targetName)
                .replace("{status}", pvpStatus);
        sendMessageWithPrefix(sender, "expansion.newbie-helper.togglepvp.admin", replacer, true);
    }

    private void sendCooldownMessage(Player player) {
        if(!hasCooldown(player)) return;
        UUID uuid = player.getUniqueId();

        long cooldownExpireMillis = this.commandCooldownMap.getOrDefault(uuid, 0L);
        long systemTimeMillis = System.currentTimeMillis();
        long cooldownMillisLeft = (cooldownExpireMillis - systemTimeMillis);
        long cooldownSecondsLeft = TimeUnit.MILLISECONDS.toSeconds(cooldownMillisLeft);
        String cooldownSecondsLeftString = Long.toString(cooldownSecondsLeft);

        Replacer replacer = message -> message.replace("{time_left}", cooldownSecondsLeftString);
        sendMessageWithPrefix(player, "expansion.newbie-helper.togglepvp.cooldown", replacer, true);
    }
}
