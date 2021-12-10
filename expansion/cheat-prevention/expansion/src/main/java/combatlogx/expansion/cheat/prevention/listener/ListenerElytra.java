package combatlogx.expansion.cheat.prevention.listener;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityToggleGlideEvent;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

public final class ListenerElytra extends CheatPreventionListener {
    public ListenerElytra(Expansion expansion) {
        super(expansion);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTag(PlayerTagEvent e) {
        Player player = e.getPlayer();
        if(player.isGliding() && shouldForcePrevent()) {
            player.setGliding(false);
            sendMessage(player, "expansion.cheat-prevention.elytra.force-disabled", null);
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onToggle(EntityToggleGlideEvent e) {
        if(!e.isGliding()) {
            return;
        }
        
        Entity entity = e.getEntity();
        if(!(entity instanceof Player)) {
            return;
        }
        
        Player player = (Player) entity;
        if(isAllowed() || !isInCombat(player)) {
            return;
        }
        
        e.setCancelled(true);
        sendMessage(player, "expansion.cheat-prevention.elytra.no-gliding", null);
    }
    
    private YamlConfiguration getConfiguration() {
        ConfigurationManager configurationManager = getExpansionConfigurationManager();
        return configurationManager.get("items.yml");
    }
    
    private boolean isAllowed() {
        YamlConfiguration configuration = getConfiguration();
        return !configuration.getBoolean("prevent-elytra");
    }
    
    private boolean shouldForcePrevent() {
        YamlConfiguration configuration = getConfiguration();
        return configuration.getBoolean("force-prevent-elytra", false);
    }
}
