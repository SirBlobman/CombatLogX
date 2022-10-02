package combatlogx.expansion.cheat.prevention.listener.legacy;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import combatlogx.expansion.cheat.prevention.listener.CheatPreventionListener;

public final class ListenerLegacyItemPickup extends CheatPreventionListener {
    public ListenerLegacyItemPickup(Expansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPickup(PlayerPickupItemEvent e) {
        Player player = e.getPlayer();
        if (!isInCombat(player)) {
            return;
        }

        if (isAllowed()) {
            return;
        }

        e.setCancelled(true);
        sendMessage(player, "expansion.cheat-prevention.items.no-pickup", null);
    }

    private boolean isAllowed() {
        Expansion expansion = getExpansion();
        ConfigurationManager configurationManager = expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("items.yml");
        return !configuration.getBoolean("prevent-pickup");
    }
}
