package combatlogx.expansion.compatibility.fabled.skyblock;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.skyblock.SkyBlockExpansion;
import com.github.sirblobman.combatlogx.api.expansion.skyblock.SkyBlockHandler;

public final class FabledSkyBlockExpansion extends SkyBlockExpansion {
    private SkyBlockHandler<?> skyBlockHandler;

    public FabledSkyBlockExpansion(@NotNull ICombatLogX plugin) {
        super(plugin);
        this.skyBlockHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("FabledSkyBlock", true);
    }

    @Override
    public @NotNull SkyBlockHandler<?> getSkyBlockHandler() {
        if (this.skyBlockHandler == null) {
            this.skyBlockHandler = new SkyBlockHandlerFabled(this);
        }

        return this.skyBlockHandler;
    }
}
