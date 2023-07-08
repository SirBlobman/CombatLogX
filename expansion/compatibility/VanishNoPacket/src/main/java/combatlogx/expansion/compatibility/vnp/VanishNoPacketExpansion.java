package combatlogx.expansion.compatibility.vnp;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.vanish.VanishExpansion;
import com.github.sirblobman.combatlogx.api.expansion.vanish.VanishHandler;

public final class VanishNoPacketExpansion extends VanishExpansion {
    private VanishHandler<?> vanishHandler;

    public VanishNoPacketExpansion(ICombatLogX plugin) {
        super(plugin);
        this.vanishHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("VanishNoPacket", true, "3");
    }

    @Override
    public @NotNull VanishHandler<?> getVanishHandler() {
        if (this.vanishHandler == null) {
            this.vanishHandler = new VanishHandlerNoPacket(this);
        }

        return this.vanishHandler;
    }
}
