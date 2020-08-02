package com.SirBlobman.combatlogx.api.olivolja3.force.field;

import org.bukkit.Location;
import org.bukkit.World;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;

public class WrapperPlayServerBlockChange extends AbstractPacket {
    private static final PacketType TYPE = PacketType.Play.Server.BLOCK_CHANGE;
    public WrapperPlayServerBlockChange() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerBlockChange(PacketContainer packet) {
        super(packet, TYPE);
    }

    public BlockPosition getLocation() {
        return this.handle.getBlockPositionModifier().read(0);
    }

    public void setLocation(BlockPosition value) {
        this.handle.getBlockPositionModifier().write(0, value);
    }

    public Location getBukkitLocation(World world) {
        return getLocation().toVector().toLocation(world);
    }

    public void setBlockData(WrappedBlockData value) {
        this.handle.getBlockData().write(0, value);
    }

    public WrappedBlockData getBlockData() {
        return this.handle.getBlockData().read(0);
    }
}