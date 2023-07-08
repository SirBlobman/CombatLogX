package combatlogx.expansion.compatibility.idisguise;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.expansion.disguise.DisguiseHandler;

import de.robingrether.idisguise.api.DisguiseAPI;
import de.robingrether.idisguise.iDisguise;

public final class DisguiseHandler_iDisguise extends DisguiseHandler<Expansion_iDisguise> {
    public DisguiseHandler_iDisguise(@NotNull Expansion_iDisguise expansion) {
        super(expansion);
    }

    @Override
    public boolean hasDisguise(@NotNull Player player) {
        DisguiseAPI api = getAPI();
        return api.isDisguised(player);
    }

    @Override
    public void removeDisguise(@NotNull Player player) {
        DisguiseAPI api = getAPI();
        api.undisguise(player);
    }

    private @NotNull DisguiseAPI getAPI() {
        iDisguise plugin = iDisguise.getInstance();
        return plugin.getAPI();
    }
}
