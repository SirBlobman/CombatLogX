package combatlogx.expansion.cheat.prevention.listener;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPickupItemEvent;

import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionConfigurationManager;

public final class ListenerModernItemPickup extends CheatPreventionListener {
    public ListenerModernItemPickup(Expansion expansion) {
        super(expansion);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onPickup(EntityPickupItemEvent e) {
        Entity entity = e.getEntity();
        if(!(entity instanceof Player)) return;

        Player player = (Player) entity;
        if(!isInCombat(player)) return;
        if(isAllowed()) return;

        e.setCancelled(true);
        sendMessage(player, "expansion.cheat-prevention.items.no-pickup", null);
    }

    private boolean isAllowed() {
        Expansion expansion = getExpansion();
        ExpansionConfigurationManager configurationManager = expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("items.yml");
        return !configuration.getBoolean("prevent-pickup");
    }
}