package com.SirBlobman.combatlogx.api.olivolja3.force.field;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.base.Objects;

public abstract class AbstractPacket {
    protected PacketContainer handle;
    protected AbstractPacket(PacketContainer handle, PacketType type) {
        if(handle == null) throw new IllegalArgumentException("Packet handle cannot be NULL!");
        if(!Objects.equal(handle.getType(), type)) throw new IllegalArgumentException(handle.getHandle() + " is not a packet of type " + type);

        this.handle = handle;
    }

    public PacketContainer getHandle() {
        return this.handle;
    }

    public void sendPacket(Player player) {
        try {
            ProtocolManager manager = ProtocolLibrary.getProtocolManager();
            manager.sendServerPacket(player, getHandle());
        } catch(InvocationTargetException ex) {
            throw new RuntimeException("Cannot send packet", ex);
        }
    }

    public void broadcastPacket() {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.broadcastServerPacket(getHandle());
    }

    @Deprecated
    public void recievePacket(Player player) {
        receivePacket(player);
    }

    public void receivePacket(Player player) {
        try {
            ProtocolManager manager = ProtocolLibrary.getProtocolManager();
            manager.recieveClientPacket(player, getHandle());
        } catch(InvocationTargetException | IllegalAccessException ex) {
            throw new RuntimeException("Cannot receive packet", ex);
        }
    }
}