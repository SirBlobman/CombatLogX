package combatlogx.expansion.compatibility.region.lands;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;

public final class LandsConfiguration implements IConfigurable {
    private boolean preventAllLandEntries;

    public LandsConfiguration() {
        this.preventAllLandEntries = false;
    }

    @Override
    public void load(@NotNull ConfigurationSection section) {
        setPreventAllLandEntries(section.getBoolean("prevent-all-land-entries", false));
    }

    public boolean isPreventAllLandEntries() {
        return this.preventAllLandEntries;
    }

    public void setPreventAllLandEntries(boolean preventAllLandEntries) {
        this.preventAllLandEntries = preventAllLandEntries;
    }
}
