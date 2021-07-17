package com.SirBlobman.combatlogx.api.olivolja3.force.field;

import java.util.UUID;

import com.comphenix.protocol.injector.server.TemporaryPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.utility.VersionUtility;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;
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

public class ForceFieldAdapter extends PacketAdapter {
    private final ForceField forceField;
    private final ICombatLogX plugin;

    ForceFieldAdapter(ForceField forceField) {
        super(forceField.getExpansion().getPlugin().getPlugin(), ListenerPriority.NORMAL,
                Client.USE_ITEM, Client.BLOCK_DIG, Server.BLOCK_CHANGE);
        this.forceField = forceField;
        this.plugin = this.forceField.getExpansion().getPlugin();
    }

    @Override
    public void onPacketReceiving(PacketEvent e) {
        if(e.isCancelled()) return;

        Player player = e.getPlayer();
        if(player instanceof TemporaryPlayer) return;

        ICombatManager combatManager = this.plugin.getCombatManager();
        if(!combatManager.isInCombat(player)) return;

        World world = player.getWorld();
        PacketContainer packetContainer = e.getPacket();
        Location location = getLocation0(world, packetContainer);
        if(location == null) return;

        if(isForceFieldBlock(location, player)) {
            PacketType packetType = packetContainer.getType();
            if(packetType == Client.BLOCK_DIG) {
                StructureModifier<PlayerDigType> playerDigTypeModifier = packetContainer.getPlayerDigTypes();
                PlayerDigType playerDigType = playerDigTypeModifier.readSafely(0);
                GameMode gameMode = player.getGameMode();

                if(playerDigType == PlayerDigType.STOP_DESTROY_BLOCK
                        || (playerDigType == PlayerDigType.START_DESTROY_BLOCK && gameMode == GameMode.CREATIVE)) {
                    this.forceField.sendForceField(player, location);
                }
            }

            if(packetType == Client.USE_ITEM) {
                this.forceField.sendForceField(player, location);
            }
        }
    }

    @Override
    public void onPacketSending(PacketEvent e) {
        if(e.isCancelled()) return;

        Player player = e.getPlayer();
        if(player instanceof TemporaryPlayer) return;

        ICombatManager combatManager = this.plugin.getCombatManager();
        if(!combatManager.isInCombat(player)) return;

        World world = player.getWorld();
        PacketContainer packetContainer = e.getPacket();
        Location location = getLocation0(world, packetContainer);

        if(isForceFieldBlock(location, player)) {
            WrappedBlockData wrappedBlockData = getWrappedBlockData();
            if(wrappedBlockData != null) {
                packetContainer.getBlockData().writeSafely(0, wrappedBlockData);
            }
        }
    }

    private boolean isForceFieldBlock(Location location, Player player) {
        UUID uuid = player.getUniqueId();
        return this.forceField.fakeBlockMap.containsKey(uuid) && this.forceField.isSafe(location, player)
                && this.forceField.isSafeSurround(location, player) && ForceField.canPlace(location)
                && this.forceField.fakeBlockMap.get(uuid).contains(location);
    }

    private WrappedBlockData getWrappedBlockData() {
        Material bukkitMaterial = this.forceField.getForceFieldMaterial();
        if(bukkitMaterial == null) return null;

        int minorVersion = VersionUtility.getMinorVersion();
        if(minorVersion < 13) {
            int data = this.forceField.getForceFieldMaterialData();
            return WrappedBlockData.createData(bukkitMaterial, data);
        }

        return WrappedBlockData.createData(bukkitMaterial);
    }

    private Location getLocation0(World world, PacketContainer packetContainer) {
        try {
            StructureModifier<BlockPosition> blockPositionModifier = packetContainer.getBlockPositionModifier();
            BlockPosition blockPosition = blockPositionModifier.readSafely(0);
            if(blockPosition == null) return getLocation1(world, packetContainer);
            return blockPosition.toLocation(world);
        } catch(FieldAccessException ex) {
            return getLocation1(world, packetContainer);
        }
    }

    private Location getLocation1(World world, PacketContainer packetContainer) {
        try {
            StructureModifier<MovingObjectPositionBlock> movingBlockPositionModifier =
                    packetContainer.getMovingBlockPositions();
            MovingObjectPositionBlock movingObjectPositionBlock = movingBlockPositionModifier.readSafely(0);
            if(movingObjectPositionBlock == null) return null;

            BlockPosition blockPosition = movingObjectPositionBlock.getBlockPosition();
            return (blockPosition == null ? null : blockPosition.toLocation(world));
        } catch(FieldAccessException ex) {
            return null;
        }
    }
}
