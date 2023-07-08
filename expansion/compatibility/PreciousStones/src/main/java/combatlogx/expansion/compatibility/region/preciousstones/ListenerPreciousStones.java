package combatlogx.expansion.compatibility.region.preciousstones;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import net.sacredlabyrinth.Phaed.PreciousStones.api.events.FieldPreCreationEvent;

public final class ListenerPreciousStones extends ExpansionListener {
    private final PreciousStonesExpansion expansion;

    public ListenerPreciousStones(@NotNull PreciousStonesExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void beforeFieldCreation(FieldPreCreationEvent e) {
        Player player = e.getPlayer();
        if (isPreventFieldCreation() && isInCombat(player)) {
            e.setCancelled(true);
            String path = "expansion.region-protection.preciousstones.prevent-field-creation";
            LanguageManager languageManager = getLanguageManager();
            languageManager.sendMessageWithPrefix(player, path);
        }
    }

    private @NotNull PreciousStonesExpansion getPreciousStonesExpansion() {
        return this.expansion;
    }

    private @NotNull PreciousStonesConfiguration getConfiguration() {
        PreciousStonesExpansion expansion = getPreciousStonesExpansion();
        return expansion.getPreciousStonesConfiguration();
    }

    private boolean isPreventFieldCreation() {
        PreciousStonesConfiguration configuration = getConfiguration();
        return configuration.isPreventFieldCreation();
    }
}
