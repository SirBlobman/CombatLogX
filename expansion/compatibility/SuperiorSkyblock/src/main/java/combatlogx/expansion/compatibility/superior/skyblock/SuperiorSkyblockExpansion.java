package combatlogx.expansion.compatibility.superior.skyblock;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.skyblock.SkyBlockExpansion;
import com.github.sirblobman.combatlogx.api.expansion.skyblock.SkyBlockHandler;

public final class SuperiorSkyblockExpansion extends SkyBlockExpansion {
    private SkyBlockHandler<?> skyBlockHandler;

    public SuperiorSkyblockExpansion(ICombatLogX plugin) {
        super(plugin);
        this.skyBlockHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("SuperiorSkyblock2", true);
    }

    @Override
    public @NotNull SkyBlockHandler<?> getSkyBlockHandler() {
        if (this.skyBlockHandler == null) {
            this.skyBlockHandler = new SkyBlockHandlerSuperior(this);
        }

        return this.skyBlockHandler;
    }
}
