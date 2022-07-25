package combatlogx.expansion.compatibility.superior.skyblock.listener;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.combatlogx.api.event.PlayerPreTagEvent;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.jetbrains.annotations.Nullable;

public final class ListenerSuperiorSkyblock extends ExpansionListener {
    public ListenerSuperiorSkyblock(Expansion expansion) {
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

        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(player);
        return superiorPlayer.getIsland();
    }

    private Set<UUID> getMemberIds(Island island) {
        if (island == null) {
            return Collections.emptySet();
        }

        List<SuperiorPlayer> islandMemberList = island.getIslandMembers(true);
        Set<UUID> islandMemberIdSet = new HashSet<>();
        for (SuperiorPlayer islandMember : islandMemberList) {
            UUID islandMemberId = islandMember.getUniqueId();
            islandMemberIdSet.add(islandMemberId);
        }

        return Collections.unmodifiableSet(islandMemberIdSet);
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

        Set<UUID> islandMemberIdSet = getMemberIds(island);
        return islandMemberIdSet.contains(playerId2);
    }
}
