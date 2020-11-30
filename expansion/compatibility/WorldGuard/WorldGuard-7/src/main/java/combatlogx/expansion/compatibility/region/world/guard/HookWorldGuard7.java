package combatlogx.expansion.compatibility.region.world.guard;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

public final class HookWorldGuard7 {
    public static StateFlag PLAYER_COMBAT = new StateFlag("player-combat", true);
    public static StateFlag MOB_COMBAT = new StateFlag("player-combat", true);
    public static BooleanFlag NO_TAGGING = new BooleanFlag("no-tagging");

    public static void registerFlags(WorldGuard7Expansion expansion) {
        try {
            PLAYER_COMBAT = registerFlag(PLAYER_COMBAT);
            MOB_COMBAT = registerFlag(MOB_COMBAT);
            NO_TAGGING = registerFlag(NO_TAGGING);
        } catch(FlagConflictException | ClassCastException ex) {
            Logger logger = expansion.getLogger();
            logger.log(Level.WARNING, "Failed to register custom WorldGuard flags because an error occurred:", ex);
        }
    }

    @SuppressWarnings("unchecked")
    private static <F extends Flag<?>> F registerFlag(F flag) throws FlagConflictException {
        WorldGuard api = WorldGuard.getInstance();
        FlagRegistry flagRegistry = api.getFlagRegistry();

        String flagName = flag.getName();
        Flag<?> currentFlag = flagRegistry.get(flagName);
        if(currentFlag == null) {
            flagRegistry.register(flag);
            return flag;
        }

        return (F) currentFlag;
    }
}