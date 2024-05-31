package combatlogx.expansion.compatibility.region.world.guard;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;

public final class WorldGuardConfiguration implements IConfigurable {
    private boolean usePvpFlag;

    public WorldGuardConfiguration() {
        this.usePvpFlag = false;
    }

    @Override
    public void load(@NotNull ConfigurationSection section) {
        setUsePvpFlag(section.getBoolean("use-pvp-flag", true));
    }

    public boolean isUsePvpFlag() {
        return usePvpFlag;
    }

    public void setUsePvpFlag(boolean usePvpFlag) {
        this.usePvpFlag = usePvpFlag;
    }
}
