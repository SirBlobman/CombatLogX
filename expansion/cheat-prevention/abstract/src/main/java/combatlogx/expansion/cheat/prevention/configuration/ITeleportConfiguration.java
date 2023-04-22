package combatlogx.expansion.cheat.prevention.configuration;

import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.github.sirblobman.api.configuration.IConfigurable;

import org.jetbrains.annotations.NotNull;

public interface ITeleportConfiguration extends IConfigurable {
    boolean isPreventPortals();
    boolean isPreventTeleportation();
    boolean isEnderPearlRetag();
    boolean isUntag();
    boolean isAllowed(@NotNull TeleportCause cause);
}
