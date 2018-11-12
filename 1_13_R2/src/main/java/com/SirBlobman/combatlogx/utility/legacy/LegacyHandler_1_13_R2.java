package com.SirBlobman.combatlogx.utility.legacy;

import com.SirBlobman.combatlogx.utility.Util;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Player.Spigot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Map;
import java.util.UUID;

public class LegacyHandler_1_13_R2 extends LegacyHandler {
    private static final Map<UUID, BossBar> BOSS_BARS = Util.newMap();

    @Override
    public double getMaxHealth(LivingEntity entity) {
        AttributeInstance maxHealthAI = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        return maxHealthAI.getValue();
    }

    @Override
    public void setMaxHealth(LivingEntity entity, double maxHealth) {
        AttributeInstance maxHealthAI = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        maxHealthAI.setBaseValue(maxHealth);
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
        Objective obj = scoreboard.registerNewObjective(name, criteria, displayName);
        return obj;
    }

}