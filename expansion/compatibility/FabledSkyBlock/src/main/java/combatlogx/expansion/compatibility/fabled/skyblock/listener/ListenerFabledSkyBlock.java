package combatlogx.expansion.compatibility.fabled.skyblock.listener;

import java.util.UUID;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.combatlogx.api.event.PlayerPreTagEvent;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import com.songoda.skyblock.api.SkyBlockAPI;
import com.songoda.skyblock.api.island.Island;
import com.songoda.skyblock.api.island.IslandManager;
import org.jetbrains.annotations.Nullable;

public final class ListenerFabledSkyBlock extends ExpansionListener {
    public ListenerFabledSkyBlock(Expansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void beforeTag(PlayerPreTagEvent e) {
        LivingEntity enemy = e.getEnemy();
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

        IslandManager islandManager = SkyBlockAPI.getIslandManager();
        return islandManager.getIsland(player);
    }

    private boolean doesTeamMatch(Player player1, Player player2) {
        UUID playerId1 = player1.getUniqueId();
        UUID playerId2 = player2.getUniqueId();
        if (playerId1.equals(playerId2)) {
            return true;
        }

        Island island1 = getIsland(player1);
        if (island1 == null) {
            return false;
        }

        Island island2 = getIsland(player2);
        if (island2 == null) {
            return false;
        }

        UUID islandId1 = island1.getIslandUUID();
        UUID islandId2 = island2.getIslandUUID();
        return islandId1.equals(islandId2);
    }
}
