package combatlogx.expansion.endcrystals;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.configuration.MainConfiguration;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.manager.ICrystalManager;

public final class ListenerCrystals_Modern extends ExpansionListener {
    public ListenerCrystals_Modern(@NotNull Expansion expansion) {
        super(expansion);
    }

    @SuppressWarnings("deprecation") // Draft API
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(org.bukkit.event.entity.EntityPlaceEvent e) {
        ICombatLogX plugin = getCombatLogX();
        MainConfiguration configuration = plugin.getConfiguration();
        if (!configuration.isLinkEndCrystals()) {
            return;
        }

        EntityType entityType = e.getEntityType();
        if (entityType != EntityType.ENDER_CRYSTAL) {
            return;
        }

        Player player = e.getPlayer();
        if (player == null) {
            return;
        }

        Entity crystal = e.getEntity();
        ICrystalManager crystalManager = plugin.getCrystalManager();
        crystalManager.setPlacer(crystal, player);
    }
}
