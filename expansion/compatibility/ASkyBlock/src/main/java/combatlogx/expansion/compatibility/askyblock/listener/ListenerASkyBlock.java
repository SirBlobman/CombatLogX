package combatlogx.expansion.compatibility.askyblock.listener;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.combatlogx.api.event.PlayerPreTagEvent;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.Island;
import org.jetbrains.annotations.Nullable;

public final class ListenerASkyBlock extends ExpansionListener {
    public ListenerASkyBlock(Expansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void beforeTag(PlayerPreTagEvent e) {
        Entity enemy = e.getEnemy();
        if (!(enemy instanceof Player)) {
            return;
        }

        Player playerEnemy = (Player) enemy;
        Player player = e.getPlayer();
        if (doesTeamMatch(player, playerEnemy)) {
            e.setCancelled(true);
        }
    }

    @Nullable
    private Island getIsland(Player player) {
        if (player == null) {
            return null;
        }

        UUID playerId = player.getUniqueId();
        ASkyBlockAPI api = ASkyBlockAPI.getInstance();
        return api.getIslandOwnedBy(playerId);
    }

    private boolean doesTeamMatch(Player player1, Player player2) {
        UUID playerId1 = player1.getUniqueId();
        UUID playerId2 = player2.getUniqueId();
        if (playerId1.equals(playerId2)) {
            return true;
        }

        Island island = getIsland(player1);
        if (island == null) {
            return false;
        }

        List<UUID> memberIdList = island.getMembers();
        return memberIdList.contains(playerId2);
    }
}
