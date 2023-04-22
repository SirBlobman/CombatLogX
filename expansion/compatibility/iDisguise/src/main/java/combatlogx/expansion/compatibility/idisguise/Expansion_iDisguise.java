package combatlogx.expansion.compatibility.idisguise;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.disguise.DisguiseExpansion;
import com.github.sirblobman.combatlogx.api.expansion.disguise.DisguiseHandler;

public final class Expansion_iDisguise extends DisguiseExpansion {
    private DisguiseHandler<?> disguiseHandler;

    public Expansion_iDisguise(@NotNull ICombatLogX plugin) {
        super(plugin);
        this.disguiseHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("iDisguise", true, "5.8");
    }

    @Override
    public @NotNull DisguiseHandler<?> getDisguiseHandler() {
        if (this.disguiseHandler == null) {
            this.disguiseHandler = new DisguiseHandler_iDisguise(this);
        }

        return this.disguiseHandler;
    }
}
