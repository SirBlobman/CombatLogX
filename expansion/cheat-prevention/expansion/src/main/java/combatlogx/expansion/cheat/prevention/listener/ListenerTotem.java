package combatlogx.expansion.cheat.prevention.listener;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityResurrectEvent;

import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionConfigurationManager;

public final class ListenerTotem extends CheatPreventionListener {
    public ListenerTotem(Expansion expansion) {
        super(expansion);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onResurrect(EntityResurrectEvent e) {
        LivingEntity entity = e.getEntity();
        if(!(entity instanceof Player)) return;
        Player player = (Player) entity;
        if(!isInCombat(player) || isAllowed()) return;

        e.setCancelled(true);
        sendMessage(player, "expansion.cheat-prevention.no-totem", null);
    }

    private YamlConfiguration getConfiguration() {
        Expansion expansion = getExpansion();
        ExpansionConfigurationManager configurationManager = expansion.getConfigurationManager();
        return configurationManager.get("items.yml");
    }

    private boolean isAllowed() {
        YamlConfiguration configuration = getConfiguration();
        return !configuration.getBoolean("prevent-totem");
    }
}