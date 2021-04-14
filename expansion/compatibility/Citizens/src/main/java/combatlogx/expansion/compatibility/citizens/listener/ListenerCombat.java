package combatlogx.expansion.compatibility.citizens.listener;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.event.PlayerPreTagEvent;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import combatlogx.expansion.compatibility.citizens.CitizensExpansion;

public final class ListenerCombat extends ExpansionListener {
    public ListenerCombat(CitizensExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void beforeTag(PlayerPreTagEvent e) {
        YamlConfiguration configuration = getConfiguration();
        if(configuration.getBoolean("npc-tagging")) return;

        LivingEntity entity = e.getEnemy();
        if(entity == null || !entity.hasMetadata("NPC")) return;

        e.setCancelled(true);
    }

    private YamlConfiguration getConfiguration() {
        Expansion expansion = getExpansion();
        ConfigurationManager configurationManager = expansion.getConfigurationManager();
        return configurationManager.get("config.yml");
    }
}