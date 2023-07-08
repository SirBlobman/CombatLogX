package combatlogx.expansion.cheat.prevention.configuration;

import org.bukkit.GameMode;

import com.github.sirblobman.api.configuration.IConfigurable;

public interface IGameModeConfiguration extends IConfigurable {
    boolean isPreventSwitching();

    boolean isUntagOnSwitch();

    boolean isForceSwitch();

    GameMode getForceMode();
}
