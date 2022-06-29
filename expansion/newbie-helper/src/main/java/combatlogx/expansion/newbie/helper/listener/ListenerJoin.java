package combatlogx.expansion.newbie.helper.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;
import combatlogx.expansion.newbie.helper.manager.ProtectionManager;

public final class ListenerJoin extends ExpansionListener {
    private final NewbieHelperExpansion expansion;

    public ListenerJoin(NewbieHelperExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (player.hasPlayedBefore()) {
            return;
        }

        if (isWorldDisabled(player)) {
            return;
        }

        ProtectionManager protectionManager = this.expansion.getProtectionManager();
        protectionManager.setProtected(player, true);
    }
}
