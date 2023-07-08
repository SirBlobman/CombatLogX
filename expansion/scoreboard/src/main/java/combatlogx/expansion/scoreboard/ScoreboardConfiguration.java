package combatlogx.expansion.scoreboard;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;

public final class ScoreboardConfiguration implements IConfigurable {
    private boolean enabled;
    private boolean savePrevious;

    public ScoreboardConfiguration() {
        setEnabled(true);
        setSavePrevious(true);
    }

    @Override
    public void load(@NotNull ConfigurationSection config) {
        setEnabled(config.getBoolean("enabled", true));
        setSavePrevious(config.getBoolean("save-previous", true));
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isSavePrevious() {
        return savePrevious;
    }

    public void setSavePrevious(boolean savePrevious) {
        this.savePrevious = savePrevious;
    }
}
