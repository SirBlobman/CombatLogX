package combatlogx.expansion.compatibility.angelchest;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import de.jeff_media.angelchest.events.AngelChestOpenEvent;
import de.jeff_media.angelchest.events.AngelChestOpenEvent.Reason;

public final class ListenerAngelChest extends ExpansionListener {
    public ListenerAngelChest(final Expansion expansion) {
        super(expansion);
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

    private YamlConfiguration getConfiguration() {
        ConfigurationManager configurationManager = getExpansionConfigurationManager();
        return configurationManager.get("config.yml");
    }

    private void checkBreaking(Player player, Cancellable e) {
        YamlConfiguration configuration = getConfiguration();
        if (!configuration.getBoolean("prevent-breaking", true)) {
            return;
        }

        LanguageManager languageManager = getLanguageManager();
        languageManager.sendMessageWithPrefix(player, "expansion.angel-chest.prevent-breaking");
        e.setCancelled(true);
    }

    private void checkOpening(Player player, Cancellable e) {
        YamlConfiguration configuration = getConfiguration();
        if (!configuration.getBoolean("prevent-opening", true)) {
            return;
        }

        LanguageManager languageManager = getLanguageManager();
        languageManager.sendMessageWithPrefix(player, "expansion.angel-chest.prevent-opening");
        e.setCancelled(true);
    }

    private void checkFastLooting(Player player, Cancellable e) {
        YamlConfiguration configuration = getConfiguration();
        if (!configuration.getBoolean("prevent-fast-looting", true)) {
            return;
        }

        LanguageManager languageManager = getLanguageManager();
        languageManager.sendMessageWithPrefix(player, "expansion.angel-chest.prevent-fast-looting");
        e.setCancelled(true);
    }
}
