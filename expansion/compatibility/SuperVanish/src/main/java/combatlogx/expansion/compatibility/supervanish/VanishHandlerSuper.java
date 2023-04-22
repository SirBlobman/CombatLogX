package combatlogx.expansion.compatibility.supervanish;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.expansion.vanish.VanishHandler;

import de.myzelyam.api.vanish.VanishAPI;

public final class VanishHandlerSuper extends VanishHandler<SuperVanishExpansion> {
    public VanishHandlerSuper(@NotNull SuperVanishExpansion expansion) {
        super(expansion);
    }

    @Override
    public boolean isVanished(@NotNull Player player) {
        return VanishAPI.isInvisible(player);
    }
}
