package combatlogx.expansion.cheat.prevention.listener;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

public final class ListenerDrop extends CheatPreventionListener {
    public ListenerDrop(Expansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        if (!isInCombat(player) || isAllowed()) {
            return;
        }

        e.setCancelled(true);
        sendMessage(player, "expansion.cheat-prevention.items.no-dropping");
    }

    private YamlConfiguration getConfiguration() {
        Expansion expansion = getExpansion();
        ConfigurationManager configurationManager = expansion.getConfigurationManager();
        return configurationManager.get("items.yml");
    }

    private boolean isAllowed() {
        YamlConfiguration configuration = getConfiguration();
        return !configuration.getBoolean("prevent-drop");
    }
}
