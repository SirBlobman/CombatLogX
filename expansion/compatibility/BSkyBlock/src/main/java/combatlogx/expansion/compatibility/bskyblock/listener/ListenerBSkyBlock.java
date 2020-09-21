package combatlogx.expansion.compatibility.bskyblock.listener;

import java.util.Set;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.ExpansionListener;

import combatlogx.expansion.compatibility.bskyblock.hook.HookBentoBox;
import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.managers.IslandsManager;

public final class ListenerBSkyBlock extends ExpansionListener {
    public ListenerBSkyBlock(Expansion expansion) {
        super(expansion);
    }

    @EventHandler(priority= EventPriority.NORMAL, ignoreCancelled=true)
    public void beforeTag(PlayerPreTagEvent e) {
        LivingEntity enemy = e.getEnemy();
        if(!(enemy instanceof Player)) return;
        Player playerEnemy = (Player) enemy;

        Player player = e.getPlayer();
        if(doesTeamMatch(player, playerEnemy)) e.setCancelled(true);
    }

    private Island getIsland(Player player) {
        if(player == null) return null;
        UUID uuid = player.getUniqueId();
        World world = player.getWorld();

        Addon addon = HookBentoBox.getBSkyBlock();
        IslandsManager islandManager = addon.getIslands();
        return islandManager.getIsland(world, uuid);
    }

    private boolean doesTeamMatch(Player player1, Player player2) {
        World world1 = player1.getWorld(); UUID worldId1 = world1.getUID();
        World world2 = player2.getWorld(); UUID worldId2 = world2.getUID();
        if(!worldId1.equals(worldId2)) return false;

        UUID uuid1 = player1.getUniqueId();
        UUID uuid2 = player2.getUniqueId();
        if(uuid1.equals(uuid2)) return true;

        Island island = getIsland(player1);
        if(island == null) return false;

        Set<UUID> memberSet = island.getMemberSet();
        return memberSet.contains(uuid2);
    }
}