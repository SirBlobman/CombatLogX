package combatlogx.expansion.compatibility.angelchest;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import de.jeff_media.angelchest.events.AngelChestOpenEvent;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public final class ListenerAngelChest extends ExpansionListener {
    public ListenerAngelChest(final Expansion expansion) {
        super(expansion);
    }

    @EventHandler
    public void onAngelChestOpen(AngelChestOpenEvent event) {
        Player player = event.getPlayer();
        ConfigurationManager configurationManager = getExpansionConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");

        switch(event.getReason()) {
            case BREAK: {
                if(configuration.getBoolean("prevent-breaking", true)) {
                    sendMessageWithPrefix(player, "expansion.angel-chest.prevent-breaking", null, true);
                    event.setCancelled(true);
                }
                break;
            }

            case OPEN_GUI: {
                if(configuration.getBoolean("prevent-opening", true)) {
                    sendMessageWithPrefix(player, "expansion.angel-chest.prevent-opening", null, true);
                    event.setCancelled(true);
                }
                break;
            }

            case FAST_LOOT: {
                if(configuration.getBoolean("prevent-fast-looting", true)) {
                    sendMessageWithPrefix(player, "expansion.angel-chest.prevent-fast-looting", null, true);
                    event.setCancelled(true);
                }
                break;
            }
        }
    }

}
