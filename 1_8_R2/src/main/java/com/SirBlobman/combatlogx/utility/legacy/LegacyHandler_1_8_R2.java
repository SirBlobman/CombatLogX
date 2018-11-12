package com.SirBlobman.combatlogx.utility.legacy;

import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.Util;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.inventivetalent.bossbar.BossBarAPI;
import org.inventivetalent.bossbar.BossBarAPI.Color;
import org.inventivetalent.bossbar.BossBarAPI.Style;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class LegacyHandler_1_8_R2 extends LegacyHandler {
    @Override
    public double getMaxHealth(LivingEntity entity) {
        try {
            Class<?> class_entity = entity.getClass();
            Method method = class_entity.getMethod("getMaxHealth");
            return (double) method.invoke(entity);
        } catch (Throwable ex) {
            return 0.0D;
        }
    }

    @Override
    public void setMaxHealth(LivingEntity entity, double maxHealth) {
        try {
            Class<?> class_entity = entity.getClass();
            Class<?> class_double = Double.TYPE;
            Method method = class_entity.getMethod("setMaxHealth", class_double);
            method.invoke(entity, maxHealth);
        } catch (Throwable ex) {

        }
        //entity.setMaxHealth(maxHealth);
    }

    @Override
    public void sendActionBar(Player player, String msg) {
        try {
            String json = "{\"text\": \"" + msg + "\"}";
            byte ACTION_BAR = 2;

            Class<?> class_IChatBaseComponent = Class.forName("net.minecraft.server.v1_8_R2.IChatBaseComponent");
            Class<?> class_ChatSerializer = getInnerClass(class_IChatBaseComponent, "ChatSerializer");
            Class<?> class_CraftPlayer = Class.forName("org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer");
            Class<?> class_PacketPlayOutChat = Class.forName("net.minecraft.server.v1_8_R2.PacketPlayOutChat");
            Class<?> class_EntityPlayer = Class.forName("net.minecraft.server.v1_8_R2.EntityPlayer");
            Class<?> class_PlayerConnection = Class.forName("net.minecraft.server.v1_8_R2.PlayerConnection");
            Class<?> class_Packet = Class.forName("net.minecraft.server.v1_8_R2.Packet");

            Method method_a = class_ChatSerializer.getMethod("a", String.class);
            Object object_IChatBaseComponent = method_a.invoke(null, json);

            Constructor<?> constructor_PacketPlayOutChat = class_PacketPlayOutChat
                    .getConstructor(class_IChatBaseComponent, Byte.TYPE);
            Object object_PacketPlayOutChat = constructor_PacketPlayOutChat.newInstance(object_IChatBaseComponent,
                    ACTION_BAR);

            Object object_CraftPlayer = class_CraftPlayer.cast(player);
            Method method_getHandle = class_CraftPlayer.getMethod("getHandle");

            Object object_EntityPlayer = method_getHandle.invoke(object_CraftPlayer);
            Field field_playerConnection = class_EntityPlayer.getField("playerConnection");

            Object object_PlayerConnection = field_playerConnection.get(object_EntityPlayer);
            Method method_sendPacket = class_PlayerConnection.getMethod("sendPacket", class_Packet);
            method_sendPacket.invoke(object_PlayerConnection, object_PacketPlayOutChat);
        } catch (Throwable ex) {
            String error = "An error has occured while sending an NMS action bar for v1_8_R2!";
            Util.print(error);
            ex.printStackTrace();
        }
    }

    @Override
    public void sendBossBar(Player player, String style, String color, String title, float progress) {
        if (PluginUtil.isEnabled("BossBarAPI", "inventivetalent")) {
            BossBarAPI.addBar(Util.newList(player), title, Color.valueOf(color), Style.valueOf(style.replace("SEGMENTED", "NOTCHED").replace("SOLID", "PROGRESS")), progress);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void removeBossBar(Player player) {
        if (PluginUtil.isEnabled("BossBarAPI", "inventivetalent")) {
            BossBarAPI.removeBar(player);
            BossBarAPI.removeAllBars(player);
        }
    }

    @Override
    public Objective createScoreboardObjective(Scoreboard scoreboard, String name, String criteria, String displayName) {
        Objective obj = scoreboard.registerNewObjective(name, criteria);
        obj.setDisplayName(displayName);
        return obj;
    }

}