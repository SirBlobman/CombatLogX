package com.SirBlobman.combatlogx.utility.legacy;

import com.SirBlobman.combatlogx.utility.Util;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Player.Spigot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;

public class LegacyHandler_1_10_R1 extends LegacyHandler {
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
    }

    @Override
    public void sendActionBar(Player player, String msg) {
        Spigot spigot = player.spigot();
        spigot.sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
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