package combatlogx.expansion.compatibility.supervanish.listener;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.event.PlayerPreTagEvent;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import combatlogx.expansion.compatibility.supervanish.SuperVanishExpansion;
import de.myzelyam.api.vanish.VanishAPI;

public final class ListenerVanish extends ExpansionListener {
    public ListenerVanish(SuperVanishExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void beforeCombat(PlayerPreTagEvent e) {
        Player player = e.getPlayer();
        if (isVanished(player) && preventVanishSelfTag()) {
            e.setCancelled(true);
        }

        LivingEntity enemy = e.getEnemy();
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
        return VanishAPI.isInvisible(player);
    }
}
