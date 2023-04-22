package combatlogx.expansion.cheat.prevention.configuration;

import com.github.sirblobman.api.configuration.IConfigurable;

public interface IFlightConfiguration extends IConfigurable {
    boolean isPreventFlying();
    boolean isPreventFallDamage();
    boolean isForceDisableFlight();
    boolean isFlightRetag();
}
