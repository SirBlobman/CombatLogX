package combatlogx.expansion.force.field.listener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.location.BlockLocation;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.api.xseries.XMaterial;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagInformation;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import combatlogx.expansion.force.field.ForceFieldExpansion;
import org.jetbrains.annotations.Nullable;

/**
 * @author olivolja3
 */
public final class ListenerForceField extends ExpansionListener {
    private final Map<UUID, Set<BlockLocation>> fakeBlockMap;
    private final ExecutorService forceFieldExecutor;

    private Permission bypassPermission;

    public ListenerForceField(ForceFieldExpansion expansion) {
        super(expansion);
        this.fakeBlockMap = new HashMap<>();
        this.forceFieldExecutor = Executors.newSingleThreadExecutor();
    }

    Map<UUID, Set<BlockLocation>> getFakeBlockMap() {
        return this.fakeBlockMap;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent e) {
        if (!isEnabled()) {
            return;
        }

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        this.fakeBlockMap.remove(uuid);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        Location toLocation = e.getTo();
        if (toLocation == null || !isEnabled()) {
            return;
        }

        Player player = e.getPlayer();
        if (canBypass(player)) {
            return;
        }

        ICombatManager combatManager = getCombatManager();
        if (!combatManager.isInCombat(player)) {
            return;
        }

        Location fromLocation = e.getFrom();
        if (Objects.equals(BlockLocation.from(toLocation), BlockLocation.from(fromLocation))) {
            return;
        }

        if (isSafe(player, toLocation)) {
            return;
        }

        updateForceField(player);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onTag(PlayerTagEvent e) {
        if (!isEnabled()) {
            return;
        }

        Player player = e.getPlayer();
        if (canBypass(player)) {
            return;
        }

        Location location = player.getLocation();
        if (isSafe(player, location)) {
            return;
        }

        updateForceField(player);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onUntag(PlayerUntagEvent e) {
        if (!isEnabled()) {
            return;
        }

        Player player = e.getPlayer();
        removeForceField(player);
    }

    public void registerProtocol() {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        ForceFieldAdapter forceFieldAdapter = new ForceFieldAdapter(getCombatLogX(), this);
        protocolManager.addPacketListener(forceFieldAdapter);
    }

    public void removeProtocol() {
        JavaPlugin plugin = getJavaPlugin();
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.removePacketListeners(plugin);

        for (Player player : Bukkit.getOnlinePlayers()) {
            removeForceField(player);
        }
    }

    public boolean isEnabled() {
        YamlConfiguration configuration = getConfiguration();
        return configuration.getBoolean("enabled", true);
    }

    public void onReload() {
        YamlConfiguration configuration = getConfiguration();
        String bypassPermissionName = configuration.getString("bypass-permission");
        if (bypassPermissionName == null || bypassPermissionName.isEmpty()) {
            this.bypassPermission = null;
            return;
        }

        String description = "CombatLogX Force Field Bypass";
        this.bypassPermission = new Permission(bypassPermissionName, description, PermissionDefault.FALSE);
    }

    public void clearData() {
        this.fakeBlockMap.clear();
    }

    private YamlConfiguration getConfiguration() {
        ConfigurationManager configurationManager = getExpansionConfigurationManager();
        return configurationManager.get("config.yml");
    }

    @Nullable
    private Permission getBypassPermission() {
        return this.bypassPermission;
    }

    private boolean canBypass(Player player) {
        Permission permission = getBypassPermission();
        if (permission == null) {
            return false;
        }

        return player.hasPermission(permission);
    }

    XMaterial getForceFieldMaterial() {
        YamlConfiguration configuration = getConfiguration();
        String materialName = configuration.getString("material");
        if (materialName == null) {
            return XMaterial.RED_STAINED_GLASS;
        }

        return XMaterial.matchXMaterial(materialName).orElse(XMaterial.RED_STAINED_GLASS);
    }

    private int getForceFieldRadius() {
        YamlConfiguration configuration = getConfiguration();
        return configuration.getInt("radius", 8);
    }

    private boolean isSafeMode() {
        YamlConfiguration configuration = getConfiguration();
        return !configuration.getBoolean("unsafe-mode", false);
    }

    boolean canPlace(BlockLocation blockLocation) {
        if (blockLocation == null) {
            return false;
        }

        World world = blockLocation.getWorld();
        if (world == null) {
            return false;
        }

        int maxY = world.getMaxHeight();
        int locationY = blockLocation.getY();
        if (locationY > maxY) {
            return false;
        }

        Location location = blockLocation.asLocation();
        if (location == null) {
            return false;
        }

        Block block = location.getBlock();
        Material material = block.getType();
        return (material == Material.AIR || !material.isSolid());
    }

    @SuppressWarnings("deprecation")
    void sendForceField(Player player, Location location) {
        XMaterial material = getForceFieldMaterial();
        Material bukkitMaterial = material.parseMaterial();
        if (bukkitMaterial == null) {
            return;
        }

        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion < 13) {
            byte data = material.getData();
            player.sendBlockChange(location, bukkitMaterial, data);
            return;
        }

        BlockData blockData = bukkitMaterial.createBlockData();
        player.sendBlockChange(location, blockData);
    }

    @SuppressWarnings("deprecation")
    private void resetBlock(Player player, Location location) {
        Block block = location.getBlock();
        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion < 13) {
            Material bukkitMaterial = block.getType();
            byte bukkitData = block.getData();
            player.sendBlockChange(location, bukkitMaterial, bukkitData);
            return;
        }

        BlockData blockData = block.getBlockData();
        player.sendBlockChange(location, blockData);
    }

    private void updateForceField(Player player) {
        ICombatManager combatManager = getCombatManager();
        if (!combatManager.isInCombat(player)) {
            return;
        }

        Location playerLocation = player.getLocation();
        if (isSafe(player, playerLocation)) {
            return;
        }

        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation == null) {
            return;
        }

        if (isSafeMode()) {
            safeForceField(player, tagInformation);
        } else {
            this.forceFieldExecutor.submit(() -> safeForceField(player, tagInformation));
        }
    }

    private void removeForceField(Player player) {
        if (isSafeMode()) {
            safeRemoveForceField(player);
        } else {
            this.forceFieldExecutor.submit(() -> safeRemoveForceField(player));
        }
    }

    private Set<BlockLocation> getForceFieldArea(Player player, TagInformation tagInformation) {
        World world = player.getWorld();
        BlockLocation playerXYZ = BlockLocation.from(player);
        if(playerXYZ == null) {
            throw new IllegalStateException("playerXYZ is somehow null?");
        }

        int radius = getForceFieldRadius();
        Set<BlockLocation> area = new HashSet<>();
        int playerX = playerXYZ.getX(), playerY = playerXYZ.getY(), playerZ = playerXYZ.getZ();
        int minX = (playerX - radius), maxX = (playerX + radius);
        int minZ = (playerZ - radius), maxZ = (playerZ + radius);

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                BlockLocation blockLocation = BlockLocation.from(world, x, playerY, z);
                Location location = blockLocation.asLocation();
                if (location == null) {
                    continue;
                }

                if (!isSafe(player, location, tagInformation)) {
                    continue;
                }

                if (!isSafeSurround(player, location, tagInformation)) {
                    continue;
                }

                for (int y = -radius; y < radius; y++) {
                    BlockLocation blockLocation2 = BlockLocation.from(world, x, playerY + y, z);
                    if (!canPlace(blockLocation2)) {
                        continue;
                    }

                    area.add(blockLocation2);
                }
            }
        }

        return area;
    }

    private void safeForceField(Player player, TagInformation tagInformation) {
        Set<BlockLocation> oldArea = new HashSet<>();
        Set<BlockLocation> newArea = getForceFieldArea(player, tagInformation);
        Set<BlockLocation> fullArea = new HashSet<>(newArea);

        UUID uuid = player.getUniqueId();
        if (this.fakeBlockMap.containsKey(uuid)) {
            oldArea = this.fakeBlockMap.get(uuid);
            newArea.removeAll(oldArea);
            oldArea.removeAll(fullArea);
        }
        this.fakeBlockMap.put(uuid, fullArea);

        for (BlockLocation blockLocation : newArea) {
            Location location = blockLocation.asLocation();
            if (location != null) sendForceField(player, location);
        }

        for (BlockLocation blockLocation : oldArea) {
            Location location = blockLocation.asLocation();
            if (location != null) resetBlock(player, location);
        }
    }

    private void safeRemoveForceField(Player player) {
        UUID uuid = player.getUniqueId();
        if (!this.fakeBlockMap.containsKey(uuid)) {
            return;
        }

        Set<BlockLocation> oldArea = new HashSet<>(this.fakeBlockMap.remove(uuid));
        for (BlockLocation blockLocation : oldArea) {
            Location location = blockLocation.asLocation();
            if (location != null) resetBlock(player, location);
        }
    }

    boolean isSafeSurround(Player player, Location location, TagInformation tagInformation) {
        Block blockLocation = location.getBlock();
        BlockFace[] blockFaceArray = {BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
        for (BlockFace blockFace : blockFaceArray) {
            Location relativeLocation = blockLocation.getRelative(blockFace).getLocation();
            if (!isSafe(player, relativeLocation, tagInformation)) {
                return true;
            }
        }

        return false;
    }

    private boolean isSafe(Player player, Location location, TagInformation tagInformation) {
        ICombatLogX plugin = getCombatLogX();
        ExpansionManager expansionManager = plugin.getExpansionManager();
        List<Expansion> enabledExpansionList = expansionManager.getEnabledExpansions();

        for (Expansion expansion : enabledExpansionList) {
            if (expansion instanceof RegionExpansion) {
                RegionExpansion regionExpansion = (RegionExpansion) expansion;
                RegionHandler regionHandler = regionExpansion.getRegionHandler();
                if (regionHandler.isSafeZone(player, location, tagInformation)) {
                    return true;
                }
            }
        }

        return false;
    }

    boolean isSafe(Player player, Location location) {
        ICombatManager combatManager = getCombatManager();
        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation == null) {
            return false;
        }

        return isSafe(player, location, tagInformation);
    }
}
