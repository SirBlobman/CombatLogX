package combatlogx.expansion.compatibility.featherboard;

import org.jetbrains.annotations.NotNull;

import org.bukkit.configuration.ConfigurationSection;

import com.github.sirblobman.api.configuration.IConfigurable;

public class FeatherBoardConfiguration implements IConfigurable {
    private String triggerName;

    public FeatherBoardConfiguration() {
        this.triggerName = "combatlogx";
    }

    @Override
    public void load(@NotNull ConfigurationSection config) {
        setTriggerName(config.getString("trigger-name", "combatlogx"));
    }

    public @NotNull String getTriggerName() {
        return this.triggerName;
    }

    public void setTriggerName(@NotNull String triggerName) {
        this.triggerName = triggerName;
    }
}
