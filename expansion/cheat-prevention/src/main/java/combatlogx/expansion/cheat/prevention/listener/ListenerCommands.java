package combatlogx.expansion.cheat.prevention.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.permissions.Permission;

import com.github.sirblobman.api.language.replacer.Replacer;
import com.github.sirblobman.api.language.replacer.StringReplacer;
import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;
import com.github.sirblobman.combatlogx.api.object.UntagReason;

import combatlogx.expansion.cheat.prevention.ICheatPreventionExpansion;
import combatlogx.expansion.cheat.prevention.configuration.ICommandConfiguration;
import org.jetbrains.annotations.NotNull;

public final class ListenerCommands extends CheatPreventionListener {
    private final Map<UUID, Long> cooldownMap;

    public ListenerCommands(@NotNull ICheatPreventionExpansion expansion) {
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

    private @NotNull ICommandConfiguration getCommandConfiguration() {
        ICheatPreventionExpansion expansion = getCheatPrevention();
        return expansion.getCommandConfiguration();
    }

    private boolean hasBypassPermission(Player player) {
        ICommandConfiguration commandConfiguration = getCommandConfiguration();
        Permission bypassPermission = commandConfiguration.getBypassPermission();
        if (bypassPermission == null) {
            return false;
        }

        return player.hasPermission(bypassPermission);
    }

    private long getNewExpireTime() {
        ICommandConfiguration commandConfiguration = getCommandConfiguration();
        long cooldownSeconds = commandConfiguration.getDelayAfterCombat();
        long cooldownMillis = TimeUnit.SECONDS.toMillis(cooldownSeconds);

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
        UUID playerId = player.getUniqueId();
        long expireMillis = getNewExpireTime();
        this.cooldownMap.put(playerId, expireMillis);
    }

    private String fixCommand(String command) {
        if (command.startsWith("/")) {
            return command;
        }

        return ("/" + command);
    }

    private boolean isBlocked(String command) {
        ICommandConfiguration commandConfiguration = getCommandConfiguration();
        return commandConfiguration.isBlocked(command);
    }

    private boolean isAllowed(String command) {
        ICommandConfiguration commandConfiguration = getCommandConfiguration();
        return commandConfiguration.isAllowed(command);
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
