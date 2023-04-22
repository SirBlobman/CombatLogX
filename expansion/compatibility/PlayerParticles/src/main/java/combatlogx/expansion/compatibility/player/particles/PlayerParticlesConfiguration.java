package combatlogx.expansion.compatibility.player.particles;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;

public final class PlayerParticlesConfiguration implements IConfigurable {
    private boolean tagDisablesParticles;
    private boolean untagEnablesParticles;

    public PlayerParticlesConfiguration() {
        this.tagDisablesParticles = true;
        this.untagEnablesParticles = false;
    }

    @Override
    public void load(@NotNull ConfigurationSection config) {
        setTagDisablesParticles(config.getBoolean("tag-disables-particles", true));
        setUntagEnablesParticles(config.getBoolean("untag-enables-particles", false));
    }

    public boolean isTagDisablesParticles() {
        return this.tagDisablesParticles;
    }

    public void setTagDisablesParticles(boolean tagDisablesParticles) {
        this.tagDisablesParticles = tagDisablesParticles;
    }

    public boolean isUntagEnablesParticles() {
        return this.untagEnablesParticles;
    }

    public void setUntagEnablesParticles(boolean untagEnablesParticles) {
        this.untagEnablesParticles = untagEnablesParticles;
    }
}
