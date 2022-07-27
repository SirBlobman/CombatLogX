package combatlogx.expansion.compatibility.player.particles;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import dev.esophose.playerparticles.api.PlayerParticlesAPI;

public final class ListenerPlayerParticles extends ExpansionListener {
    public ListenerPlayerParticles(PlayerParticlesExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTag(PlayerTagEvent e) {
        if (shouldDisable()) {
            Player player = e.getPlayer();
            PlayerParticlesAPI api = PlayerParticlesAPI.getInstance();
            api.togglePlayerParticleVisibility(player, true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onUntag(PlayerUntagEvent e) {
        if (shouldEnable()) {
            Player player = e.getPlayer();
            PlayerParticlesAPI api = PlayerParticlesAPI.getInstance();
            api.togglePlayerParticleVisibility(player, false);
        }
    }

    private YamlConfiguration getConfiguration() {
        ConfigurationManager configurationManager = getExpansionConfigurationManager();
        return configurationManager.get("config.yml");
    }

    private boolean shouldDisable() {
        YamlConfiguration configuration = getConfiguration();
        return configuration.getBoolean("tag-disables-particles", true);
    }

    private boolean shouldEnable() {
        YamlConfiguration configuration = getConfiguration();
        return configuration.getBoolean("untag-enables-particles", false);
    }
}
