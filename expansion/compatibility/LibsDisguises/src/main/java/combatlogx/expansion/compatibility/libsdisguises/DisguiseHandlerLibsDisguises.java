package combatlogx.expansion.compatibility.libsdisguises;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.expansion.disguise.DisguiseHandler;

import me.libraryaddict.disguise.DisguiseAPI;

public class DisguiseHandlerLibsDisguises extends DisguiseHandler<LibsDisguisesExpansion> {
    public DisguiseHandlerLibsDisguises(@NotNull LibsDisguisesExpansion expansion) {
        super(expansion);
    }

    @Override
    public boolean hasDisguise(@NotNull Player player) {
        return DisguiseAPI.isDisguised(player);
    }

    @Override
    public void removeDisguise(@NotNull Player player) {
        DisguiseAPI.undisguiseToAll(player);
    }
}
