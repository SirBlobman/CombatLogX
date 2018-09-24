package com.SirBlobman.worldguard.olivolja3;

import static org.bukkit.block.BlockFace.EAST;
import static org.bukkit.block.BlockFace.NORTH;
import static org.bukkit.block.BlockFace.SOUTH;
import static org.bukkit.block.BlockFace.WEST;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.SirBlobman.combatlogx.Combat;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent;
import com.SirBlobman.worldguard.WorldGuardUtil;
import com.SirBlobman.worldguard.config.ConfigWorldGuard;

public final class ForceField implements Listener {

    private static final List<BlockFace> ALL_DIRECTIONS = Arrays.asList(NORTH, EAST, SOUTH, WEST);
    private static final int radius = ConfigWorldGuard.OPTION_FORCEFIELD_SIZE;

    private Map<UUID, Set<Location>> previousUpdates = new HashMap<>();

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void updateViewedBlocks(PlayerMoveEvent e) {
        if (!ConfigWorldGuard.OPTION_FORCEFIELD_ENABLED)
            return;
        Player p = e.getPlayer();
        if (!Combat.isInCombat(p))
            return;
        Location t = e.getTo();
        Location f = e.getFrom();
        if (t.getBlockX() == f.getBlockX() && t.getBlockY() == f.getBlockY() && t.getBlockZ() == f.getBlockZ())
            return;
        Set<Location> changedBlocks = getChangedBlocks(p);
        Set<Location> removeBlocks = new HashSet<>();
        if (previousUpdates.containsKey(p.getUniqueId()))
            removeBlocks = previousUpdates.get(p.getUniqueId());
        for (Location location : changedBlocks) {
            p.sendBlockChange(location,
                    Material.getMaterial(WorldGuardUtil.getMaterial(ConfigWorldGuard.OPTION_FORCEFIELD_MATERIAL)),
                    WorldGuardUtil.getData(ConfigWorldGuard.OPTION_FORCEFIELD_MATERIAL));
            removeBlocks.remove(location);
        }
        for (Location location : removeBlocks) {
            Block block = location.getBlock();
            p.sendBlockChange(location, block.getType(), block.getData());
        }
        previousUpdates.put(p.getUniqueId(), changedBlocks);
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void pue(PlayerUntagEvent e) {
        Player p = e.getPlayer();
        Set<Location> removeBlocks;
        if (previousUpdates.containsKey(p.getUniqueId())) {
            removeBlocks = previousUpdates.get(p.getUniqueId());
        } else {
            removeBlocks = new HashSet<>();
        }
        for (Location location : removeBlocks) {
            Block block = location.getBlock();
            p.sendBlockChange(location, block.getType(), block.getData());
        }
        previousUpdates.remove(p.getUniqueId());
    }

    private Set<Location> getChangedBlocks(Player p) {
        Set<Location> locations = new HashSet<>();

        if (!Combat.isInCombat(p))
            return locations;

        Location l = p.getLocation();
        Location loc1 = l.clone().add(radius, 0, radius);
        Location loc2 = l.clone().subtract(radius, 0, radius);
        int topBlockX = loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX();
        int bottomBlockX = loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX();
        int topBlockZ = loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ();
        int bottomBlockZ = loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ();

        for (int x = bottomBlockX; x <= topBlockX; x++) {
            for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                Location location = new Location(l.getWorld(), (double) x, l.getY(), (double) z);
                if (WorldGuardUtil.isSafeZone(location))
                    continue;
                if (!isPvpSurrounding(location))
                    continue;
                for (int i = -radius; i < radius; i++) {
                    Location loc = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
                    loc.setY(loc.getY() + i);
                    if (!(loc.getBlock().getType().equals(Material.AIR)
                            || loc.getBlock().getType().equals(Material.STATIONARY_WATER)
                            || loc.getBlock().getType().equals(Material.WATER)
                            || loc.getBlock().getType().equals(Material.STATIONARY_LAVA)
                            || loc.getBlock().getType().equals(Material.LAVA)))
                        continue;
                    locations.add(new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
                }
            }
        }
        return locations;
    }

    private boolean isPvpSurrounding(Location loc) {
        for (BlockFace direction : ALL_DIRECTIONS) {
            if (WorldGuardUtil.isSafeZone(loc.getBlock().getRelative(direction).getLocation())) {
                return true;
            }
        }
        return false;
    }
}
