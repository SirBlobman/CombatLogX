package combatlogx.expansion.compatibility.region.towny;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;

public final class TownyConfiguration implements IConfigurable {
    private boolean preventAllTownEntries;

    public TownyConfiguration() {
        this.preventAllTownEntries = false;
    }

    @Override
    public void load(@NotNull ConfigurationSection section) {
        setPreventAllTownEntries(section.getBoolean("prevent-all-town-entries", false));
    }

    public boolean isPreventAllTownEntries() {
        return this.preventAllTownEntries;
    }

    public void setPreventAllTownEntries(boolean preventAllTownEntries) {
        this.preventAllTownEntries = preventAllTownEntries;
    }
}
