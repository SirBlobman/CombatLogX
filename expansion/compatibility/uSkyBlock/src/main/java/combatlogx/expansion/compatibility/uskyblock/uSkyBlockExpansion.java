package combatlogx.expansion.compatibility.uskyblock;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.skyblock.SkyBlockExpansion;
import com.github.sirblobman.combatlogx.api.expansion.skyblock.SkyBlockHandler;

public final class uSkyBlockExpansion extends SkyBlockExpansion {
    private SkyBlockHandler<?> skyBlockHandler;

    public uSkyBlockExpansion(ICombatLogX plugin) {
        super(plugin);
        this.skyBlockHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("uSkyBlock", true, "3");
    }

    @Override
    public @NotNull SkyBlockHandler<?> getSkyBlockHandler() {
        if (this.skyBlockHandler == null) {
            this.skyBlockHandler = new SkyBlockHandler_uSkyBlock(this);
        }

        return this.skyBlockHandler;
    }
}
