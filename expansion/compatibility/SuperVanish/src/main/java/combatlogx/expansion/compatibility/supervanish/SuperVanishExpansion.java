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
        if (checkDependency("PremiumVanish", true)) {
            return true;
        }

        getLogger().info("Missing PremiumVanish, checking for regular SuperVanish...");
        return checkDependency("SuperVanish", true);
    }

    @Override
    public @NotNull VanishHandler<?> getVanishHandler() {
        if (this.vanishHandler == null) {
            this.vanishHandler = new VanishHandlerSuper(this);
        }

        return this.vanishHandler;
    }
}
