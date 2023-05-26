package combatlogx.expansion.force.field.task;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import com.github.sirblobman.api.folia.details.EntityTaskDetails;
import com.github.sirblobman.api.location.BlockLocation;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagInformation;

import com.github.sirblobman.api.shaded.xseries.XMaterial;

import combatlogx.expansion.force.field.ForceFieldExpansion;
import combatlogx.expansion.force.field.configuration.ForceFieldConfiguration;

public final class ForceFieldPlayerTask extends EntityTaskDetails<Player> {
    private final ForceFieldExpansion expansion;
    private final Map<UUID, Set<BlockLocation>> fakeBlockMap;

    public ForceFieldPlayerTask(@NotNull ForceFieldExpansion expansion, @NotNull Player entity) {
        super(expansion.getPlugin().getPlugin(), entity);
        this.expansion = expansion;
        this.fakeBlockMap = new HashMap<>();
    }

    @Override
    public void run() {
        Player player = getEntity();
        if (player == null) {
            cancel();
            return;
        }

        ICombatManager combatManager = getCombatManager();
        if (!combatManager.isInCombat(player)) {
            removeForceField(player);
            cancel();
            return;
        }

        if (hasBypass(player)) {
            removeForceField(player);
            cancel();
            return;
        }

        updateForceField(player);
    }

    private @NotNull ForceFieldExpansion getExpansion() {
        return this.expansion;
    }

    private @NotNull ICombatLogX getCombatLogX() {
        ForceFieldExpansion expansion = getExpansion();
        return expansion.getPlugin();
    }

    private @NotNull ICombatManager getCombatManager() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getCombatManager();
    }

    private @NotNull ForceFieldConfiguration getConfiguration() {
        ForceFieldExpansion expansion = getExpansion();
        return expansion.getConfiguration();
    }

    private boolean hasBypass(@NotNull Player player) {
        ForceFieldConfiguration configuration = getConfiguration();
        Permission permission = configuration.getBypassPermission();
        return (permission != null && player.hasPermission(permission));
    }

    boolean isSafe(@NotNull Player player, @NotNull Location location, TagInformation tag) {
        ICombatLogX combatLogX = getCombatLogX();
        ExpansionManager expansionManager = combatLogX.getExpansionManager();
        List<Expansion> expansionList = expansionManager.getEnabledExpansions();

        for (Expansion expansion : expansionList) {
            if (!(expansion instanceof RegionExpansion)) {
                continue;
            }

            RegionExpansion regionExpansion = (RegionExpansion) expansion;
            RegionHandler<?> regionHandler = regionExpansion.getRegionHandler();
            if (regionHandler.isSafeZone(player, location, tag)) {
                return true;
            }
        }

        return false;
    }

    boolean isSafeSurround(@NotNull Player player, @NotNull Location location, @NotNull TagInformation tag) {
        Block block = location.getBlock();
        BlockFace[] faces = {BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST,
                BlockFace.WEST};
        for (BlockFace face : faces) {
            Location relative = block.getRelative(face).getLocation();
            if (isSafe(player, relative, tag)) {
                return true;
            }
        }

        return false;
    }

    private void updateForceField(@NotNull Player player) {
        ICombatManager combatManager = getCombatManager();
        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation == null) {
            removeForceField(player);
            return;
        }

        updateForceField(player, tagInformation);
    }

    private @NotNull Set<BlockLocation> getOldArea(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        return this.fakeBlockMap.computeIfAbsent(playerId, key -> new HashSet<>());
    }

    private @NotNull Set<BlockLocation> getForceFieldArea(@NotNull Player player, @NotNull TagInformation tag) {
        World world = player.getWorld();
        BlockLocation playerLocation = BlockLocation.from(player);

        Set<BlockLocation> area = new HashSet<>();
        ForceFieldConfiguration configuration = getConfiguration();
        int radius = configuration.getRadius();

        int centerX = playerLocation.getX();
        int centerY = playerLocation.getY();
        int centerZ = playerLocation.getZ();
        int minX = (centerX - radius);
        int minY = (centerY - radius);
        int minZ = (centerZ - radius);
        int maxX = (centerX + radius);
        int maxY = (centerY + radius);
        int maxZ = (centerZ + radius);

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                BlockLocation blockLocation = BlockLocation.from(world, x, centerY, z);
                Location location = blockLocation.asLocation();
                if (location == null) {
                    continue;
                }

                if (isSafe(player, location, tag) || isSafeSurround(player, location, tag)) {
                    for (int y = minY; y <= maxY; y++) {
                        BlockLocation blockLocation2 = BlockLocation.from(world, x, y, z);
                        if (canPlace(blockLocation2)) {
                            area.add(blockLocation2);
                        }
                    }
                }
            }
        }

        return area;
    }

    private void updateForceField(@NotNull Player player, @NotNull TagInformation tag) {
        Set<BlockLocation> oldArea = getOldArea(player);
        Set<BlockLocation> newArea = getForceFieldArea(player, tag);
        Set<BlockLocation> fullArea = new HashSet<>(newArea);
        newArea.removeAll(oldArea);
        oldArea.removeAll(fullArea);

        UUID playerId = player.getUniqueId();
        this.fakeBlockMap.put(playerId, fullArea);

        for (BlockLocation blockLocation : newArea) {
            Location location = blockLocation.asLocation();
            if (location != null) {
                sendForceField(player, location);
            }
        }

        for (BlockLocation blockLocation : oldArea) {
            Location location = blockLocation.asLocation();
            if (location != null) {
                resetBlock(player, location);
            }
        }
    }

    boolean canPlace(@NotNull BlockLocation location) {
        World world = location.getWorld();
        if (world == null) {
            return false;
        }

        int maxY = world.getMaxHeight();
        int locationY = location.getY();
        if (locationY > maxY) {
            return false;
        }

        Location bukkitLocation = location.asLocation();
        if (bukkitLocation == null) {
            return false;
        }

        Block block = bukkitLocation.getBlock();
        Material material = block.getType();
        return (material == Material.AIR || !material.isSolid());
    }

    void sendForceField(@NotNull Player player, @NotNull Location location) {
        ForceFieldConfiguration configuration = getConfiguration();
        XMaterial material = configuration.getMaterial();
        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion < 13) {
            sendFakeBlockLegacy(player, location, material);
        } else {
            sendFakeBlockModern(player, location, material);
        }
    }

    @SuppressWarnings("deprecation")
    private void sendFakeBlockLegacy(@NotNull Player player, @NotNull Location location, @NotNull XMaterial material) {
        Material bukkitMaterial = material.parseMaterial();
        if (bukkitMaterial == null || !bukkitMaterial.isBlock()) {
            return;
        }

        byte data = material.getData();
        player.sendBlockChange(location, bukkitMaterial, data);
    }

    private void sendFakeBlockModern(@NotNull Player player, @NotNull Location location, @NotNull XMaterial material) {
        Material bukkitMaterial = material.parseMaterial();
        if (bukkitMaterial == null || !bukkitMaterial.isBlock()) {
            return;
        }

        BlockData blockData = bukkitMaterial.createBlockData();
        player.sendBlockChange(location, blockData);
    }

    private void resetBlock(@NotNull Player player, @NotNull Location location) {
        Block block = location.getBlock();
        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion < 13) {
            resetBlockLegacy(player, block);
        } else {
            resetBlockModern(player, block);
        }
    }

    @SuppressWarnings("deprecation")
    private void resetBlockLegacy(@NotNull Player player, @NotNull Block block) {
        Location location = block.getLocation();
        Material material = block.getType();
        byte data = block.getData();
        player.sendBlockChange(location, material, data);
    }

    private void resetBlockModern(@NotNull Player player, @NotNull Block block) {
        Location location = block.getLocation();
        BlockData blockData = block.getBlockData();
        player.sendBlockChange(location, blockData);
    }

    void removeForceField(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        Set<BlockLocation> oldArea = this.fakeBlockMap.remove(playerId);
        if (oldArea == null) {
            return;
        }

        for (BlockLocation blockLocation : oldArea) {
            Location location = blockLocation.asLocation();
            if (location != null) {
                resetBlock(player, location);
            }
        }
    }

    public @NotNull Map<UUID, Set<BlockLocation>> getFakeBlockMap() {
        return this.fakeBlockMap;
    }
}
