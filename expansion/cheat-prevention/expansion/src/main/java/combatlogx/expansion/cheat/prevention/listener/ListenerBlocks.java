package combatlogx.expansion.cheat.prevention.listener;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import combatlogx.expansion.cheat.prevention.CheatPreventionExpansion;

public final class ListenerBlocks extends CheatPreventionListener {
    public ListenerBlocks(CheatPreventionExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onInteract(PlayerInteractEvent e) {
        Action action = e.getAction();
        if(action != Action.RIGHT_CLICK_BLOCK && action != Action.LEFT_CLICK_BLOCK) return;

        Player player = e.getPlayer();
        if(!isInCombat(player)) return;
        if(canInteract()) return;

        e.setCancelled(true);
        sendMessage(player, "expansion.cheat-prevention.blocks.prevent-interaction", null);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if(!isInCombat(player)) return;

        Block block = e.getBlock();
        Material material = block.getType();
        if(canBreak(material)) return;

        e.setCancelled(true);
        sendMessage(player, "expansion.cheat-prevention.blocks.prevent-breaking", null);
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        if(!isInCombat(player)) return;

        Block block = e.getBlock();
        Material material = block.getType();
        if(canPlace(material)) return;

        e.setCancelled(true);
        sendMessage(player, "expansion.cheat-prevention.blocks.prevent-placing", null);
    }

    private YamlConfiguration getConfiguration() {
        Expansion expansion = getExpansion();
        ConfigurationManager configurationManager = expansion.getConfigurationManager();
        return configurationManager.get("blocks.yml");
    }

    private boolean canBreak(Material material) {
        YamlConfiguration configuration = getConfiguration();
        if(!configuration.getBoolean("prevent-breaking")) return true;

        String materialName = material.name();
        List<String> noBreakList = configuration.getStringList("prevent-breaking-list");
        return (!noBreakList.contains("*") && !noBreakList.contains(materialName));
    }

    private boolean canPlace(Material material) {
        YamlConfiguration configuration = getConfiguration();
        if(!configuration.getBoolean("prevent-placing")) return true;

        String materialName = material.name();
        List<String> noBreakList = configuration.getStringList("prevent-placing-list");
        return (!noBreakList.contains("*") && !noBreakList.contains(materialName));
    }

    private boolean canInteract() {
        YamlConfiguration configuration = getConfiguration();
        return !configuration.getBoolean("prevent-interaction");
    }
}