package combatlogx.expansion.compatibility.iridium.skyblock.listener;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.combatlogx.api.event.PlayerPreTagEvent;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import com.iridium.iridiumskyblock.api.IridiumSkyblockAPI;
import com.iridium.iridiumskyblock.database.Island;
import com.iridium.iridiumskyblock.database.User;

public final class ListenerIridiumSkyblock extends ExpansionListener {
    public ListenerIridiumSkyblock(Expansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void beforeTag(PlayerPreTagEvent e) {
        LivingEntity enemy = e.getEnemy();
        if (!(enemy instanceof Player)) return;
        Player playerEnemy = (Player) enemy;

        Player player = e.getPlayer();
        if (doesTeamMatch(player, playerEnemy)) e.setCancelled(true);
    }

    private Island getIsland(Player player) {
        if (player == null) return null;

        IridiumSkyblockAPI api = IridiumSkyblockAPI.getInstance();
        User user = api.getUser(player);

        Optional<Island> optionalIsland = user.getIsland();
        return optionalIsland.orElse(null);
    }

    private boolean doesTeamMatch(Player player1, Player player2) {
        UUID uuid1 = player1.getUniqueId();
        UUID uuid2 = player2.getUniqueId();
        if (uuid1.equals(uuid2)) return true;

        Island island1 = getIsland(player1);
        if (island1 == null) return false;

        Island island2 = getIsland(player2);
        if (island2 == null) return false;

        int islandId1 = island1.getId();
        int islandId2 = island2.getId();
        return (islandId1 == islandId2);
    }
}
