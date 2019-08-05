package com.SirBlobman.combatlogx.listener;

import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.event.PlayerTagEvent.TagReason;
import com.SirBlobman.combatlogx.event.PlayerTagEvent.TagType;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

public class AttackListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent e) {
        Entity damaged = e.getEntity();
        Entity damager = e.getDamager();

        if (damager instanceof Projectile && ConfigOptions.OPTION_LINK_PROJECTILES) {
            Projectile projectile = (Projectile) damager;
            if (!projectile.getType().equals(EntityType.ENDER_PEARL)) {
                ProjectileSource shooter = projectile.getShooter();
                if (shooter instanceof Entity) damager = (Entity) shooter;
            }
        }

        if (damager instanceof Tameable && ConfigOptions.OPTION_LINK_PETS) {
            Tameable pet = (Tameable) damager;
            AnimalTamer petOwner = pet.getOwner();
            if (petOwner instanceof Entity) damager = (Entity) petOwner;
        }

        if (damaged instanceof LivingEntity && damager instanceof LivingEntity) {
            if (damaged instanceof Player) {
                Player player = (Player) damaged;
                LivingEntity enemy = (LivingEntity) damager;
                TagType type = (damager instanceof Player) ? TagType.PLAYER : TagType.MOB;
                TagReason reason = TagReason.ATTACKED;
                CombatUtil.tag(player, enemy, type, reason);
            }

            if (damager instanceof Player) {
                Player player = (Player) damager;
                LivingEntity enemy = (LivingEntity) damaged;
                TagType type = damaged instanceof Player ? TagType.PLAYER : TagType.MOB;
                TagReason reason = TagReason.ATTACKER;
                CombatUtil.tag(player, enemy, type, reason);
            }
        }
    }
}