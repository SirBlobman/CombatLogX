package combatlogx.expansion.compatibility.region.preciousstones;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import net.sacredlabyrinth.Phaed.PreciousStones.api.events.FieldPreCreationEvent;

public final class ListenerPreciousStones extends ExpansionListener {
    public ListenerPreciousStones(PreciousStonesExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void beforeFieldCreation(FieldPreCreationEvent e) {
        Player player = e.getPlayer();
        if (!isInCombat(player)) {
            return;
        }

        ConfigurationManager configurationManager = getExpansionConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        if (!configuration.getBoolean("prevent-field-creation")) {
            return;
        }

        e.setCancelled(true);
        String path = ("expansion.region-protection.preciousstones.prevent-field-creation");
        sendMessageWithPrefix(player, path, null);
    }
}
