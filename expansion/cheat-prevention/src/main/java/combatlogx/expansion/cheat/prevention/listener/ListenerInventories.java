package combatlogx.expansion.cheat.prevention.listener;

import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.event.PlayerReTagEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

public final class ListenerInventories extends CheatPreventionListener {
    public ListenerInventories(Expansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTag(PlayerTagEvent e) {
        if (shouldNotCloseInventories()) {
            return;
        }

        Player player = e.getPlayer();
        InventoryView openView = player.getOpenInventory();
        InventoryType viewType = openView.getType();
        player.closeInventory();

        if (shouldSendMessage(viewType)) {
            sendMessage(player, "expansion.cheat-prevention.inventory.force-closed");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTag(PlayerReTagEvent e) {
        if (shouldNotCloseInventoriesOnRetag()) {
            return;
        }

        Player player = e.getPlayer();
        player.closeInventory();
        sendMessage(player, "expansion.cheat-prevention.inventory.force-closed");
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onOpen(InventoryOpenEvent e) {
        HumanEntity human = e.getPlayer();
        if (!(human instanceof Player)) {
            return;
        }

        Player player = (Player) human;
        if (!isInCombat(player)) {
            return;
        }

        if (shouldAllowOpeningInventories()) {
            return;
        }

        e.setCancelled(true);
        sendMessage(player, "expansion.cheat-prevention.inventory.no-opening");
    }

    private YamlConfiguration getConfiguration() {
        Expansion expansion = getExpansion();
        ConfigurationManager configurationManager = expansion.getConfigurationManager();
        return configurationManager.get("inventories.yml");
    }

    private boolean shouldNotCloseInventories() {
        YamlConfiguration configuration = getConfiguration();
        return !configuration.getBoolean("close");
    }

    private boolean shouldNotCloseInventoriesOnRetag() {
        YamlConfiguration configuration = getConfiguration();
        return !configuration.getBoolean("close-on-retag");
    }

    private boolean shouldAllowOpeningInventories() {
        YamlConfiguration configuration = getConfiguration();
        return !configuration.getBoolean("prevent-opening");
    }

    private boolean shouldSendMessage(InventoryType type) {
        String typeName = type.name();
        YamlConfiguration configuration = getConfiguration();
        List<String> noMessageTypeList = configuration.getStringList("no-close-message-type-list");
        return !noMessageTypeList.contains(typeName);
    }
}
