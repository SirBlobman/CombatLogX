package combatlogx.expansion.compatibility.citizens.listener;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.event.PlayerPreTagEvent;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.utility.EntityHelper;

import combatlogx.expansion.compatibility.citizens.CitizensExpansion;

public final class ListenerCombat extends ExpansionListener {
    public ListenerCombat(CitizensExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void beforeTag(PlayerPreTagEvent e) {
        printDebug("Detected PlayerPreTagEvent....");

        YamlConfiguration configuration = getConfiguration();
        if (configuration.getBoolean("npc-tagging")) {
            printDebug("NPC tagging is allowed, ignoring event.");
            return;
        }

        LivingEntity entity = e.getEnemy();
        if (EntityHelper.isNPC(entity)) {
            printDebug("enemy is NPC, cancelling event.");
            e.setCancelled(true);
        }
    }

    private YamlConfiguration getConfiguration() {
        ConfigurationManager configurationManager = getExpansionConfigurationManager();
        return configurationManager.get("config.yml");
    }
}
