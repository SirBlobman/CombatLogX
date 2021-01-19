package combatlogx.expansion.compatibility.region.world.guard.hook;

import java.util.logging.Level;
import java.util.logging.Logger;

import combatlogx.expansion.compatibility.region.world.guard.WorldGuardExpansion;
import org.codemc.worldguardwrapper.WorldGuardWrapper;
import org.codemc.worldguardwrapper.flag.IWrappedFlag;
import org.codemc.worldguardwrapper.flag.WrappedState;

public final class HookWorldGuard {
    public static IWrappedFlag<WrappedState> PLAYER_COMBAT = null;
    public static IWrappedFlag<WrappedState> MOB_COMBAT = null;
    public static IWrappedFlag<Boolean> NO_TAGGING = null;
    public static void registerFlags(WorldGuardExpansion expansion) {
        try {
            WorldGuardWrapper instance = WorldGuardWrapper.getInstance();
            PLAYER_COMBAT = instance.registerFlag("player-combat", WrappedState.class, WrappedState.ALLOW).orElse(null);
            MOB_COMBAT = instance.registerFlag("mob-combat", WrappedState.class, WrappedState.ALLOW).orElse(null);
            NO_TAGGING = instance.registerFlag("no-tagging", Boolean.TYPE, false).orElse(null);
        } catch(Exception ex) {
            Logger logger = expansion.getLogger();
            logger.log(Level.WARNING, "Failed to register custom WorldGuard flags because an error occurred:", ex);
        }
    }
}