package combatlogx.expansion.force.field.task;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.github.sirblobman.api.folia.details.LocationTaskDetails;
import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.folia.details.RunnableTask;
import com.github.sirblobman.api.folia.scheduler.TaskScheduler;
import com.github.sirblobman.api.folia.task.WrappedTask;
import com.github.sirblobman.api.location.BlockLocation;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.api.shaded.xseries.XMaterial;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import combatlogx.expansion.force.field.ForceFieldExpansion;
import combatlogx.expansion.force.field.configuration.ForceFieldConfiguration;

public final class ForceFieldTask extends ExpansionListener implements Runnable {
    private final ForceFieldExpansion expansion;
    private final Map<UUID, Set<BlockLocation>> fakeBlockMap;
    private WrappedTask wrappedTask;

    public ForceFieldTask(@NotNull ForceFieldExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
        this.fakeBlockMap = new ConcurrentHashMap<>();
        this.wrappedTask = null;
    }

    @Override
    public void run() {
        ForceFieldConfiguration configuration = getConfiguration();
        if (!configuration.isEnabled()) {
            return;
        }

        ICombatManager combatManager = getCombatManager();
        List<Player> combatPlayerList = combatManager.getPlayersInCombat();
        for (Player player : combatPlayerList) {
            checkForceField(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        UUID playerId = player.getUniqueId();
        this.fakeBlockMap.remove(playerId);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onUntag(PlayerUntagEvent e) {
        Player player = e.getPlayer();
        removeForceField(player);
    }

    public void registerProtocol() {
        ForceFieldExpansion forceFieldExpansion = getForceFieldExpansion();
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        ForceFieldAdapter forceFieldAdapter = new ForceFieldAdapter(forceFieldExpansion);
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

    public void registerTask() {
        RunnableTask thisTask = new RunnableTask(getJavaPlugin(), this);
        thisTask.setDelay(1L);
        thisTask.setPeriod(1L);

        TaskScheduler scheduler = getCombatLogX().getFoliaHelper().getScheduler();
        this.wrappedTask = scheduler.scheduleAsyncTask(thisTask);
    }

    public void cancel() {
        if (this.wrappedTask != null) {
            this.wrappedTask.cancel();
            this.wrappedTask = null;
        }
    }

    public @NotNull Map<UUID, Set<BlockLocation>> getFakeBlockMap() {
        return this.fakeBlockMap;
    }

    boolean isSafe(@NotNull Player player, @NotNull Location location) {
        ICombatManager combatManager = getCombatManager();
        TagInformation tag = combatManager.getTagInformation(player);
        if (tag == null) {
            return false;
        }

        return isSafe(player, location, tag);
    }

    boolean isSafeSurround(@NotNull Player player, @NotNull Location location, @NotNull TagInformation tag) {
        Block blockLocation = location.getBlock();
        BlockFace[] faces = {BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH,
                BlockFace.EAST, BlockFace.WEST};
        for (BlockFace blockFace : faces) {
            Location relativeLocation = blockLocation.getRelative(blockFace).getLocation();
            if (!isSafe(player, relativeLocation, tag)) {
                return true;
            }
        }

        return false;
    }

    void sendForceField(Player player, Location location) {
        ForceFieldConfiguration configuration = getConfiguration();
        XMaterial material = configuration.getMaterial();
        int minorVersion = VersionUtility.getMinorVersion();

        if (minorVersion < 13) {
            sendFakeBlockLegacy(player, location, material);
        } else {
            sendFakeBlockModern(player, location, material);
        }
    }

    boolean canPlace(@NotNull BlockLocation blockLocation) {
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

    private @NotNull ForceFieldExpansion getForceFieldExpansion() {
        return this.expansion;
    }

    private @NotNull ForceFieldConfiguration getConfiguration() {
        ForceFieldExpansion expansion = getForceFieldExpansion();
        return expansion.getConfiguration();
    }

    private void checkForceField(@NotNull Player player) {
      Location location = player.getLocation();
      getCombatLogX().getFoliaHelper().getScheduler().scheduleLocationTask(new LocationTaskDetails(expansion.getPlugin().getPlugin(), location) {
        @Override
        public void run() {
          if (hasBypass(player)) {
            removeForceField(player);
            return;
          }

          if (isSafe(player, location)) {
            return;
          }

          updateForceField(player);
        }
      });
    }

    private boolean hasBypass(@NotNull Player player) {
        ForceFieldConfiguration configuration = getConfiguration();
        Permission bypassPermission = configuration.getBypassPermission();
        if (bypassPermission == null) {
            return false;
        }

        return player.hasPermission(bypassPermission);
    }

    private boolean isSafe(@NotNull Player player, @NotNull Location location, @NotNull TagInformation tag) {
        ICombatLogX plugin = getCombatLogX();
        ExpansionManager expansionManager = plugin.getExpansionManager();
        List<Expansion> enabledExpansionList = expansionManager.getEnabledExpansions();

        for (Expansion expansion : enabledExpansionList) {
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

    private void updateForceField(@NotNull Player player) {
        ICombatManager combatManager = getCombatManager();
        if (!combatManager.isInCombat(player)) {
            removeForceField(player);
            return;
        }

        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation == null) {
            removeForceField(player);
            return;
        }

        updateForceField(player, tagInformation);
    }

    private void updateForceField(@NotNull Player player, @NotNull TagInformation tag) {
        Set<BlockLocation> oldArea = new HashSet<>();
        Set<BlockLocation> newArea = getForceFieldArea(player, tag);
        Set<BlockLocation> fullArea = new HashSet<>(newArea);

        UUID playerId = player.getUniqueId();
        if (this.fakeBlockMap.containsKey(playerId)) {
            oldArea = this.fakeBlockMap.get(playerId);
            newArea.removeAll(oldArea);

            try {
                oldArea.removeAll(fullArea); // Sometimes causes ConcurrentModificationException?
            } catch (ConcurrentModificationException ex) {
                printDebug("Detected ForceField concurrent modification:");
                if (!isDebugModeDisabled()) {
                    ex.printStackTrace();
                }
            }
        }

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

    private @NotNull Set<BlockLocation> getForceFieldArea(@NotNull Player player, @NotNull TagInformation tagInformation) {
        World world = player.getWorld();
        BlockLocation playerBlockLocation = BlockLocation.from(player);

        Set<BlockLocation> area = new HashSet<>();
        ForceFieldConfiguration configuration = getConfiguration();
        int radius = configuration.getRadius();

        int playerX = playerBlockLocation.getX();
        int playerY = playerBlockLocation.getY();
        int playerZ = playerBlockLocation.getZ();
        int minX = (playerX - radius);
        int maxX = (playerX + radius);
        int minZ = (playerZ - radius);
        int maxZ = (playerZ + radius);

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

    private void resetBlock(Player player, Location location) {
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
        Material bukkitMaterial = material.get();
        if (bukkitMaterial == null || !bukkitMaterial.isBlock()) {
            return;
        }

        BlockData blockData = bukkitMaterial.createBlockData();
        player.sendBlockChange(location, blockData);
    }

    private void removeForceField(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        if (!this.fakeBlockMap.containsKey(playerId)) {
            return;
        }

        Set<BlockLocation> oldArea = new HashSet<>(this.fakeBlockMap.remove(playerId));
        for (BlockLocation blockLocation : oldArea) {
            Location location = blockLocation.asLocation();
            if (location != null) {
              getCombatLogX().getFoliaHelper().getScheduler().scheduleLocationTask(new LocationTaskDetails(expansion.getPlugin().getPlugin(), location) {
                @Override
                public void run() {
                  resetBlock(player, location);
                }
              });
            }
        }
    }
}
