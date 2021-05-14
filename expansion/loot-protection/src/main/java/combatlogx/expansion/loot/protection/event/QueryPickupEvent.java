package combatlogx.expansion.loot.protection.event;

import com.github.sirblobman.combatlogx.api.event.CustomPlayerEventCancellable;
import combatlogx.expansion.loot.protection.object.ProtectedItem;
import org.bukkit.entity.Player;

public class QueryPickupEvent extends CustomPlayerEventCancellable {

    private final ProtectedItem protectedItem;

    public QueryPickupEvent(final Player player, final ProtectedItem protectedItem) {
        super(player);
        this.protectedItem = protectedItem;
    }

    public ProtectedItem getProtectedItem() {
        return protectedItem;
    }
}
