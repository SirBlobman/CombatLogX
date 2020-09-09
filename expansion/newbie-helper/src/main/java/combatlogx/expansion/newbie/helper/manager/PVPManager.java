package combatlogx.expansion.newbie.helper.manager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

public final class PVPManager {
    private final Set<UUID> disabledSet;
    public PVPManager() {
        this.disabledSet = new HashSet<>();
    }

    public void setPVP(Player player, boolean pvp) {
        UUID uuid = player.getUniqueId();
        if(pvp) {
            this.disabledSet.remove(uuid);
            return;
        }

        this.disabledSet.add(uuid);
    }

    public boolean isDisabled(Player player) {
        UUID uuid = player.getUniqueId();
        return this.disabledSet.contains(uuid);
    }
}