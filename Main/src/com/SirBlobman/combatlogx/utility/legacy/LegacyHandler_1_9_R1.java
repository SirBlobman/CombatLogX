package com.SirBlobman.combatlogx.utility.legacy;

import com.SirBlobman.combatlogx.utility.Util;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;

public class LegacyHandler_1_9_R1 extends LegacyHandler {
    private static final Map<UUID, BossBar> BOSS_BARS = Util.newMap();

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

            Class<?> class_IChatBaseComponent = Class.forName("net.minecraft.server.v1_9_R1.IChatBaseComponent");
            Class<?> class_ChatSerializer = getInnerClass(class_IChatBaseComponent, "ChatSerializer");
            Class<?> class_CraftPlayer = Class.forName("org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer");
            Class<?> class_PacketPlayOutChat = Class.forName("net.minecraft.server.v1_9_R1.PacketPlayOutChat");
            Class<?> class_EntityPlayer = Class.forName("net.minecraft.server.v1_9_R1.EntityPlayer");
            Class<?> class_PlayerConnection = Class.forName("net.minecraft.server.v1_9_R1.PlayerConnection");
            Class<?> class_Packet = Class.forName("net.minecraft.server.v1_9_R1.Packet");

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
            String error = "An error has occured while sending an NMS action bar for v1_9_R1!";
            Util.print(error);
            ex.printStackTrace();
        }
    }

    @Override
    public void sendBossBar(Player player, String style, String color, String title, float progress) {
        BossBar bossBar = Bukkit.createBossBar(title, BarColor.valueOf(color), BarStyle.valueOf(style));
        bossBar.setProgress(progress);
        bossBar.setVisible(true);
        bossBar.addPlayer(player);
        BOSS_BARS.put(player.getUniqueId(), bossBar);
    }

    @Override
    public void removeBossBar(Player player) {
        UUID uuid = player.getUniqueId();
        if (BOSS_BARS.containsKey(uuid)) {
            BossBar bossBar = BOSS_BARS.get(uuid);
            bossBar.setVisible(false);
            bossBar.removePlayer(player);
            BOSS_BARS.remove(uuid);
        }
    }

    @Override
    public Objective createScoreboardObjective(Scoreboard scoreboard, String name, String criteria, String displayName) {
        Objective obj = scoreboard.registerNewObjective(name, criteria);
        obj.setDisplayName(displayName);
        return obj;
    }

}