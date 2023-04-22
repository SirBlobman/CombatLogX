package combatlogx.expansion.compatibility.vnp;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.combatlogx.api.expansion.vanish.VanishHandler;

import org.kitteh.vanish.VanishManager;
import org.kitteh.vanish.VanishPlugin;

public final class VanishHandlerNoPacket extends VanishHandler<VanishNoPacketExpansion> {
    public VanishHandlerNoPacket(@NotNull VanishNoPacketExpansion expansion) {
        super(expansion);
    }

    @Override
    public boolean isVanished(@NotNull Player player) {
        VanishManager vanishManager = getVanishManager();
        return vanishManager.isVanished(player);
    }

    private @NotNull VanishPlugin getVanish() {
        return JavaPlugin.getPlugin(VanishPlugin.class);
    }

    private @NotNull VanishManager getVanishManager() {
        VanishPlugin plugin = getVanish();
        return plugin.getManager();
    }
}
