package combatlogx.expansion.compatibility.idisguise;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.expansion.disguise.DisguiseHandler;

import de.luisagrether.idisguise.iDisguise;

public final class DisguiseHandler_iDisguise extends DisguiseHandler<Expansion_iDisguise> {
    public DisguiseHandler_iDisguise(@NotNull Expansion_iDisguise expansion) {
        super(expansion);
    }

    @Override
    public boolean hasDisguise(@NotNull Player player) {
        iDisguise plugin = iDisguise.getInstance();
        return plugin.isDisguised(player);
    }

    @Override
    public void removeDisguise(@NotNull Player player) {
        iDisguise plugin = iDisguise.getInstance();
        plugin.undisguise(player);
    }
}
