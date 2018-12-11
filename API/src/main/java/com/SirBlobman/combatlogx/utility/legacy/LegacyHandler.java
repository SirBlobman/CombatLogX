package com.SirBlobman.combatlogx.utility.legacy;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public abstract class LegacyHandler {
    /**
     * Helper method to get a class from inside of a class
     * @param original The parent class
     * @param innerClassName The name of the class inside the parent
     * @return The class with the name {@code innerClassName} inside of the {@code original} class or NULL if it doesn't exist
     */
    public static Class<?> getInnerClass(Class<?> original, String innerClassName) {
        throw new UnsupportedOperationException();
    }
    
    public static LegacyHandler getLegacyHandler() {
        throw new UnsupportedOperationException();
    }
    
    public static String getMinecraftVersion() {
        throw new UnsupportedOperationException();
    }

    public static String baseVersion() {
        throw new UnsupportedOperationException();
    }

    public static int getMajorVersion() {
        throw new UnsupportedOperationException();
    }

    public static int getMinorVersion() {
        throw new UnsupportedOperationException();
    }

    public abstract double getMaxHealth(LivingEntity entity);

    public abstract void setMaxHealth(LivingEntity entity, double maxHealth);

    public abstract void sendActionBar(Player player, String action);

    public abstract void sendBossBar(Player player, String style, String color, String title, float progress);

    public abstract void removeBossBar(Player player);

    public abstract Objective createScoreboardObjective(Scoreboard scoreboard, String name, String criteria, String displayName);
}