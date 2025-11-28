package combatlogx.expansion.compatibility.vanishgeneric;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.vanish.VanishExpansion;
import com.github.sirblobman.combatlogx.api.expansion.vanish.VanishHandler;

public final class GenericVanishExpansion extends VanishExpansion {
    private VanishHandler<?> vanishHandler;

    public GenericVanishExpansion(final ICombatLogX plugin) {
        super(plugin);
        this.vanishHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        return true;
    }

    @Override
    public @NotNull VanishHandler<?> getVanishHandler() {
        if (this.vanishHandler == null) {
            this.vanishHandler = new VanishHandlerGeneric(this);
        }

        return this.vanishHandler;
    }
}
