package combatlogx.expansion.cheat.prevention.listener;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.ExpansionConfigurationManager;

public final class ListenerRiptide extends CheatPreventionListener {
    public ListenerRiptide(Expansion expansion) {
        super(expansion);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if(!player.isRiptiding() || !isInCombat(player) || isAllowed()) return;

        e.setCancelled(true);
        sendMessage(player, "expansion.cheat-prevention.no-riptide", null);
    }

    private YamlConfiguration getConfiguration() {
        Expansion expansion = getExpansion();
        ExpansionConfigurationManager configurationManager = expansion.getConfigurationManager();
        return configurationManager.get("items.yml");
    }

    private boolean isAllowed() {
        YamlConfiguration configuration = getConfiguration();
        return !configuration.getBoolean("prevent-riptide");
    }
}