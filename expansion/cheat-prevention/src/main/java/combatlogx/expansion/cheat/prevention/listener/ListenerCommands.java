package combatlogx.expansion.cheat.prevention.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.language.replacer.Replacer;
import com.github.sirblobman.api.language.replacer.StringReplacer;
import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.object.UntagReason;

public final class ListenerCommands extends CheatPreventionListener {
    private final Map<UUID, Long> cooldownMap;

    public ListenerCommands(Expansion expansion) {
        super(expansion);
        this.cooldownMap = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void beforeCommandLowest(PlayerCommandPreprocessEvent e) {
        checkEvent(e);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void beforeCommandHigh(PlayerCommandPreprocessEvent e) {
        checkEvent(e);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onUntag(PlayerUntagEvent e) {
        UntagReason untagReason = e.getUntagReason();
        if (!untagReason.isExpire()) {
            return;
        }

        Player player = e.getPlayer();
        addCooldown(player);
    }

    private YamlConfiguration getConfiguration() {
        Expansion expansion = getExpansion();
        ConfigurationManager configurationManager = expansion.getConfigurationManager();
        return configurationManager.get("commands.yml");
    }

    private boolean hasBypassPermission(Player player) {
        YamlConfiguration configuration = getConfiguration();
        String permissionName = configuration.getString("bypass-permission");
        if (permissionName == null || permissionName.isEmpty()) {
            return false;
        }

        Permission permission = new Permission(permissionName, "CombatLogX Bypass Permission: Cheat Prevention Blocked Commands", PermissionDefault.FALSE);
        return player.hasPermission(permission);
    }

    private long getNewExpireTime() {
        YamlConfiguration configuration = getConfiguration();
        long cooldownSeconds = configuration.getLong("delay-after-combat");
        long cooldownMillis = (cooldownSeconds * 1_000L);

        long systemMillis = System.currentTimeMillis();
        return (systemMillis + cooldownMillis);
    }

    private boolean isInCooldown(Player player) {
        UUID uuid = player.getUniqueId();
        if (this.cooldownMap.containsKey(uuid)) {
            long expireMillis = this.cooldownMap.get(uuid);
            long systemMillis = System.currentTimeMillis();
            if (systemMillis < expireMillis) {
                return true;
            }

            this.cooldownMap.remove(uuid);
            return false;
        }

        return false;
    }

    private void addCooldown(Player player) {
        UUID uuid = player.getUniqueId();
        long expireMillis = getNewExpireTime();
        this.cooldownMap.put(uuid, expireMillis);
    }

    private String fixCommand(String command) {
        if (command.startsWith("/")) return command;
        return ("/" + command);
    }

    private boolean matchesAny(String string, Iterable<String> valueList) {
        String stringLower = string.toLowerCase();
        for (String value : valueList) {
            if (value.equals("*") || value.equals("/*")) {
                return true;
            }

            String valueLower = value.toLowerCase();
            if (stringLower.equals(valueLower)) {
                return true;
            }

            if (stringLower.startsWith(valueLower + " ")) {
                return true;
            }
        }

        return false;
    }

    private boolean isBlocked(String command) {
        YamlConfiguration configuration = getConfiguration();
        List<String> blockedCommandList = configuration.getStringList("blocked-command-list");
        return matchesAny(command, blockedCommandList);
    }

    private boolean isAllowed(String command) {
        YamlConfiguration configuration = getConfiguration();
        List<String> allowedCommandList = configuration.getStringList("allowed-command-list");
        return matchesAny(command, allowedCommandList);
    }

    private void checkEvent(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        if (!isInCombat(player) && !isInCooldown(player)) {
            return;
        }

        if (hasBypassPermission(player)) {
            return;
        }

        String command = e.getMessage();
        String realCommand = fixCommand(command);
        if (isAllowed(realCommand) || !isBlocked(realCommand)) {
            return;
        }

        e.setCancelled(true);
        Replacer replacer = new StringReplacer("{command}", realCommand);
        sendMessageIgnoreCooldown(player, "expansion.cheat-prevention.command-blocked", replacer);
    }
}
