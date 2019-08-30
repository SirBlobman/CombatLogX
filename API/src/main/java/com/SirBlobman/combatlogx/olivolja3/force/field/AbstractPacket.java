package com.SirBlobman.combatlogx.olivolja3.force.field;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public abstract class AbstractPacket {
    protected final PacketContainer handle;
    protected AbstractPacket(PacketContainer handle, PacketType type) {
        Validate.notNull(handle, "handle cannot be null!");
        Validate.isTrue(handle.getType() == type, handle.getHandle() + " is not a packet of type " + type);

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