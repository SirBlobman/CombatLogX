package combatlogx.expansion.compatibility.angelchest;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;

public class AngelChestConfiguration implements IConfigurable {
    private boolean preventBreaking;
    private boolean preventOpening;
    private boolean preventFastLooting;

    public AngelChestConfiguration() {
        this.preventBreaking = true;
        this.preventOpening = true;
        this.preventFastLooting = true;
    }

    @Override
    public void load(@NotNull ConfigurationSection section) {
        setPreventBreaking(section.getBoolean("prevent-breaking", true));
        setPreventOpening(section.getBoolean("prevent-opening", true));
        setPreventFastLooting(section.getBoolean("prevent-fast-looting", true));
    }

    public boolean isPreventBreaking() {
        return this.preventBreaking;
    }

    public void setPreventBreaking(boolean preventBreaking) {
        this.preventBreaking = preventBreaking;
    }

    public boolean isPreventOpening() {
        return this.preventOpening;
    }

    public void setPreventOpening(boolean preventOpening) {
        this.preventOpening = preventOpening;
    }

    public boolean isPreventFastLooting() {
        return this.preventFastLooting;
    }

    public void setPreventFastLooting(boolean preventFastLooting) {
        this.preventFastLooting = preventFastLooting;
    }
}
