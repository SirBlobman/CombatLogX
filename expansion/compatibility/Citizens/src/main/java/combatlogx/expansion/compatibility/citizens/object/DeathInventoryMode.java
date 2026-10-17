package combatlogx.expansion.compatibility.citizens.object;

import java.util.Locale;

import org.jetbrains.annotations.Nullable;

/**
 * What should happen with the stored inventory of a player when their combat NPC is killed?
 */
public enum DeathInventoryMode {
    /**
     * Drop every stored item at the location where the NPC died. (Default)
     */
    DROP,

    /**
     * Keep the stored items and give them back to the player when they log in again.
     */
    KEEP,

    /**
     * Delete the stored items. Nothing is dropped and nothing is returned to the player.
     */
    DESTROY;

    /**
     * @param value The configured name, case-insensitive.
     * @return The matching mode, or {@code null} if the value is not a valid mode.
     */
    public static @Nullable DeathInventoryMode parse(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
