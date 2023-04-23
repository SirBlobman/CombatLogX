package combatlogx.expansion.cheat.prevention.listener;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.api.shaded.xseries.XBlock;
import com.github.sirblobman.api.shaded.xseries.XMaterial;

import combatlogx.expansion.cheat.prevention.ICheatPreventionExpansion;
import combatlogx.expansion.cheat.prevention.configuration.IBlockConfiguration;

public final class ListenerBlocks extends CheatPreventionListener {
    public ListenerBlocks(@NotNull ICheatPreventionExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        Action action = e.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.LEFT_CLICK_BLOCK && action != Action.PHYSICAL) {
            return;
        }

        Block block = e.getClickedBlock();
        if (block == null) {
            return;
        }

        Player player = e.getPlayer();
        if (isPreventInteract(fetchMaterial(block)) && isInCombat(player)) {
            e.setCancelled(true);
            sendMessage(player, "expansion.cheat-prevention.blocks.prevent-interaction");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlock();

        if (isPreventBreak(fetchMaterial(block)) && isInCombat(player)) {
            e.setCancelled(true);
            sendMessage(player, "expansion.cheat-prevention.blocks.prevent-breaking");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlock();

        if (isPreventPlace(fetchMaterial(block)) && isInCombat(player)) {
            e.setCancelled(true);
            sendMessage(player, "expansion.cheat-prevention.blocks.prevent-placing");
        }
    }

    private @NotNull IBlockConfiguration getBlockConfiguration() {
        ICheatPreventionExpansion expansion = getCheatPrevention();
        return expansion.getBlockConfiguration();
    }

    private boolean isPreventBreak(@NotNull XMaterial material) {
        IBlockConfiguration blockConfiguration = getBlockConfiguration();
        return blockConfiguration.isPreventBreaking(material);
    }

    private boolean isPreventPlace(@NotNull XMaterial material) {
        IBlockConfiguration blockConfiguration = getBlockConfiguration();
        return blockConfiguration.isPreventPlacing(material);
    }

    private boolean isPreventInteract(@NotNull XMaterial material) {
        IBlockConfiguration blockConfiguration = getBlockConfiguration();
        return blockConfiguration.isPreventInteraction(material);
    }

    @SuppressWarnings("deprecation")
    private @NotNull XMaterial fetchMaterial(Block block) {
        int minorVersion = VersionUtility.getMinorVersion();
        Material bukkitType = block.getType();

        if (minorVersion < 13) {
            return XBlock.getType(block);
        } else {
            return XMaterial.matchXMaterial(bukkitType);
        }
    }
}
