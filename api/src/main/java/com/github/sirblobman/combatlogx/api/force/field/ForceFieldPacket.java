package com.github.sirblobman.combatlogx.api.force.field;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import org.bukkit.entity.Player;

import com.github.sirblobman.api.utility.Validate;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;

/** @author olivolja3 */
public abstract class ForceFieldPacket {
    protected final PacketContainer handle;
    protected ForceFieldPacket(PacketContainer handle, PacketType packetType) {
        this.handle = Validate.notNull(handle, "handle must not be null!");
        if(!Objects.equals(packetType, handle.getType())) throw new IllegalArgumentException("handle type does not match packetType");
    }

    public PacketContainer getHandle() {
        return this.handle;
    }

    public void sendPacket(Player player) {
        try {
            ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
            PacketContainer packetContainer = getHandle();
            protocolManager.sendServerPacket(player, packetContainer);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException("Failed to send packet", ex);
        }
    }

    public void broadcastPacket() {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        PacketContainer packetContainer = getHandle();
        manager.broadcastServerPacket(packetContainer);
    }

    @Deprecated
    public void recievePacket(Player player) {
        receivePacket(player);
    }

    public void receivePacket(Player player) {
        try {
            ProtocolManager manager = ProtocolLibrary.getProtocolManager();
            PacketContainer packetContainer = getHandle();
            manager.recieveClientPacket(player, packetContainer);
        } catch(InvocationTargetException | IllegalAccessException ex) {
            throw new RuntimeException("Cannot receive packet", ex);
        }
    }
}