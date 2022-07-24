package combatlogx.expansion.compatibility.cmi.listener;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.event.PlayerPreTagEvent;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.PlayerManager;
import combatlogx.expansion.compatibility.cmi.CMIExpansion;

public final class ListenerCMI extends ExpansionListener {
    public ListenerCMI(CMIExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void beforeCombat(PlayerPreTagEvent e) {
        Player player = e.getPlayer();
        if (isVanished(player) && preventVanishSelfTag()) {
            e.setCancelled(true);
        }

        Entity enemy = e.getEnemy();
        if (enemy instanceof Player) {
            Player other = (Player) enemy;
            if (isVanished(other) && preventVanishOtherTag()) {
                e.setCancelled(true);
            }
        }
    }

    private YamlConfiguration getConfiguration() {
        ConfigurationManager configurationManager = getExpansionConfigurationManager();
        return configurationManager.get("config.yml");
    }

    private boolean preventVanishSelfTag() {
        YamlConfiguration configuration = getConfiguration();
        return configuration.getBoolean("prevent-vanish-tagging-self");
    }

    private boolean preventVanishOtherTag() {
        YamlConfiguration configuration = getConfiguration();
        return configuration.getBoolean("prevent-vanish-tagging-other");
    }

    private boolean isVanished(Player player) {
        CMI cmi = CMI.getInstance();
        PlayerManager playerManager = cmi.getPlayerManager();
        CMIUser cmiUser = playerManager.getUser(player);
        return (cmiUser != null && cmiUser.isVanished());
    }
}
