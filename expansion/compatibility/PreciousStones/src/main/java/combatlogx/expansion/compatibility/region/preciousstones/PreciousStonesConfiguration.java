package combatlogx.expansion.compatibility.region.preciousstones;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;

public final class PreciousStonesConfiguration implements IConfigurable {
    private boolean preventFieldCreation;

    public PreciousStonesConfiguration() {
        this.preventFieldCreation = true;
    }

    @Override
    public void load(@NotNull ConfigurationSection config) {
        setPreventFieldCreation(config.getBoolean("prevent-field-creation", true));
    }

    public boolean isPreventFieldCreation() {
        return this.preventFieldCreation;
    }

    public void setPreventFieldCreation(boolean preventFieldCreation) {
        this.preventFieldCreation = preventFieldCreation;
    }
}
