package combatlogx.expansion.compatibility.region.world.guard.hook;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.sirblobman.api.utility.Validate;

import combatlogx.expansion.compatibility.region.world.guard.WorldGuardExpansion;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.flag.IWrappedFlag;
import org.codemc.worldguardwrapper.flag.WrappedState;

public final class HookWorldGuard {
    private final WorldGuardExpansion expansion;

    private IWrappedFlag<WrappedState> mythicMobCombatFlag;
    private IWrappedFlag<WrappedState> unknownCombatFlag;
    private IWrappedFlag<WrappedState> playerCombatFlag;
    private IWrappedFlag<WrappedState> damageCombatFlag;
    private IWrappedFlag<WrappedState> mobCombatFlag;
    private IWrappedFlag<Boolean> noTaggingFlag;

    public HookWorldGuard(WorldGuardExpansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");

        this.mythicMobCombatFlag = null;
        this.unknownCombatFlag = null;
        this.playerCombatFlag = null;
        this.damageCombatFlag = null;
        this.mobCombatFlag = null;
        this.noTaggingFlag = null;
    }

    private WorldGuardExpansion getExpansion() {
        return this.expansion;
    }

    private Logger getLogger() {
        WorldGuardExpansion expansion = getExpansion();
        return expansion.getLogger();
    }

    public void registerFlags() {
        try {
            WorldGuardWrapper wrapper = WorldGuardWrapper.getInstance();
            Class<Boolean> booleanClass = Boolean.TYPE;
            Class<WrappedState> stateClass = WrappedState.class;
            WrappedState state = WrappedState.ALLOW;

            this.mythicMobCombatFlag = wrapper.registerFlag("mythic-mob-combat", stateClass, state).orElse(null);
            this.unknownCombatFlag = wrapper.registerFlag("unknown-combat", stateClass, state).orElse(null);
            this.playerCombatFlag = wrapper.registerFlag("player-combat", stateClass, state).orElse(null);
            this.damageCombatFlag = wrapper.registerFlag("damage-combat", stateClass, state).orElse(null);
            this.mobCombatFlag = wrapper.registerFlag("mob-combat", stateClass, state).orElse(null);
            this.noTaggingFlag = wrapper.registerFlag("no-tagging", booleanClass, false).orElse(null);
        } catch(Exception ex) {
            Logger logger = getLogger();
            logger.log(Level.WARNING, "An error occurred while registering custom WorldGuard flags:", ex);
        }
    }

    public IWrappedFlag<WrappedState> getMythicMobCombatFlag() {
        return this.mythicMobCombatFlag;
    }

    public IWrappedFlag<WrappedState> getUnknownCombatFlag() {
        return this.unknownCombatFlag;
    }

    public IWrappedFlag<WrappedState> getPlayerCombatFlag() {
        return this.playerCombatFlag;
    }

    public IWrappedFlag<WrappedState> getDamageCombatFlag() {
        return this.damageCombatFlag;
    }

    public IWrappedFlag<WrappedState> getMobCombatFlag() {
        return this.mobCombatFlag;
    }

    public IWrappedFlag<Boolean> getNoTaggingFlag() {
        return noTaggingFlag;
    }
}
