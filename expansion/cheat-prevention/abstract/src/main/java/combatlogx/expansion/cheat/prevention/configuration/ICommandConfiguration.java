package combatlogx.expansion.cheat.prevention.configuration;

import org.bukkit.permissions.Permission;

import com.github.sirblobman.api.configuration.IConfigurable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ICommandConfiguration extends IConfigurable {
    int getDelayAfterCombat();
    boolean isBlocked(@NotNull String command);
    boolean isAllowed(@NotNull String command);
    @Nullable Permission getBypassPermission();
}
