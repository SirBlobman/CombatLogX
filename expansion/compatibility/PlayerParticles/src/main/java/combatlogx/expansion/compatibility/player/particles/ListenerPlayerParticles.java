package combatlogx.expansion.compatibility.player.particles;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import dev.esophose.playerparticles.api.PlayerParticlesAPI;

public final class ListenerPlayerParticles extends ExpansionListener {
    private PlayerParticlesExpansion expansion;

    public ListenerPlayerParticles(@NotNull PlayerParticlesExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTag(PlayerTagEvent e) {
        if (isTagDisablesParticles()) {
            Player player = e.getPlayer();
            PlayerParticlesAPI api = PlayerParticlesAPI.getInstance();
            api.togglePlayerParticleVisibility(player, true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onUntag(PlayerUntagEvent e) {
        if (isUntagEnablesParticles()) {
            Player player = e.getPlayer();
            PlayerParticlesAPI api = PlayerParticlesAPI.getInstance();
            api.togglePlayerParticleVisibility(player, false);
        }
    }

    private @NotNull PlayerParticlesExpansion getPlayerParticlesExpansion() {
        return this.expansion;
    }

    private @NotNull PlayerParticlesConfiguration getConfiguration() {
        PlayerParticlesExpansion expansion = getPlayerParticlesExpansion();
        return expansion.getConfiguration();
    }

    private boolean isTagDisablesParticles() {
        PlayerParticlesConfiguration configuration = getConfiguration();
        return configuration.isTagDisablesParticles();
    }

    private boolean isUntagEnablesParticles() {
        PlayerParticlesConfiguration configuration = getConfiguration();
        return configuration.isUntagEnablesParticles();
    }
}
