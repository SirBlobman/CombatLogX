package combatlogx.expansion.compatibility.angelchest;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import de.jeff_media.angelchest.events.AngelChestOpenEvent;
import de.jeff_media.angelchest.events.AngelChestOpenEvent.Reason;

public final class ListenerAngelChest extends ExpansionListener {
    private final AngelChestExpansion expansion;

    public ListenerAngelChest(@NotNull AngelChestExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onAngelChestOpen(AngelChestOpenEvent e) {
        Player player = e.getPlayer();
        if (!isInCombat(player)) {
            return;
        }

        Reason reason = e.getReason();
        switch (reason) {
            case BREAK:
                checkBreaking(player, e);
                break;
            case OPEN_GUI:
                checkOpening(player, e);
                break;
            case FAST_LOOT:
                checkFastLooting(player, e);
                break;
            default:
                break;
        }
    }

    private @NotNull AngelChestExpansion getAngelChestExpansion() {
        return this.expansion;
    }

    private @NotNull AngelChestConfiguration getConfiguration() {
        AngelChestExpansion expansion = getAngelChestExpansion();
        return expansion.getConfiguration();
    }

    private void checkBreaking(Player player, Cancellable e) {
        AngelChestConfiguration configuration = getConfiguration();
        if (configuration.isPreventBreaking()) {
            e.setCancelled(true);
            LanguageManager languageManager = getLanguageManager();
            languageManager.sendMessageWithPrefix(player, "expansion.angel-chest.prevent-breaking");
        }
    }

    private void checkOpening(Player player, Cancellable e) {
        AngelChestConfiguration configuration = getConfiguration();
        if (configuration.isPreventOpening()) {
            e.setCancelled(true);
            LanguageManager languageManager = getLanguageManager();
            languageManager.sendMessageWithPrefix(player, "expansion.angel-chest.prevent-opening");
        }
    }

    private void checkFastLooting(Player player, Cancellable e) {
        AngelChestConfiguration configuration = getConfiguration();
        if (configuration.isPreventFastLooting()) {
            e.setCancelled(true);
            LanguageManager languageManager = getLanguageManager();
            languageManager.sendMessageWithPrefix(player, "expansion.angel-chest.prevent-fast-looting");
        }
    }
}
