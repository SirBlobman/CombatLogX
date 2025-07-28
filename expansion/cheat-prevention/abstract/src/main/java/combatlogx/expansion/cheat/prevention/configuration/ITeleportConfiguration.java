package combatlogx.expansion.cheat.prevention.configuration;

import org.jetbrains.annotations.NotNull;

import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.github.sirblobman.api.configuration.IConfigurable;

public interface ITeleportConfiguration extends IConfigurable {
    boolean isPreventPortals();

    boolean isPreventTeleportation();

    boolean isEnderPearlRetag();

    boolean isUntag();

    boolean isAllowed(@NotNull TeleportCause cause);

    boolean isForceDisableEnderPearl();
}
