package combatlogx.expansion.cheat.prevention.configuration;

import org.jetbrains.annotations.NotNull;

import org.bukkit.event.inventory.InventoryType;

import com.github.sirblobman.api.configuration.IConfigurable;

public interface IInventoryConfiguration extends IConfigurable {
    boolean isClose();

    boolean isCloseOnRetag();

    boolean isPreventOpening();

    boolean isNoMessage(@NotNull InventoryType type);
}
