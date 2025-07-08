package combatlogx.expansion.compatibility.region.towny;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;

public final class TownyConfiguration implements IConfigurable {
    private boolean preventAllTownEntries;
    private boolean preventJailDuringCombat;

    public TownyConfiguration() {
        this.preventAllTownEntries = false;
        this.preventJailDuringCombat = true;
    }

    @Override
    public void load(@NotNull ConfigurationSection section) {
        setPreventAllTownEntries(section.getBoolean("prevent-all-town-entries", false));
        setPreventJailDuringCombat(section.getBoolean("prevent-jail-during-combat", true));
    }

    public boolean isPreventAllTownEntries() {
        return this.preventAllTownEntries;
    }

    public void setPreventAllTownEntries(boolean preventAllTownEntries) {
        this.preventAllTownEntries = preventAllTownEntries;
    }

    public boolean isPreventJailDuringCombat() {
        return this.preventJailDuringCombat;
    }

    public void setPreventJailDuringCombat(boolean preventJailDuringCombat) {
        this.preventJailDuringCombat = preventJailDuringCombat;
    }
}
