package combatlogx.expansion.compatibility.supervanish;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.vanish.VanishExpansion;
import com.github.sirblobman.combatlogx.api.expansion.vanish.VanishHandler;

public final class SuperVanishExpansion extends VanishExpansion {
    private VanishHandler<?> vanishHandler;

    public SuperVanishExpansion(ICombatLogX plugin) {
        super(plugin);
        this.vanishHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        boolean superVanish = checkDependency("SuperVanish", true);
        boolean premiumVanish = checkDependency("PremiumVanish", true);
        return (superVanish || premiumVanish);
    }

    @Override
    public @NotNull VanishHandler<?> getVanishHandler() {
        if (this.vanishHandler == null) {
            this.vanishHandler = new VanishHandlerSuper(this);
        }

        return this.vanishHandler;
    }
}
