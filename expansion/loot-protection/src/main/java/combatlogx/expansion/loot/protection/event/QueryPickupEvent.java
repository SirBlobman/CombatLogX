package combatlogx.expansion.loot.protection.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.github.sirblobman.combatlogx.api.event.CustomPlayerEventCancellable;

import combatlogx.expansion.loot.protection.object.ProtectedItem;

public final class QueryPickupEvent extends CustomPlayerEventCancellable {
    private static final HandlerList HANDLER_LIST;

    static {
        HANDLER_LIST = new HandlerList();
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    private final ProtectedItem protectedItem;

    public QueryPickupEvent(final Player player, final ProtectedItem protectedItem) {
        super(player);
        this.protectedItem = protectedItem;
    }

    public ProtectedItem getProtectedItem() {
        return protectedItem;
    }
}
