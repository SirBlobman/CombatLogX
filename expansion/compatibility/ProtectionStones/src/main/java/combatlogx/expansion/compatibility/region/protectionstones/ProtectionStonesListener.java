package combatlogx.expansion.compatibility.region.protectionstones;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import dev.espi.protectionstones.event.PSCreateEvent;

public final class ProtectionStonesListener extends ExpansionListener {
    public ProtectionStonesListener(Expansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onCreateRegion(PSCreateEvent e) {
        Player player = e.getPlayer();
        if (!isInCombat(player)) {
            return;
        }

        e.setCancelled(true);
        String path = ("expansion.region-protection.protectionstones.prevent-area-creation");
        LanguageManager languageManager = getLanguageManager();
        languageManager.sendMessageWithPrefix(player, path);
    }
}
