package combatlogx.expansion.force.field.task;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.github.sirblobman.api.folia.details.LocationTaskDetails;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.sirblobman.api.location.BlockLocation;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.api.shaded.xseries.XMaterial;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerDigType;
import com.comphenix.protocol.wrappers.MovingObjectPositionBlock;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import combatlogx.expansion.force.field.ForceFieldExpansion;
import combatlogx.expansion.force.field.configuration.ForceFieldConfiguration;

/**
 * @author olivolja3
 */
public final class ForceFieldAdapter extends PacketAdapter {
    private final ForceFieldExpansion expansion;

    public ForceFieldAdapter(@NotNull ForceFieldExpansion expansion) {
        this(expansion, expansion.getPlugin());
    }

    private ForceFieldAdapter(@NotNull ForceFieldExpansion expansion, @NotNull ICombatLogX combatLogX) {
        this(expansion, combatLogX.getPlugin());
    }

    private ForceFieldAdapter(@NotNull ForceFieldExpansion expansion, @NotNull JavaPlugin plugin) {
        super(plugin, ListenerPriority.NORMAL, Client.USE_ITEM, Client.BLOCK_DIG, Server.BLOCK_CHANGE);
        this.expansion = expansion;
    }

    @Override
    public void onPacketReceiving(@NotNull PacketEvent e) {
        if (e.isCancelled()) {
            return;
        }

        ForceFieldTask task = getTask();
        if (task == null) {
            return;
        }

        Player player = e.getPlayer();
        ICombatManager combatManager = getCombatManager();
        if (!combatManager.isInCombat(player)) {
            return;
        }

        TagInformation tag = combatManager.getTagInformation(player);
        if (tag == null) {
            return;
        }

        World world = player.getWorld();
        PacketContainer packet = e.getPacket();
        Location location = getLocation0(world, packet);
        if (location == null) {
            return;
        }

        getCombatLogX().getFoliaHelper().getScheduler().scheduleLocationTask(new LocationTaskDetails(plugin, location) {
          @Override
          public void run() {
            if (isForceFieldBlock(task, player, location, tag)) {
              sendForceField(task, player, location, packet);
            }
          }
        });
    }

    @Override
    public void onPacketSending(PacketEvent e) {
        if (e.isCancelled()) {
            return;
        }

        ForceFieldTask task = getTask();
        if (task == null) {
            return;
        }

        Player player = e.getPlayer();
        ICombatManager combatManager = getCombatManager();
        if (!combatManager.isInCombat(player)) {
            return;
        }

        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation == null) {
            return;
        }

        World world = player.getWorld();
        PacketContainer packetContainer = e.getPacket();
        Location location = getLocation0(world, packetContainer);
        if (location == null) {
            return;
        }

        if (isForceFieldBlock(task, player, location, tagInformation)) {
            WrappedBlockData wrappedBlockData = getWrappedBlockData();
            StructureModifier<WrappedBlockData> blockData = packetContainer.getBlockData();
            blockData.writeSafely(0, wrappedBlockData);
        }
    }

    private @NotNull ForceFieldExpansion getExpansion() {
        return this.expansion;
    }

    private @Nullable ForceFieldTask getTask() {
        ForceFieldExpansion expansion = getExpansion();
        return expansion.getTask();
    }

    private @NotNull ForceFieldConfiguration getConfiguration() {
        ForceFieldExpansion expansion = getExpansion();
        return expansion.getConfiguration();
    }

    private @NotNull ICombatLogX getCombatLogX() {
        ForceFieldExpansion expansion = getExpansion();
        return expansion.getPlugin();
    }

    private @NotNull ICombatManager getCombatManager() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getCombatManager();
    }

    private boolean isForceFieldBlock(@NotNull ForceFieldTask task, @NotNull Player player, @NotNull Location location,
                                      @NotNull TagInformation tag) {
        UUID playerId = player.getUniqueId();
        Map<UUID, Set<BlockLocation>> fakeBlockMap = task.getFakeBlockMap();

        if (fakeBlockMap.containsKey(playerId)) {
            boolean isSafe = task.isSafe(player, location);
            boolean isSafeSurround = task.isSafeSurround(player, location, tag);
            boolean canPlace = task.canPlace(BlockLocation.from(location));
            if (isSafe && isSafeSurround && canPlace) {
                BlockLocation worldXYZ = BlockLocation.from(location);
                return fakeBlockMap.get(playerId).contains(worldXYZ);
            }
        }

        return false;
    }

    private @NotNull WrappedBlockData getWrappedBlockData() {
        ForceFieldConfiguration configuration = getConfiguration();
        XMaterial material = configuration.getMaterial();
        Material bukkitMaterial = material.get();
        if (bukkitMaterial == null) {
            throw new IllegalStateException("Invalid material configuration!");
        }

        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion < 13) {
            byte data = material.getData();
            return WrappedBlockData.createData(bukkitMaterial, data);
        }

        return WrappedBlockData.createData(bukkitMaterial);
    }

    private @Nullable Location getLocation0(@NotNull World world, @NotNull PacketContainer packet) {
        try {
            StructureModifier<BlockPosition> blockPositionModifier = packet.getBlockPositionModifier();
            BlockPosition blockPosition = blockPositionModifier.readSafely(0);
            if (blockPosition == null) {
                return getLocation1(world, packet);
            }

            return blockPosition.toLocation(world);
        } catch (FieldAccessException ex) {
            return getLocation1(world, packet);
        }
    }

    private @Nullable Location getLocation1(@NotNull World world, @NotNull PacketContainer packet) {
        try {
            StructureModifier<MovingObjectPositionBlock> modifier = packet.getMovingBlockPositions();
            MovingObjectPositionBlock movingBlock = modifier.readSafely(0);
            if (movingBlock == null) {
                return null;
            }

            BlockPosition blockPosition = movingBlock.getBlockPosition();
            if (blockPosition == null) {
                return null;
            }

            return blockPosition.toLocation(world);
        } catch (FieldAccessException ex) {
            return null;
        }
    }

    private void sendForceField(@NotNull ForceFieldTask task, @NotNull Player player, @NotNull Location location,
                                @NotNull PacketContainer packet) {
        PacketType packetType = packet.getType();
        if (packetType == Client.BLOCK_DIG) {
            sendForceFieldBlockDig(task, player, location, packet);
        }

        if (packetType == Client.USE_ITEM) {
            task.sendForceField(player, location);
        }
    }

    private void sendForceFieldBlockDig(@NotNull ForceFieldTask task, @NotNull Player player,
                                        @NotNull Location location, @NotNull PacketContainer packet) {
        StructureModifier<PlayerDigType> playerDigTypeModifier = packet.getPlayerDigTypes();
        PlayerDigType digType = playerDigTypeModifier.readSafely(0);
        GameMode gameMode = player.getGameMode();

        if (digType == PlayerDigType.STOP_DESTROY_BLOCK
                || (digType == PlayerDigType.START_DESTROY_BLOCK && gameMode == GameMode.CREATIVE)) {
            task.sendForceField(player, location);
        }
    }
}
