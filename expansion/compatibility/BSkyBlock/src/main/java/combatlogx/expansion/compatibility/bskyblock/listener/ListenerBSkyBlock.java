package combatlogx.expansion.compatibility.bskyblock.listener;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.combatlogx.api.event.PlayerPreTagEvent;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.managers.AddonsManager;
import world.bentobox.bentobox.managers.IslandsManager;

public final class ListenerBSkyBlock extends ExpansionListener {
    public ListenerBSkyBlock(Expansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void beforeTag(PlayerPreTagEvent e) {
        LivingEntity enemy = e.getEnemy();
        if (!(enemy instanceof Player playerEnemy)) {
            return;
        }

        Player player = e.getPlayer();
        if (doesTeamMatch(player, playerEnemy)) {
            e.setCancelled(true);
        }
    }

    private Island getIsland(Player player) {
        if (player == null) {
            return null;
        }

        UUID playerId = player.getUniqueId();
        World world = player.getWorld();

        BentoBox bentoBox = JavaPlugin.getPlugin(BentoBox.class);
        AddonsManager addonsManager = bentoBox.getAddonsManager();
        Optional<Addon> optionalAddon = addonsManager.getAddonByName("BSkyBlock");

        if (optionalAddon.isPresent()) {
            Addon addon = optionalAddon.get();
            IslandsManager islandManager = addon.getIslands();
            return islandManager.getIsland(world, playerId);
        }

        return null;
    }

    private boolean doesTeamMatch(Player player1, Player player2) {
        World world1 = player1.getWorld();
        World world2 = player2.getWorld();

        UUID worldId1 = world1.getUID();
        UUID worldId2 = world2.getUID();
        if (!worldId1.equals(worldId2)) {
            return false;
        }

        UUID playerId1 = player1.getUniqueId();
        UUID playerId2 = player2.getUniqueId();
        if (playerId1.equals(playerId2)) {
            return true;
        }

        Island island = getIsland(player1);
        if (island == null) {
            return false;
        }

        Set<UUID> memberSet = island.getMemberSet();
        return memberSet.contains(playerId2);
    }
}
