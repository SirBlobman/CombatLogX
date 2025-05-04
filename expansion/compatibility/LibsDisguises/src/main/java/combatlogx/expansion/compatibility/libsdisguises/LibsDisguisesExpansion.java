package combatlogx.expansion.compatibility.libsdisguises;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.disguise.DisguiseExpansion;
import com.github.sirblobman.combatlogx.api.expansion.disguise.DisguiseHandler;

public final class LibsDisguisesExpansion extends DisguiseExpansion {
    private DisguiseHandler<?> disguiseHandler;

    public LibsDisguisesExpansion(@NotNull ICombatLogX plugin) {
        super(plugin);
        this.disguiseHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("LibsDisguises", true, "11");
    }

    @Override
    public @NotNull DisguiseHandler<?> getDisguiseHandler() {
        if (this.disguiseHandler == null) {
            this.disguiseHandler = new DisguiseHandlerLibsDisguises(this);
        }

        return this.disguiseHandler;
    }
}
