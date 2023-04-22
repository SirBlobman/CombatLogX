package combatlogx.expansion.compatibility.region.protectionstones;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;

public final class ProtectionStonesConfiguration implements IConfigurable {
    private boolean preventAreaCreation;

    public ProtectionStonesConfiguration() {
        this.preventAreaCreation = true;
    }

    @Override
    public void load(@NotNull ConfigurationSection config) {
        setPreventAreaCreation(config.getBoolean("prevent-area-creation", true));
    }

    public boolean isPreventAreaCreation() {
        return this.preventAreaCreation;
    }

    public void setPreventAreaCreation(boolean preventAreaCreation) {
        this.preventAreaCreation = preventAreaCreation;
    }
}
