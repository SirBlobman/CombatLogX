package combatlogx.expansion.cheat.prevention.listener.paper;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import io.papermc.paper.event.entity.EntityInsideBlockEvent;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import combatlogx.expansion.cheat.prevention.listener.CheatPreventionListener;

public final class ListenerPaperEntityInsideBlock extends CheatPreventionListener {

    public ListenerPaperEntityInsideBlock(Expansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityInsideBlock(EntityInsideBlockEvent e) {
        printDebug("Detected EntityInsideBlockEvent...");

        Entity entity = e.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        printDebug("Player: " + player.getName());

        if (!isInCombat(player)) {
            printDebug("Player is not in combat, ignoring.");
            return;
        }

        YamlConfiguration configuration = getConfiguration();
        if (!configuration.getBoolean("prevent-portals", true)) {
            printDebug("prevent-portals is disabled, ignoring.");
            return;
        }

        if (!e.getBlock().getBlockData().getMaterial().name().contains("PORTAL")) {
            return;
        }

        e.setCancelled(true);
        sendMessage(player, "expansion.cheat-prevention.teleportation.block-portal", null);
        printDebug("prevent-portals is enabled, cancelled event and sent message.");
    }

    private YamlConfiguration getConfiguration() {
        ConfigurationManager configurationManager = getExpansionConfigurationManager();
        return configurationManager.get("teleportation.yml");
    }
}

