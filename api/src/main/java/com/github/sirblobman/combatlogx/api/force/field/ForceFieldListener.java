package com.github.sirblobman.combatlogx.api.force.field;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;

import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.api.xseries.XMaterial;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.ICombatManager;
import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.object.TagType;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.wrappers.WrappedBlockData;

public abstract class ForceFieldListener implements Listener {
    private final RegionExpansion expansion;
    private final ICombatLogX plugin;
    final Map<UUID, Set<Location>> fakeBlockMap;
    public ForceFieldListener(RegionExpansion expansion) {
        this.expansion = expansion;
        this.plugin = this.expansion.getPlugin();
        this.fakeBlockMap = new HashMap<>();
    }

    public final RegionExpansion getExpansion() {
        return this.expansion;
    }

    public final ICombatLogX getPlugin() {
        return this.plugin;
    }

    public void unregisterProtocol() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        if(!pluginManager.isPluginEnabled("ProtocolLib")) return;

        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.removePacketListeners(this.plugin.getPlugin());
    }

    public void registerProtocol() {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        PacketAdapter adapter = new ForceFieldAdapter(this);
        manager.addPacketListener(adapter);
    }

    WrappedBlockData wrappedData(WrappedBlockData data) {
        XMaterial xMaterial = getForceFieldMaterial();
        Material type = xMaterial.parseMaterial();
        data.setType(type);

        if(VersionUtility.getMinorVersion() < 13) {
            int typeData = xMaterial.getData();
            data.setData(typeData);
        }

        return data;
    }

    private Set<Location> getForceFieldArea(Player player, LivingEntity enemy) {
        Set<Location> area = new HashSet<>();
        Location playerLoc = player.getLocation();
        World world = playerLoc.getWorld();
        int radius = getForceFieldRadius();
        TagType tagType = (enemy == null ? TagType.UNKNOWN : (enemy instanceof Player ? TagType.PLAYER : TagType.MOB));

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

                    area.add(new Location(location3.getWorld(), location3.getBlockX(), location3.getBlockY(), location3.getBlockZ()));
                }
            }
        }
        return area;
    }

    @SuppressWarnings("deprecation")
    private void sendForceField(Player player, Location location) {
        XMaterial xMaterial = getForceFieldMaterial();
        Material material = xMaterial.parseMaterial();
        if(material == null) material = Material.AIR;

        if(VersionUtility.getMinorVersion() >= 13) {
            player.sendBlockChange(location, material.createBlockData());
            return;
        }

        byte data = xMaterial.getData();
        player.sendBlockChange(location, material, data);
    }

    @SuppressWarnings("deprecation")
    private void resetBlock(Player player, Location location) {
        Block block = location.getBlock();
        if(VersionUtility.getMinorVersion() >= 13) {
            player.sendBlockChange(location, block.getBlockData());
            return;
        }

        player.sendBlockChange(location, block.getType(), block.getData());
    }

    public void clearData() {
        this.fakeBlockMap.clear();
    }

    public void updateForceField(Player player) {
        ICombatManager combatManager = this.plugin.getCombatManager();
        if(!combatManager.isInCombat(player)) return;

        Location playerLoc = player.getLocation();
        if(isSafe(playerLoc, player)) return;

        LivingEntity enemy = combatManager.getEnemy(player);

        Set<Location> oldArea = new HashSet<>();
        Set<Location> area = getForceFieldArea(player, enemy);
        Set<Location> area2 = new HashSet<>(area);

        UUID uuid = player.getUniqueId();
        if(fakeBlockMap.containsKey(uuid)) {
            oldArea = fakeBlockMap.get(uuid);
            area.removeAll(oldArea);
            oldArea.removeAll(area2);
        }
        fakeBlockMap.remove(uuid);

        for(Location location : oldArea) resetBlock(player, location);
        for(Location location : area) sendForceField(player, location);
        fakeBlockMap.put(uuid, area2);
    }

    public void removeForceField(Player player) {
        UUID uuid = player.getUniqueId();
        if(!fakeBlockMap.containsKey(uuid)) return;

        Set<Location> locations = new HashSet<>(fakeBlockMap.get(uuid));
        fakeBlockMap.remove(uuid);
        for(Location location : locations) resetBlock(player, location);
    }

    boolean canPlace(Location location) {
        Block block = location.getBlock();
        Material type = block.getType();
        return (type == Material.AIR || !type.isSolid());
    }

    private boolean isSafeSurround(Location location, Player player, TagType tagType) {
        Set<BlockFace> faces = new HashSet<>(Arrays.asList(BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST));
        for(BlockFace face : faces) { if(!isSafe(location.getBlock().getRelative(face).getLocation(), player, tagType)) return true; }
        return false;
    }

    boolean isSafeSurround(Location location, Player player) {
        ICombatManager combatManager = this.plugin.getCombatManager();
        LivingEntity enemy = combatManager.getEnemy(player);

        TagType tagType = (enemy == null ? TagType.UNKNOWN : (enemy instanceof Player ? TagType.PLAYER : TagType.MOB));
        return isSafeSurround(location, player, tagType);
    }

    public abstract boolean isSafe(Location location, Player player, TagType tagType);
    public abstract boolean isSafe(Location location, Player player);

    public abstract boolean isEnabled();
    public abstract boolean canBypass(Player player);
    public abstract XMaterial getForceFieldMaterial();
    public abstract int getForceFieldRadius();

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        if(!isEnabled()) return;

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        fakeBlockMap.remove(uuid);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if(!isEnabled()) return;

        Player player = e.getPlayer();
        if(canBypass(player)) return;

        ICombatManager combatManager = this.plugin.getCombatManager();
        if(!combatManager.isInCombat(player)) return;

        Location toLoc = e.getTo();
        Location fromLoc = e.getFrom();
        if(toLoc == null) return;

        if(toLoc.getBlock().equals(fromLoc.getBlock())) return;
        if(isSafe(toLoc, player)) return;

        updateForceField(player);
    }

    @EventHandler
    public void onTag(PlayerTagEvent e) {
        if(!isEnabled()) return;

        Player player = e.getPlayer();
        if(canBypass(player)) return;

        Location playerLoc = player.getLocation();
        if(isSafe(playerLoc, player)) return;

        UUID uuid = player.getUniqueId();
        Set<Location> area = getForceFieldArea(player, e.getEnemy());

        fakeBlockMap.put(uuid, area);
        for(Location location : area) sendForceField(player, location);
    }

    @EventHandler
    public void onUntag(PlayerUntagEvent e) {
        if(!isEnabled()) return;

        Player player = e.getPlayer();
        removeForceField(player);
    }
}