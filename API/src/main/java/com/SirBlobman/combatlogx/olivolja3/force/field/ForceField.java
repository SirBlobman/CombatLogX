package com.SirBlobman.combatlogx.olivolja3.force.field;

import com.SirBlobman.api.nms.NMS_Handler;
import com.SirBlobman.combatlogx.CombatLogX;
import com.SirBlobman.combatlogx.event.PlayerTagEvent;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.expansion.NoEntryExpansion;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public abstract class ForceField implements Listener {
    private final Map<UUID, Set<Location>> fakeBlocks = Util.newMap();
    private final CombatLogX plugin = JavaPlugin.getPlugin(CombatLogX.class);
    private final NoEntryExpansion expansion;
    public ForceField(NoEntryExpansion expansion) {
        this.expansion = expansion;
    }

    public void unregisterProtocol() {
        if(!PluginUtil.isEnabled("ProtocolLib")) return;

        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.removePacketListeners(plugin);
    }

    public void registerProtocol() {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        PacketAdapter adapter = new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.BLOCK_CHANGE) {
            @Override
            public void onPacketSending(PacketEvent e) {
                if(e.isCancelled()) return;

                Player player = e.getPlayer();
                if(!CombatUtil.isInCombat(player)) return;

                PacketContainer packet = e.getPacket();
                WrapperPlayServerBlockChange block = new WrapperPlayServerBlockChange(packet);

                World world = player.getWorld();
                Location location = packet.getBlockPositionModifier().read(0).toLocation(world);

                UUID uuid = player.getUniqueId();
                if(fakeBlocks.containsKey(uuid) && isSafe(location, player) && isSafeSurround(location, player) && canPlace(location) && fakeBlocks.get(uuid).contains(location)) {
                    WrappedBlockData blockData = wrappedData(block.getBlockData());
                    block.setBlockData(blockData);
                }
            }
        };
        manager.addPacketListener(adapter);
    }

    private WrappedBlockData wrappedData(WrappedBlockData data) {
        data.setType(getForceFieldMaterial());
        if(NMS_Handler.getMinorVersion() < 13) data.setData(getForceFieldMaterialData());
        return data;
    }

    private Set<Location> getForceFieldArea(Player player, LivingEntity enemy) {
        Set<Location> area = new HashSet<>();
        Location playerLoc = player.getLocation();
        World world = playerLoc.getWorld();
        int radius = getForceFieldRadius();
        PlayerTagEvent.TagType tagType = (enemy == null ? PlayerTagEvent.TagType.UNKNOWN : (enemy instanceof Player ? PlayerTagEvent.TagType.PLAYER : PlayerTagEvent.TagType.MOB));

        Location location1 = playerLoc.clone().add(radius, 0, radius);
        Location location2 = playerLoc.clone().subtract(radius, 0, radius);

        int topBlockX = Math.max(location1.getBlockX(), location2.getBlockX());
        int topBlockZ = Math.max(location1.getBlockZ(), location2.getBlockZ());
        int bottomBlockX = Math.min(location1.getBlockX(), location2.getBlockX());
        int bottomBlockZ = Math.min(location1.getBlockZ(), location2.getBlockZ());

        for(int x = bottomBlockX; x <= topBlockX; x++) {
            for(int z = bottomBlockZ; z <= topBlockZ; z++) {
                Location location = new Location(world, x, playerLoc.getY(), z);
                if(!isSafe(location, player, tagType)) continue;
                if(!isSafeSurround(location, player, tagType)) continue;

                for(int i = -radius; i < radius; i++) {
                    Location location3 = new Location(world, location.getX(), location.getY() + i, location.getZ());
                    if(!canPlace(location3)) continue;

                    area.add(location3.clone());
                }
            }
        }
        return area;
    }

    @SuppressWarnings("deprecation")
    private void sendForceField(Player player, Location location) {
        if(NMS_Handler.getMinorVersion() >= 13) {
            player.sendBlockChange(location, getForceFieldMaterial().createBlockData());
            return;
        }

        player.sendBlockChange(location, getForceFieldMaterial(), (byte) getForceFieldMaterialData());
    }

    @SuppressWarnings("deprecation")
    private void resetBlock(Player player, Location location) {
        Block block = location.getBlock();
        if(NMS_Handler.getMinorVersion() >= 13) {
            player.sendBlockChange(location, block.getBlockData());
            return;
        }

        player.sendBlockChange(location, block.getType(), block.getData());
    }

    public void clearData() {
        fakeBlocks.clear();
    }

    public void updateForceField(Player player) {
        if(!CombatUtil.isInCombat(player)) return;
        LivingEntity enemy = CombatUtil.getEnemy(player);

        Location playerLoc = player.getLocation();
        if(isSafe(playerLoc, player)) return;

        Set<Location> oldArea = new HashSet<>();
        Set<Location> area = getForceFieldArea(player, enemy);
        Set<Location> area2 = new HashSet<>(area);

        UUID uuid = player.getUniqueId();
        if(fakeBlocks.containsKey(uuid)) {
            oldArea = fakeBlocks.get(uuid);
            area.removeAll(oldArea);
            oldArea.removeAll(area2);
        }
        fakeBlocks.remove(uuid);

        for(Location location : oldArea) resetBlock(player, location);
        for(Location location : area) sendForceField(player, location);
        fakeBlocks.put(uuid, area2);
    }

    public void removeForceField(Player player) {
        UUID uuid = player.getUniqueId();
        if(!fakeBlocks.containsKey(uuid)) return;

        Set<Location> locations = new HashSet<>(fakeBlocks.get(uuid));
        fakeBlocks.remove(uuid);
        for(Location location : locations) resetBlock(player, location);
    }

    public static boolean canPlace(Location location) {
        Block block = location.getBlock();
        Material type = block.getType();
        return (type == Material.AIR || !type.isSolid());
    }

    public boolean isSafeSurround(Location location, Player player, PlayerTagEvent.TagType tagType) {
        Set<BlockFace> faces = new HashSet<>(Arrays.asList(BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST));
        for(BlockFace face : faces) { if(!isSafe(location.getBlock().getRelative(face).getLocation(), player, tagType)) return true; }
        return false;
    }

    public boolean isSafeSurround(Location location, Player player) {
        LivingEntity enemy = CombatUtil.getEnemy(player);
        PlayerTagEvent.TagType tagType = (enemy == null ? PlayerTagEvent.TagType.UNKNOWN : (enemy instanceof Player ? PlayerTagEvent.TagType.PLAYER : PlayerTagEvent.TagType.MOB));
        return isSafeSurround(location, player, tagType);
    }

    public abstract boolean isSafe(Location location, Player player, PlayerTagEvent.TagType tagType);
    public abstract boolean isSafe(Location location, Player player);
    public abstract boolean canBypass(Player player);
    public abstract Material getForceFieldMaterial();
    public abstract int getForceFieldMaterialData();
    public abstract int getForceFieldRadius();

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        fakeBlocks.remove(uuid);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if(!CombatUtil.isInCombat(player)) return;
        if(canBypass(player)) return;

        Location toLoc = e.getTo();
        Location fromLoc = e.getFrom();
        if(toLoc.getBlock().equals(fromLoc.getBlock())) return;
        if(!isSafe(toLoc, player)) return;

        updateForceField(player);
    }

    @EventHandler
    public void onTag(PlayerTagEvent e) {
        Player player = e.getPlayer();
        Location playerLoc = player.getLocation();
        if(isSafe(playerLoc, player)) return;

        UUID uuid = player.getUniqueId();
        Set<Location> area = getForceFieldArea(player, e.getEnemy());

        fakeBlocks.put(uuid, area);
        for(Location location : area) sendForceField(player, location);
    }

    @EventHandler
    public void onUntag(PlayerUntagEvent e) {
        Player player = e.getPlayer();
        removeForceField(player);
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent e) {
        Player player = e.getPlayer();
        if(!CombatUtil.isInCombat(player)) return;

        UUID uuid = player.getUniqueId();
        Block block = e.getBlock();
        Location location = block.getLocation();
        if(fakeBlocks.containsKey(uuid)) {
            Set<Location> locations = fakeBlocks.get(uuid);
            if(locations.contains(location)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if(!CombatUtil.isInCombat(player)) return;

        UUID uuid = player.getUniqueId();
        Block block = e.getBlock();
        Location location = block.getLocation();
        if(fakeBlocks.containsKey(uuid)) {
            Set<Location> locations = fakeBlocks.get(uuid);
            if(locations.contains(location)) {
                e.setCancelled(true);
            }
        }
    }
}