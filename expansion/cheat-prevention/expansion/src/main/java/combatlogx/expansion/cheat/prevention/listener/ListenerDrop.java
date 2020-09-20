package combatlogx.expansion.cheat.prevention.listener;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;

import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.ExpansionConfigurationManager;

public final class ListenerDrop extends CheatPreventionListener {
    public ListenerDrop(Expansion expansion) {
        super(expansion);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onDrop(PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        if(!isInCombat(player) || isAllowed()) return;

        e.setCancelled(true);
        sendMessage(player, "expansion.cheat-prevention.items.no-dropping", null);
    }

    private YamlConfiguration getConfiguration() {
        Expansion expansion = getExpansion();
        ExpansionConfigurationManager configurationManager = expansion.getConfigurationManager();
        return configurationManager.get("items.yml");
    }

    private boolean isAllowed() {
        YamlConfiguration configuration = getConfiguration();
        return !configuration.getBoolean("prevent-drop");
    }
}