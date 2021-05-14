package combatlogx.expansion.compatibility.region.protectionstones;

import com.github.sirblobman.combatlogx.api.ICombatManager;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import dev.espi.protectionstones.event.PSCreateEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class ProtectionStonesListener extends ExpansionListener {

    public ProtectionStonesListener(final Expansion expansion) {
        super(expansion);
    }

    @EventHandler
    public void onCreateRegion(PSCreateEvent event) {
        ICombatManager combatManager = getCombatLogX().getCombatManager();
        Player player = event.getPlayer();
        if(!combatManager.isInCombat(player)) return;
        event.setCancelled(true);
        getLanguageManager().sendMessage(player, "expansion.protectionstones-compatability.place", null, true);
    }

}
