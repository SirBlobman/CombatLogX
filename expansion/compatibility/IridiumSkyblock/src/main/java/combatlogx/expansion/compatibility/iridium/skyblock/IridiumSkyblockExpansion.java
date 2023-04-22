package combatlogx.expansion.compatibility.iridium.skyblock;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.skyblock.SkyBlockExpansion;
import com.github.sirblobman.combatlogx.api.expansion.skyblock.SkyBlockHandler;

public final class IridiumSkyblockExpansion extends SkyBlockExpansion {
    private SkyBlockHandler<?> skyBlockHandler;

    public IridiumSkyblockExpansion(ICombatLogX plugin) {
        super(plugin);
        this.skyBlockHandler = null;
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("IridiumSkyblock", true, "4");
    }

    @Override
    public @NotNull SkyBlockHandler<?> getSkyBlockHandler() {
        if (this.skyBlockHandler == null) {
            this.skyBlockHandler = new SkyBlockHandlerIridium(this);
        }

        return this.skyBlockHandler;
    }
}
