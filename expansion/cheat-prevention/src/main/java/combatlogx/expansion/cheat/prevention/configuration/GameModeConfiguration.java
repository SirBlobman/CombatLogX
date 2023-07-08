package combatlogx.expansion.cheat.prevention.configuration;

import org.jetbrains.annotations.NotNull;

import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;

import static com.github.sirblobman.api.utility.ConfigurationHelper.parseEnum;

public final class GameModeConfiguration implements IGameModeConfiguration {
    private boolean preventSwitching;
    private boolean untagOnSwitch;
    private boolean forceSwitch;
    private GameMode forceMode;

    public GameModeConfiguration() {
        this.preventSwitching = true;
        this.untagOnSwitch = false;
        this.forceSwitch = false;
        this.forceMode = GameMode.SURVIVAL;
    }

    @Override
    public void load(ConfigurationSection config) {
        setPreventSwitching(config.getBoolean("prevent-switching", true));
        setUntagOnSwitch(config.getBoolean("untag-on-switch", false));
        setForceSwitch(config.getBoolean("force-switch", false));

        String forceModeName = config.getString("force-mode", "SURVIVAL");
        setForceMode(parseEnum(GameMode.class, forceModeName, GameMode.SURVIVAL));
    }

    @Override
    public boolean isPreventSwitching() {
        return this.preventSwitching;
    }

    public void setPreventSwitching(boolean preventSwitching) {
        this.preventSwitching = preventSwitching;
    }

    @Override
    public boolean isUntagOnSwitch() {
        return this.untagOnSwitch;
    }

    public void setUntagOnSwitch(boolean untagOnSwitch) {
        this.untagOnSwitch = untagOnSwitch;
    }

    @Override
    public boolean isForceSwitch() {
        return this.forceSwitch;
    }

    public void setForceSwitch(boolean forceSwitch) {
        this.forceSwitch = forceSwitch;
    }

    @Override
    public @NotNull GameMode getForceMode() {
        return this.forceMode;
    }

    public void setForceMode(@NotNull GameMode forceMode) {
        this.forceMode = forceMode;
    }
}
