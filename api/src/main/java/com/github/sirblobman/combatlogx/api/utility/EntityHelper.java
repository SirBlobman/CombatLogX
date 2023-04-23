package com.github.sirblobman.combatlogx.api.utility;

import org.jetbrains.annotations.NotNull;

import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Tameable;
import org.bukkit.projectiles.ProjectileSource;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.configuration.MainConfiguration;

public final class EntityHelper {
    public static @NotNull Entity linkTNT(@NotNull Entity original) {
        if (!(original instanceof TNTPrimed)) {
            return original;
        }

        TNTPrimed tntEntity = (TNTPrimed) original;
        Entity source = tntEntity.getSource();
        return (source == null ? original : source);
    }

    public static @NotNull Entity linkPet(@NotNull Entity original) {
        if (!(original instanceof Tameable)) {
            return original;
        }

        Tameable tameable = (Tameable) original;
        AnimalTamer animalTamer = tameable.getOwner();
        if (!(animalTamer instanceof Entity)) {
            return original;
        }

        return (Entity) animalTamer;
    }

    public static @NotNull Entity linkProjectile(@NotNull ICombatLogX plugin, @NotNull Entity original) {
        if (!(original instanceof Projectile)) {
            return original;
        }

        Projectile projectile = (Projectile) original;
        if (isProjectileIgnored(plugin, projectile)) {
            return original;
        }

        ProjectileSource shooter = projectile.getShooter();
        if (!(shooter instanceof Entity)) {
            return original;
        }

        return (Entity) shooter;
    }

    public static boolean isNPC(@NotNull Entity entity) {
        return entity.hasMetadata("NPC");
    }

    private static boolean isProjectileIgnored(@NotNull ICombatLogX plugin, @NotNull Projectile projectile) {
        EntityType projectileType = projectile.getType();
        MainConfiguration configuration = plugin.getConfiguration();
        return configuration.isProjectileIgnored(projectileType);
    }
}
