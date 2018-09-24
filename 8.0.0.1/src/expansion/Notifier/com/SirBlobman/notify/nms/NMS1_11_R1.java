package com.SirBlobman.notify.nms;

import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_11_R1.*;
import net.minecraft.server.v1_11_R1.IChatBaseComponent.ChatSerializer;

public class NMS1_11_R1 extends NMSUtil {
    @Override
    public void action(Player p, String msg) {
        String json = json(msg);
        IChatBaseComponent icbc = ChatSerializer.a(json);
        byte ACTION = 2;
        PacketPlayOutChat pp = new PacketPlayOutChat(icbc, ACTION);

        CraftPlayer cp = (CraftPlayer) p;
        EntityPlayer ep = cp.getHandle();
        PlayerConnection pc = ep.playerConnection;
        pc.sendPacket(pp);
    }
}