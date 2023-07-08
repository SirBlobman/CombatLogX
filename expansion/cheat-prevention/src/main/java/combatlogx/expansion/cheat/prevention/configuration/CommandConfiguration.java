package combatlogx.expansion.cheat.prevention.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public final class CommandConfiguration implements ICommandConfiguration {
    private final List<String> blockedCommandList;
    private final List<String> allowedCommandList;
    private int delayAfterCombat;
    private String bypassPermissionName;
    private transient Permission bypassPermission;

    public CommandConfiguration() {
        this.delayAfterCombat = 0;
        this.blockedCommandList = new ArrayList<>();
        this.allowedCommandList = new ArrayList<>();
        this.bypassPermissionName = null;
        this.bypassPermission = null;
    }

    @Override
    public void load(ConfigurationSection config) {
        setDelayAfterCombat(config.getInt("delay-after-combat", 0));
        setBlockedCommands(config.getStringList("blocked-command-list"));
        setAllowedCommands(config.getStringList("allowed-command-list"));
        setBypassPermissionName(config.getString("bypass-permission"));
    }

    @Override
    public int getDelayAfterCombat() {
        return this.delayAfterCombat;
    }

    public void setDelayAfterCombat(int delay) {
        this.delayAfterCombat = delay;
    }

    public @Nullable String getBypassPermissionName() {
        return this.bypassPermissionName;
    }

    public void setBypassPermissionName(@Nullable String permissionName) {
        this.bypassPermissionName = permissionName;
        this.bypassPermission = null;
    }

    @Override
    public @Nullable Permission getBypassPermission() {
        if (this.bypassPermission != null) {
            return this.bypassPermission;
        }

        String permissionName = getBypassPermissionName();
        if (permissionName == null || permissionName.isEmpty()) {
            return null;
        }

        String permissionDescription = "CombatLogX CheatPrevention command blocker bypass.";
        this.bypassPermission = new Permission(permissionName, permissionDescription, PermissionDefault.FALSE);
        return this.bypassPermission;
    }

    public @NotNull List<String> getAllowedCommands() {
        return Collections.unmodifiableList(this.allowedCommandList);
    }

    public void setAllowedCommands(@NotNull Collection<String> commands) {
        this.allowedCommandList.clear();
        this.allowedCommandList.addAll(commands);
    }

    @Override
    public boolean isAllowed(@NotNull String command) {
        List<String> allowedCommandList = getAllowedCommands();
        if (allowedCommandList.contains("*")) {
            return true;
        }

        String commandLowercase = command.toLowerCase(Locale.US);
        for (String allowedCommand : allowedCommandList) {
            String allowedCommandLowercase = allowedCommand.toLowerCase(Locale.US);
            if (commandLowercase.equals(allowedCommandLowercase)) {
                return true;
            }

            String withSpace = (allowedCommand + " ");
            if (commandLowercase.startsWith(withSpace)) {
                return true;
            }
        }

        return false;
    }

    public @NotNull List<String> getBlockedCommands() {
        return Collections.unmodifiableList(this.blockedCommandList);
    }

    public void setBlockedCommands(@NotNull Collection<String> commands) {
        this.blockedCommandList.clear();
        this.blockedCommandList.addAll(commands);
    }

    @Override
    public boolean isBlocked(@NotNull String command) {
        List<String> blockedCommandList = getBlockedCommands();
        if (blockedCommandList.contains("*")) {
            return true;
        }

        String commandLowercase = command.toLowerCase(Locale.US);
        for (String blockedCommand : blockedCommandList) {
            String blockedCommandLowercase = blockedCommand.toLowerCase(Locale.US);
            if (commandLowercase.equals(blockedCommandLowercase)) {
                return true;
            }

            String withSpace = (blockedCommand + " ");
            if (commandLowercase.startsWith(withSpace)) {
                return true;
            }
        }

        return false;
    }
}
