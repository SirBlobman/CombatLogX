package combatlogx.expansion.cheat.prevention.listener;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;

import combatlogx.expansion.cheat.prevention.ICheatPreventionExpansion;
import combatlogx.expansion.cheat.prevention.configuration.IItemConfiguration;

public final class ListenerRiptide extends CheatPreventionListener {
    public ListenerRiptide(@NotNull ICheatPreventionExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (!player.isRiptiding()) {
            return;
        }

        if (!isInCombat(player)) {
            return;
        }

        if (isPreventRiptide()) {
            e.setCancelled(true);
            sendMessage(player, "expansion.cheat-prevention.no-riptide");
        }

        if (isRiptideRetag()) {
            ICombatManager combatManager = getCombatManager();
            combatManager.tag(player, null, TagType.UNKNOWN, TagReason.UNKNOWN);
        }
    }

    private @NotNull IItemConfiguration getItemConfiguration() {
        ICheatPreventionExpansion expansion = getCheatPrevention();
        return expansion.getItemConfiguration();
    }

    private boolean isPreventRiptide() {
        IItemConfiguration itemConfiguration = getItemConfiguration();
        return itemConfiguration.isPreventRiptide();
    }

    private boolean isRiptideRetag() {
        IItemConfiguration itemConfiguration = getItemConfiguration();
        return itemConfiguration.isRiptideRetag();
    }
}
