package combatlogx.expansion.compatibility.region.world.guard;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;

public final class HookWorldGuard6 {
    public static final StateFlag PLAYER_COMBAT = new StateFlag("player-combat", true);
    public static final StateFlag MOB_COMBAT = new StateFlag("player-combat", true);
    public static final BooleanFlag NO_TAGGING = new BooleanFlag("no-tagging");

    public static void registerFlags(WorldGuard6Expansion expansion) {
        try {
            Class<?> class_DefaultFlag = DefaultFlag.class;
            Class<?> class_Field = Field.class;

            final Flag<?>[] defaultFlagList = DefaultFlag.flagsList;
            int defaultFlagListLength = defaultFlagList.length;

            Flag<?>[] flagArray = new Flag[defaultFlagListLength + 3];
            System.arraycopy(defaultFlagList, 0, flagArray, 0, defaultFlagListLength);
            flagArray[defaultFlagListLength] = PLAYER_COMBAT;
            flagArray[defaultFlagListLength + 1] = MOB_COMBAT;
            flagArray[defaultFlagListLength + 2] = NO_TAGGING;

            Field field_DefaultFlag_flagsList = class_DefaultFlag.getField("flagsList");
            Field field_Field_modifiers = class_Field.getDeclaredField("modifiers");
            field_Field_modifiers.setAccessible(true);
            field_Field_modifiers.setInt(field_DefaultFlag_flagsList, field_DefaultFlag_flagsList.getModifiers() & ~Modifier.FINAL);
            field_DefaultFlag_flagsList.set(null, flagArray);
        } catch(ReflectiveOperationException ex) {
            Logger logger = expansion.getLogger();
            logger.log(Level.WARNING, "Failed to register custom WorldGuard flags because an error occurred:", ex);
        }
    }
}