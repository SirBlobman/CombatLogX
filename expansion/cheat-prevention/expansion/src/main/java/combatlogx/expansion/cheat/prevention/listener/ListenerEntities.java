package combatlogx.expansion.cheat.prevention.listener;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

public final class ListenerEntities extends CheatPreventionListener {
    public ListenerEntities(Expansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInteract(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        if (!isInCombat(player)) return;
        if (isEnabled()) return;

        e.setCancelled(true);
        sendMessage(player, "expansion.cheat-prevention.no-entity-interaction", null);
    }

    private YamlConfiguration getConfiguration() {
        Expansion expansion = getExpansion();
        ConfigurationManager configurationManager = expansion.getConfigurationManager();
        return configurationManager.get("entities.yml");
    }

    private boolean isEnabled() {
        YamlConfiguration configuration = getConfiguration();
        return !configuration.getBoolean("prevent-interaction");
    }
}
