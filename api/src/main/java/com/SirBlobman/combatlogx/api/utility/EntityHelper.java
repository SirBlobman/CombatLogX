package com.SirBlobman.combatlogx.api.utility;

import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.projectiles.ProjectileSource;

public final class EntityHelper {
    public static Entity linkPet(Entity original) {
        if(!(original instanceof Tameable)) return original;
        Tameable tameable = (Tameable) original;

        AnimalTamer animalTamer = tameable.getOwner();
        return (animalTamer instanceof Entity ? (Entity) animalTamer : original);
    }

    public static Entity linkProjectile(Entity original) {
        if(!(original instanceof Projectile)) return original;
        Projectile projectile = (Projectile) original;

        ProjectileSource shooter = projectile.getShooter();
        return (shooter instanceof Entity ? (Entity) shooter : original);
    }
}