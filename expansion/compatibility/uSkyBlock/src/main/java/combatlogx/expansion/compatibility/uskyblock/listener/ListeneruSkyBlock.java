package combatlogx.expansion.compatibility.uskyblock.listener;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.PluginManager;

import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.ExpansionListener;

import us.talabrek.ultimateskyblock.api.IslandInfo;
import us.talabrek.ultimateskyblock.api.uSkyBlockAPI;

public class ListeneruSkyBlock extends ExpansionListener {
    public ListeneruSkyBlock(Expansion expansion) {
        super(expansion);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void beforeTag(PlayerPreTagEvent e) {
        LivingEntity enemy = e.getEnemy();
        if(!(enemy instanceof Player)) return;
        Player playerEnemy = (Player) enemy;

        Player player = e.getPlayer();
        if(doesTeamMatch(player, playerEnemy)) e.setCancelled(true);
    }

    private IslandInfo getIsland(Player player) {
        if(player == null) return null;
        PluginManager pluginManager = Bukkit.getPluginManager();
        uSkyBlockAPI api = (uSkyBlockAPI) pluginManager.getPlugin("uSkyBlock");
        return (api == null ? null : api.getIslandInfo(player));
    }

    private boolean doesTeamMatch(Player player1, Player player2) {
        UUID uuid1 = player1.getUniqueId();
        UUID uuid2 = player2.getUniqueId();
        if(uuid1.equals(uuid2)) return true;

        IslandInfo island = getIsland(player1);
        if(island == null) return false;

        String playerName2 = player2.getName();
        Set<String> memberSet = island.getMembers();
        return memberSet.contains(playerName2);
    }
}