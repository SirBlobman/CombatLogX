package combatlogx.expansion.cheat.prevention.configuration;

import com.github.sirblobman.api.configuration.IConfigurable;

public interface IItemConfiguration extends IConfigurable {
    boolean isPreventDrop();

    boolean isPreventPickup();

    boolean isPreventElytra();

    boolean isForcePreventElytra();

    boolean isElytraRetag();

    boolean isPreventTotem();

    boolean isPreventRiptide();

    boolean isRiptideRetag();
}
