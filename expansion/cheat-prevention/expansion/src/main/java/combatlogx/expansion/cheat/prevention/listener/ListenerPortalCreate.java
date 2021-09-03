package combatlogx.expansion.cheat.prevention.listener;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.PortalCreateEvent;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

public final class ListenerPortalCreate extends CheatPreventionListener {
    public ListenerPortalCreate(Expansion expansion) {
        super(expansion);
    }
    
    @EventHandler(priority= EventPriority.NORMAL, ignoreCancelled=true)
    public void onPortalCreate(PortalCreateEvent e) {
        Entity entity = e.getEntity();
        if(!(entity instanceof Player)) return;
        
        Player player = (Player) entity;
        if(isInCombat(player) && shouldPreventPortalCreation()) {
            e.setCancelled(true);
            sendMessage(player, "expansion.cheat-prevention.blocks.prevent-portal-creation", null);
        }
    }
    
    private YamlConfiguration getConfiguration() {
        ConfigurationManager configurationManager = getExpansionConfigurationManager();
        return configurationManager.get("blocks.yml");
    }
    
    private boolean shouldPreventPortalCreation() {
        YamlConfiguration configuration = getConfiguration();
        return configuration.getBoolean("prevent-portal-creation");
    }
}
