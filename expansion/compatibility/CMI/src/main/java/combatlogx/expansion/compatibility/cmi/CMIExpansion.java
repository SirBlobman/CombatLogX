package combatlogx.expansion.compatibility.cmi;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.vanish.VanishExpansion;
import com.github.sirblobman.combatlogx.api.expansion.vanish.VanishHandler;

public final class CMIExpansion extends VanishExpansion {
    private VanishHandler<?> vanishHandler;

    public CMIExpansion(ICombatLogX plugin) {
        super(plugin);
        this.vanishHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("CMI", true, "9");
    }

    @Override
    public @NotNull VanishHandler<?> getVanishHandler() {
        if (this.vanishHandler == null) {
            this.vanishHandler = new VanishHandlerCMI(this);
        }

        return this.vanishHandler;
    }
}
