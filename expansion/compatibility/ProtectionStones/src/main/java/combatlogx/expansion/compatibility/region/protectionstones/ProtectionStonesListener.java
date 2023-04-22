package combatlogx.expansion.compatibility.region.protectionstones;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import dev.espi.protectionstones.event.PSCreateEvent;

public final class ProtectionStonesListener extends ExpansionListener {
    private final ProtectionStonesExpansion expansion;

    public ProtectionStonesListener(@NotNull ProtectionStonesExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onCreateRegion(PSCreateEvent e) {
        Player player = e.getPlayer();
        if (isInCombat(player) && isPreventAreaCreation()) {
            e.setCancelled(true);
            String path = ("expansion.region-protection.protectionstones.prevent-area-creation");
            LanguageManager languageManager = getLanguageManager();
            languageManager.sendMessageWithPrefix(player, path);
        }
    }

    private @NotNull ProtectionStonesExpansion getProtectionStonesExpansion() {
        return this.expansion;
    }

    private @NotNull ProtectionStonesConfiguration getConfiguration() {
        ProtectionStonesExpansion expansion = getProtectionStonesExpansion();
        return expansion.getProtectionStonesConfiguration();
    }

    private boolean isPreventAreaCreation() {
        ProtectionStonesConfiguration configuration = getConfiguration();
        return configuration.isPreventAreaCreation();
    }
}
