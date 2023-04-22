package combatlogx.expansion.cheat.prevention.configuration;

import org.bukkit.event.inventory.InventoryType;

import com.github.sirblobman.api.configuration.IConfigurable;

import org.jetbrains.annotations.NotNull;

public interface IInventoryConfiguration extends IConfigurable {
    boolean isClose();
    boolean isCloseOnRetag();
    boolean isPreventOpening();
    boolean isNoMessage(@NotNull InventoryType type);
}
