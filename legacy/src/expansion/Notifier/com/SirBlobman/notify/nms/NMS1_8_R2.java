package com.SirBlobman.notify.nms;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

public class NMS1_8_R2 extends NMSUtil {
    @Override
    public void action(Player player, String msg) {
        try {
            String json = json(msg);
            byte ACTION_BAR = 2;
            
            Class<?> class_IChatBaseComponent = Class.forName("net.minecraft.server.v1_8_R2.IChatBaseComponent");
            Class<?> class_ChatSerializer = ReflectionUtil.getInnerClass(class_IChatBaseComponent, "ChatSerializer");
            Class<?> class_CraftPlayer = Class.forName("org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer");
            Class<?> class_PacketPlayOutChat = Class.forName("net.minecraft.server.v1_8_R2.PacketPlayOutChat");
            Class<?> class_EntityPlayer = Class.forName("net.minecraft.server.v1_8_R2.EntityPlayer");
            Class<?> class_PlayerConnection = Class.forName("net.minecraft.server.v1_8_R2.PlayerConnection");
            Class<?> class_Packet = Class.forName("net.minecraft.server.v1_8_R2.Packet");
            
            Method method_a = class_ChatSerializer.getMethod("a", String.class);
            Object object_IChatBaseComponent = method_a.invoke(null, json);
            
            Constructor<?> constructor_PacketPlayOutChat = class_PacketPlayOutChat.getConstructor(class_IChatBaseComponent, Byte.TYPE);
            Object object_PacketPlayOutChat = constructor_PacketPlayOutChat.newInstance(object_IChatBaseComponent, ACTION_BAR);
            
            Object object_CraftPlayer = class_CraftPlayer.cast(player);
            Method method_getHandle = class_CraftPlayer.getMethod("getHandle");
            
            Object object_EntityPlayer = method_getHandle.invoke(object_CraftPlayer);
            Field field_playerConnection = class_EntityPlayer.getField("playerConnection");
            
            Object object_PlayerConnection = field_playerConnection.get(object_EntityPlayer);
            Method method_sendPacket = class_PlayerConnection.getMethod("sendPacket", class_Packet);
            method_sendPacket.invoke(object_PlayerConnection, object_PacketPlayOutChat);
        } catch(Throwable ex) {
            String error = "An error has occured while sending an NMS action bar for v1_8_R2!";
            print(error);
            ex.printStackTrace();
        }
    }
}