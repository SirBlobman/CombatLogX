package combatlogx.expansion.compatibility.vanishgeneric;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.expansion.vanish.VanishHandler;

public final class VanishHandlerGeneric extends VanishHandler<GenericVanishExpansion> {
    public VanishHandlerGeneric(@NotNull final GenericVanishExpansion expansion) {
        super(expansion);
    }

    @Override
    public boolean isVanished(@NotNull final Player player) {
        return player.hasMetadata("vanished");
    }
}
