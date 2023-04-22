package combatlogx.expansion.compatibility.essentials;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.combatlogx.api.expansion.vanish.VanishHandler;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

public final class VanishHandlerEssentialsX extends VanishHandler<EssentialsExpansion> {
    public VanishHandlerEssentialsX(@NotNull EssentialsExpansion expansion) {
        super(expansion);
    }

    @Override
    public boolean isVanished(@NotNull Player player) {
        User user = getUser(player);
        return user.isVanished();
    }

    private @NotNull Essentials getEssentials() {
        return JavaPlugin.getPlugin(Essentials.class);
    }

    private @NotNull User getUser(@NotNull Player player) {
        Essentials essentials = getEssentials();
        return essentials.getUser(player);
    }
}
