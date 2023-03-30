package combatlogx.expansion.force.field.task;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.location.BlockLocation;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.api.shaded.xseries.XMaterial;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagInformation;

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
import org.jetbrains.annotations.Nullable;

/**
 * @author olivolja3
 */
public final class ForceFieldAdapter extends PacketAdapter {
    private final ForceFieldExpansion expansion;

    public ForceFieldAdapter(ForceFieldExpansion expansion) {
        super(expansion.getPlugin().getPlugin(), ListenerPriority.NORMAL,
                Client.USE_ITEM, Client.BLOCK_DIG, Server.BLOCK_CHANGE);
        this.expansion = expansion;
    }

    private ForceFieldExpansion getExpansion() {
        return this.expansion;
    }

    private ForceFieldTask getTask() {
        ForceFieldExpansion expansion = getExpansion();
        return expansion.getTask();
    }

    private ForceFieldConfiguration getConfiguration() {
        ForceFieldExpansion expansion = getExpansion();
        return expansion.getConfiguration();
    }

    private ICombatManager getCombatManager() {
        ForceFieldExpansion expansion = getExpansion();
        ICombatLogX combatLogX = expansion.getPlugin();
        return combatLogX.getCombatManager();
    }

    @Override
    public void onPacketReceiving(PacketEvent e) {
        if (e.isCancelled()) {
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

        ForceFieldTask task = getTask();
        if (isForceFieldBlock(player, location, tagInformation)) {
            PacketType packetType = packetContainer.getType();
            if (packetType == Client.BLOCK_DIG) {
                StructureModifier<PlayerDigType> playerDigTypeModifier = packetContainer.getPlayerDigTypes();
                PlayerDigType playerDigType = playerDigTypeModifier.readSafely(0);
                GameMode gameMode = player.getGameMode();

                if (playerDigType == PlayerDigType.STOP_DESTROY_BLOCK
                        || (playerDigType == PlayerDigType.START_DESTROY_BLOCK && gameMode == GameMode.CREATIVE)) {
                    task.sendForceField(player, location);
                }
            }

            if (packetType == Client.USE_ITEM) {
                task.sendForceField(player, location);
            }
        }
    }

    @Override
    public void onPacketSending(PacketEvent e) {
        if (e.isCancelled()) {
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

        if (isForceFieldBlock(player, location, tagInformation)) {
            WrappedBlockData wrappedBlockData = getWrappedBlockData();
            if (wrappedBlockData != null) {
                packetContainer.getBlockData().writeSafely(0, wrappedBlockData);
            }
        }
    }

    private boolean isForceFieldBlock(Player player, Location location, TagInformation tagInformation) {
        UUID playerId = player.getUniqueId();
        ForceFieldTask task = getTask();
        Map<UUID, Set<BlockLocation>> fakeBlockMap = task.getFakeBlockMap();

        if (fakeBlockMap.containsKey(playerId)) {
            boolean isSafe = task.isSafe(player, location);
            boolean isSafeSurround = task.isSafeSurround(player, location, tagInformation);
            boolean canPlace = task.canPlace(BlockLocation.from(location));
            if (isSafe && isSafeSurround && canPlace) {
                BlockLocation worldXYZ = BlockLocation.from(location);
                return fakeBlockMap.get(playerId).contains(worldXYZ);
            }
        }

        return false;
    }

    private WrappedBlockData getWrappedBlockData() {
        ForceFieldConfiguration configuration = getConfiguration();
        XMaterial material = configuration.getMaterial();
        Material bukkitMaterial = material.parseMaterial();
        if (bukkitMaterial == null) {
            return null;
        }

        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion < 13) {
            byte data = material.getData();
            return WrappedBlockData.createData(bukkitMaterial, data);
        }

        return WrappedBlockData.createData(bukkitMaterial);
    }

    @Nullable
    private Location getLocation0(World world, PacketContainer packetContainer) {
        try {
            StructureModifier<BlockPosition> blockPositionModifier = packetContainer.getBlockPositionModifier();
            BlockPosition blockPosition = blockPositionModifier.readSafely(0);
            if (blockPosition == null) {
                return getLocation1(world, packetContainer);
            }

            return blockPosition.toLocation(world);
        } catch (FieldAccessException ex) {
            return getLocation1(world, packetContainer);
        }
    }

    @Nullable
    private Location getLocation1(World world, PacketContainer packetContainer) {
        try {
            StructureModifier<MovingObjectPositionBlock> movingBlockPositionModifier =
                    packetContainer.getMovingBlockPositions();
            MovingObjectPositionBlock movingObjectPositionBlock = movingBlockPositionModifier.readSafely(0);
            if (movingObjectPositionBlock == null) {
                return null;
            }

            BlockPosition blockPosition = movingObjectPositionBlock.getBlockPosition();
            return (blockPosition == null ? null : blockPosition.toLocation(world));
        } catch (FieldAccessException ex) {
            return null;
        }
    }
}
