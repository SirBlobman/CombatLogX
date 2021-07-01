package com.github.sirblobman.combatlogx.api.utility;

import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.projectiles.ProjectileSource;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;

import org.jetbrains.annotations.Nullable;

public final class EntityHelper {
    public static Entity linkPet(Entity original) {
        if(!(original instanceof Tameable)) return original;
        Tameable tameable = (Tameable) original;

        AnimalTamer animalTamer = tameable.getOwner();
        return (animalTamer instanceof Entity ? (Entity) animalTamer : original);
    }

    public static Entity linkProjectile(ICombatLogX plugin, Entity original) {
        if(!(original instanceof Projectile)) return original;

        Projectile projectile = (Projectile) original;
        if(isProjectileIgnored(plugin, projectile)) return original;

        ProjectileSource shooter = projectile.getShooter();
        return (shooter instanceof Entity ? (Entity) shooter : original);
    }

    public static boolean isNPC(@Nullable Entity entity) {
        return (entity != null && entity.hasMetadata("NPC"));
    }

    private static boolean isProjectileIgnored(ICombatLogX plugin, Projectile projectile) {
        if(projectile == null) return true;

        ConfigurationManager configurationManager = plugin.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        List<String> entityTypeNameList = configuration.getStringList("ignored-projectiles");

        EntityType entityType = projectile.getType();
        String entityTypeName = entityType.name();
        return entityTypeNameList.contains(entityTypeName);
    }
}
