package combatlogx.expansion.cheat.prevention.listener;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryOpenEvent;

import com.SirBlobman.combatlogx.api.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.ExpansionConfigurationManager;

public final class ListenerInventories extends CheatPreventionListener {
    public ListenerInventories(Expansion expansion) {
        super(expansion);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onTag(PlayerTagEvent e) {
        if(shouldNotCloseInventories()) return;
        Player player = e.getPlayer();
        player.closeInventory();
        sendMessage(player, "expansion.cheat-prevention.inventory.force-closed", null);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onOpen(InventoryOpenEvent e) {
        HumanEntity human = e.getPlayer();
        if(!(human instanceof Player)) return;

        Player player = (Player) human;
        if(!isInCombat(player)) return;
        if(shouldAllowOpeningInventories()) return;

        e.setCancelled(true);
        sendMessage(player, "expansion.cheat-prevention.inventory.no-opening", null);
    }

    private YamlConfiguration getConfiguration() {
        Expansion expansion = getExpansion();
        ExpansionConfigurationManager configurationManager = expansion.getConfigurationManager();
        return configurationManager.get("inventories.yml");
    }

    private boolean shouldNotCloseInventories() {
        YamlConfiguration configuration = getConfiguration();
        return !configuration.getBoolean("close");
    }

    private boolean shouldAllowOpeningInventories() {
        YamlConfiguration configuration = getConfiguration();
        return !configuration.getBoolean("prevent-opening");
    }
}