package combatlogx.expansion.loot.protection.event;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.github.sirblobman.combatlogx.api.event.CustomPlayerEventCancellable;

import combatlogx.expansion.loot.protection.object.ProtectedItem;

public final class QueryPickupEvent extends CustomPlayerEventCancellable {
    private static final HandlerList HANDLER_LIST;

    static {
        HANDLER_LIST = new HandlerList();
    }

    private final ProtectedItem protectedItem;

    public QueryPickupEvent(@NotNull Player player, @NotNull ProtectedItem protectedItem) {
        super(player);
        this.protectedItem = protectedItem;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return getHandlerList();
    }

    public @NotNull ProtectedItem getProtectedItem() {
        return protectedItem;
    }
}
