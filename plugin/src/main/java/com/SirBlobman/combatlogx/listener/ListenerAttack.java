package com.SirBlobman.combatlogx.listener;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.event.PlayerPreTagEvent;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

public class ListenerAttack implements Listener {
    private final ICombatLogX plugin;
    public ListenerAttack(ICombatLogX plugin) {
        this.plugin = plugin;
    }

    private Entity linkProjectile(Entity original) {
        if(original == null) return null;

        FileConfiguration config = this.plugin.getConfig("config.yml");
        if(!config.getBoolean("link-projectiles")) return original;

        if(original instanceof Projectile) {
            Projectile projectile = (Projectile) original;
            ProjectileSource shooter = projectile.getShooter();
            if(shooter instanceof Entity) return (Entity) shooter;
        }

        return original;
    }

    private Entity linkPet(Entity original) {
        if(original == null) return null;

        FileConfiguration config = this.plugin.getConfig("config.yml");
        if(!config.getBoolean("link-pets")) return original;

        if(original instanceof Tameable) {
            Tameable pet = (Tameable) original;
            AnimalTamer owner = pet.getOwner();
            if(owner instanceof Entity) return (Entity) owner;
        }

        return original;
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onAttack(EntityDamageByEntityEvent e) {
        Entity damager = linkPet(linkProjectile(e.getDamager()));
        Entity damaged = e.getEntity();

        if(!(damaged instanceof LivingEntity)) return;
        LivingEntity attacked = (LivingEntity) damaged;

        if(!(damager instanceof LivingEntity)) return;
        LivingEntity attacker = (LivingEntity) damager;

        if(attacked instanceof Player) {
            Player player = (Player) attacked;
            PlayerPreTagEvent.TagType tagType = (attacker instanceof Player ? PlayerPreTagEvent.TagType.PLAYER : PlayerPreTagEvent.TagType.MOB);
            PlayerPreTagEvent.TagReason tagReason = PlayerPreTagEvent.TagReason.ATTACKED;

            ICombatManager combatManager = this.plugin.getCombatManager();
            combatManager.tag(player, attacker, tagType, tagReason);
        }

        if(attacker instanceof Player) {
            Player player = (Player) attacker;
            PlayerPreTagEvent.TagType tagType = (attacked instanceof Player ? PlayerPreTagEvent.TagType.PLAYER : PlayerPreTagEvent.TagType.MOB);
            PlayerPreTagEvent.TagReason tagReason = PlayerPreTagEvent.TagReason.ATTACKER;

            ICombatManager combatManager = this.plugin.getCombatManager();
            combatManager.tag(player, attacked, tagType, tagReason);
        }
    }
}