package com.SirBlobman.combatlogx.utility.legacy;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public abstract class LegacyHandler {
    public static Class<?> getInnerClass(Class<?> original, String innerClassName) {
        throw new UnsupportedOperationException();
    }
    
    public static LegacyHandler getLegacyHandler() {
        throw new UnsupportedOperationException();
    }

    public abstract double getMaxHealth(LivingEntity entity);

    public abstract void setMaxHealth(LivingEntity entity, double maxHealth);

    public abstract void sendActionBar(Player player, String action);

    public abstract void sendBossBar(Player player, String style, String color, String title, float progress);

    public abstract void removeBossBar(Player player);

    public abstract Objective createScoreboardObjective(Scoreboard scoreboard, String name, String criteria, String displayName);
}