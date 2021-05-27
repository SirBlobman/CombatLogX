package com.github.sirblobman.combatlogx.force.field;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.object.WorldXYZ;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.api.xseries.XMaterial;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.ICombatManager;
import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.object.TagType;
import com.github.sirblobman.combatlogx.listener.CombatListener;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.wrappers.WrappedBlockData;

public class ListenerForceField extends CombatListener {
    protected final Map<UUID, Set<WorldXYZ>> fakeBlockMap;
    public ListenerForceField(ICombatLogX plugin) {
        super(plugin);
        this.fakeBlockMap = new HashMap<>();
    }

    public void registerProtocol() {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        ForceFieldAdapter forceFieldAdapter = new ForceFieldAdapter(this);
        protocolManager.addPacketListener(forceFieldAdapter);
    }

    public void removeProtocol() {
        JavaPlugin plugin = getPlugin().getPlugin();
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.removePacketListeners(plugin);

        for(Player player : Bukkit.getOnlinePlayers()) {
            removeForceField(player);
        }
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void onUntag(PlayerUntagEvent e) {
        if(!isEnabled()) return;

        Player player = e.getPlayer();
        removeForceField(player);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        if(!isEnabled()) return;

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        fakeBlockMap.remove(uuid);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Location fromLocation = e.getFrom();
        Location toLocation = e.getTo();
        if(toLocation == null) return;
        if(!isEnabled()) return;

        Player player = e.getPlayer();
        if(canBypass(player)) return;

        ICombatLogX plugin = getPlugin();
        ICombatManager combatManager = plugin.getCombatManager();
        if(!combatManager.isInCombat(player)) return;

        if(Objects.equals(WorldXYZ.from(fromLocation), WorldXYZ.from(toLocation))) return;
        if(isSafe(player, toLocation)) return;
        updateForceField(player);
    }

    @EventHandler
    public void onTag(PlayerTagEvent e) {
        if(!isEnabled()) return;

        Player player = e.getPlayer();
        if(canBypass(player)) return;

        Location playerLocation = player.getLocation();
        if(isSafe(player, playerLocation)) return;

        UUID uuid = player.getUniqueId();
        Set<WorldXYZ> area = getForceFieldArea(player, e.getEnemy());

        fakeBlockMap.put(uuid, area);
        for(WorldXYZ worldXYZ : area) {
            Location location = worldXYZ.asLocation();
            if(location != null) sendForceField(player, location);
        }
    }

    public boolean isEnabled() {
        YamlConfiguration configuration = getConfiguration();
        return configuration.getBoolean("enabled", true);
    }

    protected WrappedBlockData wrapData(WrappedBlockData data) {
        XMaterial material = getForceFieldMaterial();
        Material bukkitMaterial = material.parseMaterial();
        data.setType(bukkitMaterial);

        if(VersionUtility.getMinorVersion() < 13) {
            int typeData = material.getData();
            data.setData(typeData);
        }

        return data;
    }

    private Set<WorldXYZ> getForceFieldArea(Player player, LivingEntity enemy) {
        Set<WorldXYZ> area = new HashSet<>();
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
                if(!isSafe(player, location, tagType)) continue;
                if(!isSafeSurround(location, player, tagType)) continue;

                for(int i = -radius; i < radius; i++) {
                    Location location3 = new Location(world, location.getX(), location.getY() + i, location.getZ());
                    if(!canPlace(location3)) continue;

                    Location newLocation = new Location(location3.getWorld(), location3.getBlockX(), location3.getBlockY(), location3.getBlockZ());
                    area.add(WorldXYZ.from(newLocation));
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
        ICombatManager combatManager = getPlugin().getCombatManager();
        if(!combatManager.isInCombat(player)) return;

        Location playerLocation = player.getLocation();
        if(isSafe(player, playerLocation)) return;

        LivingEntity enemy = combatManager.getEnemy(player);
        Set<WorldXYZ> oldArea = new HashSet<>();
        Set<WorldXYZ> area = getForceFieldArea(player, enemy);
        Set<WorldXYZ> area2 = new HashSet<>(area);

        UUID uuid = player.getUniqueId();
        if(fakeBlockMap.containsKey(uuid)) {
            oldArea = fakeBlockMap.get(uuid);
            area.removeAll(oldArea);
            oldArea.removeAll(area2);
        }

        fakeBlockMap.remove(uuid);
        for(WorldXYZ worldXYZ : oldArea) {
            Location location = worldXYZ.asLocation();
            if(location != null) resetBlock(player, location);
        }

        for(WorldXYZ worldXYZ : area) {
            Location location = worldXYZ.asLocation();
            if(location != null) sendForceField(player, location);
        }

        fakeBlockMap.put(uuid, area2);
    }

    public void removeForceField(Player player) {
        UUID uuid = player.getUniqueId();
        if(!this.fakeBlockMap.containsKey(uuid)) return;

        Set<WorldXYZ> locationSet = new HashSet<>(this.fakeBlockMap.remove(uuid));
        for(WorldXYZ worldXYZ : locationSet) {
            Location location = worldXYZ.asLocation();
            if(location != null) resetBlock(player, location);
        }
    }

    boolean canPlace(Location location) {
        Block block = location.getBlock();
        Material material = block.getType();
        return (material == Material.AIR || !material.isSolid());
    }

    private boolean isSafeSurround(Location location, Player player, TagType tagType) {
        Block centerBlock = location.getBlock();
        BlockFace[] faceArray = {BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};

        for(BlockFace face : faceArray) {
            Location relativeLocation = centerBlock.getRelative(face).getLocation();
            if(!isSafe(player, relativeLocation)) {
                return true;
            }
        }

        return false;
    }

    boolean isSafeSurround(Location location, Player player) {
        ICombatLogX plugin = getPlugin();
        ICombatManager combatManager = plugin.getCombatManager();
        LivingEntity enemy = combatManager.getEnemy(player);

        TagType tagType = (enemy == null ? TagType.UNKNOWN : (enemy instanceof Player ? TagType.PLAYER : TagType.MOB));
        return isSafeSurround(location, player, tagType);
    }

    private boolean isSafe(Player player, Location location, TagType tagType) {
        ICombatLogX plugin = getPlugin();
        ExpansionManager expansionManager = plugin.getExpansionManager();
        List<Expansion> enabledExpansionList = expansionManager.getEnabledExpansions();

        for(Expansion expansion : enabledExpansionList) {
            if(expansion instanceof RegionExpansion) {
                RegionExpansion regionExpansion = (RegionExpansion) expansion;
                RegionHandler regionHandler = regionExpansion.getRegionHandler();
                if(regionHandler.isSafeZone(player, location, tagType)) {
                    return true;
                }
            }
        }

        return false;
    }

    protected boolean isSafe(Player player, Location location) {
        return isSafe(player, location, TagType.UNKNOWN);
    }

    private YamlConfiguration getConfiguration() {
        ICombatLogX plugin = getPlugin();
        ConfigurationManager configurationManager = plugin.getConfigurationManager();
        return configurationManager.get("force-field.yml");
    }

    private boolean canBypass(Player player) {
        YamlConfiguration configuration = getConfiguration();
        String bypassPermissionName = configuration.getString("bypass-permission");
        if(bypassPermissionName == null || bypassPermissionName.isEmpty()) return false;

        Permission permission = new Permission(bypassPermissionName, "CombatLogX Force Field Bypass", PermissionDefault.FALSE);
        return player.hasPermission(permission);
    }

    private XMaterial getForceFieldMaterial() {
        YamlConfiguration configuration = getConfiguration();
        String materialName = configuration.getString("material");
        if(materialName == null) return XMaterial.RED_STAINED_GLASS;

        return XMaterial.matchXMaterial(materialName).orElse(XMaterial.RED_STAINED_GLASS);
    }

    private int getForceFieldRadius() {
        YamlConfiguration configuration = getConfiguration();
        return configuration.getInt("radius", 8);
    }
}
