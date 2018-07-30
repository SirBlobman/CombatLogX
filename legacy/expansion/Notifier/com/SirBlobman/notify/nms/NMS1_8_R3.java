package com.SirBlobman.notify.nms;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.*;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;

public class NMS1_8_R3 extends NMSUtil {
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