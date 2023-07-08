package combatlogx.expansion.compatibility.cmi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.expansion.vanish.VanishHandler;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.PlayerManager;

public final class VanishHandlerCMI extends VanishHandler<CMIExpansion> {
    public VanishHandlerCMI(@NotNull CMIExpansion expansion) {
        super(expansion);
    }

    @Override
    public boolean isVanished(@NotNull Player player) {
        CMIUser user = getUser(player);
        return (user != null && user.isVanished());
    }

    private @NotNull CMI getCMI() {
        return CMI.getInstance();
    }

    private @NotNull PlayerManager getPlayerManager() {
        CMI cmi = getCMI();
        return cmi.getPlayerManager();
    }

    private @Nullable CMIUser getUser(@NotNull Player player) {
        PlayerManager playerManager = getPlayerManager();
        return playerManager.getUser(player);
    }
}
