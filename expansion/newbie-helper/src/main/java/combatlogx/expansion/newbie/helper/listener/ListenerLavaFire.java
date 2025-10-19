package combatlogx.expansion.newbie.helper.listener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

import com.github.sirblobman.api.location.BlockLocation;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import com.github.sirblobman.api.shaded.xseries.XMaterial;

import combatlogx.expansion.newbie.helper.NewbieHelperExpansion;
import combatlogx.expansion.newbie.helper.configuration.NewbieHelperConfiguration;
import combatlogx.expansion.newbie.helper.manager.ProtectionManager;

public final class ListenerLavaFire extends ExpansionListener {
    private final NewbieHelperExpansion expansion;
    private final Set<BlockLocation> playerHazardSet;

    public ListenerLavaFire(@NotNull NewbieHelperExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
        this.playerHazardSet = new HashSet<>();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        XMaterial bucketType = XMaterial.matchXMaterial(e.getBucket());
        if(bucketType != XMaterial.LAVA_BUCKET) {
            return;
        }

        Block clickedBlock = e.getBlockClicked();
        BlockFace clickedFace = e.getBlockFace();
        Block newLava = clickedBlock.getRelative(clickedFace);
        BlockLocation lavaLocation = BlockLocation.from(newLava);
        addPlayerHazard(lavaLocation);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBucketFill(PlayerBucketFillEvent e) {
        Block clickedBlock = e.getBlockClicked();
        XMaterial clickedBlockType = XMaterial.matchXMaterial(clickedBlock.getType());
        if (clickedBlockType == XMaterial.LAVA) {
            BlockLocation lavaLocation = BlockLocation.from(clickedBlock);
            removePlayerHazard(lavaLocation);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {
        Block blockPlaced = e.getBlockPlaced();
        XMaterial blockPlacedType = XMaterial.matchXMaterial(blockPlaced.getType());
        if (blockPlacedType == XMaterial.FIRE) {
            BlockLocation fireLocation = BlockLocation.from(blockPlaced);
            addPlayerHazard(fireLocation);
        }

        if (blockPlacedType != XMaterial.FIRE && blockPlacedType != XMaterial.LAVA) {
            BlockLocation blockLocation = BlockLocation.from(blockPlaced);
            addPlayerHazard(blockLocation);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        Block blockBroken = e.getBlock();
        XMaterial blockBrokenType = XMaterial.matchXMaterial(blockBroken.getType());
        if (blockBrokenType == XMaterial.FIRE) {
            BlockLocation fireLocation = BlockLocation.from(blockBroken);
            removePlayerHazard(fireLocation);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFireFade(@NotNull BlockFadeEvent e) {
        Block block = e.getBlock();
        XMaterial blockType = XMaterial.matchXMaterial(block.getType());
        if (blockType == XMaterial.FIRE) {
            BlockLocation fireLocation = BlockLocation.from(block);
            removePlayerHazard(fireLocation);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFireSpread(@NotNull BlockSpreadEvent e) {
        Block source = e.getSource();
        BlockLocation sourceLocation = BlockLocation.from(source);
        if (isPlayerHazard(sourceLocation)) {
            Block block = e.getBlock();
            BlockLocation fireLocation = BlockLocation.from(block);
            removePlayerHazard(fireLocation);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLavaFlow(@NotNull BlockFromToEvent e) {
        Block sourceBlock = e.getBlock();
        BlockLocation sourceLocation = BlockLocation.from(sourceBlock);
        if (isPlayerHazard(sourceLocation)) {
            Block toBlock = e.getToBlock();
            BlockLocation toLocation = BlockLocation.from(toBlock);
            addPlayerHazard(toLocation);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDamage(EntityDamageByBlockEvent e) {
        Entity damaged = e.getEntity();
        if (!(damaged instanceof Player player)) {
            return;
        }

        if (isBlockProtection()) {
            NewbieHelperExpansion expansion = getNewbieHelperExpansion();
            BlockLocation blockLocation = BlockLocation.from(e.getDamager());
            ProtectionManager protectionManager = expansion.getProtectionManager();
            if (protectionManager.isProtected(player) && isPlayerHazard(blockLocation)) {
                printDebug("Detected damage from player-placed hazard, cancelled.");
                e.setCancelled(true);
            }
        }
    }

    private void addPlayerHazard(@NotNull BlockLocation location) {
        printDebug("Detected player-placed lava/fire at " + location);
        this.playerHazardSet.add(location);
    }

    private void removePlayerHazard(@NotNull BlockLocation location) {
        if (this.playerHazardSet.remove(location)) {
            printDebug("Detected removal of player lava/fire at " + location);
        }
    }

    private boolean isPlayerHazard(@NotNull BlockLocation location) {
        return this.playerHazardSet.contains(location);
    }

    private @NotNull NewbieHelperExpansion getNewbieHelperExpansion() {
        return this.expansion;
    }

    private @NotNull NewbieHelperConfiguration getConfiguration() {
        NewbieHelperExpansion expansion = getNewbieHelperExpansion();
        return expansion.getConfiguration();
    }

    private boolean isBlockProtection() {
        NewbieHelperConfiguration configuration = getConfiguration();
        return configuration.isBlockProtection();
    }
}
